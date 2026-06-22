# 🎓 EmpowerLearn

> Plataforma educacional desenvolvida para conectar professores, alunos e instituições de ensino de forma rápida, segura e eficiente.

![Badge](https://img.shields.io/badge/Status-Concluído-brightgreen)
![Badge](https://img.shields.io/badge/Java-17-orange)
![Badge](https://img.shields.io/badge/Spring%20Boot-3.x-green)
![Badge](https://img.shields.io/badge/Web-Responsivo-blue)

---

## 📋 Sobre o Projeto

A **EmpowerLearn** é uma plataforma digital voltada para o setor educacional, criada para facilitar o recrutamento de professores e promover conexões entre profissionais da educação, alunos e instituições de ensino.

A plataforma busca reduzir a burocracia dos processos de contratação, oferecendo uma experiência moderna, intuitiva e eficiente para todos os usuários.

---

## ✨ Funcionalidades

### 👨‍🏫 Professores

* Cadastro e autenticação
* Criação de perfil profissional
* Cadastro de especialidades e experiência
* Upload de foto de perfil
* Visualização de oportunidades

### 🎓 Alunos/Pais

* Cadastro e login
* Consulta de perfis de professores
* Busca por profissionais qualificados

### 🏫 Instituições

* Cadastro e autenticação
* Busca de professores por perfil
* Consulta de informações profissionais

### 🌐 Plataforma

* Página institucional
* Página Sobre Nós
* Página Como Funciona
* Página de Diferenciais
* Página de Contato
* Dashboard do usuário
* Histórico de perfis visualizados
* Sistema de curtidas
* Consulta de perfis

---

## 🛠️ Tecnologias Utilizadas

### Front-end

* HTML5
* CSS3
* JavaScript
* Design Responsivo
* Local Storage

### Back-end

* Java 17
* Spring Boot
* Spring Data JPA
* Maven
* API REST

---


### Pré-requisitos

* Java 17+
* Maven
* IDE Java (IntelliJ IDEA ou VS Code)

---

## 📁 Estrutura do Projeto

```text
EmpowerLearn/
├── css/
├── js/
├── imagens/
│
├── index.html
├── login.html
├── sobre.html
├── contato.html
├── como-funcionna.html
├── diferenciais.html
├── daschboard.html
├── historico-perfis.html
├── ver-perfil.html
├── ver-curtidas.html
│
├── empowerlearn-api/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── br/com/empowerlearn/
│   │   │   │       ├── config/
│   │   │   │       │   └── WebConfig.java
│   │   │   │       │
│   │   │   │       ├── controller/
│   │   │   │       │   ├── AlunoController.java
│   │   │   │       │   ├── ProfessorController.java
│   │   │   │       │   └── InstituicaoController.java
│   │   │   │       │
│   │   │   │       ├── model/
│   │   │   │       │   ├── Aluno.java
│   │   │   │       │   ├── Professor.java
│   │   │   │       │   └── Instituicao.java
│   │   │   │       │
│   │   │   │       ├── repository/
│   │   │   │       │   ├── AlunoRepository.java
│   │   │   │       │   ├── ProfessorRepository.java
│   │   │   │       │   └── InstituicaoRepository.java
│   │   │   │       │
│   │   │   │       ├── service/
│   │   │   │       │   └── CepService.java
│   │   │   │
│   │   │   └── EmpowerLearnApiApplication.java
│   │   │
│   │   └── resources/
│   │       └── application.properties
│   │
│   ├── pom.xml
│   ├── mvnw
│   └── docker-compose.yml
│
└── README.md
```

---

## 🔌 Principais Endpoints da API

### Alunos

| Método | Endpoint          |
| ------ | ----------------- |
| POST   | /api/alunos       |
| POST   | /api/alunos/login |

### Professores

| Método | Endpoint                          |
| ------ | --------------------------------- |
| POST   | /api/professores                  |
| POST   | /api/professores/login            |
| POST   | /api/professores/{id}/upload-foto |

### Instituições

| Método | Endpoint                |
| ------ | ----------------------- |
| POST   | /api/instituicoes       |
| POST   | /api/instituicoes/login |

---

## 🎯 Objetivo

Promover a qualidade da educação através da tecnologia, conectando talentos e oportunidades de forma acessível, eficiente e segura.

---

## 📄 Licença

Projeto acadêmico desenvolvido para escola de TI.
