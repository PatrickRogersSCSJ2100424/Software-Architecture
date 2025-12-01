package hackathonsystem.model;

/**
 * Subclass for teams in the Cybersecurity category.
 */
public class CybersecurityTeam extends AbstractHackathonTeam {

    private boolean adheresToSecurityStandards; 

    public CybersecurityTeam(int teamNumber, String teamName, String category, String university, boolean eligibility, String initials, int[] scores, boolean adheresToSecurityStandards) {
        super(teamNumber, teamName, category, university, eligibility, initials, scores);
        this.adheresToSecurityStandards = adheresToSecurityStandards;
    }

    /**
     * Calculates the overall score (0-5) using a weighted average specific to Cybersecurity.
     */
   @Override
public double getOverallScore() {
    if (!eligibility) {
        return 0.0;
    }
    double weightedScore = 
        (scores[0] * 0.10) +  
        (scores[1] * 0.15) +  
        (scores[2] * 0.30) +  
        (scores[3] * 0.25) +  
        (scores[4] * 0.20);   
    if (adheresToSecurityStandards) {
        weightedScore += 0.5; 
    }
    weightedScore = Math.max(0, weightedScore);
    return Math.min(5.0, weightedScore); 
}
    
    @Override
    public String getUniqueAttributeDetails() {
        return String.format("Unique Attribute: Adheres to Security Standards: %s", adheresToSecurityStandards ? "Yes (Bonus Applied)" : "No");
    }
}
