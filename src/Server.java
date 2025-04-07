import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class Server extends JFrame {
    private JTextField portField, fileField;
    private JButton startButton;
    private JTextArea logArea;

    private ExecutorService pool;
    private ServerSocket serverSocket;
    private boolean isRunning = false;

    public Server() {
        setTitle("Dictionary Server");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel topPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        topPanel.add(new JLabel("Port:"));
        portField = new JTextField("12345");
        topPanel.add(portField);

        topPanel.add(new JLabel("Dictionary File Path:"));
        fileField = new JTextField("dictionary.json");
        topPanel.add(fileField);

        startButton = new JButton("Start Server");
        topPanel.add(startButton);

        JButton closeButton = new JButton("Close Server");
        topPanel.add(closeButton);

        closeButton.addActionListener(e -> stopServer());


        startButton.addActionListener(this::startServer);

        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Server Log"));

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }

    private void startServer(ActionEvent e) {
        if (isRunning) {
            log("Server is already running.");
            return;
        }

        int port;
        try {
            port = Integer.parseInt(portField.getText().trim());
        } catch (NumberFormatException ex) {
            log("Invalid port number.");
            return;
        }

        String dictFile = fileField.getText().trim();
        java.io.File file = new java.io.File(dictFile);
        if (!file.exists() || !file.isFile()) {
            log("Dictionary file not found: " + dictFile);
            log("Please enter a valid file path and try again.");
            return;
        }

        log("Dictionary file verified: " + dictFile);
        log("Attempting to start server on port " + port + "...");

        pool = Executors.newFixedThreadPool(10);


        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(port);
                isRunning = true;
                log("Server started successfully on port " + port);

                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    log("📡 Client connected: " + clientSocket.getInetAddress());
                    pool.execute(new ClientHandler(clientSocket, port, dictFile, this::log));

                }
            } catch (IOException ex) {
                log("Could not bind to port " + port + ": " + ex.getMessage());
                isRunning = false;
            }
        }).start();
    }


    private void log(String msg) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(msg + "\n");
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Server::new);
    }

    private void stopServer() {
        if (!isRunning) {
            log("Server is not running.");
            return;
        }

        try {
            log("Shutting down server...");

            if (pool != null) {
                pool.shutdownNow();
            }

            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }

            isRunning = false;
            log("Server has been stopped.");
        } catch (IOException e) {
            log("Error while stopping server: " + e.getMessage());
        }
    }

}
