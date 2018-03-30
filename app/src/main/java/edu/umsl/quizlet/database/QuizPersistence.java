package edu.umsl.quizlet.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.Date;
import java.util.ArrayList;

import edu.umsl.quizlet.dataClasses.Answer;
import edu.umsl.quizlet.dataClasses.Course;
import edu.umsl.quizlet.dataClasses.Question;
import edu.umsl.quizlet.dataClasses.Quiz;
import edu.umsl.quizlet.dataClasses.Session;

/**
 * Created by Austin on 4/18/2017.
 */

public class QuizPersistence {
    private SQLiteDatabase mDatabase;
    private Context mContext;

    public static QuizPersistence sPersistence;

    public static QuizPersistence sharedInstance(Context context) {
        if (sPersistence == null) {
            sPersistence = new QuizPersistence(context);
        }
        return sPersistence;
    }

    private QuizPersistence(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new QuizletDbHelper(context).getWritableDatabase();
    }

    public void nukeAll() {
        mDatabase.beginTransaction();
        mDatabase.delete(QuizletDbSchema.QuizToQuestionTable.NAME, null, null);
        mDatabase.delete(QuizletDbSchema.QuestionTable.NAME, null, null);
        mDatabase.delete(QuizletDbSchema.QuestionToAnswerTable.NAME, null, null);
        mDatabase.delete(QuizletDbSchema.AnswerTable.NAME, null, null);
        mDatabase.delete(QuizletDbSchema.SessionTable.NAME, null, null);
        mDatabase.delete(QuizletDbSchema.GroupUserTable.NAME, null, null);
        mDatabase.delete(QuizletDbSchema.GroupTable.NAME, null, null);
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();
    }

    //--------------------------Course to quiz table---------------------------------------------/

    public void setCourseToQuiz(Quiz quiz, String courseId) {
        mDatabase.beginTransaction();
        Cursor cursor = mDatabase.query(QuizletDbSchema.CourseToQuizTable.NAME,
                null,
                QuizletDbSchema.CourseToQuizTable.Columns.COURSE_ID + " = ? AND " +
                        QuizletDbSchema.CourseToQuizTable.Columns.QUIZ_ID + " = ? ",
                new String[]{quiz.getId(), courseId},
                null, null, "1");
        boolean exists = (cursor.getCount() > 0); // false if item is not in the db, true if it is
        cursor.close();
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();

        if (!exists) {
            mDatabase.beginTransaction();
            ContentValues contentValues = getCourseToQuizTableContentValues(quiz, courseId);
            mDatabase.insert(QuizletDbSchema.CourseToQuizTable.NAME, null, contentValues); // inserting into the db
            mDatabase.setTransactionSuccessful();
            mDatabase.endTransaction();
        }
    }

    //--------------------------Quiz-------------------------------------------------------------/

