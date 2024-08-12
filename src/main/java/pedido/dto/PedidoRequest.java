package pedido.dto;

import lombok.*;
import pedido.util.LocalDateAdapter;

import javax.validation.constraints.NotNull;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "pedido")
@XmlAccessorType(XmlAccessType.FIELD)
public class PedidoRequest {

    @NotNull
    @XmlElement(name = "numeroControle")
    private String numeroControle;

    @XmlElement(name = "dataCadastro", required = false)
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate dataCadastro;

    @NotNull
    @XmlElement(name = "nome")
    private String nome;

    @NotNull
    @XmlElement(name = "valorUnitario")
    private BigDecimal valorUnitario;

    @XmlElement(name = "quantidade")
    private Integer quantidade;

    @NotNull
    @XmlElement(name = "codigoCliente")
    private Integer codigoCliente;

    public void afterUnmarshal(Unmarshaller u, Object parent) {
        if (this.dataCadastro == null) {
            this.dataCadastro = LocalDate.now(); // Define um valor padrão
        }
    }

}
