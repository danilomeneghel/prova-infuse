package pedido.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;
import pedido.model.Pedido;
import pedido.dto.PedidoRequest;
import pedido.model.Pedidos;
import pedido.service.PedidoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pedidos")
@Tag( name = "Pedidos", description = "Cadastro de Pedidos" )
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
        }

        for (PedidoRequest pedidoRequest : pedidosRequest) {
            try {
                List<Pedido> pedidos = pedidoService.consultarPedidos(pedidoRequest.getNumeroControle(), null);
                if (pedidos.isEmpty()) {
                    pedidoService.criarPedido(pedidoRequest);
                }
            } catch (RuntimeException e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>("Pedido(s) importado(s) com sucesso", HttpStatus.OK);
    }

    @PostMapping(path = "/importar-json", consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> importarPedidosJson(@RequestBody List<PedidoRequest> pedidosRequest) {
        return importarPedidos(pedidosRequest);
    }

    @PostMapping(path = "/importar-arquivo-json", consumes = "multipart/form-data")
    public ResponseEntity<String> importarPedidosArquivoJson(@RequestParam("arquivos") MultipartFile[] arquivos) {
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

                    importarPedidos(pedidosRequest);
                } catch (IOException e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Erro ao ler o arquivo JSON: " + e.getMessage());
                }
            } else {
                return ResponseEntity.badRequest().body("Arquivo vazio: " + arquivo.getOriginalFilename());
            }
        }
        return ResponseEntity.ok("Arquivo(s) JSON importado(s) com sucesso");
    }

    @PostMapping(path = "/importar-xml", consumes = "application/xml", produces = "application/json")
    public ResponseEntity<String> importarPedidosXml(@RequestBody String xmlString) throws JAXBException {
        List<PedidoRequest> pedidosRequest = parseXmlToPedidoRequests(xmlString);
        return importarPedidos(pedidosRequest);
    }

    @PostMapping(path = "/importar-arquivo-xml", consumes = "multipart/form-data")
    public ResponseEntity<String> importarPedidosArquivoXml(@RequestParam("arquivos") MultipartFile[] arquivos) {
        for (MultipartFile arquivo : arquivos) {
            if (!arquivo.isEmpty()) {
                try {
                    String xml = new String(arquivo.getBytes(), StandardCharsets.UTF_8);
                    List<PedidoRequest> pedidosRequest = parseXmlToPedidoRequests(xml);

                    importarPedidos(pedidosRequest);
                } catch (IOException e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Erro ao ler o arquivo XML: " + e.getMessage());
                }
            } else {
                return ResponseEntity.badRequest().body("Arquivo vazio: " + arquivo.getOriginalFilename());
            }
        }
        return ResponseEntity.ok("Arquivo(s) XML importado(s) com sucesso");
    }

    public List<PedidoRequest> parseXmlToPedidoRequests(String xmlString) {
        try {
            // Converte a String XML para InputStream
            InputStream xmlInputStream = new ByteArrayInputStream(xmlString.getBytes(StandardCharsets.UTF_8));

            // Cria o contexto JAXB e o unmarshaller
            JAXBContext jaxbContext = JAXBContext.newInstance(Pedidos.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            // Deserializa o XML
            Pedidos pedidos = (Pedidos) unmarshaller.unmarshal(xmlInputStream);

            // Verifica se a lista de pedidos é nula, e se for, inicialize-a como uma lista vazia
            List<PedidoRequest> pedidoRequests = pedidos.getPedido();
            if (pedidoRequests == null) {
                pedidoRequests = Collections.emptyList();
            }

            return pedidoRequests;
        } catch (JAXBException e) {
            // Trate a exceção de acordo com sua necessidade
            e.printStackTrace();
            throw new RuntimeException("Erro ao deserializar XML", e);
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
