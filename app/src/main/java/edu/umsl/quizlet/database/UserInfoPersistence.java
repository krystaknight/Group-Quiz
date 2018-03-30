package edu.umsl.quizlet.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import edu.umsl.quizlet.dataClasses.Course;
import edu.umsl.quizlet.dataClasses.Group;
import edu.umsl.quizlet.dataClasses.GroupUser;
import edu.umsl.quizlet.dataClasses.User;

/**
 * Created by klkni on 4/6/2017.
 */

public class UserInfoPersistence {

    private SQLiteDatabase mDatabase;
    private Context mContext;
    private String clickedCourseId;

    public static UserInfoPersistence sPersistence;

    public static UserInfoPersistence sharedInstance(Context context) {
        if (sPersistence == null) {
            sPersistence = new UserInfoPersistence(context);
        }
        return sPersistence;
    }

    private UserInfoPersistence(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new QuizletDbHelper(context).getWritableDatabase();
    }




    //-------------------------USER-----------------------------------------------------//

    //user query
    private UserInfoCursorWrapper queryUser(String whereClause, String[] whereArgs) {
        Cursor dbCursor = mDatabase.query(QuizletDbSchema.UserTable.NAME, null, whereClause, whereArgs, null, null, null);
        return new UserInfoCursorWrapper(dbCursor);
    }

    //Get User
    public User getUser() {
        User user = null;
        UserInfoCursorWrapper cursor = queryUser(null, null);
        try {
            while (cursor.moveToNext()) {
                user = cursor.getUser();
            }
        } finally {
            cursor.close();
        }
        return user;
    }

    // Adds user to the table
    public void addUser(User user) {
        //First need to check if the user is already in the database
        if (!existsInTable(user.getId(), QuizletDbSchema.UserTable.NAME)) {
            mDatabase.beginTransaction();
            ContentValues contentValues = getUserContentValues(user);
            mDatabase.insert(QuizletDbSchema.UserTable.NAME, null, contentValues); // inserting into the db
            mDatabase.setTransactionSuccessful();
            mDatabase.endTransaction();
        } else {
            Log.e("TAG", "User " + user.getFirst() + " " + user.getLast() + " is already in the table");
        }
    }
//---------------------------GROUP---------------------------------------------------------------//

    public void addGroup(Group group) {
        if (!existsInTable(group.getId(), QuizletDbSchema.GroupTable.NAME)) {
            mDatabase.beginTransaction();
            ContentValues contentValues = getGroupContentValues(group);
            mDatabase.insert(QuizletDbSchema.GroupTable.NAME, null, contentValues);
            mDatabase.setTransactionSuccessful();
            mDatabase.endTransaction();
        } else {
            mDatabase.beginTransaction();
            ContentValues contentValues = getGroupContentValues(group);
            mDatabase.update(QuizletDbSchema.GroupTable.NAME, contentValues, null, null);
            mDatabase.setTransactionSuccessful();
            mDatabase.endTransaction();
        }

        addGroupMembersToDb(group.getGroupUsers());

    }

    private void addGroupMembersToDb(ArrayList<GroupUser> groupUsers) {
        mDatabase.execSQL("delete from " + QuizletDbSchema.GroupUserTable.NAME);
        for (int i = 0; i < groupUsers.size(); i++) {
            mDatabase.beginTransaction();
            ContentValues contentValues = getGroupUserContentValues(groupUsers.get(i));
            mDatabase.insert(QuizletDbSchema.GroupUserTable.NAME, null, contentValues);
            mDatabase.setTransactionSuccessful();
            mDatabase.endTransaction();
        }
    }

    private UserInfoCursorWrapper queryGroup(String whereClause, String[] whereArgs) {
        Cursor dbCursor = mDatabase.query(QuizletDbSchema.GroupTable.NAME, null, whereClause, whereArgs, null, null, null);
        return new UserInfoCursorWrapper(dbCursor);
    }

    public Group getGroup() {
        Group group = null;
        UserInfoCursorWrapper cursor = queryGroup(null, null);
        try {
            while (cursor.moveToNext()) {
                group = cursor.getGroup();

            }
        } finally {
            cursor.close();
        }

        group.setGroupUsers(getGroupUsers());
        return group;
    }

