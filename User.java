import java.util.Random;

/**
 * @author Iain Woodburn
 */
public class User {

    private String username;
    private String password;
    private SessionCookie cookie;

    public User() {
        this.username = "root";
        this.password = "cs180";
    } //end User

    public User(String username , String password , SessionCookie cookie) {
        this.username = username;
        this.password = password;
        this.cookie = cookie;
    } //end User

    public String getName() {
        return this.username;
    } //end getName

    public boolean checkPassword(String password) {
        return this.password.equals(password);
    } //end checkPassword

    public SessionCookie getCookie() {
        return this.cookie;
    } //end getCookie

    public void setCookie(SessionCookie cookie) {
        this.cookie = cookie;
    } //end setCookie

} //end User
