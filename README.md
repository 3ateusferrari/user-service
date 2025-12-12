# User Service API

API RESTful para gerenciamento e autenticação de usuários, construída com Java e Spring Boot. Este projeto serve como um microserviço robusto para registro de usuários, autenticação baseada em JWT e operações CRUD.

## Principais Funcionalidades

-   **Autenticação e Autorização:** Sistema completo de login e registro com JSON Web Tokens (JWT).
-   **Gerenciamento de Usuários:** Operações CRUD (Create, Read, Update, Delete) para usuários.
-   **Segurança:** Criptografia de senhas utilizando BCrypt.
-   **Banco de Dados:** Versionamento de schema com Flyway para garantir consistência.
-   **Documentação:** Documentação da API gerada automaticamente com SpringDoc (OpenAPI).
-   **Paginação:** Listagem de usuários com suporte a paginação.

## Tecnologias Utilizadas

-   **Backend:** Java, Spring Boot, Spring Security, Spring Data JPA
-   **Banco de Dados:** PostgreSQL
-   **Autenticação:** JSON Web Tokens (JWT)
-   **Migrations:** Flyway
-   **Build:** Maven
-   **Containerização:** Docker e Docker Compose
-   **Documentação:** SpringDoc (OpenAPI)

## Como Executar

1.  **Clone o repositório:**
    ```bash
    git clone https://github.com/seu-usuario/user-service.git
    cd user-service
    ```

2.  **Configure o ambiente:**
    Crie um arquivo `.env` na raiz do projeto com a senha do banco de dados:
    ```
    POSTGRES_PASSWORD=admin
    ```

3.  **Inicie o banco de dados com Docker:**
    ```bash
    docker-compose up -d
    ```

4.  **Execute a aplicação:**
    Use o Maven para iniciar o servidor Spring Boot.
    ```bash
    ./mvnw spring-boot:run
    ```

5.  **Acesse a API:**
    -   A aplicação estará disponível em `http://localhost:8080`.
    -   A documentação da API (Swagger UI) pode ser acessada em `http://localhost:8080/swagger-ui.html`.
