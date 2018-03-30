package edu.umsl.quizlet.dataClasses;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by landon on 4/4/17.
 */

public class Course {
    private String id;
    private String courseID;
    private String extendedID;
    private String name;
    private String semester;
    private String instructor;

    public Course(String id, String courseID, String extendedID, String name, String semester, String instructor) {
        this.id = id;
        this.courseID = courseID;
        this.extendedID = extendedID;
        this.name = name;
        this.semester = semester;
        this.instructor = instructor;
    }

    // the construction accepts the jsonObjects and the array location of the course in the enrolledCourses array
    public Course(JSONObject jsonObject, int location) {
        try {
            this.id = jsonObject.getJSONArray("enrolledCourses").optJSONObject(location).getString("_id");
            this.courseID = jsonObject.getJSONArray("enrolledCourses").optJSONObject(location).getString("courseId");
            this.extendedID = jsonObject.getJSONArray("enrolledCourses").optJSONObject(location).getString("extendedID");
            this.name = jsonObject.getJSONArray("enrolledCourses").optJSONObject(location).getString("name");
            this.semester = jsonObject.getJSONArray("enrolledCourses").optJSONObject(location).getString("semester");
            this.instructor = jsonObject.getJSONArray("enrolledCourses").optJSONObject(location).getString("instructor");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getId() {
        return id;
    }

    public String getCourseId() {
        return courseID;
    }

    public String getExtendedId() {
        return extendedID;
    }

    public String getName() {
        return name;
    }

    public String getSemester() {
        return semester;
    }

    public String getInstructor() {
        return instructor;
    }
}