package hackathonsystem.controller;

import hackathonsystem.model.User;
import hackathonsystem.view.AdminGUIView;
import hackathonsystem.view.JudgeGUIView;
import hackathonsystem.view.StaffGUIView;
import javax.swing.JOptionPane;

/**
 * Handles user login and delegates the launching of the correct GUI view based on the role.
 */
public class AuthenticationManager {
    private UserRepository userRepository;
    private HackathonManager hackathonManager;

    public AuthenticationManager(UserRepository userRepo, HackathonManager hackManager) {
        this.userRepository = userRepo;
        this.hackathonManager = hackManager;
    }

    public User attemptLogin(String username, String password) {
         return userRepository.findUser(username, password);
    }

    public void launchGUIForUser(User authenticatedUser) {
        if (authenticatedUser == null) {
            return;
        }
        
        String role = authenticatedUser.getRole();
        
        switch (role) {
            case "Admin":
            case "Organizer": // Both roles get the highest privileges view
                new AdminGUIView(hackathonManager).setVisible(true);
                break;
            case "Judge":
                new JudgeGUIView(hackathonManager).setVisible(true);
                break;
            case "Staff":
                new StaffGUIView(hackathonManager).setVisible(true);
                break;
            default:
                JOptionPane.showMessageDialog(null, "Role not recognized. Access denied.", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}