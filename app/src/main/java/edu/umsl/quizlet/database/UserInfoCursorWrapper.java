package edu.umsl.quizlet.database;

import android.database.Cursor;
import android.database.CursorWrapper;


import java.sql.Date;
import java.text.Format;
import java.text.SimpleDateFormat;

import edu.umsl.quizlet.dataClasses.Course;
import edu.umsl.quizlet.dataClasses.Group;
import edu.umsl.quizlet.dataClasses.GroupUser;
import edu.umsl.quizlet.dataClasses.User;


/**
 * Created by klkni on 4/6/2017.
 */

public class UserInfoCursorWrapper extends CursorWrapper {
    public UserInfoCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    //USER
    public User getUser() {
        String id = getString(getColumnIndex(QuizletDbSchema.UserTable.Columns._ID));
        String firstName = getString(getColumnIndex(QuizletDbSchema.UserTable.Columns.FIRST_NAME));
        String lastName = getString(getColumnIndex(QuizletDbSchema.UserTable.Columns.LAST_NAME));
        String email = getString(getColumnIndex(QuizletDbSchema.UserTable.Columns.EMAIL));
        String userId = getString(getColumnIndex(QuizletDbSchema.UserTable.Columns.USER_ID));

        return new User(id, userId, email, firstName, lastName);
    }


    //COURSE
    public Course getCourse() {
        String id = getString(getColumnIndex(QuizletDbSchema.CourseTable.Columns._ID));
        String name = getString(getColumnIndex(QuizletDbSchema.CourseTable.Columns.COURSE_NAME));
        String courseId = getString(getColumnIndex(QuizletDbSchema.CourseTable.Columns.COURSE_ID));
        String extendedId = getString(getColumnIndex(QuizletDbSchema.CourseTable.Columns.EXTENDED_ID));
        String semester = getString(getColumnIndex(QuizletDbSchema.CourseTable.Columns.SEMESTER));
        String instructor = getString(getColumnIndex(QuizletDbSchema.CourseTable.Columns.INSTRUCTOR));

        return new Course(id, courseId, extendedId, name, semester, instructor);
    }

    public Group getGroup() {
        String id = getString(getColumnIndex(QuizletDbSchema.GroupTable.Columns.GROUPID));
        String name = getString(getColumnIndex(QuizletDbSchema.GroupTable.Columns.GROUPNAME));

        return new Group(id,name);

    }

    public GroupUser getGroupUser() {
        String id = getString(getColumnIndex(QuizletDbSchema.GroupUserTable.Columns.USER_ID));
        String email = getString(getColumnIndex(QuizletDbSchema.GroupUserTable.Columns.EMAIL));
        String firstName = getString(getColumnIndex(QuizletDbSchema.GroupUserTable.Columns.FIRST_NAME));
        String lastName = getString(getColumnIndex(QuizletDbSchema.GroupUserTable.Columns.LAST_NAME));
        String status = getString(getColumnIndex(QuizletDbSchema.GroupUserTable.Columns.SINGLEQUIZSTATUS));
        boolean leader = (getInt(getColumnIndex(QuizletDbSchema.GroupUserTable.Columns.LEADER)) == 1);

        return new GroupUser(id, email, firstName, lastName,status,leader);
    }

}
