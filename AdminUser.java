// AdminUser.java
package hackathonsystem.model;
public class AdminUser extends User {
    public AdminUser(String username, String password) {
        super(username, password, "Admin");
    }
}