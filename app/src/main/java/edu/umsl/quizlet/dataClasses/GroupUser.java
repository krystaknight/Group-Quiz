package edu.umsl.quizlet.dataClasses;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by harprabh on 4/30/17.
 * This Object is for the the users in the group
 */

public class GroupUser {

    private String userID;
    private String email;
    private String firstName;
    private String lastName;
    private String singleQuizStatus;
    private boolean leader; // true if leader


    public GroupUser(String userID, String email, String firstName, String lastName, String singleQuizStatus, boolean leader){
        this.userID = userID;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.singleQuizStatus = singleQuizStatus;
        this.leader = leader;
    }

    public GroupUser(JSONObject jsonObject) {
        try {
            this.userID = jsonObject.getString("userID");
            this.email = jsonObject.getString("email");
            this.firstName = jsonObject.getString("first");
            this.lastName = jsonObject.getString("last");
            this.singleQuizStatus = "";
            this.leader = false;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getSingleQuizStatus() {
        return singleQuizStatus;
    }

    public void setSingleQuizStatus(String singleQuizStatus) {
        this.singleQuizStatus = singleQuizStatus;
    }

    public boolean getLeader() {
        return leader;
    }

    public void setLeader(boolean leader) {
        this.leader = leader;
    }

    public String getUserID() {
        return userID;
    }

    public String getEmail() {
        return email;
    }

    public String getFirst() {
        return firstName;
    }

    public String getLast() {
        return lastName;
    }

}
