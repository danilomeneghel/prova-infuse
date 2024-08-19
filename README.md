# Prova Infuse

Avaliação técnica de uma API de Cadastro de Pedidos, desenvolvido em Java com Spring-Boot.

## Características

- API RESTful
- Validation
- MockMVC

## Requisitos

- Java JDK 21
- Apache Maven >= 3.9.8
- MySql 8
- Docker (Opcional)

## Tecnologias

- Java
- JPA
- Hibernate
- Maven
- Spring
- Lombok
- Jakarta
- XML
- JSON
- MySql
- JUnit
- Docker

## Instalação

```
$ git clone https://github.com/danilomeneghel/prova-infuse.git

$ cd prova-infuse
```

## MySql

Abra seu MySql e crie a base de dados:

prova_infuse


## Maven

Para carregar o projeto, digite no terminal:

```
$ mvn clean spring-boot:run -Dspring-boot.run.profiles=dev
```

Aguarde carregar todo o serviço web. <br>
Após concluído, digite o endereço abaixo em seu navegador, nele será listado os pedidos
cadastrados na API. <br>

http://localhost:8080/api/pedidos


## Docker (Opcional)

Para rodar o projeto via Docker, bastar executar o seguinte comando:

```
$ docker build -t projeto .
$ docker run -p 8080:8080 -d projeto
```

Ou via Docker-Compose:

```
$ docker-compose up
```

Aguarde baixar as dependências e carregar todo o projeto, esse processo é demorado. <br>
Caso conclua e não rode pela primeira vez, tente novamente executando o mesmo comando. <br>

Para encerrar tudo digite:

```
$ docker-compose down
```


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
