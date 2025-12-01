package hackathonsystem.model;

import java.util.Arrays;

public class WebDevTeam extends AbstractHackathonTeam {
    private boolean usesModernFramework; 

    public WebDevTeam(int teamNumber, String teamName, String category, String university, boolean eligibility, String initials, int[] scores, boolean usesModernFramework) {
        super(teamNumber, teamName, category, university, eligibility, initials, scores);
        this.usesModernFramework = usesModernFramework;
    }

    @Override
public double getOverallScore() {
    if (!eligibility) {
        return 0.0;
    }
    double totalScore = 0;
    for (int score : scores) {
        totalScore += score;
    }
    if (usesModernFramework) {
        totalScore += 2.5; 
    } else {
        totalScore -= 5.0; 
    }
    totalScore = Math.max(0, totalScore);
    double finalScore = totalScore / 5.0; 
    return Math.min(5.0, finalScore); 
}
    
    @Override
    public String getUniqueAttributeDetails() {
        return String.format("Unique Attribute: Uses Modern Framework: %s", usesModernFramework ? "Yes (Bonus Applied)" : "No (Penalty Applied)");
    }
}
