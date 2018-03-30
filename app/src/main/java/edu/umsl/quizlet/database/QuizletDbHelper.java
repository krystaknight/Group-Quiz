package edu.umsl.quizlet.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static android.content.ContentValues.TAG;

/**
 * Created by Landon Bland on 4/15/2017.
 * This contains the definitions for creating the database for the application
 */

public class QuizletDbHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "edu.umsl.quizlet";

    public QuizletDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    // Database Table creation sql Statements

    private static final String UserTable = "CREATE TABLE " +
            QuizletDbSchema.UserTable.NAME + " (" +
            QuizletDbSchema.UserTable.Columns._ID + " TEXT PRIMARY KEY, " +
            QuizletDbSchema.UserTable.Columns.USER_ID + " TEXT, " +
            QuizletDbSchema.UserTable.Columns.EMAIL + " TEXT, " +
            QuizletDbSchema.UserTable.Columns.FIRST_NAME + " TEXT, " +
            QuizletDbSchema.UserTable.Columns.LAST_NAME + " TEXT " +
            ")";

    private static final String CourseTable = "CREATE TABLE " +
            QuizletDbSchema.CourseTable.NAME + " (" +
            QuizletDbSchema.CourseTable.Columns._ID + " TEXT PRIMARY KEY, " +
            QuizletDbSchema.CourseTable.Columns.COURSE_ID + " TEXT, " +
            QuizletDbSchema.CourseTable.Columns.EXTENDED_ID + " TEXT, " +
            QuizletDbSchema.CourseTable.Columns.COURSE_NAME + " TEXT, " +
            QuizletDbSchema.CourseTable.Columns.SEMESTER + " TEXT, " +
            QuizletDbSchema.CourseTable.Columns.INSTRUCTOR + " TEXT " +
            ")";

    private static final String CourseToQuizTable = "CREATE TABLE " +
            QuizletDbSchema.CourseToQuizTable.NAME + " (" +
            QuizletDbSchema.CourseToQuizTable.Columns._ID + " integer primary key autoincrement, " +
            QuizletDbSchema.CourseToQuizTable.Columns.COURSE_ID + " TEXT, " +
            QuizletDbSchema.CourseToQuizTable.Columns.QUIZ_ID + " TEXT " +
            ")";

    private static final String QuizTable = "CREATE TABLE " +
            QuizletDbSchema.QuizTable.NAME + " (" +
            QuizletDbSchema.QuizTable.Columns._ID + " TEXT PRIMARY KEY, " +
            QuizletDbSchema.QuizTable.Columns.AVAILABLE_DATE + " INTEGER, " +
            QuizletDbSchema.QuizTable.Columns.DESCRIPTION + " TEXT, " +
            QuizletDbSchema.QuizTable.Columns.EXPIRY_DATE + " INTEGER, " +
            QuizletDbSchema.QuizTable.Columns.TEXT + " TEXT, " +
            QuizletDbSchema.QuizTable.Columns.TIMED + " INTEGER, " +
            QuizletDbSchema.QuizTable.Columns.TIMED_LENGTH + " INTEGER " +
            ")";

    private static final String QuizToQuestionTable = "CREATE TABLE " +
            QuizletDbSchema.QuizToQuestionTable.NAME + " (" +
            QuizletDbSchema.QuizToQuestionTable.Columns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            QuizletDbSchema.QuizToQuestionTable.Columns.QUIZ_ID + " TEXT, " +
            QuizletDbSchema.QuizToQuestionTable.Columns.QUESTION_ID + " TEXT " +
            ")";

    private static final String QuestionTable = "CREATE TABLE " +
            QuizletDbSchema.QuestionTable.NAME + " (" +
            QuizletDbSchema.QuestionTable.Columns._ID + " TEXT PRIMARY KEY, " +
            QuizletDbSchema.QuestionTable.Columns.TITLE + " TEXT, " +
            QuizletDbSchema.QuestionTable.Columns.TEXT + " TEXT, " +
            QuizletDbSchema.QuestionTable.Columns.POINTS_POSSIBLE + " INTEGER, " +
            QuizletDbSchema.QuestionTable.Columns.GROUP_ANSWER + " INTEGER, " +
            QuizletDbSchema.QuestionTable.Columns.CORRECT_ANSWER + " INTEGER, " +
            QuizletDbSchema.QuestionTable.Columns.GROUP_SCORE + " INTEGER " +
            ")";

    private static final String QuestionToAnswerTable = "CREATE TABLE " +
            QuizletDbSchema.QuestionToAnswerTable.NAME + " (" +
            QuizletDbSchema.QuestionToAnswerTable.Columns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            QuizletDbSchema.QuestionToAnswerTable.Columns.QUESTION_ID + " TEXT, " +
            QuizletDbSchema.QuestionToAnswerTable.Columns.ANSWER_ID + " TEXT " +
            ")";

    private static final String AnswerTable = "CREATE TABLE " +
            QuizletDbSchema.AnswerTable.NAME + " (" +
            QuizletDbSchema.AnswerTable.Columns._ID + " TEXT PRIMARY KEY, " +
            QuizletDbSchema.AnswerTable.Columns.VALUE + " TEXT, " +
            QuizletDbSchema.AnswerTable.Columns.TEXT + " TEXT, " +
            QuizletDbSchema.AnswerTable.Columns.SORT_ORDER + " INTEGER, " +
            QuizletDbSchema.AnswerTable.Columns.CONFIDENCE + " INTEGER " +
            ")";

    private static final String SessionTable = "CREATE TABLE " +
            QuizletDbSchema.SessionTable.NAME + " (" +
            QuizletDbSchema.SessionTable.Columns._ID + " TEXT PRIMARY KEY, " +
            QuizletDbSchema.SessionTable.Columns.QUIZ_ID + " TEXT, " +
            QuizletDbSchema.SessionTable.Columns.USER_ID + " TEXT, " +
            QuizletDbSchema.SessionTable.Columns.CURRENT_QUESTION + " INTEGER, " +
            QuizletDbSchema.SessionTable.Columns.USER_STATUS + " INTEGER, " +
            QuizletDbSchema.SessionTable.Columns.IS_LEADER + " INTEGER, " +
            QuizletDbSchema.SessionTable.Columns.TIME_REMAINING + " INTEGER " +
            ")";

    private static final String GroupTable = "CREATE TABLE " +
            QuizletDbSchema.GroupTable.NAME + " (" +
            QuizletDbSchema.GroupTable.Columns.GROUPID + " TEXT PRIMARY KEY, " +
            QuizletDbSchema.GroupTable.Columns.GROUPNAME + " TEXT )";

    private static final String GroupUserTable = "CREATE TABLE " +
            QuizletDbSchema.GroupUserTable.NAME + " ("+
            QuizletDbSchema.GroupUserTable.Columns.USER_ID + " TEX PRIMARY KEY, " +
            QuizletDbSchema.GroupUserTable.Columns.EMAIL + " TEXT , " +
            QuizletDbSchema.GroupUserTable.Columns.FIRST_NAME + " TEXT , " +
            QuizletDbSchema.GroupUserTable.Columns.LAST_NAME + " TEXT , " +
            QuizletDbSchema.GroupUserTable.Columns.SINGLEQUIZSTATUS + " TEXT , " +
            QuizletDbSchema.GroupUserTable.Columns.LEADER + " INTEGER " + ")";

    public static final String QuizHistoryTable = "CREATE TABLE " +
            QuizletDbSchema.QuizHistoryTable.NAME + " (" +
            QuizletDbSchema.QuizHistoryTable.Columns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            QuizletDbSchema.QuizHistoryTable.Columns.COURSE_ID + " TEXT, " +
            QuizletDbSchema.QuizHistoryTable.Columns.SCORE + " INTEGER, " +
            QuizletDbSchema.QuizHistoryTable.Columns.TITLE + " INTEGER " +
            ")";


    // Method is called during creation of the database
    @Override
    public void onCreate(SQLiteDatabase db) {
        //Creating tables
        db.execSQL(UserTable); //Table for UserTable
        db.execSQL(CourseTable); //Table for CourseTable
        db.execSQL(CourseToQuizTable); //Table for CourseToQuizTable
        db.execSQL(QuizTable); //Table for QuizTable
        db.execSQL(QuizToQuestionTable); //Table for QuizToQuestionTable
        db.execSQL(QuestionTable); //Table for QuestionTable
        db.execSQL(QuestionToAnswerTable); //Table for QuestionToAnswerTable
        db.execSQL(AnswerTable); //Table for AnswerTable
        db.execSQL(SessionTable); //Table for SessionTable
        db.execSQL(QuizHistoryTable); //Table for QuizHistoryTable
        db.execSQL(GroupTable);
        Log.e(TAG, GroupUserTable);
        db.execSQL(GroupUserTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // On upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + QuizletDbSchema.UserTable.NAME);
        db.execSQL("DROP TABLE IF EXISTS " + QuizletDbSchema.CourseTable.NAME);
        db.execSQL("DROP TABLE IF EXISTS " + QuizletDbSchema.CourseToQuizTable.NAME);
        db.execSQL("DROP TABLE IF EXISTS " + QuizletDbSchema.QuizTable.NAME);
        db.execSQL("DROP TABLE IF EXISTS " + QuizletDbSchema.QuizToQuestionTable.NAME);
        db.execSQL("DROP TABLE IF EXISTS " + QuizletDbSchema.QuestionTable.NAME);
        db.execSQL("DROP TABLE IF EXISTS " + QuizletDbSchema.QuestionToAnswerTable.NAME);
        db.execSQL("DROP TABLE IF EXISTS " + QuizletDbSchema.AnswerTable.NAME);
        db.execSQL("DROP TABLE IF EXISTS " + QuizletDbSchema.SessionTable.NAME);
        db.execSQL("DROP TABLE IF EXISTS " + QuizletDbSchema.QuizHistoryTable.NAME);
        db.execSQL("DROP TABLE IF EXISTS " + QuizletDbSchema.GroupTable.NAME);
        db.execSQL("DROP TABLE IF EXISTS " + QuizletDbSchema.GroupUserTable.NAME);

        //create new tables
        onCreate(db);
    }
}
