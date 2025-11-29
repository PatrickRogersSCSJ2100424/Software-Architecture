package hackathonsystem.controller;

import hackathonsystem.model.AbstractHackathonTeam;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeamList {
    private List<AbstractHackathonTeam> teams;
    // ⭐ NEW: Field to track the next available ID ⭐
    private int nextTeamId = 101; 

    private final List<String> availableCategories;
    private final List<String> availableJudges;
    private final Map<Integer, String> judgeAssignments;

    public TeamList() {
        this.teams = new ArrayList<>();
        this.availableCategories = new ArrayList<>(Arrays.asList("Cybersecurity", "Web Development", "Mobile Applications", "Artificial Intelligence", "Data Science", "Cloud Computing", "Sustainability Tech"));
        this.availableJudges = new ArrayList<>(Arrays.asList("Judge Alice", "Judge Bob", "Judge Carol", "Judge David"));
        this.judgeAssignments = new HashMap<>();
    }
    
    // ⭐ MODIFIED: Updated to track the highest ID when a team is added ⭐
    public void addTeam(AbstractHackathonTeam team) {
        teams.add(team);
        // Ensure the counter is always ahead of the highest existing team number
        if (team.getTeamNumber() >= this.nextTeamId) {
            this.nextTeamId = team.getTeamNumber() + 1;
        }
    }
    
    // ⭐ NEW: Getter for the next ID (used when creating a new team) ⭐
    public int getNextTeamId() {
        return nextTeamId;
    }

    // ⭐ NEW: Setter to reset the ID counter after loading data from a file ⭐
    public void setNextTeamId(int nextId) {
        this.nextTeamId = nextId;
    }
    
    public AbstractHackathonTeam findTeamByNumber(int teamNumber) {
        for (AbstractHackathonTeam team : teams) {
            if (team.getTeamNumber() == teamNumber) {
                return team;
            }
        }
        return null;
    }

    public List<AbstractHackathonTeam> getAllTeams() {
        return teams;
    }

    public AbstractHackathonTeam getTopTeam() {
        if (teams.isEmpty()) return null;
        return teams.stream()
                .max(Comparator.comparingDouble(AbstractHackathonTeam::getOverallScore))
                .orElse(null);
    }

    public double calculateAverageOverallScore() {
        if (teams.isEmpty()) return 0.0;
        return teams.stream()
                .mapToDouble(AbstractHackathonTeam::getOverallScore)
                .average()
                .orElse(0.0);
    }

    public int[] calculateScoreFrequency() {
        int[] frequency = new int[11];  
        for (AbstractHackathonTeam team : teams) {
            for (int score : team.getScores()) {
                if (score >= 0 && score <= 10) {
                    frequency[score]++;
                }
            }
        }
        return frequency;
    }

    public List<AbstractHackathonTeam> getTeamsSortedByScore() {
        List<AbstractHackathonTeam> sortedTeams = new ArrayList<>(this.teams);
        
        sortedTeams.sort(Comparator.comparingDouble(AbstractHackathonTeam::getOverallScore).reversed());
        
        return sortedTeams;
    }
 
    public void removeTeam(int teamNumber) {
        teams.removeIf(t -> t.getTeamNumber() == teamNumber);
        judgeAssignments.remove(teamNumber); 
    }
    
    public List<String> getAvailableCategories() {
        return availableCategories;
    }

    public boolean addCategory(String category) {
        String normalized = category.trim();
        if (!normalized.isEmpty() && !availableCategories.contains(normalized)) {
            return availableCategories.add(normalized);
        }
        return false;
    }
    
    public boolean removeCategory(String category) {
        String normalized = category.trim();
        boolean isCategoryInUse = teams.stream()
            .anyMatch(t -> t.getCategory().equalsIgnoreCase(normalized));
        
        if (isCategoryInUse) {
            return false; 
        }
        return availableCategories.remove(normalized);
    }

    public List<String> getAvailableJudges() {
        return availableJudges;
    }
    
    public Map<Integer, String> getJudgeAssignments() {
        return judgeAssignments;
    }

    public void assignJudgeToTeam(int teamNumber, String judgeName) {
        judgeAssignments.put(teamNumber, judgeName);
    }

    public String getJudgeForTeam(int teamNumber) {
        return judgeAssignments.getOrDefault(teamNumber, "Unassigned");
    }
}