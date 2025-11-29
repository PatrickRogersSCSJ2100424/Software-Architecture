package hackathonsystem.controller;

import hackathonsystem.model.User;
import hackathonsystem.model.AdminUser;
import hackathonsystem.model.JudgeUser;
import hackathonsystem.model.StaffUser;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages user accounts and authentication data.
 */
public class UserRepository {
    private List<User> users;

    public UserRepository() {
        this.users = new ArrayList<>();
        initializeUsers();
    }

    private void initializeUsers() {
        // Admin/Organizer: Full Access
        users.add(new AdminUser("admin", "admin123"));
        users.add(new AdminUser("organizer", "orgpass"));
        
        // Judge: Scoring Access
        users.add(new JudgeUser("judge1", "judgepass"));
        users.add(new JudgeUser("judge2", "judgepass"));
        
        // Staff: Details and Eligibility Access
        users.add(new StaffUser("staff1", "staffpass"));
    }

    public User findUser(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(username) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }
}