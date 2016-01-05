/**
 * @author Iain Woodburn, iwoodbur@purdue.edu, LC4 30-Oct-15.
 * @author Yangtao Hu, hu385@purdue.edu, LC4 30-Oct-15
 */

import java.util.ArrayList;
import java.util.Arrays;

public class CircularBuffer {

    private int size;
    private ArrayList<String> buff = new ArrayList<>();
    private int messageNum = 0;
    private int numOfMessages = 0;

    public CircularBuffer(int size) {
        this.size = size;
    } //end CircularBuffer

    public void put(String message) {

        if (this.messageNum == 9999) {
            this.messageNum = 0;
        } //end if

        buff.add(String.format("%04d", messageNum) + ") " + message);

        if (this.numOfMessages >= this.size) {
            buff.remove(0);
            this.numOfMessages--;
        } //end if
        this.numOfMessages++;
        this.messageNum++;
    } //end CircularBuffer

    public String[] getNewest(int numMessages) {

        ArrayList<String> messages = new ArrayList<>();
        String[] finalMessages;
        int j = 0;

        if (numMessages == 0 ) {
            return new String[0];
        } //end if

        if (numMessages == 1 && !buff.isEmpty()) {
            return new String[] {buff.get(buff.size() - 1)};
        } //end if

        if (numMessages > numOfMessages) {
            buff.trimToSize();
            numMessages = buff.size();
            return getNewest(numOfMessages);
        } //end if

        if (numMessages < 0) {
            return null;
        } //end if

        for (int i = 0; i < numMessages; i++) {
            //if (messages[i] != null) {
            messages.add(buff.get(buff.size() - 1 - i));
            //}
        } //end for

        finalMessages = new String[messages.size()];

        for (int i = messages.size() - 1; i >= 0; i--) {

            if (buff.get(i) != null) {
                finalMessages[i] = messages.get(j);
            } //end if
            j++;
        } //end for

        return finalMessages;

    } //end getNewest

} //end CircularBuffer
