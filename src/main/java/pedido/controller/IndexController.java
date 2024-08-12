package pedido.controller;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@OpenAPIDefinition(info = @Info(title = "API REST", description = "Cadastro de Pedidos"))
@Controller
public class IndexController {

    @GetMapping( "/" )
    public String index() {
        return "index";
    }

}