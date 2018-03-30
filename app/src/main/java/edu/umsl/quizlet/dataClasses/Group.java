package edu.umsl.quizlet.dataClasses;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by landon on 4/4/17.
 */

public class Group {
    private String id;
    private String name;
    private ArrayList<String> courseIDs;
    private ArrayList<GroupUser> groupUsers;

    public Group(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Group(String id, String name, ArrayList<String> courseIDs, ArrayList<GroupUser> groupUsers) {
        this.id = id;
        this.name = name;
        this.courseIDs = courseIDs;
        this.groupUsers = groupUsers;
    }

    public Group(JSONObject jsonObject) {
        try {
            this.id = jsonObject.getString("_id");
            this.name = jsonObject.getString("name");

        } catch (JSONException e) {
            Log.e("JSON Group Exception", e.toString());
            e.printStackTrace();
        }
    }

    private ArrayList<String> getCourseIdsFromJson(JSONArray jsonArray) {
        ArrayList<String> courseIds = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                courseIds.add(jsonArray.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return courseIds;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ArrayList<GroupUser> getGroupUsers() {
        return groupUsers;
    }

    public void setGroupUsers(ArrayList<GroupUser> groupUsers) {
        this.groupUsers = groupUsers;
    }

    public void setAllUsers(JSONObject jsonObject) {
        try {
            this.groupUsers = getUsersFromJson(jsonObject.getJSONArray("users"));
        } catch (Exception e) {
            Log.e("Parse Exception", e.toString());
            e.printStackTrace();
        }
    }

    private ArrayList<GroupUser> getUsersFromJson(JSONArray jsonArray) {
        ArrayList<GroupUser> groupUsers = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                groupUsers.add(new GroupUser(jsonArray.getJSONObject(i)));
            }
        } catch (JSONException e) {
            Log.e("JSON Exception", e.toString());
            e.printStackTrace();
        }
        return groupUsers;
    }

    public void setGroupUsers(JSONArray jsonArray) {
        ArrayList<GroupUser> users = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                users.add(new GroupUser(jsonArray.getJSONObject(i)));
            } catch (JSONException e) {
                Log.e("JSON Exception", e.toString());
                e.printStackTrace();
            }
        }
        this.groupUsers = users;
    }
}