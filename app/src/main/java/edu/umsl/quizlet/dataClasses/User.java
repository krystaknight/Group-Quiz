package edu.umsl.quizlet.dataClasses;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by landon on 4/4/17.
 */

public class User {
    private String id;
    private String userID;
    private String email;
    private String firstName;
    private String lastName;


    public User(String id, String userID, String email, String firstName, String lastName) {
        this.id = id;
        this.userID = userID;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public User(JSONObject jsonObject) {
        try {
            this.id = jsonObject.getString("_id");
            this.userID = jsonObject.getString("userID");
            this.email = jsonObject.getString("email");
            this.firstName = jsonObject.getString("first");
            this.lastName = jsonObject.getString("last");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getId() {
        return id;
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