    public void updateGroupLeader(String userId){
        mDatabase.beginTransaction();
        ContentValues contentValues = new ContentValues();
        contentValues.put(QuizletDbSchema.GroupUserTable.Columns.LEADER, 1);
        mDatabase.update(QuizletDbSchema.GroupUserTable.NAME, contentValues, QuizletDbSchema.GroupUserTable.Columns.USER_ID +" = ?",new String[] {userId});
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();
    }

    public void updateGroupUserProgress(String userId, String status){
        mDatabase.beginTransaction();
        ContentValues contentValues = new ContentValues();
        contentValues.put(QuizletDbSchema.GroupUserTable.Columns.SINGLEQUIZSTATUS, status);
        mDatabase.update(QuizletDbSchema.GroupUserTable.NAME, contentValues, QuizletDbSchema.GroupUserTable.Columns.USER_ID +" = ?",new String[] {userId});
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();

    }
    private UserInfoCursorWrapper queryGroupUsers(String whereClause, String[] whereArgs){
        Cursor dbCursor = mDatabase.query(QuizletDbSchema.GroupUserTable.NAME,null,whereClause, whereArgs,null,null,null);
        return new UserInfoCursorWrapper(dbCursor);
    }

    private ArrayList<GroupUser> getGroupUsers() {
        ArrayList<GroupUser> groupUsers = new ArrayList<>();

        UserInfoCursorWrapper cursorWrapper = queryGroupUsers(null,null);
        try{
            while(cursorWrapper.moveToNext()){
                groupUsers.add(cursorWrapper.getGroupUser());
            }
        }finally {
            cursorWrapper.close();
        }
        return groupUsers;
    }


    //--------------------------COURSES-------------------------------------------------------------//

    //Courses Query
    private UserInfoCursorWrapper queryCourses(String whereClause, String[] whereArgs) {
        Cursor dbCursor = mDatabase.query(QuizletDbSchema.CourseTable.NAME, null, whereClause, whereArgs, null, null, null);
        return new UserInfoCursorWrapper(dbCursor);
    }


    //Get All Courses for User
    public List<Course> getCourses() {
        List<Course> allCourses = new ArrayList<>();
        UserInfoCursorWrapper courseCursor = queryCourses(null, null);
        try {
            while (courseCursor.moveToNext()) {
                allCourses.add(courseCursor.getCourse());
            }
        } finally {
            courseCursor.close();
        }
        return allCourses;
    }

    //Temp for now to get a course
    public Course getCourse() {
        Course course = null;
        UserInfoCursorWrapper cursor = queryCourses(null, null);
        try {
            while (cursor.moveToNext()) {
                course = cursor.getCourse();
            }
        } finally {
            cursor.close();
        }
        return course;
    }

    //Adds New Course to the phone db
    public void addCourse(Course c) {
        if (!existsInTable(c.getId(), QuizletDbSchema.CourseTable.NAME)) { //Checks if course is already in the the database
            mDatabase.beginTransaction(); //open database for use
            ContentValues values = getCourseContentValues(c);
            mDatabase.insert(QuizletDbSchema.CourseTable.NAME, null, values);
            mDatabase.setTransactionSuccessful();
            mDatabase.endTransaction();
        }
    }


    public void setClickedCourse(String name){
        List<Course> courses = getCourses();
        name = name.trim();
        for(Course course: courses){
            if(name.equals(course.getName().trim())){
                clickedCourseId = course.getCourseId();
            }
        }
    }

    public String getClickedCourseId(){
        return clickedCourseId;
    }


    // This function finds row by id and prevents table from having duplicate rows
    // true is returned if row exists in the table
    private boolean existsInTable(String id, String TableName, String... idColumnName) {
        mDatabase.beginTransaction();
        String idColumn;
        if (idColumnName.length > 0) {
            idColumn = idColumnName[0];
        } else {
            idColumn = "_id";
        }
        Cursor cursor = mDatabase.query(TableName, null, idColumn + " = ?", new String[]{id}, null, null, "1");
        boolean exists = (cursor.getCount() > 0); // false if item is not in the db, true if it is
        cursor.close();
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();
        return exists;
    }


