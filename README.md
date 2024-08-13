# Prova Infuse

Avaliação técnica de uma API de Cadastro de Pedidos, desenvolvido em Java com Spring-Boot.

## Características

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
- XML
- JSON
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
$ mvn spring-boot:run
```

Aguarde carregar todo o serviço web. <br>
Após concluído, digite o endereço abaixo em seu navegador, nele será listado os pedidos
cadastrados na API. <br>

http://localhost:8080/api/pedidos

## Importação de Arquivos

Exemplo da estrutura dos dados para importação de arquivos:

- XML

```
<pedidos>
    <pedido>
        <numeroControle>12345</numeroControle>
        <dataCadastro>2024-08-10</dataCadastro>
        <nome>Produto A</nome>
        <valorUnitario>100.00</valorUnitario>
        <quantidade>10</quantidade>
        <codigoCliente>1</codigoCliente>
    </pedido>
    <pedido>
        <numeroControle>12346</numeroControle>
        <nome>Produto B</nome>
        <valorUnitario>50.00</valorUnitario>
        <quantidade>3</quantidade>
        <codigoCliente>2</codigoCliente>
    </pedido>
</pedidos>
```

- JSON

```
[
    {
        "numeroControle": "12345",
        "dataCadastro": "2024-08-10",
        "nome": "Produto A",
        "valorUnitario": 100.00,
        "quantidade": 10,
        "codigoCliente": 1
    },
    {
        "numeroControle": "12346",
        "nome": "Produto B",
        "valorUnitario": 50.00,
        "quantidade": 3,
        "codigoCliente": 2
    }
]
```

## Swagger

Documentação da API RESTful:

http://localhost:8080/swagger-ui.html


## Testes

Para realizar os testes, execute o seguinte comando no terminal:

```
$ mvn test
```

## Licença

Projeto licenciado sob <a href="LICENSE">The MIT License (MIT)</a>.<br><br>


Desenvolvido por<br>
Danilo Meneghel<br>
danilo.meneghel@gmail.com<br>
http://danilomeneghel.github.io/<br>
