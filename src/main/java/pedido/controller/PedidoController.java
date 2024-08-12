package pedido.controller;

import pedido.model.Pedido;
import pedido.dto.PedidoRequest;
import pedido.model.Pedidos;
import pedido.service.PedidoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.annotation.PostConstruct;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Value("${pedido.importar.json.path}")
    private String jsonFilePath;

    @Value("${pedido.importar.xml.path}")
    private String xmlFilePath;

    private final PedidoService pedidoService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostConstruct
    public void init() {
        // Processar os arquivos de pedidos na inicialização da aplicação
        importarPedidosJson();
        importarPedidosXml();
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

    @PostMapping(path = "/importar-json", consumes = "application/json")
    public ResponseEntity<String> importarPedidosJson(@RequestBody List<PedidoRequest> pedidosRequest) {
        if (pedidosRequest.size() > 10) {
            return new ResponseEntity<>("Número máximo de pedidos é 10", HttpStatus.BAD_REQUEST);
        }

        for (PedidoRequest request : pedidosRequest) {
            try {
                pedidoService.criarPedido(request);
            } catch (RuntimeException e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>("Pedidos importados com sucesso", HttpStatus.OK);
    }

    private void importarPedidosJson() {
        Path path = Paths.get(jsonFilePath);
        if (Files.exists(path)) {
            try {
                List<PedidoRequest> pedidosRequest = objectMapper.readValue(Files.readString(path),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, PedidoRequest.class));

                if (pedidosRequest.size() > 10) {
                    System.out.println("Número máximo de pedidos é 10");
                    return;
                }

                for (PedidoRequest request : pedidosRequest) {
                    try {
                        pedidoService.criarPedido(request);
                    } catch (RuntimeException e) {
                        System.out.println(e.getMessage());
                    }
                }
            } catch (IOException e) {
                System.out.println("Erro ao ler o arquivo JSON: " + e.getMessage());
            }
        } else {
            System.out.println("Arquivo JSON não encontrado em: " + jsonFilePath);
        }
    }

    @PostMapping(path = "/importar-xml", consumes = "application/xml", produces = "application/json")
    public ResponseEntity<String> importarPedidosXml(@RequestBody String xml) {
        try {
            List<PedidoRequest> pedidosRequest = parseXmlToPedidoRequests(xml);

            if (pedidosRequest.size() > 10) {
                return new ResponseEntity<>("Número máximo de pedidos é 10", HttpStatus.BAD_REQUEST);
            }

            for (PedidoRequest request : pedidosRequest) {
                try {
                    pedidoService.criarPedido(request);
                } catch (RuntimeException e) {
                    return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
                }
            }
            return new ResponseEntity<>("Pedidos importados com sucesso", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Erro ao processar XML", HttpStatus.BAD_REQUEST);
        }
    }

    private void importarPedidosXml() {
        Path path = Paths.get(xmlFilePath);
        if (Files.exists(path)) {
            try {
                String xml = Files.readString(path);
                List<PedidoRequest> pedidosRequest = parseXmlToPedidoRequests(xml);

                if (pedidosRequest.size() > 10) {
                    System.out.println("Número máximo de pedidos é 10");
                    return;
                }

                for (PedidoRequest request : pedidosRequest) {
                    try {
                        pedidoService.criarPedido(request);
                    } catch (RuntimeException e) {
                        System.out.println(e.getMessage());
                    }
                }
            } catch (IOException e) {
                System.out.println("Erro ao ler o arquivo XML: " + e.getMessage());
            }
        } else {
            System.out.println("Arquivo XML não encontrado em: " + xmlFilePath);
        }
    }

    private List<PedidoRequest> parseXmlToPedidoRequests(String xml) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Pedidos.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            Pedidos pedidos = (Pedidos) unmarshaller.unmarshal(new StringReader(xml));
            for (PedidoRequest pedidoRequest : pedidos.getPedido()) {
                if (pedidoRequest.getDataCadastro() == null) {
                    pedidoRequest.setDataCadastro(LocalDate.now()); // Define uma data padrão
                }
            }
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
