package pedido.model;

import lombok.Data;
import pedido.dto.PedidoRequest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@Data
@XmlRootElement(name = "pedidos")
@XmlAccessorType(XmlAccessType.FIELD)
public class Pedidos {

    @XmlElement(name = "pedido")
    private List<PedidoRequest> pedido;

}
