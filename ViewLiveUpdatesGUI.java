package hackathonsystem.view;

import hackathonsystem.controller.HackathonManager;
import hackathonsystem.model.AbstractHackathonTeam;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;

public class ViewLiveUpdatesGUI extends JFrame {
    private final HackathonManager manager;

    private JTable teamTable;
    private DefaultTableModel tableModel;
    private JButton registerTeamButton;
    private int nextTeamNumber = 120; 

    private static final Color PRIMARY_COLOR = new Color(52, 73, 94);
    private static final Color ACCENT_COLOR = new Color(46, 204, 113);
    private static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 18);
    private static final Font LABEL_FONT = new Font("SansSerif", Font.PLAIN, 12);

    public ViewLiveUpdatesGUI(HackathonManager manager) {
        this.manager = manager;

        setTitle("Live Hackathon Updates");
        setSize(1100, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        setLayoutComponents();
        refreshTable();
    }

    private void initComponents() {
        tableModel = new DefaultTableModel(new Object[]{
                "Team #", "Team Name", "University", "Category", "Eligibility",
                "Score 1", "Score 2", "Score 3", "Score 4", "Score 5", "Overall"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // read-only table
            }
        };

        teamTable = new JTable(tableModel);
        teamTable.setFont(LABEL_FONT);

        registerTeamButton = createStyledButton("Register Team");
        registerTeamButton.addActionListener(this::openTeamRegistrationDialog);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(ACCENT_COLOR);
        button.setForeground(Color.WHITE);
        button.setFont(LABEL_FONT.deriveFont(Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void setLayoutComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel("Live Hackathon Updates", SwingConstants.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(teamTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(registerTeamButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        this.add(mainPanel);
    }

    private void refreshTable() {
        tableModel.setRowCount(0); 

        for (AbstractHackathonTeam team : manager.getTeamList().getAllTeams()) {
            tableModel.addRow(new Object[]{
                    team.getTeamNumber(),
                    team.getTeamName(),
                    team.getUniversity(),
                    team.getCategory(),
                    team.isEligibility() ? "Eligible" : "Pending",
                    team.getScores()[0],
                    team.getScores()[1],
                    team.getScores()[2],
                    team.getScores()[3],
                    team.getScores()[4],
                    String.format("%.2f", team.getOverallScore()) 
            });
        }
    }

    private void openTeamRegistrationDialog(ActionEvent e) {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));

        JTextField teamNameField = new JTextField("Cyber Hackers");
        JTextField universityField = new JTextField("SEGI");
        JTextField leaderNameField = new JTextField("PATRICK");
        JTextField leaderEmailField = new JTextField("pat@example.com");
        JTextField leaderIdField = new JTextField("SCSJ2100424");
        JTextField leaderDobField = new JTextField("2003-03-28");
        JTextField membersField = new JTextField("Tanu,Sha,Knee");

        JComboBox<String> categoryBox = new JComboBox<>(
                manager.getTeamList().getAvailableCategories().toArray(new String[0])
        );

        panel.add(new JLabel("Team Name:"));
        panel.add(teamNameField);
        panel.add(new JLabel("University:"));
        panel.add(universityField);
        panel.add(new JLabel("Team Leader Name:"));
        panel.add(leaderNameField);
        panel.add(new JLabel("Leader Email:"));
        panel.add(leaderEmailField);
        panel.add(new JLabel("Leader Student ID:"));
        panel.add(leaderIdField);
        panel.add(new JLabel("Leader DOB:"));
        panel.add(leaderDobField);
        panel.add(new JLabel("Team Members (comma-separated):"));
        panel.add(membersField);
        panel.add(new JLabel("Category:"));
        panel.add(categoryBox);

        int result = JOptionPane.showConfirmDialog(
                this, panel, "Register New Team", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            if (teamNameField.getText().isEmpty() || universityField.getText().isEmpty() ||
                    leaderNameField.getText().isEmpty() || leaderEmailField.getText().isEmpty() ||
                    leaderIdField.getText().isEmpty() || leaderDobField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "All mandatory fields must be filled.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String teamName = teamNameField.getText().trim();
            String university = universityField.getText().trim();
            String leaderName = leaderNameField.getText().trim();
            String leaderEmail = leaderEmailField.getText().trim();
            String leaderId = leaderIdField.getText().trim();
            String leaderDob = leaderDobField.getText().trim();
            String[] members = membersField.getText().split(",");
            String category = (String) categoryBox.getSelectedItem();

            if (!leaderId.toUpperCase().startsWith("S")) {
                JOptionPane.showMessageDialog(this, "Eligibility rule violated: Leader must be a student.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Duplicate check: same leader + category
            boolean duplicateExists = manager.getTeamList().getAllTeams().stream()
                    .anyMatch(t -> t.getCustomAttributeDetails().equalsIgnoreCase(leaderName) &&
                            t.getCategory().equalsIgnoreCase(category));
            if (duplicateExists) {
                JOptionPane.showMessageDialog(this, "Duplicate team with same leader and category exists.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int teamNumber = nextTeamNumber++;
            AbstractHackathonTeam team = new AbstractHackathonTeam(
                    teamNumber, teamName, category, university, false, leaderName, new int[]{0,0,0,0,0}) {
                @Override
                public double getOverallScore() { return 0; }
                @Override
                public String getUniqueAttributeDetails() { return Arrays.toString(members); }
            };

            manager.getTeamList().addTeam(team);
            refreshTable();

            JOptionPane.showMessageDialog(this, "Team registered successfully! Team Number: " + teamNumber, "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}

