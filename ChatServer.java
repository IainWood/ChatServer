/**
 * @author Iain Woodburn, iwoodbur@purdue.edu, LC4 30-Oct-15.
 * @author Yangtao Hu, hu385@purdue.edu, LC4 30-Oct-15
 */

import java.util.Scanner;
import java.util.Random;
import java.util.ArrayList;

public class ChatServer {

    private User[] users;
    private int maxMessages;
    private CircularBuffer buffer;
    private int numOfUsers = 0; //always default root user
    private ArrayList<Long> usedIDs = new ArrayList<>();

    public ChatServer(User[] usersArr , int maxMessages) {
        this.users = new User[usersArr.length + 1];

        for (int i = 0; i < usersArr.length; i++) {
            users[i] = usersArr[i];
        } //end for

        long defaultID = generateLong();
        users[users.length - 1] = new User("root" , "cs180" , null);
        usedIDs.add(defaultID);
        this.maxMessages = maxMessages;
        this.buffer = new CircularBuffer(this.maxMessages);
    } //end ChatServer

    /**
     * First element is cookie ID
     * Second element is username
     * Third element is password
     */
    public String addUser(String[] args) {

        if (args[0] == null || args[1] == null || args[2] == null || args[3] == null) {
            return MessageFactory.makeErrorMessage(10);
        } //end if

        String username = args[2];
        String password = args[3].trim();

        if (username.length() < 1 || username.length() > 20
                || password.length() < 4 || password.length() > 40) {

            for (int i = 0; i < users.length; i++) {

                if (users[i] != null && users[i].getName().equals(username)) {
                    return MessageFactory.makeErrorMessage(22);
                } else {
                    return MessageFactory.makeErrorMessage(24);
                } //end if

            } //end for

        } //end if

        for (int i = 0; i < users.length; i++) {

            if (users[i] != null && users[i].getName().equals(username)) {
                return MessageFactory.makeErrorMessage(22);
            } //end if

        } //end for

        for (int i = 0; i < username.length(); i++) {

            if (!Character.isLetterOrDigit(username.charAt(i))) {
                return MessageFactory.makeErrorMessage(24);
            } //end if

        } //end for

        for (int i = 0; i < password.length(); i++) {

            if (!Character.isLetterOrDigit(password.charAt(i))) {
                return MessageFactory.makeErrorMessage(24);
            } //end if

        } //end for

        users[numOfUsers++] = new User(username , password , null);
        return "SUCCESS\r\n";
    } //end addUser

    /**
     * First element is username
     * Second element is password
     */
    public String userLogin(String[] args) {

        if (args[0] == null || args [1] == null || args[2] == null) {
            return MessageFactory.makeErrorMessage(10);
        } //end if

        String username = args[1].trim();
        String password = args[2].trim();
        User temp = new User();

        long cookieID = generateLong();
        usedIDs.add(cookieID);

        for (int i = 0; i < users.length; i++) {

            if (users[i] != null && username.equals(users[i].getName())) {

                if (users[i].getCookie() != null) {
                    return MessageFactory.makeErrorMessage(25);
                } //end if

                temp = users[i];
                users[i].setCookie(new SessionCookie(cookieID));
                break;
            } else if (i == users.length - 1) {
                return MessageFactory.makeErrorMessage(20);
            } //end if

        } //end for

        if (!temp.checkPassword(password)) {
            return MessageFactory.makeErrorMessage(21);
        } //end if

        return "SUCCESS\t" + String.format("%04d", cookieID) + "\r\n";
    } //end userLogin

    /**
     * First element is cookie ID
     * Second element is messages
     */
    public String postMessage(String[] args , String name) {

        //String cookie = args[1];
        String message = args[2];

        if (message == null || message.trim().equals("") ) {
            return MessageFactory.makeErrorMessage(24);
        } //end if

        buffer.put(name + ": " + message.trim());

        return "SUCCESS\r\n";
    } //end postMessage

    /**
     * First element is cookie ID
     * Second element is numMessages
     */
    public String getMessages(String[] args) {

        int numOfMessages = 0;

        try {
            numOfMessages = Integer.parseInt(args[2].trim());
        } catch (Exception e) {
            return MessageFactory.makeErrorMessage(24);
        } //end try

        String messages = "";
        String[] arr;

        if (numOfMessages < 1) {
            return MessageFactory.makeErrorMessage(24);
        } //end if

        arr = buffer.getNewest(numOfMessages);

        for (int i = 0; i < arr.length; i++) {

            if (arr[i] != null && i == arr.length - 1) {
                messages += arr[i];
            } else if (arr[i] != null && i < numOfMessages) {
                messages += arr[i] + "\t";
            } //end if

        } //end for

        return "SUCCESS\t" + messages + "\r\n";
    } //end getMessages

    public boolean userExists(User user) {

        for (int i = 0; i < users.length; i++) {

            if (user.getName().equals(users[i].getName())) {
                return true;
            } //end if

        } //end for
        return false;
    } //end userExists

    public boolean cookieIsValid(SessionCookie cookie) {

        for (int i = 0; i < users.length; i++) {

            if (users[i].getCookie().equals(cookie)) {
                return true;
            } //end if

        } //end for

        return false;
    } //end cookieIsValid

