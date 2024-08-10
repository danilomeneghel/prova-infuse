package pedido.repository;

import pedido.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    Pedido findByNumeroControle(String numeroControle);
    List<Pedido> findAllByDataCadastro(LocalDate dataCadastro);
}
