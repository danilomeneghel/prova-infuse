package pedido.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pedido.Application;
import pedido.ApplicationTests;
import pedido.dto.PedidoRequest;
import pedido.model.Pedido;
import pedido.model.Pedidos;
import pedido.service.PedidoService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PedidoController.class)
@ExtendWith(MockitoExtension.class)
public class PedidoControllerTest extends ApplicationTests {

    @Value("${pedido.importar.file.json}")
    private String fileJson;

    @Value("${pedido.importar.file.xml}")
    private String fileXml;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PedidoService pedidoService;

    @Autowired
    private ResourceLoader resourceLoader;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        this.objectMapper = new ObjectMapper();
    }

    @Test
    public void testCriarPedido() throws Exception {
        PedidoRequest pedidoRequest = new PedidoRequest();
        pedidoRequest.setNumeroControle("12345");
        pedidoRequest.setNome("Produto Teste");
        pedidoRequest.setValorUnitario(BigDecimal.valueOf(100));
        pedidoRequest.setQuantidade(10);
        pedidoRequest.setCodigoCliente(1);

        Pedido pedido = new Pedido();
        pedido.setNumeroControle("12345");
        pedido.setNome("Produto Teste");
        pedido.setValorUnitario(BigDecimal.valueOf(100));
        pedido.setQuantidade(10);
        pedido.setCodigoCliente(1);
        pedido.setValorTotal(BigDecimal.valueOf(900)); // 10% de desconto

        when(pedidoService.criarPedido(pedidoRequest)).thenReturn(pedido);

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pedidoRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    public void testImportarPedidosJson() throws Exception {
        PedidoRequest pedidoRequest = new PedidoRequest();
        pedidoRequest.setNumeroControle("12345");
        pedidoRequest.setNome("Produto Teste");
        pedidoRequest.setValorUnitario(BigDecimal.valueOf(100));
        pedidoRequest.setQuantidade(10);
        pedidoRequest.setCodigoCliente(1);

        List<PedidoRequest> pedidosRequest = Collections.singletonList(pedidoRequest);

        Pedido pedido = new Pedido();
        pedido.setNumeroControle("12345");
        pedido.setNome("Produto Teste");
        pedido.setValorUnitario(BigDecimal.valueOf(100));
        pedido.setQuantidade(10);
        pedido.setCodigoCliente(1);

        when(pedidoService.criarPedido(pedidoRequest)).thenReturn(pedido);

        // Serializa a lista de pedidos como JSON
        String jsonPedidos = objectMapper.writeValueAsString(pedidosRequest);

        mockMvc.perform(post("/api/pedidos/importar-json")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPedidos))
                .andExpect(status().isOk())
                .andExpect(content().string("Pedido(s) importado(s) com sucesso"));
    }

    @Test
    public void testImportarPedidosArquivoJson() throws Exception {
        Resource resource = resourceLoader.getResource("classpath:" + fileJson);

        if (!resource.exists()) {
            throw new RuntimeException("O arquivo JSON não foi encontrado: " + fileJson);
        }

        try (InputStream is = resource.getInputStream()) {
            String jsonContent = new String(is.readAllBytes(), StandardCharsets.UTF_8);

            MockMultipartFile jsonFile = new MockMultipartFile(
                    "arquivos",
                    "pedidos.json",
                    MediaType.APPLICATION_JSON_VALUE,
                    jsonContent.getBytes(StandardCharsets.UTF_8)
            );

            mockMvc.perform(MockMvcRequestBuilders.multipart("/api/pedidos/importar-arquivo-json")
                            .file(jsonFile)
                            .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().string("Arquivo(s) JSON importado(s) com sucesso"));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao carregar o arquivo JSON: " + e.getMessage(), e);
        }
    }

    @Test
    public void testImportarPedidosXml() throws Exception {
        PedidoRequest pedidoRequest = new PedidoRequest();
        pedidoRequest.setNumeroControle("54321");
        pedidoRequest.setNome("Produto Teste2");
        pedidoRequest.setValorUnitario(BigDecimal.valueOf(100));
        pedidoRequest.setQuantidade(10);
        pedidoRequest.setCodigoCliente(1);

        List<PedidoRequest> pedidosRequest = Collections.singletonList(pedidoRequest);

        Pedido pedido = new Pedido();
        pedido.setNumeroControle("54321");
        pedido.setNome("Produto Teste2");
        pedido.setValorUnitario(BigDecimal.valueOf(100));
        pedido.setQuantidade(10);
        pedido.setCodigoCliente(1);

        when(pedidoService.criarPedido(pedidoRequest)).thenReturn(pedido);

        Pedidos pedidosContainer = new Pedidos();
        pedidosContainer.setPedido(pedidosRequest);

        // Serializa o objeto Pedidos para XML
        String xmlPedidos;
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Pedidos.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            marshaller.marshal(pedidosContainer, baos);
            xmlPedidos = new String(baos.toByteArray(), StandardCharsets.UTF_8);
        } catch (JAXBException e) {
            throw new RuntimeException("Erro ao gerar XML", e);
        }

        mockMvc.perform(post("/api/pedidos/importar-xml")
                        .contentType(MediaType.APPLICATION_XML)
                        .content(xmlPedidos))
                .andExpect(status().isOk())
                .andExpect(content().string("Pedido(s) importado(s) com sucesso"));
    }

    @Test
    public void testImportarPedidosArquivoXml() throws Exception {
        Resource resource = resourceLoader.getResource("classpath:" + fileXml);

        if (!resource.exists()) {
            throw new RuntimeException("O arquivo XML não foi encontrado: " + fileXml);
        }

        try (InputStream is = resource.getInputStream()) {
            String xmlContent = new String(is.readAllBytes(), StandardCharsets.UTF_8);

            MockMultipartFile xmlFile = new MockMultipartFile(
                    "arquivos",
                    "pedidos.xml",
                    MediaType.APPLICATION_XML_VALUE,
                    xmlContent.getBytes(StandardCharsets.UTF_8)
            );

            mockMvc.perform(MockMvcRequestBuilders.multipart("/api/pedidos/importar-arquivo-xml")
                            .file(xmlFile)
                            .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().string("Arquivo(s) XML importado(s) com sucesso"));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao carregar o arquivo XML: " + e.getMessage(), e);
        }
    }

    @Test
    public void testConsultarPedidosPorNumeroControle() throws Exception {
        Pedido pedido = new Pedido();
        pedido.setNumeroControle("12345");
        pedido.setNome("Produto Teste");
        pedido.setValorUnitario(BigDecimal.valueOf(100));
        pedido.setQuantidade(1);
        pedido.setCodigoCliente(1);
        pedido.setValorTotal(BigDecimal.valueOf(100));

        when(pedidoService.consultarPedidos("12345", null)).thenReturn(List.of(pedido));

        mockMvc.perform(get("/api/pedidos")
                        .param("numeroControle", "12345"))
                .andExpect(status().isOk());
    }

    @Test
    public void testConsultarPedidosPorDataCadastro() throws Exception {
        Pedido pedido = new Pedido();
        pedido.setNumeroControle("12345");
        pedido.setNome("Produto Teste");
        pedido.setValorUnitario(BigDecimal.valueOf(100));
        pedido.setQuantidade(1);
        pedido.setCodigoCliente(1);
        pedido.setValorTotal(BigDecimal.valueOf(100));
        pedido.setDataCadastro(LocalDate.now());

        when(pedidoService.consultarPedidos(null, LocalDate.now())).thenReturn(List.of(pedido));

        mockMvc.perform(get("/api/pedidos")
                        .param("dataCadastro", LocalDate.now().toString()))
                .andExpect(status().isOk());
    }
}
