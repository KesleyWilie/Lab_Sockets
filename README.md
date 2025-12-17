

# Laboratório de Sockets - Serviço de Mensagens 💬

> **Disciplina:** Sistemas Distribuídos  
> **Curso:** Análise e Desenvolvimento de Sistemas  
> **Instituição:** IFPB  

## 📖 Sobre o Projeto

Este projeto consiste em uma aplicação distribuída baseada no paradigma **Cliente/Servidor** utilizando a API de **Sockets (TCP/IP)** do Java. 

O sistema simula um serviço de mensagens onde múltiplos clientes podem se conectar simultaneamente a um servidor para solicitar frases (curiosidades, citações, etc.) armazenadas em um arquivo de texto. O projeto foi desenvolvido para demonstrar conceitos de concorrência (Multithreading), persistência de conexões e protocolos de comunicação binária.

## 🚀 Funcionalidades

- **Arquitetura Multithread:** O servidor utiliza o modelo *Thread-per-Connection*, capaz de atender vários clientes simultaneamente sem bloqueios.
- **Protocolo Personalizado:** Comunicação via fluxo de dados primitivos (`DataInputStream`/`DataOutputStream`) com verificação de integridade.
- **Conexão Híbrida:** Suporte a conexões **Persistentes** (múltiplas requisições na mesma sessão) e **Transientes** (uma requisição por conexão).
- **Consultas Variadas:**
  - Busca por ID específico.
  - Busca de mensagem aleatória.
- **Validação Robusta:** Tratamento de erros para IDs inválidos, estouro de faixa e desconexões abruptas.
- **Suporte a UTF-8:** Compatibilidade total com acentuação e caracteres especiais.
- **Testes Automatizados:** Script de teste incluído para validar concorrência e protocolo.

## 🛠️ Tecnologias Utilizadas

- **Java JDK 8+** (Core Java, `java.net`, `java.io`, `java.lang.Thread`)

## 📂 Estrutura do Projeto

A estrutura de pacotes deve seguir o padrão abaixo para que os comandos funcionem corretamente:


```

/ (Raiz do Projeto)
│
├── src/
│   └── main/
│       └── java/
│           ├── mensagens.txt  <-- Arquivo com as frases
│           └── com/
│               └── ifpb/
│                   ├── ServidorMensagens.java
│                   ├── ClienteMensagens.java
│                   └── TesteAutomatizado.java
│
└── README.md

```

## ⚙️ Como Rodar o Projeto

### 1. Pré-requisitos
Certifique-se de ter o [Java JDK](https://www.oracle.com/java/technologies/downloads/) instalado e configurado no PATH do seu sistema.

### 2. Compilação
Abra o terminal na pasta raiz dos códigos fonte (`src/main/java`) e execute o comando abaixo para compilar todos os arquivos com encoding UTF-8 (essencial para evitar erros de acentuação no Windows):

```bash
cd src/main/java
javac -encoding UTF-8 com/ifpb/*.java

```

### 3. Executando o Servidor

No terminal onde você compilou o código, inicie o servidor. Ele ficará aguardando conexões na porta **5000**.

```bash
java -Dfile.encoding=UTF-8 com.ifpb.ServidorMensagens

```

### 4. Executando o Cliente

Abra um **novo terminal**, navegue até a mesma pasta (`src/main/java`) e inicie o cliente:

```bash
java -Dfile.encoding=UTF-8 com.ifpb.ClienteMensagens

```

*Siga as instruções do menu interativo para solicitar mensagens.*

### 5. Executando os Testes Automatizados

Para validar o funcionamento do servidor e a concorrência, você pode rodar o script de testes (certifique-se de que o servidor já esteja rodando em outro terminal):

```bash
java -Dfile.encoding=UTF-8 com.ifpb.TesteAutomatizado

```

## 📝 Formato do Arquivo `mensagens.txt`

O arquivo deve estar na raiz da execução (`src/main/java`) e seguir o formato:

* **Linha 1:** Número inteiro indicando o total de mensagens.
* **Linhas seguintes:** As mensagens (uma por linha).

Exemplo:

```text
3
A vida sem reflexão não vale a pena ser vivida. - Sócrates
A verdadeira força vem do equilíbrio interior. - Mestre Splinter
A paciência é o caminho para a vitória. - Mestre Splinter

```


