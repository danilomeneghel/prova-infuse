package pedido.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pedido.model.Pedido;

import java.time.LocalDate;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    Pedido findByNumeroControle(String numeroControle);

    List<Pedido> findAllByDataCadastro(LocalDate dataCadastro);
}
