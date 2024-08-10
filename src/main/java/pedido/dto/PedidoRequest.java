package pedido.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@XmlRootElement(name = "pedido")
public class PedidoRequest {

    @NotNull
    @XmlElement(name = "numeroControle")
    private String numeroControle;

    @XmlElement(name = "dataCadastro")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
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
