# EmpowerLearn

## Sobre o Projeto

A EmpowerLearn é uma plataforma educacional desenvolvida para conectar professores, alunos e instituições de ensino de forma rápida, segura e eficiente.

O sistema permite o cadastro e autenticação de diferentes tipos de usuários, consulta de perfis de professores, visualização de curtidas, histórico de acessos e navegação por páginas institucionais que apresentam a proposta da plataforma.

---

## Objetivo

Facilitar a conexão entre profissionais da educação e instituições de ensino, reduzindo a burocracia dos processos de contratação e ampliando as oportunidades para professores e alunos.

---

# Tecnologias Utilizadas

## Frontend (Web)

- HTML5
- CSS3
- JavaScript
- Design Responsivo

## Backend (API)

- Java
- Spring Boot
- Spring Web
- Spring Data JPA
- Maven

---

# Estrutura do Projeto

## Frontend

### Páginas

- index.html
- login.html
- sobre.html
- contato.html
- como-funcionna.html
- diferenciais.html
- daschboard.html
- historico-perfis.html
- ver-perfil.html
- ver-curtidas.html

### Pastas

- css/
- js/
- imagens/

---

## Backend

### Config

- WebConfig.java

### Controllers

- AlunoController.java
- ProfessorController.java
- InstituicaoController.java

### Models

- Aluno.java
- Professor.java
- Instituicao.java

### Repositories

- AlunoRepository.java
- ProfessorRepository.java
- InstituicaoRepository.java

### Services

- CepService.java

### Classe Principal

- EmpowerLearnApiApplication.java

---

# Funcionalidades

## Usuários

### Alunos

- Cadastro
- Login
- Consulta de professores

### Professores

- Cadastro
- Login
- Gerenciamento de perfil
- Upload de foto

### Instituições

- Cadastro
- Login
- Busca de professores

---

## Plataforma

- Página inicial institucional
- Página sobre a empresa
- Página de diferenciais
- Página de contato
- Explicação de funcionamento da plataforma
- Visualização de perfis
- Histórico de perfis visitados
- Visualização de curtidas
- Dashboard do usuário

---

# Integração Frontend e Backend

O frontend realiza requisições para a API através do endereço:

```javascript
http://localhost:8080/api
```

As operações incluem:

- Login
- Cadastro
- Consulta de perfis
- Upload de foto de perfil
- Busca de informações dos usuários

---

# Como Executar

## Backend

1. Abrir o projeto Java.
2. Executar a classe:

```java
EmpowerLearnApiApplication
```

3. A API ficará disponível em:

```
http://localhost:8080
```

---

## Frontend

1. Abrir os arquivos HTML em um navegador.
2. Certificar-se de que a API está em execução.
3. Utilizar a página:

```
index.html
```

como ponto de entrada da aplicação.

---

# Equipe

Projeto desenvolvido para a Escola de TI, com foco na aplicação de conceitos de desenvolvimento web, programação orientada a objetos, integração frontend/backend e boas práticas de engenharia de software.
