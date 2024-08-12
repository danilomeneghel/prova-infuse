package pedido.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import org.modelmapper.ModelMapper;
import org.springframework.web.multipart.MultipartFile;
import pedido.model.Pedido;
import pedido.dto.PedidoRequest;
import pedido.model.Pedidos;
import pedido.service.PedidoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private ModelMapper modelMapper = new ModelMapper();
    
    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
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
    public void importarPedidosJson(@RequestBody List<PedidoRequest> pedidosRequest) {
        importarPedidos(pedidosRequest);
    }

    @PostMapping("/importar-arquivo-json")
    public ResponseEntity<String> importarPedidosArquivoJson(@RequestParam("arquivos") MultipartFile[] arquivos) {
        for (MultipartFile arquivo : arquivos) {
            if (!arquivo.isEmpty()) {
                try {
                    String jsonContent = new String(arquivo.getBytes(), StandardCharsets.UTF_8);

                    List<Map<String, Object>> pedidosMapList = objectMapper.readValue(
                            jsonContent,
                            new TypeReference<List<Map<String, Object>>>() {}
                    );

                    List<PedidoRequest> pedidosRequest = pedidosMapList.stream()
                            .map(map -> modelMapper.map(map, PedidoRequest.class))
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
        return ResponseEntity.ok("Todos os arquivos foram processados com sucesso");
    }

    @PostMapping(path = "/importar-xml", consumes = "application/xml", produces = "application/json")
    public void importarPedidosXml(@RequestBody String xml) {
        List<PedidoRequest> pedidosRequest = parseXmlToPedidoRequests(xml);
        importarPedidos(pedidosRequest);
    }

    @PostMapping("/importar-arquivo-xml")
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
        return ResponseEntity.ok("Todos os arquivos XML foram processados com sucesso");
    }

    private List<PedidoRequest> parseXmlToPedidoRequests(String xml) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Pedidos.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            Pedidos pedidos = (Pedidos) unmarshaller.unmarshal(new StringReader(xml));
            return pedidos.getPedido();
        } catch (JAXBException e) {
            throw new RuntimeException("Erro ao processar XML", e);
        }
    }

    @GetMapping
    public ResponseEntity<List<Pedido>> consultarPedidos(
            @RequestParam(required = false) String numeroControle,
            @RequestParam(required = false) LocalDate dataCadastro) {
        List<Pedido> pedidos = pedidoService.consultarPedidos(numeroControle, dataCadastro);
        return new ResponseEntity<>(pedidos, HttpStatus.OK);
    }
}
