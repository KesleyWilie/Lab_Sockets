package com.ifpb;

import java.io.*;
import java.net.*;

/**
 * Script de Teste Automatizado para o Serviço de Mensagens
 * Realiza uma bateria de testes para validar o funcionamento do servidor
 */
public class TesteAutomatizado {
    private static final String HOST = "localhost";
    private static final int PORTA = 5000;
    private static int testesPassados = 0;
    private static int testesTotal = 0;

    public static void main(String[] args) {
        System.out.println("=== BATERIA DE TESTES DO SERVIÇO DE MENSAGENS ===\n");

        testarMensagemEspecifica();
        testarMensagemAleatoria();
        testarNumeroInvalido();
        testarNumeroNegativo();
        testarNumeroZero();
        testarConexaoPersistente();
        testarMultiplasConexoes();

        System.out.println("\n" + "=".repeat(60));
        System.out.println("RESULTADO FINAL: " + testesPassados + "/" + testesTotal + " testes passaram");
        System.out.println("=".repeat(60));
    }

    private static void testarMensagemEspecifica() {
        System.out.println("[TESTE 1] Mensagem Específica (número 1)");
        testesTotal++;

        try (Socket socket = new Socket(HOST, PORTA);
             DataOutputStream saida = new DataOutputStream(socket.getOutputStream());
             DataInputStream entrada = new DataInputStream(socket.getInputStream())) {

            saida.writeInt(1);
            saida.writeBoolean(false);
            saida.flush();

            String resposta = entrada.readUTF().trim();

            if (resposta.startsWith("OK|")) {
                System.out.println("✓ PASSOU - Resposta: " + resposta.substring(0, Math.min(50, resposta.length())) + "...");
                testesPassados++;
            } else {
                System.out.println("✗ FALHOU - Resposta inesperada: " + resposta);
            }

        } catch (Exception e) {
            System.out.println("✗ FALHOU - Erro: " + e.getMessage());
        }
        System.out.println();
    }

    private static void testarMensagemAleatoria() {
        System.out.println("[TESTE 2] Mensagem Aleatória (número 0)");
        testesTotal++;

        try (Socket socket = new Socket(HOST, PORTA);
             DataOutputStream saida = new DataOutputStream(socket.getOutputStream());
             DataInputStream entrada = new DataInputStream(socket.getInputStream())) {

            saida.writeInt(0);
            saida.writeBoolean(false);
            saida.flush();

            String resposta = entrada.readUTF().trim();

            if (resposta.startsWith("OK|")) {
                System.out.println("✓ PASSOU - Mensagem aleatória recebida");
                testesPassados++;
            } else {
                System.out.println("✗ FALHOU - Resposta inesperada: " + resposta);
            }

        } catch (Exception e) {
            System.out.println("✗ FALHOU - Erro: " + e.getMessage());
        }
        System.out.println();
    }

    private static void testarNumeroInvalido() {
        System.out.println("[TESTE 3] Número Inválido (número 999)");
        testesTotal++;

        try (Socket socket = new Socket(HOST, PORTA);
             DataOutputStream saida = new DataOutputStream(socket.getOutputStream());
             DataInputStream entrada = new DataInputStream(socket.getInputStream())) {

            saida.writeInt(999);
            saida.writeBoolean(false);
            saida.flush();

            String resposta = entrada.readUTF().trim();

            if (resposta.startsWith("ERRO|")) {
                System.out.println("✓ PASSOU - Erro detectado corretamente");
                System.out.println("  Mensagem: " + resposta.substring(5));
                testesPassados++;
            } else {
                System.out.println("✗ FALHOU - Deveria retornar erro: " + resposta);
            }

        } catch (Exception e) {
            System.out.println("✗ FALHOU - Erro: " + e.getMessage());
        }
        System.out.println();
    }

