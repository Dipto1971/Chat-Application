import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private ServerSocket serverSocket;
    private boolean running;
    private List<ClientHandler> clients;

    public Server(int port) {
        try {
            serverSocket = new ServerSocket(port);
            running = true;
            clients = new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        System.out.println("Server started.");

        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress().getHostAddress());

                // Create a new thread or process to handle the client communication
                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                clients.add(clientHandler);
                clientHandler.start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void addClient(ClientHandler client) {
        clients.add(client);
        sendConnectedUsersToAllClients();
    }

    public synchronized void removeClient(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        sendConnectedUsersToAllClients();
    }

    public synchronized void sendConnectedUsersToAllClients() {
        StringBuilder userList = new StringBuilder();
        userList.append("[uls]");
        userList.append(clients.get(0).getUsername());
        for (int i = 1; i < clients.size(); i++) {
            ClientHandler client = clients.get(i);
            userList.append("|").append(client.getUsername());
        }
        userList.append("\n");
        for (ClientHandler client : clients) {
            client.sendMessage(userList.toString());
        }
    }

    public synchronized void sendMessageToClient(ClientHandler sender, int receiverIndex, String message) {
        if (receiverIndex >= 0 && receiverIndex < clients.size()) {
            ClientHandler recievHandler = clients.get(receiverIndex);
            recievHandler.sendMessage("[msg]" + sender.getUsername() + ": " + message);
        } else {
            sender.sendMessage("Invalid receiver index.");
        }
    }

    public synchronized void sendMessageToAll(ClientHandler sender, String message) {
        for (ClientHandler client : clients) {
            client.sendMessage("[msg]" + sender.getUsername() + " to All: " + message);
        }
    }

    public static void main(String[] args) {
        int port = 1234; // Specify your desired port number
        Server server = new Server(port);
        server.start();
    }

    private class ClientHandler extends Thread {
        private Socket socket;
        private BufferedReader inputStream;
        private PrintWriter outputStream;
        private Server server;
        private String username;

        public ClientHandler(Socket socket, Server server) {
            this.socket = socket;
            this.server = server;
            try {
                inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                outputStream = new PrintWriter(socket.getOutputStream(), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public String getUsername() {
            return username;
        }

        public void sendMessage(String message) {
            outputStream.println(message);
        }

        @Override
        public void run() {
            try {

                username = inputStream.readLine();

                // Notify all clients about the new user
                server.sendConnectedUsersToAllClients();

                String message;
                while ((message = inputStream.readLine()) != null) {
                    if (message.startsWith("/talk")) {
                        String[] parts = message.split(" ");
                        if (parts.length >= 3) {
                            int receiverIndex = Integer.parseInt(parts[1]);
                            String receiverMessage = message.substring(message.indexOf(parts[2]));
                            server.sendMessageToClient(this, receiverIndex, receiverMessage);
                        } else {
                            sendMessage("Invalid command. Please use the format: /talk <receiverIndex> <message>");
                        }
                    } 
                    else if(message.startsWith("/anno")){
                        server.sendMessageToAll(this, message.substring(5));

                    }
                    else {
                        server.sendConnectedUsersToAllClients();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // Remove the client from the server's list and notify all clients
                server.removeClient(this);
                server.sendConnectedUsersToAllClients();
                closeResources();
            }
        }

        private void closeResources() {
            try {
                inputStream.close();
                outputStream.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}