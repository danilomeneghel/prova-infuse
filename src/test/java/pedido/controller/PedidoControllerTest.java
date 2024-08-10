package pedido.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import pedido.dto.PedidoRequest;
import pedido.model.Pedido;
import pedido.service.PedidoService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PedidoController.class)
public class PedidoControllerTest {

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
        PedidoRequest request = new PedidoRequest();
        request.setNumeroControle("12345");
        request.setNome("Produto Teste");
        request.setValorUnitario(BigDecimal.valueOf(100));
        request.setQuantidade(10);
        request.setCodigoCliente(1);

        Pedido pedido = new Pedido();
        pedido.setNumeroControle("12345");
        pedido.setNome("Produto Teste");
        pedido.setValorUnitario(BigDecimal.valueOf(100));
        pedido.setQuantidade(10);
        pedido.setCodigoCliente(1);
        pedido.setValorTotal(BigDecimal.valueOf(900)); // 10% de desconto

        when(pedidoService.criarPedido((PedidoRequest) any(PedidoRequest.class))).thenReturn(pedido);

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numeroControle", is("12345")))
                .andExpect(jsonPath("$.valorTotal", is(900)));
    }

    @Test
    public void testImportarPedidosJson() throws Exception {
        PedidoRequest request = new PedidoRequest();
        request.setNumeroControle("12345");
        request.setNome("Produto Teste");
        request.setValorUnitario(BigDecimal.valueOf(100));
        request.setQuantidade(10);
        request.setCodigoCliente(1);

        when(pedidoService.criarPedido((PedidoRequest) any(PedidoRequest.class))).thenReturn(new Pedido());

        mockMvc.perform(post("/api/pedidos/importar-json")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(request))))
                .andExpect(status().isOk())
                .andExpect(content().string("Pedidos importados com sucesso"));
    }

    @Test
    public void testImportarPedidosXml() throws Exception {
        String xml = "<pedido><numeroControle>12345</numeroControle><nome>Produto Teste</nome><valorUnitario>100</valorUnitario><quantidade>10</quantidade><codigoCliente>1</codigoCliente></pedido>";

        when(pedidoService.criarPedido((PedidoRequest) any(PedidoRequest.class))).thenReturn(new Pedido());

        mockMvc.perform(post("/api/pedidos/importar-xml")
                        .contentType(MediaType.APPLICATION_XML)
                        .content(xml))
                .andExpect(status().isOk())
                .andExpect(content().string("Pedidos importados com sucesso"));
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

