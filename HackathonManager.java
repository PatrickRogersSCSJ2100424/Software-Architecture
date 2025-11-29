package hackathonsystem.controller;

import hackathonsystem.model.*;
import java.io.*;
import java.util.*;

public class HackathonManager {
    private TeamList teamList;
    private final String dataFilePath = "HackathonTeams.csv"; 
    private final String reportFilePath = "HackathonReport.txt";

    public HackathonManager() {
        this.teamList = new TeamList();
    }

    public boolean updateTeamScores(int teamNumber, int[] newScores) {
        AbstractHackathonTeam team = teamList.findTeamByNumber(teamNumber);
        if (team != null && newScores != null && newScores.length == 5) {
            for(int score : newScores) {
                if (score < 0 || score > 10) return false;
            }
            team.setScores(newScores);
            return true;
        }
        return false;
    }

    public boolean updateTeamDetails(int teamNumber, String newName, String newUniversity) {
        AbstractHackathonTeam team = teamList.findTeamByNumber(teamNumber);
        if (team != null) {
            team.setTeamName(newName);
            team.setUniversity(newUniversity);
            // This version intentionally does not update category or eligibility
            return true;
        }
        return false;
    }

    public boolean updateTeamDetails(int teamNumber, String newName, String newUniversity, String newCategory, boolean newEligibility) {
        AbstractHackathonTeam team = teamList.findTeamByNumber(teamNumber);
        if (team != null) {
            team.setTeamName(newName);
            team.setUniversity(newUniversity);
            
            // Requires setCategory and setEligibility in AbstractHackathonTeam
            team.setCategory(newCategory);  
            team.setEligibility(newEligibility); 
            
            return true;
        }
        return false;
    }

    public boolean disqualifyTeam(int teamNumber) {
        AbstractHackathonTeam team = teamList.findTeamByNumber(teamNumber);
        if (team != null) {
            team.setEligibility(false);
            return true;
        }
        return false;
    }

    public boolean removeTeam(int teamNumber) {
        int initialSize = teamList.getAllTeams().size();
        teamList.removeTeam(teamNumber);
        return teamList.getAllTeams().size() < initialSize;
    }

    public List<String> getAvailableCategories() {
        return teamList.getAvailableCategories();
    }

    public boolean addCategory(String category) {
        return teamList.addCategory(category);
    }

    public boolean removeCategory(String category) {
        return teamList.removeCategory(category);
    }

    public List<String> getAvailableJudges() {
        return teamList.getAvailableJudges();
    }
    
    public Map<Integer, String> getJudgeAssignments() {
        return teamList.getJudgeAssignments();
    }
    
    public void assignJudge(int teamNumber, String judgeName) {
        teamList.assignJudgeToTeam(teamNumber, judgeName);
    }

    // --- File I/O Logic ---

