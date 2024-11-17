package ru.laalka.chat.server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientManager implements Runnable {

    private final Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String name;

    public final static ArrayList<ClientManager> clients = new ArrayList<>();

    public ClientManager(Socket socket) {
        this.socket = socket;
        try {
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            name = bufferedReader.readLine();
            clients.add(this);
            System.out.println(name + " подключился к чату.");
            broadcastMessage("SERVER: " + name+ " подключился к чату");
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    @Override
    public void run() {
        String messageFromClient;
        while (socket.isConnected()) {
            try {
                messageFromClient = bufferedReader.readLine();
                // for macOS
                if (messageFromClient == null) {
                    closeEverything(socket, bufferedReader, bufferedWriter);
                    break;
                }
                broadcastMessage(messageFromClient);
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }
    private boolean checkPrivateMessage(String message){
        String[] messageParts = message.split(" ");
        return messageParts[1].startsWith("@");
    }

    private void privateMassage(String message){
        String[] messageParts = message.split(" ");
        messageParts[1] = messageParts[1].substring(1);
        for(ClientManager client: clients){
            try{
                if (client.name.equals(messageParts[1])){
                    client.bufferedWriter.write(message);
                    client.bufferedWriter.newLine();
                    client.bufferedWriter.flush();
                }
            }catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    private void broadcastMessage(String message) {
        System.out.println(message);
        if (checkPrivateMessage(message)) {
            privateMassage(message);
        } else {
            for (ClientManager client : clients) {
                try {
                    if (!client.name.equals(name)) {
                        client.bufferedWriter.write(message);
                        client.bufferedWriter.newLine();
                        client.bufferedWriter.flush();
                    }
                } catch (IOException e) {
                    closeEverything(socket, bufferedReader, bufferedWriter);
                }
            }
        }
    }

    private void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClient();
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void removeClient() {
        clients.remove(this);
        System.out.println(name + " покинул чат.");
        broadcastMessage("SERVER: " + name + " покинул чат");
    }


}
