import java.io.*;
import java.net.*;
import java.util.*;

import javax.swing.DefaultListModel;

public class Client {
    private boolean isAlive;
    private Socket socket;
    private PrintWriter outputStream;
    private BufferedReader inputStream;
    private int receiverIndex = -1; // Added field to store receiver index
    private ChatBoxFrame chatBoxFrame;
    // a private ClientHandler clientHandler;

    public PrintWriter getOutputStream() {
        return outputStream;
    }

    public void setChatBoxFrame(ChatBoxFrame chatBoxFrame) {
        this.chatBoxFrame = chatBoxFrame;
    }

    public Client(String serverAddress, int serverPort) {
        LoginFrame loginFrame = new LoginFrame(this);
        loginFrame.setVisible(true);
        try {
            socket = new Socket(serverAddress, serverPort);
            outputStream = new PrintWriter(socket.getOutputStream(), true);
            inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        // Create a separate thread to read messages from the server
        Thread readerThread = new Thread(new ServerReader());
        readerThread.start();

        try {
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

            // System.out.print("Enter your username: ");
            // String username = consoleReader.readLine();

            // Send the username to the server
            // outputStream.println(username);

            String message;
            while ((message = consoleReader.readLine()) != null) {
                if (message.equals("/ul")) {
                    outputStream.println(message);
                } else {
                    if (message.startsWith("/talk")) {

                        String[] parts = message.split(" ");
                        if (parts.length >= 2) {
                            receiverIndex = Integer.parseInt(parts[1]); // Update the receiver index
                        } else {
                            System.out.println("Invalid command. Please use the format: /talk <receiverIndex>");
                        }
                    } else {
                        if (receiverIndex != -1) {
                            outputStream.println("/talk " + receiverIndex + " " + message);
                        } else {
                            System.out.println(
                                    "No receiver index specified. Please use the command /talk <receiverIndex> to specify the recipient.");
                        }
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
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

    private void updateJList(String[] userList) {
        DefaultListModel <String> chatMemberList = chatBoxFrame.getChatMembersListModel();
        for (String user : userList) {
            if (!chatMemberList.contains(user))
                chatMemberList.addElement(user);
        }
    }

    private String[] getUserList(String message) {
        String[] userList = message.split("\\|");
        return userList;
    }

    private class ServerReader implements Runnable {
        @Override
        public void run() {
            try {
                String message;
                while ((message = inputStream.readLine()) != null) {
                    if (message.startsWith("[uls]")) {
                        System.out.println(message);
                        updateJList(getUserList(message.substring(5)));
                    } else if (message.startsWith("[msg]")) {
                        System.out.println(message);
                        chatBoxFrame.getChatTextArea().append("From " + message.substring(5) + "\n");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                closeResources();
            }
        }
    }

    public static void main(String[] args) {
        String serverAddress = "localhost"; // Replace with the server address
        int serverPort = 1234; // Replace with the server port

        Client client = new Client(serverAddress, serverPort);
        client.start();

    }
}