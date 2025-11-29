// JudgeUser.java
package hackathonsystem.model;
public class JudgeUser extends User {
    public JudgeUser(String username, String password) {
        super(username, password, "Judge");
    }
}