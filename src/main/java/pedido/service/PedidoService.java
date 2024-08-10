package pedido.service;

import pedido.model.Pedido;
import pedido.dto.PedidoRequest;
import pedido.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    public Pedido criarPedido(PedidoRequest request) {
        if (pedidoRepository.findByNumeroControle(request.getNumeroControle()) != null) {
            throw new RuntimeException("Número de controle já cadastrado.");
        }

        Pedido pedido = new Pedido();
        pedido.setNumeroControle(request.getNumeroControle());
        pedido.setDataCadastro(request.getDataCadastro() != null ? request.getDataCadastro() : LocalDate.now());
        pedido.setNome(request.getNome());
        pedido.setValorUnitario(request.getValorUnitario());
        pedido.setQuantidade(request.getQuantidade() != null ? request.getQuantidade() : 1);
        pedido.setCodigoCliente(request.getCodigoCliente());

        BigDecimal valorTotal = pedido.getValorUnitario().multiply(BigDecimal.valueOf(pedido.getQuantidade()));
        if (pedido.getQuantidade() >= 10) {
            valorTotal = valorTotal.multiply(BigDecimal.valueOf(0.9));
        } else if (pedido.getQuantidade() > 5) {
            valorTotal = valorTotal.multiply(BigDecimal.valueOf(0.95));
        }
        pedido.setValorTotal(valorTotal);

        return pedidoRepository.save(pedido);
    }

    public List<Pedido> consultarPedidos(String numeroControle, LocalDate dataCadastro) {
        if (numeroControle != null) {
            Pedido pedido = pedidoRepository.findByNumeroControle(numeroControle);
            return pedido != null ? List.of(pedido) : List.of();
        } else if (dataCadastro != null) {
            return pedidoRepository.findAllByDataCadastro(dataCadastro);
        } else {
            return pedidoRepository.findAll();
        }
    }
}