    private static void testarNumeroNegativo() {
        System.out.println("[TESTE 4] Número Negativo (número -5)");
        testesTotal++;

        try (Socket socket = new Socket(HOST, PORTA);
             DataOutputStream saida = new DataOutputStream(socket.getOutputStream());
             DataInputStream entrada = new DataInputStream(socket.getInputStream())) {

            saida.writeInt(-5);
            saida.writeBoolean(false);
            saida.flush();

            String resposta = entrada.readUTF().trim();

            if (resposta.startsWith("ERRO|")) {
                System.out.println("✓ PASSOU - Erro detectado corretamente");
                testesPassados++;
            } else {
                System.out.println("✗ FALHOU - Deveria retornar erro: " + resposta);
            }

        } catch (Exception e) {
            System.out.println("✗ FALHOU - Erro: " + e.getMessage());
        }
        System.out.println();
    }

    private static void testarNumeroZero() {
        System.out.println("[TESTE 5] Consistência de Mensagem Aleatória");
        testesTotal++;
        int tentativas = 5;
        int sucessos = 0;

        for (int i = 0; i < tentativas; i++) {
            try (Socket socket = new Socket(HOST, PORTA);
                 DataOutputStream saida = new DataOutputStream(socket.getOutputStream());
                 DataInputStream entrada = new DataInputStream(socket.getInputStream())) {

                saida.writeInt(0);
                saida.writeBoolean(false);
                saida.flush();

                String resposta = entrada.readUTF().trim();

                if (resposta.startsWith("OK|")) {
                    sucessos++;
                }

            } catch (Exception e) {
                // Ignora erros individuais
            }
        }

        if (sucessos == tentativas) {
            System.out.println("✓ PASSOU - Todas as " + tentativas + " requisições aleatórias foram bem-sucedidas");
            testesPassados++;
        } else {
            System.out.println("✗ FALHOU - Apenas " + sucessos + "/" + tentativas + " requisições foram bem-sucedidas");
        }
        System.out.println();
    }

    private static void testarConexaoPersistente() {
        System.out.println("[TESTE 6] Conexão Persistente (múltiplas requisições)");
        testesTotal++;

        try (Socket socket = new Socket(HOST, PORTA);
             DataOutputStream saida = new DataOutputStream(socket.getOutputStream());
             DataInputStream entrada = new DataInputStream(socket.getInputStream())) {

            // Primeira requisição
            saida.writeInt(1);
            saida.writeBoolean(true); // Manter conexão
            saida.flush();
            entrada.readUTF();

            // Segunda requisição na mesma conexão
            saida.writeInt(2);
            saida.writeBoolean(true);
            saida.flush();
            entrada.readUTF();

            // Terceira requisição
            saida.writeInt(0);
            saida.writeBoolean(false); // Encerrar conexão
            saida.flush();
            String resposta = entrada.readUTF().trim();

            if (resposta.startsWith("OK|")) {
                System.out.println("✓ PASSOU - 3 requisições na mesma conexão funcionaram corretamente");
                testesPassados++;
            } else {
                System.out.println("✗ FALHOU - Última resposta inesperada: " + resposta);
            }

        } catch (Exception e) {
            System.out.println("✗ FALHOU - Erro: " + e.getMessage());
        }
        System.out.println();
    }

    private static void testarMultiplasConexoes() {
        System.out.println("[TESTE 7] Múltiplas Conexões Simultâneas");
        testesTotal++;

        Thread[] threads = new Thread[5];
        final int[] sucessos = {0};

        for (int i = 0; i < 5; i++) {
            final int id = i + 1;
            threads[i] = new Thread(() -> {
                try (Socket socket = new Socket(HOST, PORTA);
                     DataOutputStream saida = new DataOutputStream(socket.getOutputStream());
                     DataInputStream entrada = new DataInputStream(socket.getInputStream())) {

                    saida.writeInt(id);
                    saida.writeBoolean(false);
                    saida.flush();

                    String resposta = entrada.readUTF().trim();

                    if (resposta.startsWith("OK|")) {
                        synchronized (sucessos) {
                            sucessos[0]++;
                        }
                    }

                } catch (Exception e) {
                    // Ignora erros individuais
                }
            });
            threads[i].start();
        }

        // Aguarda todas as threads
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (sucessos[0] == 5) {
            System.out.println("✓ PASSOU - 5 clientes simultâneos atendidos com sucesso");
            testesPassados++;
        } else {
            System.out.println("✗ FALHOU - Apenas " + sucessos[0] + "/5 clientes foram atendidos");
        }
        System.out.println();
    }
}
