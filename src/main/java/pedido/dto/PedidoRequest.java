package pedido.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pedido.util.LocalDateAdapter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "pedido")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonInclude(JsonInclude.Include.NON_NULL)
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

}
