import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class DictionaryClientUI extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainContainer;

    // Page 1 components
    private JTextField portField;
    private JButton connectButton;

    // Page 2 components
    private JTextField wordField;
    private JTextArea inputArea1, inputArea2, inputArea3, outputArea;
    private JButton startButton;
    private JLabel guideLabel;
    private JPanel inputPanel1, inputPanel2, inputPanel3;
    private String selectedFunction = "Query";

    private Map<String, JButton> functionButtons = new HashMap<>();
    private Client connection;

    public DictionaryClientUI() {
        setTitle("Online Dictionary");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        mainContainer.add(createConnectionPage(), "connect");
        mainContainer.add(createFunctionPage(), "functions");

        add(mainContainer);
        setVisible(true);
    }

    // Page 1: Port connection
    private JPanel createConnectionPage() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel portLabel = new JLabel("Enter Port Number:");
        portField = new JTextField(10);
        connectButton = new JButton("Connect");

        connectButton.addActionListener(e -> handleConnect());

        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(portLabel, gbc);
        gbc.gridx = 1;
        panel.add(portField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        panel.add(connectButton, gbc);

        return panel;
    }

    // Page 2: Full UI after successful connection
    private JPanel createFunctionPage() {
        JPanel container = new JPanel(new BorderLayout());

        // Sidebar using buttons
        String[] functions = {"Query", "Add", "Remove", "Add Meaning", "Update Meaning"};
        JPanel sidebar = new JPanel(new GridLayout(functions.length, 1));
        sidebar.setPreferredSize(new Dimension(160, 0));
        sidebar.setBorder(BorderFactory.createTitledBorder("Functions"));

        for (String func : functions) {
            JButton btn = new JButton(func);
            btn.setFocusPainted(false);
            btn.setBackground(Color.LIGHT_GRAY);
            btn.setHorizontalAlignment(SwingConstants.LEFT);
            btn.addActionListener(e -> {
                selectedFunction = func;
                updateGuide();
                functionButtons.values().forEach(b -> b.setBackground(Color.LIGHT_GRAY));
                btn.setBackground(Color.GRAY);
            });
            functionButtons.put(func, btn);
            sidebar.add(btn);
        }
        functionButtons.get("Query").setBackground(Color.GRAY); // default highlight
        container.add(sidebar, BorderLayout.WEST);

        // Right panel content
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

        JPanel wordPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        wordPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        wordPanel.add(new JLabel("Word:"));
        wordField = new JTextField(30); // wider
        wordField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        wordField.setPreferredSize(new Dimension(400, 30));
        wordPanel.add(wordField);

        startButton = new JButton("Start");
        startButton.setPreferredSize(new Dimension(100, 30)); // bigger button
        startButton.setFont(new Font("SansSerif", Font.BOLD, 14));

        startButton.addActionListener(e -> handleStart());
        wordPanel.add(startButton); // 🟢 Start button moved beside Word input

        guideLabel = new JLabel("Query: Enter a word to get its meaning.");
        guideLabel.setForeground(Color.BLUE);

        // First input panel (for meaning or original meaning)
        inputArea1 = new JTextArea(4, 60);
        inputArea1.setLineWrap(true);
        inputArea1.setWrapStyleWord(true);
        inputPanel1 = new JPanel(new BorderLayout());
        inputPanel1.setBorder(BorderFactory.createTitledBorder("Enter Meaning"));
        inputPanel1.add(new JScrollPane(inputArea1), BorderLayout.CENTER);

        // Second input panel (only used for Update Meaning)
        inputArea2 = new JTextArea(4, 60);
        inputArea2.setLineWrap(true);
        inputArea2.setWrapStyleWord(true);
        inputPanel2 = new JPanel(new BorderLayout());
        inputPanel2.setBorder(BorderFactory.createTitledBorder(""));
        inputPanel2.add(new JScrollPane(inputArea2), BorderLayout.CENTER);

        outputArea = new JTextArea(5, 60);
        outputArea.setEditable(false);
        outputArea.setBorder(BorderFactory.createTitledBorder("Server Response"));

        rightPanel.add(wordPanel);
        rightPanel.add(guideLabel);
        rightPanel.add(inputPanel1);
        rightPanel.add(inputPanel2);
        rightPanel.add(new JScrollPane(outputArea));

        container.add(rightPanel, BorderLayout.CENTER);
        return container;
    }

    private void handleConnect() {
        try {
            String portText = portField.getText().trim();
            if (portText.isEmpty()) throw new Exception("Port number cannot be empty.");
            int port = Integer.parseInt(portText);
            if (port < 1024 || port > 65535) throw new Exception("Port number must be between 1024 and 65535.");

            connection = new Client("localhost", port);
            JOptionPane.showMessageDialog(this, "Connected to port " + port);
            cardLayout.show(mainContainer, "functions");
            setSize(800, 550);
            setLocationRelativeTo(null);

            selectedFunction = "Query";
            updateGuide();
            functionButtons.values().forEach(b -> b.setBackground(Color.LIGHT_GRAY));
            functionButtons.get("Query").setBackground(Color.GRAY);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid port number. Please enter a valid number.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to connect: " + e.getMessage());
        }
    }



    private void updateGuide() {
        inputPanel1.setVisible(true);
        inputPanel2.setVisible(false); // hidden by default
        inputPanel1.setBorder(BorderFactory.createTitledBorder("Enter Meaning")); // default title

        switch (selectedFunction) {
            case "Query":
                guideLabel.setText("Query: Enter a word to get its meaning.");
                inputPanel1.setVisible(false);
                break;
            case "Add":
                guideLabel.setText("Add: Enter a word and its meanings (one per line).");
                break;
            case "Remove":
                guideLabel.setText("Remove: Enter the word to be removed.");
                inputPanel1.setVisible(false);
                break;
            case "Add Meaning":
                guideLabel.setText("Add Meaning: Enter the word and one new meaning.");
                break;
            case "Update Meaning":
                guideLabel.setText("Update Meaning: Enter the word, original meaning, and new meaning.");
                inputPanel1.setBorder(BorderFactory.createTitledBorder("Enter Original Meaning"));
                inputPanel2.setBorder(BorderFactory.createTitledBorder("Enter New Meaning"));
                inputPanel2.setVisible(true);
                break;
            default:
                guideLabel.setText("Select a function.");
        }

        inputArea1.setText("");
        inputArea2.setText("");
        outputArea.setText("");
    }



    private void handleStart() {
        String word = wordField.getText().trim();
        if (word.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a word.");
            return;
        }

        try {
            String response = "";
            switch (selectedFunction) {
                case "Query":
                    response = connection.query(word);
                    break;

                case "Add":
                    String[] addLines = inputArea1.getText().trim().split("\\n");
                    List<String> meanings = new ArrayList<>();
                    for (String line : addLines) {
                        if (!line.trim().isEmpty()) meanings.add(line.trim());
                    }
                    if (meanings.isEmpty()) throw new Exception("Please enter at least one meaning.");
                    response = connection.add(word, meanings);
                    break;

                case "Remove":
                    response = connection.remove(word);
                    break;

                case "Add Meaning":
                    String meaning = inputArea1.getText().trim();
                    if (meaning.isEmpty()) throw new Exception("Please enter a new meaning.");
                    response = connection.addMeaning(word, meaning);
                    break;

                case "Update Meaning":
                    String original = inputArea1.getText().trim();
                    String updated = inputArea2.getText().trim();
                    if (original.isEmpty()) throw new Exception("Original meaning cannot be empty.");
                    if (updated.isEmpty()) throw new Exception("New meaning cannot be empty.");
                    response = connection.updateMeaning(word, original, updated);
                    break;

                default:
                    response = "Invalid function.";
            }

            outputArea.setText(response);
            inputArea1.setText("");
            inputArea2.setText("");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(DictionaryClientUI::new);
    }
}