    public long generateLong() {
        Random rand = new Random();
        boolean done = false;
        long range = 9999L;
        long id = 0;
        while (!done) {
            id = (long)(rand.nextDouble() * range);
            if (!usedIDs.contains(id) && id >= 0) {
                done = true;
            } //end if

        } //end while
        return id;
    } //end generateLong

    /**
     * This method begins server execution.
     */
    public void run() {
        boolean verbose = false;
        System.out.printf("The VERBOSE option is off.\n\n");
        Scanner in = new Scanner(System.in);

        while (true) {
            System.out.printf("Input Server Request: ");
            String command = in.nextLine();

            // this allows students to manually place "\r\n" at end of command
            // in prompt
            command = replaceEscapeChars(command);

            if (command.startsWith("kill"))
                break;

            if (command.startsWith("verbose")) {
                verbose = !verbose;
                System.out.printf("VERBOSE has been turned %s.\n\n", verbose ? "on" : "off");
                continue;
            }

            String response = null;
            try {
                response = parseRequest(command);
            } catch (Exception ex) {
                response = MessageFactory.makeErrorMessage(MessageFactory.UNKNOWN_ERROR,
                        String.format("An exception of %s occurred.", ex.getMessage()));
                ex.printStackTrace();
            }

            // change the formatting of the server response so it prints well on
            // the terminal (for testing purposes only)
            if (response.startsWith("SUCCESS\t"))
                response = response.replace("\t", "\n");

            // print the server response
            if (verbose)
                System.out.printf("response:\n");
            System.out.printf("\"%s\"\n\n", response);
        }

        in.close();
    }

    /**
     * Replaces "poorly formatted" escape characters with their proper values.
     * For some terminals, when escaped characters are entered, the terminal
     * includes the "\" as a character instead of entering the escape character.
     * This function replaces the incorrectly inputed characters with their
     * proper escaped characters.
     *
     * @param str
     *            - the string to be edited
     * @return the properly escaped string
     */
    private static String replaceEscapeChars(String str) {
        str = str.replace("\\r", "\r");
        str = str.replace("\\n", "\n");
        str = str.replace("\\t", "\t");

        return str;
    }

    /**
     * Determines which client command the request is using and calls the
     * function associated with that command.
     *
     * @param request
     *            - the full line of the client request (CRLF included)
     * @return the server response
     */
    public String parseRequest(String request) {

        String[] tokens = request.split("\t");
        User currentUser = new User();

        switch (tokens[0].trim()) {
            case "ADD-USER":

                if (tokens.length != 4) {
                    return MessageFactory.makeErrorMessage(10);
                } //end if

                if (tokens[1] != null && (Long.parseLong(tokens[1]) < 0 || Long.parseLong(tokens[1]) > 9999)) {
                    return MessageFactory.makeErrorMessage(24);
                } //end if

                return addUser(new String[] {tokens[0] , tokens[1] , tokens[2] , tokens[3]});

            case "USER-LOGIN":

                if (tokens.length != 3) {
                    return MessageFactory.makeErrorMessage(10);
                } //end if

                return userLogin(new String[] {tokens[0] , tokens[1] , tokens[2]});

            case "POST-MESSAGE":

                if (tokens.length != 3) {
                    return MessageFactory.makeErrorMessage(10);
                } //end if

                for (int i = 0; i < users.length; i++) {

                    try {

                        if (users[i] != null && users[i].getCookie() != null
                                && users[i].getCookie().getID() == Long.parseLong(tokens[1])) {
                            currentUser = users[i];
                            break;
                        } else if (Long.parseLong(tokens[1]) > 9999 || Long.parseLong(tokens[1]) < 0) {
                            return MessageFactory.makeErrorMessage(24);
                        } else if (i == users.length - 1) {
                            return MessageFactory.makeErrorMessage(23);
                        } //end if

                    } catch (Exception e) {
                        MessageFactory.makeErrorMessage(24);
                    } //end try

                } //end for

                if (currentUser.getCookie() == null) {
                    return MessageFactory.makeErrorMessage(24);
                } //end if

                if (tokens[2].trim().equals("")) {
                    return MessageFactory.makeErrorMessage(24);
                } //end if

                if (!currentUser.getCookie().hasTimedOut()) {
                    currentUser.getCookie().updateTimeOfActivity();
                    return postMessage(new String[] {tokens[0] , tokens[1] , tokens[2]} , currentUser.getName());
                } else {
                    //currentUser.setCookie(null);
                    return MessageFactory.makeErrorMessage(05);
                }

            case "GET-MESSAGES":

                if (tokens.length != 3) {
                    return MessageFactory.makeErrorMessage(10);
                } //end if

                for (int i = 0; i < users.length; i++) {

                    if (users[i] != null && users[i].getCookie() != null
                            && users[i].getCookie().getID() == Long.parseLong(tokens[1])) {
                        currentUser = users[i];
                        break;
                    } //end if
                } //end for

                if (currentUser.getCookie() == null) {
                    return MessageFactory.makeErrorMessage(23);
                } //end if

                if (currentUser.getCookie().hasTimedOut()) {
                    currentUser.setCookie(null);
                    return MessageFactory.makeErrorMessage(05);
                } //end if

                return getMessages(new String[] {tokens[0] , tokens[1] , tokens[2]});

            default:
                return MessageFactory.makeErrorMessage(11);
        } //end switch

    }

} //end ChatServer