    public void readTeamDataFromFile() {
        System.out.println("\n--- Starting CSV File Reading ---");
        try (BufferedReader br = new BufferedReader(new FileReader(dataFilePath))) {
            String line;
            int lineNumber = 0;
            
            while ((line = br.readLine()) != null) {
                lineNumber++;

                line = line.replace("\uFEFF", "").trim();
                if (line.isEmpty() || line.startsWith("Team ID")) continue;

                String[] parts = line.split(",");

                if (parts.length < 9) { 
                    System.err.println("Line " + lineNumber + ": Skipping line due to insufficient fields (< 9): " + line);
                    continue;
                }
                
                try {
                    int teamNumber = Integer.parseInt(parts[0].trim());
                    String teamName = parts[1].trim();
                    String university = parts[2].trim();  
                    String category = parts[3].trim();    
                    boolean eligibility = true; 

                    String initials = (teamName.contains(" ") ? 
                                             (teamName.substring(0, 1) + teamName.substring(teamName.lastIndexOf(" ") + 1, teamName.lastIndexOf(" ") + 2)) 
                                             : teamName.substring(0, Math.min(2, teamName.length()))).toUpperCase();
                    
                    int[] scores = new int[5]; 
                    scores[0] = Integer.parseInt(parts[5].trim()); 
                    scores[1] = Integer.parseInt(parts[6].trim()); 
                    scores[2] = Integer.parseInt(parts[7].trim()); 
                    scores[3] = Integer.parseInt(parts[8].trim()); 
                    scores[4] = Integer.parseInt(parts[9].trim());
                    
                    // Simple Unique Attribute Logic (based on team name content)
                    boolean csAttribute = teamName.toLowerCase().contains("secure") || category.toLowerCase().contains("secure");
                    boolean wdAttribute = teamName.toLowerCase().contains("web") || category.toLowerCase().contains("web") || teamName.toLowerCase().contains("dev");
                    
                    AbstractHackathonTeam team;
                    
                    // Assuming WebDevTeam and CybersecurityTeam classes exist and have constructors matching the implementation below
                    if (category.equalsIgnoreCase("Cybersecurity")) {
                        // Placeholder/Fallback implementation for compilation
                        team = new WebDevTeam(teamNumber, teamName, category, university, eligibility, initials, scores, false); 
                    } else if (category.equalsIgnoreCase("Web Development")) {
                        team = new WebDevTeam(teamNumber, teamName, category, university, eligibility, initials, scores, wdAttribute);
                    } else {
                        // Default to WebDevTeam or another generic team type if the category is unknown
                        team = new WebDevTeam(teamNumber, teamName, category, university, eligibility, initials, scores, false); 
                    }
                    
                    teamList.addTeam(team);
                    
                } catch (NumberFormatException e) { 
                    System.err.println("Line " + lineNumber + ": Error parsing number field in line: " + line);
                } catch (Exception e) {
                    System.err.println("Line " + lineNumber + ": General error processing line: " + line + ". Details: " + e.getMessage());
                }
            }
            System.out.println("--- File reading complete. Total teams loaded: " + teamList.getAllTeams().size() + " ---");
            
        } catch (FileNotFoundException e) {
            System.err.println("\n*** FATAL ERROR: Input file not found at path: " + dataFilePath + " ***");
            System.err.println("Ensure HackathonTeams.csv is in the project's root folder.");
        } catch (IOException e) {
            System.err.println("Error: An I/O error occurred while reading the file. Details: " + e.getMessage());
        }
    }
    
    // Generates a report and writes it to a text file.
    public void generateReportAndWriteToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(reportFilePath))) {
            bw.write("--- HACKATHON SYSTEM FINAL REPORT ---\n");
            bw.write("Generated on: " + new Date() + "\n");
            bw.write("Total Teams Processed: " + teamList.getAllTeams().size() + "\n");
            bw.write("Average Overall Score: " + String.format("%.2f", teamList.calculateAverageOverallScore()) + " (max 5.0)\n");
            
            AbstractHackathonTeam topTeam = teamList.getTopTeam();
            if (topTeam != null) {
                bw.write("Top Scoring Team: " + topTeam.getTeamName() + " (" + topTeam.getTeamNumber() + ") with a score of " + String.format("%.2f", topTeam.getOverallScore()) + "\n");
            } else {
                bw.write("No teams available to calculate top team.\n");
            }
            
            bw.write("\n--- Score Distribution (Individual Score Frequency 0-10) ---\n");
            int[] frequency = teamList.calculateScoreFrequency();
            bw.write("Score | Count\n");
            bw.write("------|------\n");
            for (int i = 0; i < frequency.length; i++) {
                bw.write(String.format("%5d | %5d\n", i, frequency[i]));
            }
            
            bw.write("\n--- Full Team List (Ranked by Overall Score) ---\n");
            bw.write(String.format("%-5s | %-15s | %-30s | %-15s | %-15s | %-10s | %-20s\n", 
                "Rank", "ID", "Name", "Category", "Eligibility", "Score", "Assigned Judge"));
            bw.write("-----------------------------------------------------------------------------------------------------------------\n"); 
            
            List<AbstractHackathonTeam> rankedTeams = teamList.getTeamsSortedByScore();
            
            for (int i = 0; i < rankedTeams.size(); i++) {
                AbstractHackathonTeam team = rankedTeams.get(i);
                String judge = teamList.getJudgeForTeam(team.getTeamNumber()); 
                bw.write(String.format("%-5d | %-15d | %-30s | %-15s | %-15s | %-10.2f | %-20s\n",
                    (i + 1), 
                    team.getTeamNumber(), 
                    team.getTeamName(), 
                    team.getCategory(), 
                    team.isEligibility() ? "Eligible" : "INELIGIBLE",
                    team.getOverallScore(),
                    judge)); 
            }
            
            System.out.println("Report successfully generated and written to: " + reportFilePath);
        } catch (IOException e) {
             System.err.println("An I/O error occurred while writing the report file. Details: " + e.getMessage());
        }
    }

    public TeamList getTeamList() {
        return teamList;
    }

}
