package ru.laaka.chat.client;

import java.io.*;
import java.net.Socket;

public class Client {
    private final Socket socket;
    private final String name;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    public Client(Socket socket, String userName) {
        this.socket = socket;
        name = userName;
        try {
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }


    /**
     * Слушатель для входящих сообщений
     */
    public void listenForMessages() {
        new Thread(new Runnable() {
            @Override
            public void run() {

            }
        }).start();
    }
    /**
     * Отправить сообщение
     */
    public void sendMessage() {

    }

    private void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            if (bufferedReader != null) {
                bufferedWriter.close();
            }
            if (bufferedWriter != null) {
                bufferedReader.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
