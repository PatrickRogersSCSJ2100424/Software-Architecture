package hackathonsystem.view;

import hackathonsystem.controller.AuthenticationManager;
import hackathonsystem.controller.HackathonManager;
import hackathonsystem.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter; 
import java.awt.event.WindowEvent; 

public class LoginGUIView extends JFrame {
    private final AuthenticationManager authManager;
    private final HackathonManager hackathonManager; 

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton viewUpdatesButton; 

    private static final Color PRIMARY_COLOR = new Color(52, 73, 94);
    private static final Color ACCENT_COLOR = new Color(46, 204, 113);
    private static final Font LABEL_FONT = new Font("SansSerif", Font.BOLD, 12);
    private static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 18);
    private static final Color HINT_COLOR = Color.GRAY;

    public LoginGUIView(AuthenticationManager authManager, HackathonManager hackathonManager) {
        this.authManager = authManager;
        this.hackathonManager = hackathonManager;

        setTitle("Hackathon System Login");
        setSize(400, 300);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); 
        
        setLocationRelativeTo(null);

        initComponents();
        setLayoutComponents();
        addWindowCloseConfirmation(); 
    }
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(ACCENT_COLOR);
        button.setForeground(Color.WHITE);
        button.setFont(LABEL_FONT.deriveFont(Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void initComponents() {
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);

        loginButton = createStyledButton("Login");
        viewUpdatesButton = createStyledButton("View Live Updates");

        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        loginButton.addActionListener((ActionEvent e) -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            User authenticatedUser = authManager.attemptLogin(username, password);

            if (authenticatedUser != null) {
                authManager.launchGUIForUser(authenticatedUser);

                usernameField.setText("");
                passwordField.setText("");
                
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Invalid username or password.",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        viewUpdatesButton.addActionListener(e -> {
            new ViewLiveUpdatesGUI(hackathonManager).setVisible(true);
        });
    }

    private void setLayoutComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Hackathon System Access", SwingConstants.CENTER);
        title.setFont(TITLE_FONT);
        title.setForeground(PRIMARY_COLOR);
        mainPanel.add(title, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        inputPanel.setBackground(Color.WHITE);

        inputPanel.add(new JLabel("Username:"));
        inputPanel.add(usernameField);
        inputPanel.add(new JLabel("Password:"));
        inputPanel.add(passwordField);

        mainPanel.add(inputPanel, BorderLayout.CENTER);

        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));
        southPanel.setBackground(Color.WHITE);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(loginButton);
        buttonPanel.add(viewUpdatesButton);

        // MODIFIED HINT LABEL: Using JTextArea for multi-line text without HTML
        JTextArea hintTextArea = new JTextArea();
        hintTextArea.setText("Examples: admin, admin123\n staff1, staffpass | judge1, judgepass | organizer, orgpass");
        hintTextArea.setEditable(false);
        hintTextArea.setBackground(Color.WHITE); // Match the panel background
        hintTextArea.setForeground(HINT_COLOR);
        hintTextArea.setFont(LABEL_FONT.deriveFont(Font.ITALIC));
        hintTextArea.setLineWrap(true);
        hintTextArea.setWrapStyleWord(true);
        // Center alignment is done by setting the JTextArea's alignment and adding it to the Box layout
        hintTextArea.setAlignmentX(Component.CENTER_ALIGNMENT);
        hintTextArea.setBorder(BorderFactory.createEmptyBorder()); // Remove default border

        southPanel.add(buttonPanel);
        southPanel.add(Box.createVerticalStrut(5));
        // Add the JTextArea instead of the JLabel
        southPanel.add(hintTextArea); 

        mainPanel.add(southPanel, BorderLayout.SOUTH);

        this.add(mainPanel);
    }

    private void addWindowCloseConfirmation() {
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int confirm = JOptionPane.showConfirmDialog(
                    LoginGUIView.this,
                    "Are you sure you want to exit the application? The final report will be generated now.",
                    "Exit Confirmation",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    // Critical: Generate the final report before closing
                    hackathonManager.generateReportAndWriteToFile();
                    System.exit(0); 
                }
            }
        });
    }
}