package pedido.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import pedido.ApplicationTests;
import pedido.model.Pedido;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PedidoRepositoryTest extends ApplicationTests {

    @Autowired
    private PedidoRepository pedidoRepository;

    @BeforeEach
    void setUp() {
        pedidoRepository.deleteAll();
    }

    @Test
    void testFindByNumeroControle() {
        Pedido pedido1 = new Pedido();
        pedido1.setNumeroControle("12345");
        pedido1.setDataCadastro(LocalDate.now());
        pedidoRepository.save(pedido1);

        Pedido pedido = pedidoRepository.findByNumeroControle("12345");

        assertThat(pedido).isNotNull();
        assertThat(pedido.getNumeroControle()).isEqualTo("12345");
    }

    @Test
    void testFindAllByDataCadastro() {
        LocalDate dataCadastro = LocalDate.now();

        Pedido pedido1 = new Pedido();
        pedido1.setNumeroControle("12345");
        pedido1.setDataCadastro(dataCadastro);
        pedidoRepository.save(pedido1);

        Pedido pedido2 = new Pedido();
        pedido2.setNumeroControle("67890");
        pedido2.setDataCadastro(dataCadastro);
        pedidoRepository.save(pedido2);

        List<Pedido> pedidos = pedidoRepository.findAllByDataCadastro(dataCadastro);

        assertThat(pedidos).hasSize(2);
        assertThat(pedidos).extracting(Pedido::getNumeroControle).containsExactlyInAnyOrder("12345", "67890");
    }
}
