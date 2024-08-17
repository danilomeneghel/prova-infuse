package pedido.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import pedido.ApplicationTests;
import pedido.controller.PedidoController;
import pedido.dto.PedidoRequest;
import pedido.model.Pedido;
import pedido.repository.PedidoRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PedidoServiceTest extends ApplicationTests {

    @Mock
    private PedidoRepository pedidoRepository;

    @InjectMocks
    private PedidoService pedidoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCriarPedidoComSucesso() {
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

        when(pedidoRepository.findByNumeroControle(any())).thenReturn(null);
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        Pedido resultado = pedidoService.criarPedido(request);

        assertNotNull(resultado);
        assertEquals("12345", resultado.getNumeroControle());
        assertEquals(BigDecimal.valueOf(900), resultado.getValorTotal());
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
    }

    @Test
    public void testCriarPedidoNumeroControleExistente() {
        PedidoRequest request = new PedidoRequest();
        request.setNumeroControle("12345");

        when(pedidoRepository.findByNumeroControle(any())).thenReturn(new Pedido());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            pedidoService.criarPedido(request);
        });

        assertEquals("Número de controle já cadastrado.", thrown.getMessage());
        verify(pedidoRepository, times(0)).save(any(Pedido.class));
    }

    @Test
    public void testConsultarPedidosPorNumeroControle() {
        Pedido pedido = new Pedido();
        pedido.setNumeroControle("12345");
        pedido.setNome("Produto Teste");
        pedido.setValorUnitario(BigDecimal.valueOf(100));
        pedido.setQuantidade(1);
        pedido.setCodigoCliente(1);
        pedido.setValorTotal(BigDecimal.valueOf(100));

        when(pedidoRepository.findByNumeroControle(any())).thenReturn(pedido);

        List<Pedido> resultados = pedidoService.consultarPedidos("12345", null);

        assertNotNull(resultados);
        assertEquals(1, resultados.size());
        assertEquals("12345", resultados.get(0).getNumeroControle());
    }

    @Test
    public void testConsultarPedidosPorDataCadastro() {
        Pedido pedido = new Pedido();
        pedido.setNumeroControle("12345");
        pedido.setNome("Produto Teste");
        pedido.setValorUnitario(BigDecimal.valueOf(100));
        pedido.setQuantidade(1);
        pedido.setCodigoCliente(1);
        pedido.setValorTotal(BigDecimal.valueOf(100));
        pedido.setDataCadastro(LocalDate.of(2024, 8, 10));

        when(pedidoRepository.findAllByDataCadastro(any())).thenReturn(List.of(pedido));

        List<Pedido> resultados = pedidoService.consultarPedidos(null, LocalDate.of(2024, 8, 10));

        assertNotNull(resultados);
        assertEquals(1, resultados.size());
        assertEquals("12345", resultados.get(0).getNumeroControle());
    }
}
