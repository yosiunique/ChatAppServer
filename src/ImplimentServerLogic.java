import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class ImplimentServerLogic {

    // Map username to client output stream to send messages
    private static ConcurrentHashMap<String, ObjectOutputStream> activeUsers = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(222)) {
            System.out.println("Server is waiting on port 222...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connected to: " + clientSocket.getInetAddress().getHostAddress());
                new Thread(new ClientHandler(clientSocket)).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    static class ClientHandler implements Runnable {
        private final Socket socket;
        private String username;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (
                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
            ) {
                // Read initial client info (username)
                Object object = in.readObject();
                if (!(object instanceof ClientDetails clientDetails)) {
                    System.out.println("Invalid client details received. Closing connection.");
                    socket.close();
                    return;
                }

                username = clientDetails.getUserName();
                if (username == null || username.isEmpty()) {
                    System.out.println("Username invalid. Closing connection.");
                    socket.close();
                    return;
                }

                activeUsers.put(username, out);
                System.out.println("User connected: " + username);

                // Send welcome message
                clientDetails.setMessage("Welcome " + username + "! You are connected.");
                out.writeObject(clientDetails);
                out.flush();

                // Keep listening for messages to route
                while (true) {
                    Object incoming = in.readObject();
                    if (incoming instanceof ClientDetails message) {
                        String receiver = message.getReciver();
                        System.out.println("Message from " + username + " to " + receiver + ": " + message.getMessage());

                        ObjectOutputStream receiverOut = activeUsers.get(receiver);
                        if (receiverOut != null) {
                            try {
                                receiverOut.writeObject(message);
                                receiverOut.flush();
                            } catch (IOException e) {
                                System.out.println("Error sending message to " + receiver + ": " + e.getMessage());
                            }
                        } else {
                            // Receiver offline - notify sender
                            ClientDetails errorMsg = new ClientDetails();
                            errorMsg.setUserName("Server");
                            errorMsg.setMessage("User '" + receiver + "' is not connected.");
                            out.writeObject(errorMsg);
                            out.flush();
                        }
                    } else {
                        System.out.println("Unknown object from client " + username);
                    }
                }

            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Connection lost with user " + username);
            } finally {
                if (username != null) {
                    activeUsers.remove(username);
                    System.out.println("User disconnected: " + username);
                }
                try {
                    socket.close();
                } catch (IOException ignore) {}
            }
        }
    }
}

