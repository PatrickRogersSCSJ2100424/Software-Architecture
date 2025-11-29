package hackathonsystem.model;
public class StaffUser extends User {
    public StaffUser(String username, String password) {
        super(username, password, "Staff");
    }
}