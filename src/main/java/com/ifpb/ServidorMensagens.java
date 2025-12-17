package com.ifpb;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Servidor de Mensagens - Laboratório de Sockets
 * Implementa um servidor multithread que gerencia um banco de mensagens
 * e atende requisições de clientes via TCP/IP
 */
public class ServidorMensagens {
    private static final int PORTA = 5000;
    private static final int TAMANHO_MENSAGEM = 150; // Tamanho fixo das mensagens do protocolo
    private static List<String> mensagens = new ArrayList<>();
    private static int totalMensagens = 0;

    public static void main(String[] args) {
        // Configura encoding UTF-8 para o console
        try {
            System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        } catch (Exception e) {
            System.err.println("Aviso: Não foi possível configurar UTF-8");
        }// Configura encoding UTF-8 para o console
        try {
            System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        } catch (Exception e) {
            System.err.println("Aviso: Não foi possível configurar UTF-8");
        }
        carregarMensagens("mensagens.txt");

        try (ServerSocket serverSocket = new ServerSocket(PORTA)) {
            System.out.println("=== SERVIDOR DE MENSAGENS ===");
            System.out.println("Servidor iniciado na porta " + PORTA);
            System.out.println("Total de mensagens: " + totalMensagens);
            System.out.println("Aguardando conexões...\n");

            while (true) {
                Socket clienteSocket = serverSocket.accept();
                System.out.println("[NOVO CLIENTE] Conexão aceita de: " +
                        clienteSocket.getInetAddress().getHostAddress());

                // Cria uma thread para atender o cliente
                Thread threadCliente = new Thread(new AtendimentoCliente(clienteSocket));
                threadCliente.start();
            }
        } catch (IOException e) {
            System.err.println("Erro no servidor: " + e.getMessage());
        }
    }

    /**
     * Carrega as mensagens de um arquivo texto
     */
    private static void carregarMensagens(String nomeArquivo) {
        try (BufferedReader br = new BufferedReader(new FileReader(nomeArquivo))) {
            String linha = br.readLine();
            totalMensagens = Integer.parseInt(linha.trim());

            while ((linha = br.readLine()) != null) {
                mensagens.add(linha);
            }

            if (mensagens.size() != totalMensagens) {
                System.err.println("AVISO: Número de mensagens difere do declarado!");
                totalMensagens = mensagens.size();
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar mensagens: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Classe interna que implementa o atendimento de cada cliente em uma thread separada
     */
    static class AtendimentoCliente implements Runnable {
        private Socket socket;
        private DataInputStream entrada;
        private DataOutputStream saida;
        private String enderecoCliente;

        public AtendimentoCliente(Socket socket) {
            this.socket = socket;
            this.enderecoCliente = socket.getInetAddress().getHostAddress();
        }

        @Override
        public void run() {
            try {
                entrada = new DataInputStream(socket.getInputStream());
                saida = new DataOutputStream(socket.getOutputStream());

                boolean conexaoPersistente = true;

                while (conexaoPersistente) {
                    // Protocolo de requisição:
                    // [numeroMensagem:int][persistente:boolean]
                    int numeroMensagem = entrada.readInt();
                    boolean manterConexao = entrada.readBoolean();

                    System.out.println("[" + enderecoCliente + "] Requisição: mensagem " +
                            numeroMensagem + " | Persistente: " + manterConexao);

                    String resposta = processarRequisicao(numeroMensagem);
                    enviarResposta(resposta);

                    conexaoPersistente = manterConexao;
                }

                System.out.println("[" + enderecoCliente + "] Conexão encerrada pelo cliente");
                socket.close();

            } catch (EOFException e) {
                System.out.println("[" + enderecoCliente + "] Cliente desconectou");
            } catch (IOException e) {
                System.err.println("[" + enderecoCliente + "] Erro: " + e.getMessage());
            } finally {
                try {
                    if (socket != null && !socket.isClosed()) {
                        socket.close();
                    }
                } catch (IOException e) {
                    System.err.println("Erro ao fechar socket: " + e.getMessage());
                }
            }
        }

        /**
         * Processa a requisição do cliente e retorna a mensagem apropriada
         */
        private String processarRequisicao(int numero) {
            // Caso 1: Mensagem aleatória (número 0)
            if (numero == 0) {
                Random random = new Random();
                int indiceAleatorio = random.nextInt(totalMensagens);
                return "OK|" + mensagens.get(indiceAleatorio);
            }

            // Caso 2: Número válido (1 a M)
            if (numero >= 1 && numero <= totalMensagens) {
                return "OK|" + mensagens.get(numero - 1);
            }

            // Caso 3: Número inválido
            return "ERRO|Número inválido! Use valores de 0 a " + totalMensagens +
                    " (0 = aleatória)";
        }

        /**
         * Envia resposta ao cliente com tamanho fixo de TAMANHO_MENSAGEM bytes
         */
        private void enviarResposta(String resposta) throws IOException {
            // Garante que a mensagem tenha tamanho fixo
            if (resposta.length() > TAMANHO_MENSAGEM) {
                resposta = resposta.substring(0, TAMANHO_MENSAGEM);
            } else {
                resposta = String.format("%-" + TAMANHO_MENSAGEM + "s", resposta);
            }

            saida.writeUTF(resposta);
            saida.flush();
        }
    }
}
