/**
 * Created by Iain Woodburn on 30-Oct-15.
 */

public class LaunchServer {

    /**
     * This main method is for testing purposes only.
     * @param args - the command line arguments
     */
    public static void main(String[] args) {
        // Create a ChatServer and start it
        (new ChatServer(new User[3], 10)).run();
    } //end main

} //end LaunchServer
