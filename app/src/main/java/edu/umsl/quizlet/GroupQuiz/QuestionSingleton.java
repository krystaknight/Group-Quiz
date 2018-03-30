package edu.umsl.quizlet.GroupQuiz;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import edu.umsl.quizlet.database.QuizPersistence;
import edu.umsl.quizlet.database.QuizletDbHelper;
import edu.umsl.quizlet.dataClasses.Question;
import edu.umsl.quizlet.dataClasses.Quiz;

/**
 * Created by landon on 4/17/17.
 */

public class QuestionSingleton {
    private static QuestionSingleton sQuestionSingleton;
    private SQLiteDatabase mDatabase;
    private Context mContext;
    private String mQuizId;
    private QuizPersistence sQuizPersistence;
    private Quiz mQuiz;

    public static QuestionSingleton get(Context context) {
        if (sQuestionSingleton == null) {
            sQuestionSingleton = new QuestionSingleton(context);
        }
        sQuestionSingleton.sQuizPersistence = QuizPersistence.sharedInstance(sQuestionSingleton.mContext);
        return sQuestionSingleton;
    }

    private QuestionSingleton(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new QuizletDbHelper(mContext)
                .getWritableDatabase();
    }

    public ArrayList<Question> getQuestions() {
        return this.mQuiz.getQuestions();
    }

    public Question getQuestion(int num) {
        return this.mQuiz.getQuestion(num);
    }

    public String getQuizId() {
        return mQuizId;
    }

    public int addPointToAnswer(int questionNumber, int answerNumber) {
        int value = mQuiz.getQuestion(questionNumber).addConfidencePoint(answerNumber);
        sQuizPersistence.updateAnswer(mQuiz.getQuestion(questionNumber).getAvailableAnswers().get(answerNumber));
        return value;
    }

    public int subtractPointFromAnswer(int questionNumber, int answerNumber) {
        int value = mQuiz.getQuestion(questionNumber).subtractConfidencePoint(answerNumber);
        sQuizPersistence.updateAnswer(mQuiz.getQuestion(questionNumber).getAvailableAnswers().get(answerNumber));
        return value;

    }

    public void setQuizId(String quizId) {
        this.mQuizId = quizId;
        this.mQuiz = this.sQuizPersistence.getQuiz(quizId);
    }

    public void saveAnswers() {
        sQuizPersistence.updateAllAnswers(mQuiz);
    }
}