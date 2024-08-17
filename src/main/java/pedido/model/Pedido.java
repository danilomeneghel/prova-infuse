package pedido.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String numeroControle;

    private LocalDate dataCadastro;

    private String nome;

    private BigDecimal valorUnitario;

    private Integer quantidade = 1;

    private Integer codigoCliente;

    private BigDecimal valorTotal;
}