    public void updateAllAnswers(Quiz quiz) {
        ArrayList<Question> questions = quiz.getQuestions();
        for (Question question : questions) {
            ArrayList<Answer> answers = question.getAvailableAnswers();
            for (Answer answer : answers) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(QuizletDbSchema.AnswerTable.Columns.CONFIDENCE, answer.getConfidence());
                mDatabase.beginTransaction();
                mDatabase.update(QuizletDbSchema.AnswerTable.NAME, contentValues, QuizletDbSchema.AnswerTable.Columns._ID + " = ?", new String[]{answer.getId()});
                mDatabase.setTransactionSuccessful();
                mDatabase.endTransaction();
            }
        }
    }

    public void updateAnswer(Answer answer) {
        Log.e("SAVE ANSWER", "Save answer called on " + answer.toString());
        ContentValues contentValues = new ContentValues();
        contentValues.put(QuizletDbSchema.AnswerTable.Columns.CONFIDENCE, answer.getConfidence());
        mDatabase.beginTransaction();
        mDatabase.update(QuizletDbSchema.AnswerTable.NAME, contentValues, QuizletDbSchema.AnswerTable.Columns._ID + " = ?", new String[]{answer.getId()});
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();
    }

    public void updateCorrectAnswers(Quiz quiz) {
        ArrayList<Question> questions = quiz.getQuestions();
        for (Question question : questions) {
            Log.e("DATABASE", "updating correct answer " + question.getCorrectAnswer());
            ContentValues contentValues = new ContentValues();
            contentValues.put(QuizletDbSchema.QuestionTable.Columns.CORRECT_ANSWER, question.getCorrectAnswer());
            mDatabase.beginTransaction();
            mDatabase.update(QuizletDbSchema.QuestionTable.NAME, contentValues, QuizletDbSchema.QuestionTable.Columns._ID + " = ?", new String[] {question.getId()});
            mDatabase.setTransactionSuccessful();
            mDatabase.endTransaction();
        }
    }

    public void updateGroupScores(Quiz quiz) {
        ArrayList<Question> questions = quiz.getQuestions();
        for (Question question : questions) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(QuizletDbSchema.QuestionTable.Columns.GROUP_SCORE, question.getGroupScore());
            mDatabase.beginTransaction();
            mDatabase.update(QuizletDbSchema.QuestionTable.NAME, contentValues, QuizletDbSchema.QuestionTable.Columns._ID + " = ?", new String[] {question.getId()});
            mDatabase.setTransactionSuccessful();
            mDatabase.endTransaction();
        }
    }

    public void updateGroupScore(String questionId, int groupScore) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(QuizletDbSchema.QuestionTable.Columns.GROUP_SCORE, groupScore);
            mDatabase.beginTransaction();
            mDatabase.update(QuizletDbSchema.QuestionTable.NAME, contentValues, QuizletDbSchema.QuestionTable.Columns._ID + " = ?", new String[] {questionId});
            mDatabase.setTransactionSuccessful();
            mDatabase.endTransaction();
    }

    public void setQuiz(Quiz quiz) {
        if (!existsInTable(quiz.getId(), QuizletDbSchema.QuizTable.NAME)) {
            mDatabase.beginTransaction();
            ContentValues contentValues = getQuizContentValues(quiz);
            mDatabase.insert(QuizletDbSchema.QuizTable.NAME, null, contentValues); // inserting into the db
            mDatabase.setTransactionSuccessful();
            mDatabase.endTransaction();

            // Setting questions in DB if necessary
            ArrayList<Question> questions = quiz.getQuestions();
            if (questions != null && questions.size() > 0) {
                for (int i = 0; i < questions.size(); i++) {
                    if (!existsInTable(questions.get(i).getId(), QuizletDbSchema.QuizToQuestionTable.NAME, QuizletDbSchema.QuizToQuestionTable.Columns.QUESTION_ID)) {
                        mDatabase.beginTransaction();
                        contentValues = getQuizToQuestionContentValues(quiz, questions.get(i));
                        mDatabase.insert(QuizletDbSchema.QuizToQuestionTable.NAME, null, contentValues);
                        mDatabase.setTransactionSuccessful();
                        mDatabase.endTransaction();
                    }
                    setQuestion(questions.get(i));
                }
            }
        } else {
            Log.e("TAG", "Quiz " + quiz.getDescription() + " is already in the table");
            ContentValues contentValues;
            ArrayList<Question> questions = quiz.getQuestions();
            if (questions != null && questions.size() > 0) {
                for (int i = 0; i < questions.size(); i++) {
                    if (!existsInTable(questions.get(i).getId(), QuizletDbSchema.QuizToQuestionTable.NAME, QuizletDbSchema.QuizToQuestionTable.Columns.QUESTION_ID)) {
                        mDatabase.beginTransaction();
                        contentValues = getQuizToQuestionContentValues(quiz, questions.get(i));
                        mDatabase.insert(QuizletDbSchema.QuizToQuestionTable.NAME, null, contentValues);
                        mDatabase.setTransactionSuccessful();
                        mDatabase.endTransaction();
                    }
                    setQuestion(questions.get(i));
                }
            }
        }
    }

    public Quiz getQuiz(String _id) {
        if (_id == null) return null;
        Cursor cursor = mDatabase.query(
                QuizletDbSchema.QuizTable.NAME,
                null,
                QuizletDbSchema.QuizTable.Columns._ID + "= ?",
                new String[]{_id},
                null,
                null,
                null
        );
        if (cursor.moveToFirst() && cursor != null) {
            QuizCursorWrapper quizCursorWrapper = new QuizCursorWrapper(cursor);
            Quiz quiz = quizCursorWrapper.getQuiz();
            cursor.close();
            return quiz;
        } else {
            return null;
        }
    }

    public String getQuizIdFromCourseId(String _id) {
        Log.e("TAG", "_id passed: " + _id);
        Cursor cursor = mDatabase.query(
                QuizletDbSchema.CourseToQuizTable.NAME,
                new String[]{
                        QuizletDbSchema.CourseToQuizTable.Columns.QUIZ_ID
                },
                QuizletDbSchema.CourseToQuizTable.Columns.COURSE_ID + "= ?",
                new String[]{_id},
                null,
                null,
                null
        );
        String quizId = "";
        try {
            if (cursor.moveToFirst()) {
                quizId = cursor.getString(cursor.getColumnIndex(QuizletDbSchema.CourseToQuizTable.Columns.QUIZ_ID));
            }
        } finally {
            cursor.close();
        }

        return quizId;
    }

    public String getCourseIdFromQuizId(String _id) {
        Cursor cursor = mDatabase.query(
                QuizletDbSchema.CourseToQuizTable.NAME,
                new String[]{
                        QuizletDbSchema.CourseToQuizTable.Columns.COURSE_ID
                },
                QuizletDbSchema.CourseToQuizTable.Columns.QUIZ_ID + "= ?",
                new String[]{_id},
                null,
                null,
                null
        );
        String courseId = "";
        try {
            if (cursor.moveToFirst()) {
                courseId = cursor.getString(cursor.getColumnIndex(QuizletDbSchema.CourseToQuizTable.Columns.COURSE_ID));
            }
        } finally {
            cursor.close();
        }
        return courseId;
    }

    //--------------------------Question---------------------------------------------------------/
    public void setQuestion(Question question) {
        if (!existsInTable(question.getId(), QuizletDbSchema.QuestionTable.NAME)) {
            mDatabase.beginTransaction();
            ContentValues contentValues = getQuestionContentValues(question);
            mDatabase.insert(QuizletDbSchema.QuestionTable.NAME, null, contentValues); // inserting into the db
            mDatabase.setTransactionSuccessful();
            mDatabase.endTransaction();

            // Setting answers in DB if necessary
            ArrayList<Answer> answers = question.getAvailableAnswers();
            if (answers != null && answers.size() > 0) {
                for (int i = 0; i < answers.size(); i++) {
                    mDatabase.beginTransaction();
                    contentValues = getQuestionToAnswerContentValues(question, answers.get(i));
                    mDatabase.insert(QuizletDbSchema.QuestionToAnswerTable.NAME, null, contentValues);
                    mDatabase.setTransactionSuccessful();
                    mDatabase.endTransaction();
                    setAnswer(answers.get(i));
                }
            }
        } else {
            Log.e("TAG", "Question " + question.getText() + " is already in the table");
        }
    }

    public Question getQuestion(String _id) {
        Cursor cursor = mDatabase.query(
                QuizletDbSchema.QuizTable.NAME,
                null,
                QuizletDbSchema.QuizTable.Columns._ID + "= ?",
                new String[]{_id},
                null,
                null,
                null
        );
        cursor.moveToFirst();
        QuestionCursorWrapper questionCursorWrapper = new QuestionCursorWrapper(cursor);
        Question question = questionCursorWrapper.getQuestion();
        cursor.close();
        return question;
    }

    //--------------------------Answer-----------------------------------------------------------/
    public void setAnswer(Answer answer) {
        if (!existsInTable(answer.getId(), QuizletDbSchema.AnswerTable.NAME)) {
            mDatabase.beginTransaction();
            ContentValues contentValues = getAnswerContentValues(answer);
            mDatabase.insert(QuizletDbSchema.AnswerTable.NAME, null, contentValues); // inserting into the db
            mDatabase.setTransactionSuccessful();
            mDatabase.endTransaction();
        }
    }

    public Answer getAnswer(String _id) {
        Cursor cursor = mDatabase.query(
                QuizletDbSchema.AnswerTable.NAME,
                null,
                QuizletDbSchema.AnswerTable.Columns._ID + "= ?",
                new String[]{_id},
                null,
                null,
                null
        );
        if (cursor != null && cursor.moveToFirst()) {
            AnswerCursorWrapper answerCursorWrapper = new AnswerCursorWrapper(cursor);
            Answer answer = answerCursorWrapper.getAnswer();
            cursor.close();
            return answer;
        } else {
            return null;
        }
    }


    //-------------------------Session Table-----------------------------------------------------/

    public void setSessionTable(Session session) {
        if (!existsInTable(session.getSessionId(), QuizletDbSchema.SessionTable.NAME, QuizletDbSchema.SessionTable.Columns._ID)) {
//            Log.e("Session Table", "Session is not in DB, Adding Session");
            mDatabase.beginTransaction();
            ContentValues contentValues = getSessionContentValues(session);
            mDatabase.insert(QuizletDbSchema.SessionTable.NAME, null, contentValues);
            mDatabase.setTransactionSuccessful();
            mDatabase.endTransaction();

        } else {
            //Log.e("Session Table", "Session is already in the table Updating Session Table.");
            mDatabase.beginTransaction();
            ContentValues contentValues = getSessionContentValues(session);
            mDatabase.update(QuizletDbSchema.SessionTable.NAME, contentValues, null, null);
            mDatabase.setTransactionSuccessful();
            mDatabase.endTransaction();
        }
    }

    public void setCurrentQuestion(int currentQuestion){
        mDatabase.beginTransaction();
        ContentValues contentValues = new ContentValues();
        contentValues.put(QuizletDbSchema.SessionTable.Columns.CURRENT_QUESTION, currentQuestion);
        mDatabase.update(QuizletDbSchema.SessionTable.NAME,contentValues, null,null);
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();

    }

    public void setUserLocation(int userLocation){
        mDatabase.beginTransaction();
        ContentValues contentValues = new ContentValues();
        contentValues.put(QuizletDbSchema.SessionTable.Columns.USER_STATUS, userLocation);
        mDatabase.update(QuizletDbSchema.SessionTable.NAME,contentValues, null,null);
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();
    }

    public void setIsLeader(boolean isLeader){
        mDatabase.beginTransaction();
        ContentValues contentValues = new ContentValues();
        contentValues.put(QuizletDbSchema.SessionTable.Columns.IS_LEADER, (isLeader ? 1:0));
        Log.e("DATABASE LEADER", Boolean.toString(isLeader));
        Log.e("DATABASE", contentValues.toString());
        mDatabase.update(QuizletDbSchema.SessionTable.NAME,contentValues, null,null);
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();
    }

    public Session getSession() {
        Cursor cursor = mDatabase.query(
                QuizletDbSchema.SessionTable.NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
        if (cursor.moveToFirst() && cursor != null) {
            SessionCursorWrapper sessionCursorWrapper = new SessionCursorWrapper(cursor);
            Session session = sessionCursorWrapper.getSession();
            cursor.close();
            return session;
        } else {
            Log.e("getSession", " QuizPersistence: No Session in Table");
        }
        cursor.close();
        return null;
    }

    // This function finds row by id and prevents table from having duplicate rows
    // true is returned if row exists in the table
    private boolean existsInTable(String id, String TableName, String... idColumnName) {
        mDatabase.beginTransaction();
        String idColumn;
        if (idColumnName.length > 0) {
            idColumn = idColumnName[0];
        } else {
            if(TableName.equals("question")){
                idColumn="qid";
            }else {
                idColumn = "_id";
            }
        }
        Cursor cursor = mDatabase.query(TableName, null, idColumn + " = ?", new String[]{id}, null, null, "1");
        boolean exists = (cursor.getCount() > 0); // false if item is not in the db, true if it is
        cursor.close();
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();
        return exists;
    }

    //----------Get Content Values Functions-----------------------------------------------------//
    private static ContentValues getCourseContentValues(Course c) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(QuizletDbSchema.CourseTable.Columns._ID, c.getId());  //This is a String in QuizModel
        contentValues.put(QuizletDbSchema.CourseTable.Columns.COURSE_ID, c.getCourseId());  //This is a String in QuizModel
        contentValues.put(QuizletDbSchema.CourseTable.Columns.COURSE_NAME, c.getName());  //This is a String in QuizModel
        contentValues.put(QuizletDbSchema.CourseTable.Columns.EXTENDED_ID, c.getExtendedId());  //This is a String in QuizModel
        contentValues.put(QuizletDbSchema.CourseTable.Columns.INSTRUCTOR, c.getCourseId());  //This is a String in QuizModel
        contentValues.put(QuizletDbSchema.CourseTable.Columns.SEMESTER, c.getSemester());  //This is a String in QuizModel
        return contentValues;
    }

    private static ContentValues getQuizContentValues(Quiz q) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(QuizletDbSchema.QuizTable.Columns._ID, q.getId());  //This is a String in QuizModel
        contentValues.put(QuizletDbSchema.QuizTable.Columns.DESCRIPTION, q.getDescription());  //This is a String in QuizModel
        contentValues.put(QuizletDbSchema.QuizTable.Columns.TEXT, q.getText());  //This is STring in QuizModel
        contentValues.put(QuizletDbSchema.QuizTable.Columns.AVAILABLE_DATE, q.getAvailableDate().getTime()); // This is Date in QuizModel Converted to String
        contentValues.put(QuizletDbSchema.QuizTable.Columns.EXPIRY_DATE, q.getExpiryDate().getTime()); // This is Date in QuizMOdel Converted to String
        contentValues.put(QuizletDbSchema.QuizTable.Columns.TIMED, q.getTimed()); // This is Date in QuizModel
        contentValues.put(QuizletDbSchema.QuizTable.Columns.TIMED_LENGTH, q.getTimedLength());
        return contentValues;
    }

    private static ContentValues getQuestionContentValues(Question q) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(QuizletDbSchema.QuestionTable.Columns._ID, q.getId());
        contentValues.put(QuizletDbSchema.QuestionTable.Columns.GROUP_ANSWER, q.getGroupAnswer());
        contentValues.put(QuizletDbSchema.QuestionTable.Columns.CORRECT_ANSWER, q.getCorrectAnswer());
        contentValues.put(QuizletDbSchema.QuestionTable.Columns.GROUP_SCORE, q.getGroupScore());
        contentValues.put(QuizletDbSchema.QuestionTable.Columns.POINTS_POSSIBLE, q.getPointsPossible());
        contentValues.put(QuizletDbSchema.QuestionTable.Columns.TEXT, q.getText());
        contentValues.put(QuizletDbSchema.QuestionTable.Columns.TITLE, q.getTitle());
        return contentValues;
    }

    private static ContentValues getAnswerContentValues(Answer a) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(QuizletDbSchema.AnswerTable.Columns._ID, a.getId());
        contentValues.put(QuizletDbSchema.AnswerTable.Columns.CONFIDENCE, a.getConfidence());
        contentValues.put(QuizletDbSchema.AnswerTable.Columns.SORT_ORDER, a.getSortOrder());
        contentValues.put(QuizletDbSchema.AnswerTable.Columns.TEXT, a.getText());
        contentValues.put(QuizletDbSchema.AnswerTable.Columns.VALUE, a.getValue());
        return contentValues;
    }

    private static ContentValues getCourseToQuizTableContentValues(Quiz q, String CourseID) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(QuizletDbSchema.CourseToQuizTable.Columns.COURSE_ID, CourseID);
        contentValues.put(QuizletDbSchema.CourseToQuizTable.Columns.QUIZ_ID, q.getId());
        return contentValues;
    }

    private static ContentValues getQuestionToAnswerContentValues(Question q, Answer a) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(QuizletDbSchema.QuestionToAnswerTable.Columns.QUESTION_ID, q.getId());
        contentValues.put(QuizletDbSchema.QuestionToAnswerTable.Columns.ANSWER_ID, a.getId());
        return contentValues;
    }

    private static ContentValues getQuizToQuestionContentValues(Quiz quiz, Question question) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(QuizletDbSchema.QuizToQuestionTable.Columns.QUESTION_ID,question.getId());
        contentValues.put(QuizletDbSchema.QuizToQuestionTable.Columns.QUIZ_ID, quiz.getId());
        return contentValues;
    }

    private static ContentValues getSessionContentValues(Session session) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(QuizletDbSchema.SessionTable.Columns._ID, session.getSessionId());
        contentValues.put(QuizletDbSchema.SessionTable.Columns.USER_ID, session.getUserId());
        contentValues.put(QuizletDbSchema.SessionTable.Columns.QUIZ_ID, session.getQuizId());
        contentValues.put(QuizletDbSchema.SessionTable.Columns.CURRENT_QUESTION, session.getCurrentQuestion());
        contentValues.put(QuizletDbSchema.SessionTable.Columns.IS_LEADER, (session.isLeader() ? 1:0));
        contentValues.put(QuizletDbSchema.SessionTable.Columns.USER_STATUS, session.getUserStatus());
        contentValues.put(QuizletDbSchema.SessionTable.Columns.TIME_REMAINING, session.getTimeRemaining().getTime());
        return contentValues;
    }

    //----------------------------------------CursorWrapper---------------------------------------//

    public class QuizCursorWrapper extends CursorWrapper {
        public QuizCursorWrapper(Cursor cursor) {
            super(cursor);
        }

        // QuizInfo
        public Quiz getQuiz() {
            String id = getString(getColumnIndex(QuizletDbSchema.QuizTable.Columns._ID));
            Date availableDate = new Date(getLong(getColumnIndex(QuizletDbSchema.QuizTable.Columns.AVAILABLE_DATE)));
            String description = getString(getColumnIndex(QuizletDbSchema.QuizTable.Columns.DESCRIPTION));
            Date expiryDate = new Date(getLong(getColumnIndex(QuizletDbSchema.QuizTable.Columns.EXPIRY_DATE)));
            String text = getString(getColumnIndex(QuizletDbSchema.QuizTable.Columns.TEXT));
            boolean timed = 1 == getInt(getColumnIndex(QuizletDbSchema.QuizTable.Columns.TIMED));
            int time_length = getInt(getColumnIndex(QuizletDbSchema.QuizTable.Columns.TIMED_LENGTH));
            Quiz quiz = new Quiz(id, description, text, availableDate, expiryDate, timed, time_length);
            mDatabase.beginTransaction();
            Cursor cursor = mDatabase.query(
                    true,
                    QuizletDbSchema.QuizToQuestionTable.NAME + ", " + QuizletDbSchema.QuestionTable.NAME,
                    new String[]{
                            QuizletDbSchema.QuestionTable.Columns._ID,
                            QuizletDbSchema.QuestionTable.Columns.GROUP_ANSWER,
                            QuizletDbSchema.QuestionTable.Columns.CORRECT_ANSWER,
                            QuizletDbSchema.QuestionTable.Columns.GROUP_SCORE,
                            QuizletDbSchema.QuestionTable.Columns.POINTS_POSSIBLE,
                            QuizletDbSchema.QuestionTable.Columns.TEXT,
                            QuizletDbSchema.QuestionTable.Columns.TITLE
                    },
                    QuizletDbSchema.QuizToQuestionTable.Columns.QUIZ_ID + "= ? AND " +
                            QuizletDbSchema.QuizToQuestionTable.Columns.QUESTION_ID + " = " +
                            QuizletDbSchema.QuestionTable.Columns._ID,
                    new String[] {id},
                    QuizletDbSchema.QuizToQuestionTable.Columns.QUESTION_ID,

                    null,
                    null,
                    null
            );
            cursor.moveToFirst();
            QuestionCursorWrapper questionCursorWrapper = new QuestionCursorWrapper(cursor);
            ArrayList<Question> questions = questionCursorWrapper.getQuestions();
            cursor.close();

            mDatabase.setTransactionSuccessful();
            mDatabase.endTransaction();

            quiz.setQuestions(questions);
            return quiz;
        }
    }

    public class QuestionCursorWrapper extends CursorWrapper {
        public QuestionCursorWrapper(Cursor cursor) {
            super(cursor);
        }

        public ArrayList<Question> getQuestions() {
            ArrayList<Question> questions = new ArrayList<>();
            for (int i = 0; i < getWrappedCursor().getCount(); i++) {
                questions.add(getQuestion());
                getWrappedCursor().moveToNext();
            }
            return questions;
        }

        public Question getQuestion() {
            String _id = getString(getColumnIndex(QuizletDbSchema.QuestionTable.Columns._ID));
            int groupAnswer = getInt(getColumnIndex(QuizletDbSchema.QuestionTable.Columns.GROUP_ANSWER));
            int correctAnswer = getInt(getColumnIndex(QuizletDbSchema.QuestionTable.Columns.CORRECT_ANSWER));
            int groupScore = getInt(getColumnIndex(QuizletDbSchema.QuestionTable.Columns.GROUP_SCORE));
            int pointsPossible = getInt(getColumnIndex(QuizletDbSchema.QuestionTable.Columns.POINTS_POSSIBLE));
            String text = getString(getColumnIndex(QuizletDbSchema.QuestionTable.Columns.TEXT));
            String title = getString(getColumnIndex(QuizletDbSchema.QuestionTable.Columns.TITLE));
            mDatabase.beginTransaction();
            Cursor cursor = mDatabase.query(
                    QuizletDbSchema.QuestionToAnswerTable.NAME + ", " + QuizletDbSchema.AnswerTable.NAME,
                    new String[]{
                            QuizletDbSchema.AnswerTable.Columns._ID,
                            QuizletDbSchema.AnswerTable.Columns.CONFIDENCE,
                            QuizletDbSchema.AnswerTable.Columns.TEXT,
                            QuizletDbSchema.AnswerTable.Columns.VALUE,
                            QuizletDbSchema.AnswerTable.Columns.SORT_ORDER
                    },
                    QuizletDbSchema.QuestionToAnswerTable.Columns.QUESTION_ID + "= ? AND " +
                            QuizletDbSchema.QuestionToAnswerTable.Columns.ANSWER_ID + " = " +
                            QuizletDbSchema.AnswerTable.Columns._ID,
                    new String[]{_id},
                    null,
                    null,
                    null
            );
            cursor.moveToFirst();
            AnswerCursorWrapper answerCursorWrapper = new AnswerCursorWrapper(cursor);
            ArrayList<Answer> answers = answerCursorWrapper.getAnswers();
            cursor.close();
            mDatabase.setTransactionSuccessful();
            mDatabase.endTransaction();

            return new Question(_id, title, text, pointsPossible, groupScore, groupAnswer, correctAnswer, answers);
        }
    }

    public class AnswerCursorWrapper extends CursorWrapper {
        public AnswerCursorWrapper(Cursor cursor) {
            super(cursor);
        }

        public ArrayList<Answer> getAnswers() {
            ArrayList<Answer> answers = new ArrayList<>();
            for (int i = 0; i < getWrappedCursor().getCount(); i++) {
                answers.add(getAnswer());
                getWrappedCursor().moveToNext();
            }
            return answers;
        }

        public Answer getAnswer() {
            String _id = getString(getColumnIndex(QuizletDbSchema.AnswerTable.Columns._ID));
            int confidence = getInt(getColumnIndex(QuizletDbSchema.AnswerTable.Columns.CONFIDENCE));
            int sortOrder = getInt(getColumnIndex(QuizletDbSchema.AnswerTable.Columns.SORT_ORDER));
            String text = getString(getColumnIndex(QuizletDbSchema.AnswerTable.Columns.TEXT));
            String value = getString(getColumnIndex(QuizletDbSchema.AnswerTable.Columns.VALUE));
            return new Answer(value, text, sortOrder, confidence);
        }
    }

    private class SessionCursorWrapper extends CursorWrapper {
        private SessionCursorWrapper(Cursor cursor) {
            super(cursor);
        }

        private Session getSession() {
            String id = getString(getColumnIndex(QuizletDbSchema.SessionTable.Columns._ID));
            String userId = getString(getColumnIndex(QuizletDbSchema.SessionTable.Columns.USER_ID));
            String quizId = getString(getColumnIndex(QuizletDbSchema.SessionTable.Columns.QUIZ_ID));
            int currentQuestion = getInt(getColumnIndex(QuizletDbSchema.SessionTable.Columns.CURRENT_QUESTION));
            int userLocation = getInt(getColumnIndex(QuizletDbSchema.SessionTable.Columns.USER_STATUS));
            boolean isLeader = (getInt(getColumnIndex(QuizletDbSchema.SessionTable.Columns.IS_LEADER)) == 1);
            Date timeRemaining = new Date(getLong(getColumnIndex(QuizletDbSchema.SessionTable.Columns.TIME_REMAINING)));

            return new Session(id, userId, quizId, currentQuestion, timeRemaining, userLocation, isLeader);
        }
    }

}
