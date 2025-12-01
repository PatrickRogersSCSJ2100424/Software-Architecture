package hackathonsystem.view;

import hackathonsystem.controller.HackathonManager;
import hackathonsystem.model.AbstractHackathonTeam;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Comparator;
import javax.swing.border.TitledBorder;

public class AdminGUIView extends JFrame {
    private HackathonManager manager;
    
    private JTable teamTable;
    private JTextArea summaryArea;
    private DefaultTableModel tableModel;
    private JButton refreshButton, removeTeamButton, editTeamButton; 
    private JTextField removeTeamIdField;
    
    private JComboBox<String> categoryCombo; 
    private JComboBox<String> filterCategoryCombo; 
    
    private JTextField newCategoryField;
    private JComboBox<String> teamAssignCombo;
    private JComboBox<String> judgeAssignCombo;
    private JTextArea assignmentDisplayArea;
    
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185); 
    private static final Color ACCENT_COLOR = new Color(236, 240, 241);
    private static final Color EDIT_COLOR = new Color(52, 152, 219);
    private static final Color REMOVE_COLOR = new Color(231, 76, 60);
    private static final Font HEADER_FONT = new Font("SansSerif", Font.BOLD, 18);
    private static final Font LABEL_FONT = new Font("SansSerif", Font.PLAIN, 12);
    private static final Font MONOSPACE_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 12);

    public AdminGUIView(HackathonManager manager) {
        this.manager = manager;
        setTitle("Admin/Organizer Dashboard - Role: Admin");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initComponents();
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Team Management", createTeamManagementPanel());
        tabbedPane.addTab("Admin Tools", createAdminToolsPanel());
        
        add(tabbedPane);
        loadData();
    }
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFont(LABEL_FONT.deriveFont(Font.BOLD, 12));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void initComponents() {
        String[] columnNames = {"ID", "Name", "Category", "University", "Eligibility", "Score (max 5.0)"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        teamTable = new JTable(tableModel);
        
        teamTable.setRowHeight(25);
        teamTable.setFont(LABEL_FONT);
        teamTable.getTableHeader().setBackground(PRIMARY_COLOR);
        teamTable.getTableHeader().setForeground(Color.WHITE);
        teamTable.getTableHeader().setFont(HEADER_FONT.deriveFont(Font.BOLD, 13));
        teamTable.setSelectionBackground(PRIMARY_COLOR.brighter());
        teamTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        teamTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);

        summaryArea = new JTextArea(5, 60);
        summaryArea.setEditable(false);
        summaryArea.setBackground(ACCENT_COLOR);
        summaryArea.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR.brighter(), 1));
        summaryArea.setFont(MONOSPACE_FONT);
        
        refreshButton = createStyledButton("Refresh Data");
        refreshButton.addActionListener(e -> loadData());
        
        editTeamButton = createStyledButton("Edit Selected Team Details");
        editTeamButton.setBackground(EDIT_COLOR); 
        editTeamButton.addActionListener(e -> editTeamAction());
        
        removeTeamButton = createStyledButton("Remove Team");
        removeTeamButton.setBackground(REMOVE_COLOR); 
        removeTeamButton.addActionListener(e -> removeTeam());
        
        removeTeamIdField = new JTextField(5);
        removeTeamIdField.setHorizontalAlignment(JTextField.CENTER);
        removeTeamIdField.setToolTipText("Enter Team ID to remove");

        filterCategoryCombo = new JComboBox<>();
        filterCategoryCombo.addActionListener(e -> {
            if (filterCategoryCombo.isPopupVisible() || e.getActionCommand() != null) {
                String selectedCategory = (String) filterCategoryCombo.getSelectedItem();
                refreshTable(selectedCategory);
            }
        });
    }

    private JPanel createRemoveTeamPanel() {
        JPanel removePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        removePanel.setBackground(Color.WHITE);
        
        removePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(REMOVE_COLOR), 
                "Remove Team", 
                TitledBorder.LEFT, TitledBorder.TOP, LABEL_FONT.deriveFont(Font.BOLD), REMOVE_COLOR),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        removePanel.add(new JLabel("ID:"));
        removePanel.add(removeTeamIdField);
        removePanel.add(removeTeamButton);
        return removePanel;
    }

    private JPanel createTeamManagementPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel topContainer = new JPanel(new BorderLayout(10, 10));
        topContainer.setBackground(Color.WHITE);
        
        // --- NORTH: Summary Area ---
        JScrollPane summaryScrollPane = new JScrollPane(summaryArea);
        summaryScrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 1), 
            "Hackathon Summary Statistics", 
            TitledBorder.LEFT, TitledBorder.TOP, HEADER_FONT.deriveFont(Font.BOLD, 14), PRIMARY_COLOR
        ));
        topContainer.add(summaryScrollPane, BorderLayout.NORTH);
        
        // --- CENTER: Control Bar (Filter, Actions, Remove) ---
        JPanel controlBar = new JPanel(new BorderLayout(20, 0));
        controlBar.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0)); // Padding between summary and table

        // 1. LEFT: Filter Controls
        JPanel filterControls = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterControls.setBackground(Color.WHITE);
        filterControls.add(new JLabel("Filter by Category:"));
        filterControls.add(filterCategoryCombo);
        
        JButton clearFilterButton = createStyledButton("Clear Filter");
        clearFilterButton.setBackground(new Color(155, 89, 182)); // Purple accent
        clearFilterButton.addActionListener(e -> filterCategoryCombo.setSelectedIndex(0));
        filterControls.add(clearFilterButton);
        
        controlBar.add(filterControls, BorderLayout.WEST);

        // 2. CENTER: Main Action Buttons
        JPanel actionButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        actionButtons.setBackground(Color.WHITE);
        actionButtons.add(refreshButton);
        actionButtons.add(editTeamButton); 
        
        controlBar.add(actionButtons, BorderLayout.CENTER);

        // 3. RIGHT: Remove Team Controls
        controlBar.add(createRemoveTeamPanel(), BorderLayout.EAST);
        
        topContainer.add(controlBar, BorderLayout.CENTER);
        
        mainPanel.add(topContainer, BorderLayout.NORTH);

        // --- CENTER: Team Table ---
        JScrollPane tableScrollPane = new JScrollPane(teamTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 1), 
            "Team Rankings (Sorted by Overall Score)", 
            TitledBorder.LEFT, TitledBorder.TOP, HEADER_FONT.deriveFont(Font.BOLD, 14), PRIMARY_COLOR
        ));
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);
        
        return mainPanel;
    }
    
    private JPanel createAdminToolsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 20, 20));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        panel.add(createCategoryManagementPanel());
        panel.add(createJudgeAssignmentPanel());
        
        return panel;
    }

    private JPanel createCategoryManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 1), "Category Management", TitledBorder.LEFT, TitledBorder.TOP, HEADER_FONT.deriveFont(Font.BOLD, 14), PRIMARY_COLOR
        ));
        
        JPanel controls = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        controls.setBorder(new EmptyBorder(10, 10, 10, 10));
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        if (categoryCombo == null) {
            categoryCombo = new JComboBox<>();
        }
        
        JButton removeButton = createStyledButton("Remove Selected Category");
        removeButton.setBackground(REMOVE_COLOR);
        removeButton.addActionListener(e -> removeCategoryAction());
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        controls.add(new JLabel("Categories:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        controls.add(categoryCombo, gbc);
        
        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0;
        controls.add(removeButton, gbc);

        newCategoryField = new JTextField(20);
        JButton addButton = createStyledButton("Add New Category");
        addButton.addActionListener(e -> addCategoryAction());

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        controls.add(new JLabel("New Name:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        controls.add(newCategoryField, gbc);
        
        gbc.gridx = 2; gbc.gridy = 1; gbc.weightx = 0;
        controls.add(addButton, gbc);
        
        panel.add(controls, BorderLayout.NORTH);
        
        refreshCategoryList();
        return panel;
    }
    
    private void refreshCategoryList() {
        if (categoryCombo != null) {
            categoryCombo.removeAllItems();
            for (String category : manager.getAvailableCategories()) {
                categoryCombo.addItem(category);
            }
        }
    }
    
    private void addCategoryAction() {
        String newCategory = newCategoryField.getText().trim();
        if (newCategory.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Category name cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (manager.addCategory(newCategory)) {
            JOptionPane.showMessageDialog(this, "Category '" + newCategory + "' added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            newCategoryField.setText("");
            refreshCategoryList();
            populateFilterCombo(); 
        } else {
            JOptionPane.showMessageDialog(this, "Category already exists or failed to add.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void removeCategoryAction() {
        String selectedCategory = (String) categoryCombo.getSelectedItem();
        if (selectedCategory == null) return;
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to remove category '" + selectedCategory + "'?\n(This will fail if any team is currently using it.)", 
            "Confirm Removal", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (manager.removeCategory(selectedCategory)) {
                JOptionPane.showMessageDialog(this, "Category removed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshCategoryList();
                populateFilterCombo(); 
            } else {
                JOptionPane.showMessageDialog(this, "Failed to remove category. It might be currently in use by a team.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private JPanel createJudgeAssignmentPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 1), "Judge Assignment", TitledBorder.LEFT, TitledBorder.TOP, HEADER_FONT.deriveFont(Font.BOLD, 14), PRIMARY_COLOR
        ));

        JPanel controlPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        controlPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        teamAssignCombo = new JComboBox<>();
        judgeAssignCombo = new JComboBox<>();

        JButton assignButton = createStyledButton("Assign Judge");
        assignButton.addActionListener(e -> assignJudgeAction());

        controlPanel.add(new JLabel("Select Team (ID - Name):"));
        controlPanel.add(teamAssignCombo);
        controlPanel.add(new JLabel("Select Judge:"));
        controlPanel.add(judgeAssignCombo);
        controlPanel.add(new JLabel("")); 
        controlPanel.add(assignButton);
        
        assignmentDisplayArea = new JTextArea();
        assignmentDisplayArea.setEditable(false);
        assignmentDisplayArea.setFont(MONOSPACE_FONT);
        JScrollPane scrollPane = new JScrollPane(assignmentDisplayArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Current Judge Assignments"));

        panel.add(controlPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void refreshAssignmentCombos() {
        teamAssignCombo.removeAllItems();
        List<AbstractHackathonTeam> sortedTeams = manager.getTeamList().getAllTeams();
        sortedTeams.sort(Comparator.comparingInt(AbstractHackathonTeam::getTeamNumber)); 

        for (AbstractHackathonTeam team : sortedTeams) {
            teamAssignCombo.addItem(team.getTeamNumber() + " - " + team.getTeamName());
        }
        
        judgeAssignCombo.removeAllItems();
        for (String judge : manager.getAvailableJudges()) {
            judgeAssignCombo.addItem(judge);
        }
    }
    
    private void assignJudgeAction() {
        String selectedTeamString = (String) teamAssignCombo.getSelectedItem();
        String selectedJudge = (String) judgeAssignCombo.getSelectedItem();
        
        if (selectedTeamString == null || selectedJudge == null) {
            JOptionPane.showMessageDialog(this, "Please select both a Team and a Judge.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            int teamId = Integer.parseInt(selectedTeamString.split(" - ")[0]);
            
            manager.assignJudge(teamId, selectedJudge);
            JOptionPane.showMessageDialog(this, selectedJudge + " assigned to Team ID " + teamId + " successfully.", "Assignment Success", JOptionPane.INFORMATION_MESSAGE);
            refreshAssignmentDisplay();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error parsing Team ID.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshAssignmentDisplay() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-10s %-30s %s%n", "TEAM ID", "TEAM NAME", "ASSIGNED JUDGE"));
        sb.append("------------------------------------------------------------------------\n");
        
        Map<Integer, String> assignments = manager.getJudgeAssignments();
        
        List<AbstractHackathonTeam> sortedTeams = manager.getTeamList().getAllTeams();
        sortedTeams.sort(Comparator.comparingInt(AbstractHackathonTeam::getTeamNumber));

        for (AbstractHackathonTeam team : sortedTeams) {
            String judge = assignments.getOrDefault(team.getTeamNumber(), "Unassigned");
            sb.append(String.format("%-10d %-30s %s%n", team.getTeamNumber(), team.getTeamName(), judge));
        }
        
        assignmentDisplayArea.setText(sb.toString());
    }
    
    private AbstractHackathonTeam getTeamById(int teamId) {
        for (AbstractHackathonTeam team : manager.getTeamList().getAllTeams()) {
            if (team.getTeamNumber() == teamId) {
                return team;
            }
        }
        return null;
    }
    
    private void editTeamAction() {
        int selectedRow = teamTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a team from the table to edit.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int teamId = (int) tableModel.getValueAt(selectedRow, 0);
            AbstractHackathonTeam team = getTeamById(teamId);

            if (team == null) {
                JOptionPane.showMessageDialog(this, "Selected team not found in data.", "Error", JOptionPane.ERROR_MESSAGE);
                loadData();
                return;
            }

            JTextField nameField = new JTextField(team.getTeamName());
            JTextField universityField = new JTextField(team.getUniversity());
            JComboBox<String> categoryField = new JComboBox<>();
            JCheckBox eligibilityCheck = new JCheckBox("Eligible", team.isEligibility());

            for (String category : manager.getAvailableCategories()) {
                categoryField.addItem(category);
            }
            categoryField.setSelectedItem(team.getCategory());

            JPanel editPanel = new JPanel(new GridLayout(0, 2, 10, 5));
            editPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            editPanel.add(new JLabel("Team ID:"));
            editPanel.add(new JLabel(String.valueOf(teamId)));
            
            editPanel.add(new JLabel("Team Name:"));
            editPanel.add(nameField);
            
            editPanel.add(new JLabel("University:"));
            editPanel.add(universityField);
            
            editPanel.add(new JLabel("Category:"));
            editPanel.add(categoryField);
            
            editPanel.add(new JLabel("Eligibility:"));
            editPanel.add(eligibilityCheck);

            int result = JOptionPane.showConfirmDialog(this, editPanel, 
                    "Edit Details for Team ID " + teamId, JOptionPane.OK_CANCEL_OPTION, 
                    JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                String newName = nameField.getText().trim();
                String newUniversity = universityField.getText().trim();
                String newCategory = (String) categoryField.getSelectedItem();
                boolean newEligibility = eligibilityCheck.isSelected();

                if (newName.isEmpty() || newUniversity.isEmpty() || newCategory == null) {
                    JOptionPane.showMessageDialog(this, "Team Name and University must be filled.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                boolean success = manager.updateTeamDetails(teamId, newName, newUniversity, newCategory, newEligibility);

                if (success) {
                    JOptionPane.showMessageDialog(this, "Team " + teamId + " details updated successfully.", "Update Success", JOptionPane.INFORMATION_MESSAGE);
                    loadData(); 
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update team details. Check Manager implementation.", "Update Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "An error occurred during team editing: " + e.getMessage(), "System Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateFilterCombo() {
        String selected = (String) filterCategoryCombo.getSelectedItem();
        filterCategoryCombo.removeAllItems();
        
        filterCategoryCombo.addItem("ALL CATEGORIES"); 
        
        for (String category : manager.getAvailableCategories()) {
            filterCategoryCombo.addItem(category);
        }
        
        if (selected != null && filterCategoryCombo.getItemCount() > 1 && filterCategoryCombo.isEditable()) {
            filterCategoryCombo.setSelectedItem(selected);
        } else {
             filterCategoryCombo.setSelectedIndex(0);
        }
    }
    
    private void refreshTable(String categoryFilter) {
        tableModel.setRowCount(0);

        List<AbstractHackathonTeam> allTeams = manager.getTeamList().getTeamsSortedByScore();
        
        List<AbstractHackathonTeam> filteredTeams = allTeams.stream()
            .filter(team -> categoryFilter == null || "ALL CATEGORIES".equals(categoryFilter) || team.getCategory().equals(categoryFilter))
            .toList();
            
        for (AbstractHackathonTeam team : filteredTeams) {
            Object[] row = new Object[] {
                team.getTeamNumber(),
                team.getTeamName(),
                team.getCategory(),
                team.getUniversity(),
                team.isEligibility() ? "Yes" : "No",
                String.format("%.2f", team.getOverallScore())
            };
            tableModel.addRow(row);
        }
    }

    private void loadData() {
        populateFilterCombo();
        
        refreshTable((String) filterCategoryCombo.getSelectedItem());

        updateSummary();
        refreshCategoryList(); 
        refreshAssignmentCombos(); 
        refreshAssignmentDisplay();
    }
    
    private void updateSummary() {
        AbstractHackathonTeam topTeam = manager.getTeamList().getTopTeam();
        double avgScore = manager.getTeamList().calculateAverageOverallScore();
        int totalTeams = manager.getTeamList().getAllTeams().size();
        
        StringBuilder summary = new StringBuilder();
        summary.append("--- HACKATHON OVERVIEW ---\n");
        summary.append(String.format("Total Teams: %d\n", totalTeams));
        summary.append(String.format("Average Overall Score: %.2f / 5.0\n", avgScore));
        if (topTeam != null) {
            summary.append(String.format("Top Team: %s (ID %d) - Score: %.2f\n", 
                topTeam.getTeamName(), topTeam.getTeamNumber(), topTeam.getOverallScore()));
        } else {
            summary.append("Top Team: N/A\n");
        }
        
        summaryArea.setText(summary.toString());
    }
    
    private void removeTeam() {
        try {
            int teamNum = Integer.parseInt(removeTeamIdField.getText().trim());
            
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to PERMANENTLY REMOVE Team " + teamNum + "?", 
                "Confirm Removal", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                if (manager.removeTeam(teamNum)) {
                    JOptionPane.showMessageDialog(this, "Team " + teamNum + " successfully removed.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    removeTeamIdField.setText("");
                    loadData(); 
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to remove team. Check Team ID.", "Removal Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric Team ID to remove.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
