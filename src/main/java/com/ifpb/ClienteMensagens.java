package com.ifpb;

import java.io.*;
import java.net.*;
import java.util.Scanner;

/**
 * Cliente de Mensagens - Laboratório de Sockets
 * Implementa um cliente que se conecta ao servidor de mensagens
 * e permite ao usuário solicitar mensagens do banco
 */
public class ClienteMensagens {
    private static final String HOST = "localhost";
    private static final int PORTA = 5000;

    private Socket socket;
    private DataOutputStream saida;
    private DataInputStream entrada;
    private Scanner scanner;

    public ClienteMensagens() {
        scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        ClienteMensagens cliente = new ClienteMensagens();
        cliente.executar();
    }

    /**
     * Método principal que gerencia a execução do cliente
     */
    public void executar() {
        System.out.println("=== CLIENTE DE MENSAGENS ===");
        System.out.println("Conectando ao servidor " + HOST + ":" + PORTA + "...");

        try {
            conectar();
            System.out.println("Conexão estabelecida com sucesso!\n");
            exibirMenu();

            boolean continuar = true;

            while (continuar) {
                System.out.print("\nDigite o número da mensagem (0 para aleatória, -1 para sair): ");
                int numeroMensagem = lerNumero();

                if (numeroMensagem == -1) {
                    continuar = false;
                    System.out.println("\nEncerrando conexão...");
                } else {
                    enviarRequisicao(numeroMensagem, continuar);
                    String resposta = receberResposta();
                    exibirResposta(resposta);
                }
            }

            desconectar();
            System.out.println("Cliente finalizado.");

        } catch (IOException e) {
            System.err.println("Erro de comunicação: " + e.getMessage());
        }
    }

    /**
     * Estabelece conexão com o servidor
     */
    private void conectar() throws IOException {
        socket = new Socket(HOST, PORTA);
        saida = new DataOutputStream(socket.getOutputStream());
        entrada = new DataInputStream(socket.getInputStream());
    }

    /**
     * Envia requisição ao servidor
     * Protocolo: [numeroMensagem:int][persistente:boolean]
     */
    private void enviarRequisicao(int numeroMensagem, boolean manterConexao) throws IOException {
        saida.writeInt(numeroMensagem);
        saida.writeBoolean(manterConexao);
        saida.flush();
    }

    /**
     * Recebe resposta do servidor
     */
    private String receberResposta() throws IOException {
        String resposta = entrada.readUTF();
        return resposta.trim(); // Remove espaços de preenchimento
    }

    /**
     * Exibe a resposta recebida do servidor
     */
    private void exibirResposta(String resposta) {
        String[] partes = resposta.split("\\|", 2);

        if (partes[0].equals("OK")) {
            System.out.println("\n" + "=".repeat(70));
            System.out.println("MENSAGEM RECEBIDA:");
            System.out.println(partes[1]);
            System.out.println("=".repeat(70));
        } else if (partes[0].equals("ERRO")) {
            System.out.println("\n" + "!".repeat(70));
            System.out.println("ERRO: " + partes[1]);
            System.out.println("!".repeat(70));
        }
    }

    /**
     * Encerra a conexão com o servidor
     */
    private void desconectar() {
        try {
            if (entrada != null) entrada.close();
            if (saida != null) saida.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.err.println("Erro ao fechar conexão: " + e.getMessage());
        }
    }

    /**
     * Exibe o menu de instruções
     */
    private void exibirMenu() {
        System.out.println("=".repeat(70));
        System.out.println("INSTRUÇÕES:");
        System.out.println("  - Digite 0 para receber uma mensagem aleatória");
        System.out.println("  - Digite um número entre 1 e M para receber uma mensagem específica");
        System.out.println("  - Digite -1 para sair");
        System.out.println("=".repeat(70));
    }

    /**
     * Lê um número inteiro do usuário com tratamento de erros
     */
    private int lerNumero() {
        while (true) {
            try {
                String input = scanner.nextLine();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("Entrada inválida! Digite um número inteiro: ");
            }
        }
    }
}