    //----------Get Content Values Functions-----------------------------------------------------//
    private static ContentValues getCourseContentValues(Course course) {
        ContentValues values = new ContentValues();
        values.put(QuizletDbSchema.CourseTable.Columns._ID, course.getId());
        values.put(QuizletDbSchema.CourseTable.Columns.COURSE_ID, course.getCourseId());
        values.put(QuizletDbSchema.CourseTable.Columns.EXTENDED_ID, course.getExtendedId());
        values.put(QuizletDbSchema.CourseTable.Columns.COURSE_NAME, course.getName());
        values.put(QuizletDbSchema.CourseTable.Columns.SEMESTER, course.getSemester());
        values.put(QuizletDbSchema.CourseTable.Columns.INSTRUCTOR, course.getInstructor());
        return values;
    }

    private static ContentValues getUserContentValues(User user) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(QuizletDbSchema.UserTable.Columns._ID, user.getId());
        contentValues.put(QuizletDbSchema.UserTable.Columns.USER_ID, user.getUserID());
        contentValues.put(QuizletDbSchema.UserTable.Columns.EMAIL, user.getEmail());
        contentValues.put(QuizletDbSchema.UserTable.Columns.FIRST_NAME, user.getFirst());
        contentValues.put(QuizletDbSchema.UserTable.Columns.LAST_NAME, user.getLast());
        return contentValues;
    }

    private static ContentValues getGroupContentValues(Group group) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(QuizletDbSchema.GroupTable.Columns.GROUPID, group.getId());
        contentValues.put(QuizletDbSchema.GroupTable.Columns.GROUPNAME, group.getName());
        return contentValues;
    }

    private static ContentValues getGroupUserContentValues(GroupUser groupUser) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(QuizletDbSchema.GroupUserTable.Columns.EMAIL, groupUser.getEmail());
        contentValues.put(QuizletDbSchema.GroupUserTable.Columns.USER_ID, groupUser.getUserID());
        contentValues.put(QuizletDbSchema.GroupUserTable.Columns.FIRST_NAME, groupUser.getFirst());
        contentValues.put(QuizletDbSchema.GroupUserTable.Columns.LAST_NAME, groupUser.getLast());
        contentValues.put(QuizletDbSchema.GroupUserTable.Columns.SINGLEQUIZSTATUS, groupUser.getSingleQuizStatus());
        contentValues.put(QuizletDbSchema.GroupUserTable.Columns.LEADER, (groupUser.getLeader() ? 1:0));
        return contentValues;
    }

    //deletes the user and courses from the database on logout
    public void deleteUser() {
        mDatabase.beginTransaction();
        mDatabase.delete(QuizletDbSchema.UserTable.NAME, null, null);
        mDatabase.delete(QuizletDbSchema.CourseTable.NAME, null, null);
        mDatabase.delete(QuizletDbSchema.CourseToQuizTable.NAME, null, null);
        mDatabase.delete(QuizletDbSchema.QuizTable.NAME, null, null);
        mDatabase.delete(QuizletDbSchema.QuizToQuestionTable.NAME, null, null);
        mDatabase.delete(QuizletDbSchema.QuestionTable.NAME, null, null);
        mDatabase.delete(QuizletDbSchema.QuestionToAnswerTable.NAME, null, null);
        mDatabase.delete(QuizletDbSchema.AnswerTable.NAME, null, null);
        mDatabase.delete(QuizletDbSchema.CourseToQuizTable.NAME, null, null);
        mDatabase.delete(QuizletDbSchema.SessionTable.NAME, null, null);
        mDatabase.delete(QuizletDbSchema.QuizHistoryTable.NAME, null, null);
        mDatabase.delete(QuizletDbSchema.GroupTable.NAME, null, null);
        mDatabase.delete(QuizletDbSchema.GroupUserTable.NAME, null, null);
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();

    }

}
