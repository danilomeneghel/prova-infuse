# Prova Infuse

Avaliação técnica de uma API de Cadastro de Pedido, desenvolvido em Java com Spring-Boot.

## Características

- CRUD
- API RESTful
- Validation
- MockMVC

## Requisitos

- Java JDK 17
- Apache Maven >= 3.9.8
- MySql 8

## Tecnologias

- Java
- JPA
- Maven
- Spring
- Lombok
- MySql
- JUnit

## Instalação

```
$ git clone https://github.com/danilomeneghel/prova-infuse.git

$ cd prova-infuse
```

## MySql

Abra seu MySql e crie 2 bases de dados:

prova_infuse

prova_infuse_test


## Maven

Para carregar o projeto, digite no terminal:

```
$ ./mvnw spring-boot:run
```

Aguarde carregar todo o serviço web. <br>
Após concluído, digite o endereço abaixo em seu navegador, nele será listado os pedidos 
cadastrados na API. <br>

http://localhost:8080/api/pedidos


## Swagger

Documentação da API RESTful:

http://localhost:8080/swagger-ui.html


## Testes

Para realizar os testes, execute o seguinte comando no terminal:

```
$ ./mvnw test
```

## Licença

Projeto licenciado sob <a href="LICENSE">The MIT License (MIT)</a>.<br><br>


Desenvolvido por<br>
Danilo Meneghel<br>
danilo.meneghel@gmail.com<br>
http://danilomeneghel.github.io/<br>
