package hackathonsystem.view;

import hackathonsystem.controller.HackathonManager;
import hackathonsystem.model.AbstractHackathonTeam;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import javax.swing.border.TitledBorder;

public class JudgeGUIView extends JFrame {
    private final HackathonManager manager;
    private JTextField teamNumberSearch;
    private JTextField score1, score2, score3, score4, score5;
    private JTextArea teamDetailsArea;
    
    private JButton searchButton, updateScoresButton, generateReportButton; 
    
    private static final Color PRIMARY_COLOR = new Color(52, 152, 219); 
    private static final Color SUCCESS_COLOR = new Color(39, 174, 96); 
    private static final Color BACKGROUND_COLOR = new Color(244, 246, 247); 
    private static final Color REPORT_COLOR = new Color(142, 68, 173); 
    private static final Font HEADER_FONT = new Font("SansSerif", Font.BOLD, 16);
    private static final Font LABEL_FONT = new Font("SansSerif", Font.PLAIN, 12);


    public JudgeGUIView(HackathonManager manager) {
        this.manager = manager;
        setTitle("Judge Scoring Interface - Role: Judge");
        setSize(600, 500);
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
        
        score1 = new JTextField(5);
        score2 = new JTextField(5);
        score3 = new JTextField(5);
        score4 = new JTextField(5);
        score5 = new JTextField(5);

        JTextField[] scoreFields = {score1, score2, score3, score4, score5};
        for (JTextField field : scoreFields) {
            field.setHorizontalAlignment(JTextField.CENTER);
            field.setFont(HEADER_FONT.deriveFont(Font.PLAIN, 14));
        }

        teamDetailsArea = new JTextArea(5, 40);
        teamDetailsArea.setEditable(false);
        teamDetailsArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        teamDetailsArea.setForeground(Color.DARK_GRAY);

        searchButton = createStyledButton("Search Team", PRIMARY_COLOR);
        searchButton.addActionListener(e -> searchTeam());
        
        updateScoresButton = createStyledButton("Update Scores", SUCCESS_COLOR);
        updateScoresButton.addActionListener(e -> updateScores());
        
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

        JPanel scoreInputPanel = new JPanel(new GridLayout(3, 4, 15, 15));
        scoreInputPanel.setBackground(Color.WHITE);
        // MODIFIED: Changed 0-10 to 0-5 in the UI title
        scoreInputPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(SUCCESS_COLOR), "Enter New Scores (0-5)",
            TitledBorder.LEFT, TitledBorder.TOP, HEADER_FONT, SUCCESS_COLOR
        ));
        
        scoreInputPanel.add(new JLabel("Score 1 (Concept):")); scoreInputPanel.add(score1);
        scoreInputPanel.add(new JLabel("Score 2 (Design):")); scoreInputPanel.add(score2);
        scoreInputPanel.add(new JLabel("Score 3 (Functionality):")); scoreInputPanel.add(score3);
        scoreInputPanel.add(new JLabel("Score 4 (Innovation):")); scoreInputPanel.add(score4);
        scoreInputPanel.add(new JLabel("Score 5 (Presentation):")); scoreInputPanel.add(score5);
        scoreInputPanel.add(new JLabel("")); 

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        actionPanel.setBackground(BACKGROUND_COLOR);
        actionPanel.add(updateScoresButton);
        actionPanel.add(generateReportButton); 
        
        bottomContainer.add(scoreInputPanel, BorderLayout.NORTH);
        bottomContainer.add(actionPanel, BorderLayout.CENTER);
        
        mainPanel.add(bottomContainer, BorderLayout.SOUTH);
        
        add(mainPanel);
    }

    private void searchTeam() {
        try {
            int teamNum = Integer.parseInt(teamNumberSearch.getText().trim());
            AbstractHackathonTeam team = manager.getTeamList().findTeamByNumber(teamNum);
            
            if (team != null) {
                teamDetailsArea.setText(team.getFullDetails());
                int[] scores = team.getScores();
                if (scores.length == 5) {
                    score1.setText(String.valueOf(scores[0]));
                    score2.setText(String.valueOf(scores[1]));
                    score3.setText(String.valueOf(scores[2]));
                    score4.setText(String.valueOf(scores[3]));
                    score5.setText(String.valueOf(scores[4]));
                }
            } else {
                teamDetailsArea.setText("Team not found for ID: " + teamNum);
                score1.setText(""); score2.setText(""); score3.setText(""); score4.setText(""); score5.setText("");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric Team ID.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean isScoreValid(int score) {
        return score >= 0 && score <= 5;
    }

    private void updateScores() {
        try {
            int teamNum = Integer.parseInt(teamNumberSearch.getText().trim());
            int[] newScores = new int[5];
            
            newScores[0] = Integer.parseInt(score1.getText().trim());
            newScores[1] = Integer.parseInt(score2.getText().trim());
            newScores[2] = Integer.parseInt(score3.getText().trim());
            newScores[3] = Integer.parseInt(score4.getText().trim());
            newScores[4] = Integer.parseInt(score5.getText().trim());
            
            // NEW VALIDATION: Check each score is between 0 and 5
            for (int score : newScores) {
                if (!isScoreValid(score)) {
                    JOptionPane.showMessageDialog(this, 
                        "All scores must be within the valid range of 0 to 5.", 
                        "Score Validation Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return; // Stop execution if any score is invalid
                }
            }
            // End of NEW VALIDATION

            if (manager.updateTeamScores(teamNum, newScores)) {
                JOptionPane.showMessageDialog(this, "Scores successfully updated for Team " + teamNum, "Success", JOptionPane.INFORMATION_MESSAGE);
                searchTeam();
            } else {
                // Modified error message for clarity on the 0-5 range
                JOptionPane.showMessageDialog(this, "Failed to update scores. Check Team ID and ensure all 5 scores are 0-5.", "Update Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            // Modified error message for clarity on the 0-5 range
            JOptionPane.showMessageDialog(this, "All scores must be valid integers (0-5).", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void generateReport() {
        manager.generateReportAndWriteToFile();
        JOptionPane.showMessageDialog(this, "Report generated successfully. Check HackathonReport.txt", "Report Success", JOptionPane.INFORMATION_MESSAGE);
    }

}

