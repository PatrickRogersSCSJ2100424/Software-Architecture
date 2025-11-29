package hackathonsystem.model;

import java.util.Arrays;

/**
 * Abstract base class for all hackathon teams.
 * Defines common attributes, the 7-argument constructor, and forces subclasses to implement unique scoring logic.
 */
public abstract class AbstractHackathonTeam {
    
    protected int teamNumber;
    protected String teamName;
    protected String university;
    protected String category;
    protected int[] scores;
    protected boolean eligibility;
    protected String customAttributeDetails; 

    // CONSTRUCTOR (7 arguments - used by the subclasses to initialize common fields)
    public AbstractHackathonTeam(int teamNumber, String teamName, String category, String university, boolean eligibility, String customAttributeDetails, int[] scores) {
        this.teamNumber = teamNumber;
        this.teamName = teamName;
        this.university = university;
        this.category = category;
        this.eligibility = eligibility;
        this.customAttributeDetails = customAttributeDetails;
        
        // Ensure score array is exactly 5 elements long
        if (scores == null || scores.length != 5) {
            this.scores = new int[]{0, 0, 0, 0, 0};
        } else {
            this.scores = scores;
        }
    }

    // MANDATORY ABSTRACT METHOD: Polymorphic scoring calculation (must return 0-5 range).
    public abstract double getOverallScore();
    
    // Concrete Method: Returns detailed info about the team.
    public String getFullDetails() {
        return String.format(
            "Team ID: %d\nName: %s\nUniversity: %s\nCategory: %s\nEligibility: %s\nCustom Detail (Initials): %s\nScores (5 Criteria): %s\nOverall Score (max 5.0): %.2f\n%s",
            teamNumber, teamName, university, 
            category, 
            eligibility ? "Eligible" : "INELIGIBLE",
            getCustomAttributeDetails(),
            Arrays.toString(scores),
            getOverallScore(),
            // Append the unique attribute details from the subclass
            getUniqueAttributeDetails()
        );
    }
    
    // Abstract method placeholder for unique attribute description (to be implemented by subclasses)
    public abstract String getUniqueAttributeDetails();

    // Getters and Setters
    public int getTeamNumber() { return teamNumber; }
    public String getTeamName() { return teamName; }
    public String getUniversity() { return university; }
    public String getCategory() { return category; }
    public int[] getScores() { return scores; }
    public boolean isEligibility() { return eligibility; }
    public String getCustomAttributeDetails() { return customAttributeDetails; } // Initials/Detail
    
    public void setScores(int[] scores) { 
        if (scores != null && scores.length == 5) {
            this.scores = scores;
        }
    }
    public void setEligibility(boolean eligibility) {
        this.eligibility = eligibility;
    }
    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }
    public void setUniversity(String university) {
        this.university = university;
    }
    public void setCategory(String category) {
        this.category = category;
    }

}
