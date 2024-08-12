package pedido.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pedido.ApplicationTests;
import pedido.dto.PedidoRequest;
import pedido.model.Pedido;
import pedido.model.Pedidos;
import pedido.service.PedidoService;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class PedidoControllerTest extends ApplicationTests {

    @Value("${pedido.importar.json.path}")
    private String jsonFilePath;

    @Value("${pedido.importar.xml.path}")
    private String xmlFilePath;

    @Mock
    private PedidoService pedidoService;

    @InjectMocks
    private PedidoController pedidoController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(pedidoController).build();
        this.objectMapper = new ObjectMapper();
    }

    @Test
    public void testCriarPedido() throws Exception {
        PedidoRequest pedidoRequest = new PedidoRequest();
        pedidoRequest.setNumeroControle("12345");
        pedidoRequest.setNome("Produto Teste");
        pedidoRequest.setValorUnitario(BigDecimal.valueOf(100));
        pedidoRequest.setQuantidade(10);
        pedidoRequest.setCodigoCliente(1);

        Pedido pedido = new Pedido();
        pedido.setNumeroControle("12345");
        pedido.setNome("Produto Teste");
        pedido.setValorUnitario(BigDecimal.valueOf(100));
        pedido.setQuantidade(10);
        pedido.setCodigoCliente(1);
        pedido.setValorTotal(BigDecimal.valueOf(900)); // 10% de desconto

        when(pedidoService.criarPedido(pedidoRequest)).thenReturn(pedido);

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pedidoRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    public void testImportarPedidosJson() throws Exception {
        PedidoRequest pedidoRequest = new PedidoRequest();
        pedidoRequest.setNumeroControle("12345");
        pedidoRequest.setNome("Produto Teste");
        pedidoRequest.setValorUnitario(BigDecimal.valueOf(100));
        pedidoRequest.setQuantidade(10);
        pedidoRequest.setCodigoCliente(1);

        when(pedidoService.criarPedido(pedidoRequest)).thenReturn(new Pedido());

        mockMvc.perform(post("/api/pedidos/importar-json")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonFilePath))
                .andExpect(status().isOk())
                .andExpect(content().string("Pedidos importados com sucesso"));
    }

    @Test
    public void testImportarPedidosXml() throws Exception {
        PedidoRequest pedidoRequest = new PedidoRequest();
        pedidoRequest.setNumeroControle("12345");
        pedidoRequest.setNome("Produto Teste");
        pedidoRequest.setValorUnitario(BigDecimal.valueOf(100));
        pedidoRequest.setQuantidade(10);
        pedidoRequest.setCodigoCliente(1);

        when(pedidoService.criarPedido(pedidoRequest)).thenReturn(new Pedido());

        String xml = null;
        List<PedidoRequest> pedidosRequest = null;
        Path path = Paths.get(xmlFilePath);
        if (Files.exists(path)) {
            xml = Files.readString(path);
            pedidosRequest = parseXmlToPedidoRequests(xml);
        }

        for (PedidoRequest request : pedidosRequest) {
            try {
                mockMvc.perform(post("/api/pedidos/importar-xml")
                                .contentType(MediaType.APPLICATION_XML)
                                .content(request.toString()))
                        .andExpect(status().isOk())
                        .andExpect(content().string("Pedidos importados com sucesso"));
            } catch (RuntimeException e) {
                throw new RuntimeException("Erro ao importar XML", e);
            }
        }
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

    @Test
    public void testConsultarPedidosPorNumeroControle() throws Exception {
        Pedido pedido = new Pedido();
        pedido.setNumeroControle("12345");
        pedido.setNome("Produto Teste");
        pedido.setValorUnitario(BigDecimal.valueOf(100));
        pedido.setQuantidade(1);
        pedido.setCodigoCliente(1);
        pedido.setValorTotal(BigDecimal.valueOf(100));

        when(pedidoService.consultarPedidos("12345", null)).thenReturn(List.of(pedido));

        mockMvc.perform(get("/api/pedidos")
                        .param("numeroControle", "12345"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].numeroControle", is("12345")))
                .andExpect(jsonPath("$[0].valorTotal", is(100)));
    }

    @Test
    public void testConsultarPedidosPorDataCadastro() throws Exception {
        Pedido pedido = new Pedido();
        pedido.setNumeroControle("12345");
        pedido.setNome("Produto Teste");
        pedido.setValorUnitario(BigDecimal.valueOf(100));
        pedido.setQuantidade(1);
        pedido.setCodigoCliente(1);
        pedido.setValorTotal(BigDecimal.valueOf(100));
        pedido.setDataCadastro(LocalDate.of(2024, 8, 10));

        when(pedidoService.consultarPedidos(null, LocalDate.of(2024, 8, 10))).thenReturn(List.of(pedido));

        mockMvc.perform(get("/api/pedidos")
                        .param("dataCadastro", "2024-08-10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].numeroControle", is("12345")))
                .andExpect(jsonPath("$[0].valorTotal", is(100)));
    }
}
