package pedido.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;
import pedido.dto.PedidoRequest;

import java.util.List;

@Data
@XmlRootElement(name = "pedidos")
@XmlAccessorType(XmlAccessType.FIELD)
public class Pedidos {

    @XmlElement(name = "pedido")
    private List<PedidoRequest> pedido;

}
