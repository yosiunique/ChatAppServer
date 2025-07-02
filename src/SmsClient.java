import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.Socket;

public class SmsClient {
    private Socket socket;
    final static String USERNAME=JOptionPane.showInputDialog("Enter your username");
    final String reciver=JOptionPane.showInputDialog("Enter your reciver name");
    private ObjectOutputStream objectWriter;

    private ObjectInputStream objectReader;
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SmsClient client = new SmsClient();
            ChatAppUI chatUI = new ChatAppUI();

            chatUI.setVisible(true);

            // Connect once at startup


            client.connect("10.1.81.54", 222, USERNAME, chatUI);
            chatUI.getSendButton().addActionListener((ActionEvent e) -> {
                String text = chatUI.getMessageField().getText().trim();
                if (!text.isEmpty()) {
                    chatUI.getTextArea().setCaretColor(chatUI.getTextArea().getCaretColor());
                    chatUI.getTextArea().append("Me: " + text + "\n");
                    // Change "ygetchew" to the actual receiver username you want to message
                    client.sendMessage(USERNAME, text, chatUI);
                }
            });
        });
    }

    public void connect(String host, int port, String username, ChatAppUI chatUI) {
        try {
            socket = new Socket(host, port);
            objectWriter = new ObjectOutputStream(socket.getOutputStream());
            objectReader = new ObjectInputStream(socket.getInputStream());

            // Send initial ClientDetails to register username with server
            ClientDetails clientDetails = new ClientDetails();
            clientDetails.setUserName(username);
            clientDetails.setMessage("has connected");
            clientDetails.setReciver(reciver); // no receiver on connect
            objectWriter.writeObject(clientDetails);
            objectWriter.flush();

            // Background thread to receive messages
            new Thread(() -> {
                try {
                    while (true) {
                        Object response = objectReader.readObject();
                        if (response instanceof ClientDetails responseDetails) {
                            chatUI.getTextArea().append(
                                    responseDetails.getUserName() + ": " + responseDetails.getMessage() + "\n"
                            );
                        }
                    }
                } catch (Exception e) {
                    chatUI.getTextArea().append("Disconnected: " + e.getMessage() + "\n");
                }
            }).start();

        } catch (IOException e) {
            chatUI.getTextArea().append("Connection Error: " + e.getMessage() + "\n");
        }
    }

    public void sendMessage(String username, String message, ChatAppUI chatUI) {
        try {
            ClientDetails client = new ClientDetails();
            client.setUserName(username); // your username
            client.setMessage(message);
            client.setReciver(reciver); // set the receiver username here
            objectWriter.writeObject(client);
            objectWriter.flush();
        } catch (IOException e) {
            chatUI.getTextArea().append("Error Sending: " + e.getMessage() + "\n");
        }
        chatUI.getMessageField().setText("");
    }
}

// GUI code
class ChatAppUI extends JFrame {
    private final JTextArea chatArea;
    private final JTextField messageField;
    private final JButton sendButton;

    public ChatAppUI() {
        setTitle("Chat Client");
        setSize(400, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(chatArea);

        messageField = new JTextField();
        sendButton = new JButton("Send");

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);
    }

    public JTextArea getTextArea() {
        return chatArea;
    }

    public JTextField getMessageField() {
        return messageField;
    }

    public JButton getSendButton() {
        return sendButton;
    }
}
