package hackathonsystem;

import hackathonsystem.controller.AuthenticationManager;
import hackathonsystem.controller.HackathonManager;
import hackathonsystem.controller.UserRepository;
import hackathonsystem.view.LoginGUIView;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // 1. Initialize Controllers (M and C)
        UserRepository userRepo = new UserRepository();
        HackathonManager hackathonManager = new HackathonManager();
        AuthenticationManager authManager = new AuthenticationManager(userRepo, hackathonManager);

        // 2. Load Initial Data (File I/O)
        hackathonManager.readTeamDataFromFile(); 

        // 3. Launch the Login View (V) with both managers
        SwingUtilities.invokeLater(() -> {
            new LoginGUIView(authManager, hackathonManager).setVisible(true);
        });
    }
}
