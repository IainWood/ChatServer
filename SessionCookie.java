/**
 * @author Iain Woodburn, iwoodbur@purdue.edu, LC4 30-Oct-15.
 * @author Yangtao Hu, hu385@purdue.edu, LC4 30-Oct-15
 */

import java.util.ArrayList;

public class SessionCookie {
    private long id;
    public static int timeoutLength = 300; //represented in seconds
    private ArrayList<Long> usedIDs = new ArrayList<>();
    private long originalVal;
    private long updatedVal;


    public SessionCookie(long id) {
        this.id = id;
        this.originalVal = System.currentTimeMillis();
    } //end SessionCookie

    public boolean hasTimedOut() {
        updatedVal = System.currentTimeMillis();
        if (this.updatedVal - this.originalVal > timeoutLength * 1000) {
            return true;
        } //end if

        return false;
    } //end hasTimedOut

    public void updateTimeOfActivity() {
        this.originalVal = System.currentTimeMillis();
    } //end updateTimeOfActivity

    public long getID() {
        return this.id;
    } //end getID
    
} //end SessionCookie
