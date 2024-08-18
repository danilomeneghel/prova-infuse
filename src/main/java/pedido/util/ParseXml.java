package pedido.util;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import pedido.dto.PedidoRequest;
import pedido.model.Pedidos;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

public class ParseXml {

    public static List<PedidoRequest> parseXmlToPedidoRequests(String xmlString) {
        try {
            // Converte a String XML para InputStream
            InputStream xmlInputStream = new ByteArrayInputStream(xmlString.getBytes(StandardCharsets.UTF_8));

            // Cria o contexto JAXB e o unmarshaller
            JAXBContext jaxbContext = JAXBContext.newInstance(Pedidos.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            // Deserializa o XML
            Pedidos pedidos = (Pedidos) unmarshaller.unmarshal(xmlInputStream);

            // Verifica se a lista de pedidos é nula, e se for, inicialize-a como uma lista vazia
            List<PedidoRequest> pedidoRequests = pedidos.getPedido();
            if (pedidoRequests == null) {
                pedidoRequests = Collections.emptyList();
            }

            return pedidoRequests;
        } catch (JAXBException e) {
            // Trate a exceção de acordo com sua necessidade
            e.printStackTrace();
            throw new RuntimeException("Erro ao deserializar XML", e);
        }
    }

}
