package hackathonsystem.view;

import hackathonsystem.controller.HackathonManager;
import hackathonsystem.model.AbstractHackathonTeam;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import javax.swing.border.TitledBorder;


public class StaffGUIView extends JFrame {
    private final HackathonManager manager;
    private JTextField teamNumberSearch, nameField, universityField;
    private JTextArea teamDetailsArea;
    private JButton searchButton, updateDetailsButton, disqualifyButton, generateReportButton; 

    private static final Color PRIMARY_COLOR = new Color(149, 165, 166); 
    private static final Color WARNING_COLOR = new Color(230, 126, 34); 
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113); 
    private static final Color REPORT_COLOR = new Color(142, 68, 173); 
    private static final Color BACKGROUND_COLOR = new Color(249, 249, 249);
    private static final Font HEADER_FONT = new Font("SansSerif", Font.BOLD, 16);
    private static final Font LABEL_FONT = new Font("SansSerif", Font.PLAIN, 12);

    public StaffGUIView(HackathonManager manager) {
        this.manager = manager;
        setTitle("Staff Management Interface - Role: Staff");
        setSize(550, 550); 
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initComponents();
        setLayoutComponents();
    }
    
    private JButton createStyledButton(String text, Color background) {
        JButton button = new JButton(text);
        button.setBackground(background);
        button.setForeground(Color.WHITE);
        button.setFont(LABEL_FONT.deriveFont(Font.BOLD, 13));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
    
    private void initComponents() {
        teamNumberSearch = new JTextField(8);
        teamNumberSearch.setHorizontalAlignment(JTextField.CENTER);
        
        nameField = new JTextField(20);
        universityField = new JTextField(20);
        
        nameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY), 
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        universityField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY), 
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        teamDetailsArea = new JTextArea(8, 40);
        teamDetailsArea.setEditable(false);
        teamDetailsArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        teamDetailsArea.setForeground(Color.DARK_GRAY);

        searchButton = createStyledButton("Search Team", PRIMARY_COLOR.darker());
        searchButton.addActionListener(e -> searchTeam());
        
        updateDetailsButton = createStyledButton("Update Details", SUCCESS_COLOR);
        updateDetailsButton.addActionListener(e -> updateDetails());
        
        disqualifyButton = createStyledButton("Disqualify Team", WARNING_COLOR);
        disqualifyButton.addActionListener(e -> disqualifyTeam());
        
        generateReportButton = createStyledButton("Generate Report (.txt)", REPORT_COLOR);
        generateReportButton.addActionListener(e -> generateReport());
    }

    private void setLayoutComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR.darker()), "Find Team", 
            TitledBorder.CENTER, TitledBorder.TOP, HEADER_FONT, PRIMARY_COLOR.darker()
        ));
        
        JLabel searchLabel = new JLabel("Enter Team ID:");
        searchLabel.setFont(LABEL_FONT.deriveFont(Font.BOLD));
        searchPanel.add(searchLabel);
        searchPanel.add(teamNumberSearch);
        searchPanel.add(searchButton);
        
        mainPanel.add(searchPanel, BorderLayout.NORTH);

        JScrollPane detailsScrollPane = new JScrollPane(teamDetailsArea);
        detailsScrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1), 
            "Team Details (Current Status)", 
            TitledBorder.LEFT, TitledBorder.TOP, LABEL_FONT, Color.DARK_GRAY
        ));
        mainPanel.add(detailsScrollPane, BorderLayout.CENTER);

        JPanel bottomContainer = new JPanel(new BorderLayout(15, 15));
        bottomContainer.setBackground(BACKGROUND_COLOR);
        
        JPanel detailInputPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        detailInputPanel.setBackground(Color.WHITE);
        detailInputPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(SUCCESS_COLOR), "Update Details",
            TitledBorder.LEFT, TitledBorder.TOP, HEADER_FONT, SUCCESS_COLOR
        ));
        
        detailInputPanel.add(new JLabel("New Name:"));
        detailInputPanel.add(nameField);
        detailInputPanel.add(new JLabel("New University:"));
        detailInputPanel.add(universityField);

        JPanel actionPanel = new JPanel(new GridBagLayout());
        actionPanel.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Button 1: Update Details
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1.0;
        actionPanel.add(updateDetailsButton, gbc);
        
        // Button 2: Disqualify Team
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        actionPanel.add(disqualifyButton, gbc);
        
        // Button 3: Generate Report (Ensured visibility)
        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 1.0;
        actionPanel.add(generateReportButton, gbc);


        bottomContainer.add(detailInputPanel, BorderLayout.NORTH);
        bottomContainer.add(actionPanel, BorderLayout.SOUTH);
        
        mainPanel.add(bottomContainer, BorderLayout.SOUTH);
        
        add(mainPanel);
    }

    private void searchTeam() {
        try {
            int teamNum = Integer.parseInt(teamNumberSearch.getText().trim());
            AbstractHackathonTeam team = manager.getTeamList().findTeamByNumber(teamNum);
            
            if (team != null) {
                teamDetailsArea.setText(team.getFullDetails());
                nameField.setText(team.getTeamName());
                universityField.setText(team.getUniversity());
            } else {
                teamDetailsArea.setText("Team not found for ID: " + teamNum);
                nameField.setText(""); universityField.setText("");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric Team ID.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateDetails() {
        try {
            int teamNum = Integer.parseInt(teamNumberSearch.getText().trim());
            String newName = nameField.getText().trim();
            String newUniversity = universityField.getText().trim();
            
            // This call now correctly maps to the 3-argument overload in HackathonManager
            if (manager.updateTeamDetails(teamNum, newName, newUniversity)) {
                JOptionPane.showMessageDialog(this, "Details successfully updated for Team " + teamNum, "Success", JOptionPane.INFORMATION_MESSAGE);
                searchTeam(); 
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update details. Check Team ID.", "Update Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please search for a team first.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void disqualifyTeam() {
        try {
            int teamNum = Integer.parseInt(teamNumberSearch.getText().trim());
            
            AbstractHackathonTeam team = manager.getTeamList().findTeamByNumber(teamNum);
            if (team == null) {
                JOptionPane.showMessageDialog(this, "Team not found for ID: " + teamNum, "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!team.isEligibility()) {
                JOptionPane.showMessageDialog(this, "Team " + teamNum + " is ALREADY disqualified.", "Info", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to DISQUALIFY Team " + teamNum + "? This will set the overall score to 0.0.", 
                "Confirm Disqualification", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                if (manager.disqualifyTeam(teamNum)) {
                    JOptionPane.showMessageDialog(this, "Team " + teamNum + " successfully disqualified.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    searchTeam();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to disqualify team. Check Team ID.", "Update Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please search for a team first.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void generateReport() {
        manager.generateReportAndWriteToFile();
        JOptionPane.showMessageDialog(this, "Report generated successfully. Check HackathonReport.txt", "Report Success", JOptionPane.INFORMATION_MESSAGE);
    }
}