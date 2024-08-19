package pedido.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.xml.bind.JAXBException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pedido.dto.PedidoRequest;
import pedido.model.Pedido;
import pedido.service.PedidoService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import pedido.util.ParseXml;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;
    private final ObjectMapper objectMapper;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @PostMapping(consumes = {"application/json", "application/xml"}, produces = "application/json")
    public ResponseEntity<Pedido> criarPedido(@RequestBody PedidoRequest pedidoRequest) {
        try {
            Pedido pedido = pedidoService.criarPedido(pedidoRequest);
            return new ResponseEntity<>(pedido, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<String> importarPedidos(List<PedidoRequest> pedidosRequest) {
        if (pedidosRequest.size() > 10) {
            return new ResponseEntity<>("Número máximo de pedidos é 10", HttpStatus.BAD_REQUEST);
        } else {
            for (PedidoRequest pedidoRequest : pedidosRequest) {
                try {
                    List<Pedido> pedidos = pedidoService.consultarPedidos(pedidoRequest.getNumeroControle(), null);
                    if (pedidos.isEmpty()) {
                        pedidoService.criarPedido(pedidoRequest);
                    } else {
                        return new ResponseEntity<>("Pedido já importado", HttpStatus.BAD_REQUEST);
                    }
                } catch (RuntimeException e) {
                    return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
                }
            }
            return new ResponseEntity<>("Pedido(s) importado(s) com sucesso", HttpStatus.OK);
        }
    }

    @PostMapping(path = "/importar-json", consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> importarPedidosJson(@RequestBody List<PedidoRequest> pedidosRequest) {
        return importarPedidos(pedidosRequest);
    }

    @PostMapping(path = "/importar-arquivo-json", consumes = "multipart/form-data")
    public ResponseEntity<String> importarPedidosArquivoJson(@RequestParam("arquivos") MultipartFile[] arquivos) {
        ResponseEntity<String> pedidosImportados = null;

        for (MultipartFile arquivo : arquivos) {
            if (!arquivo.isEmpty()) {
                try {
                    // Lê o conteúdo do arquivo JSON como uma String
                    String jsonContent = new String(arquivo.getBytes(), StandardCharsets.UTF_8);

                    // Converte o JSON para uma lista de mapas
                    List<Map<String, Object>> pedidosMapList = objectMapper.readValue(
                            jsonContent,
                            new TypeReference<List<Map<String, Object>>>() {}
                    );

                    // Converte a lista de mapas para uma lista de PedidoRequest usando ObjectMapper
                    List<PedidoRequest> pedidosRequest = pedidosMapList.stream()
                            .map(map -> objectMapper.convertValue(map, PedidoRequest.class))
                            .collect(Collectors.toList());

                    pedidosImportados = importarPedidos(pedidosRequest);
                } catch (IOException e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Erro ao ler o arquivo JSON: " + e.getMessage());
                }
            } else {
                return ResponseEntity.badRequest().body("Arquivo vazio: " + arquivo.getOriginalFilename());
            }
        }

        if (pedidosImportados.getStatusCode() != HttpStatus.BAD_REQUEST) {
            return ResponseEntity.ok("Arquivo(s) JSON importado(s) com sucesso");
        } else {
            return pedidosImportados;
        }
    }

    @PostMapping(path = "/importar-xml", consumes = "application/xml", produces = "application/json")
    public ResponseEntity<String> importarPedidosXml(@RequestBody String xmlString) throws JAXBException {
        List<PedidoRequest> pedidosRequest = ParseXml.parseXmlToPedidoRequests(xmlString);
        return importarPedidos(pedidosRequest);
    }

    @PostMapping(path = "/importar-arquivo-xml", consumes = "multipart/form-data")
    public ResponseEntity<String> importarPedidosArquivoXml(@RequestParam("arquivos") MultipartFile[] arquivos) {
        ResponseEntity<String> pedidosImportados = null;

        for (MultipartFile arquivo : arquivos) {
            if (!arquivo.isEmpty()) {
                try {
                    String xmlString = new String(arquivo.getBytes(), StandardCharsets.UTF_8);
                    List<PedidoRequest> pedidosRequest = ParseXml.parseXmlToPedidoRequests(xmlString);

                    pedidosImportados = importarPedidos(pedidosRequest);
                } catch (IOException e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Erro ao ler o arquivo XML: " + e.getMessage());
                }
            } else {
                return ResponseEntity.badRequest().body("Arquivo vazio: " + arquivo.getOriginalFilename());
            }
        }

        if (pedidosImportados.getStatusCode() != HttpStatus.BAD_REQUEST) {
            return ResponseEntity.ok("Arquivo(s) XML importado(s) com sucesso");
        } else {
            return pedidosImportados;
        }
    }

    @GetMapping
    public ResponseEntity<List<Pedido>> consultarPedidos(
            @RequestParam(required = false) String numeroControle,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataCadastro) {
        List<Pedido> pedidos = pedidoService.consultarPedidos(numeroControle, dataCadastro);
        return new ResponseEntity<>(pedidos, HttpStatus.OK);
    }

}
