package edu.umsl.quizlet;

import edu.umsl.quizlet.dataClasses.Question;
import edu.umsl.quizlet.dataClasses.Quiz;
import edu.umsl.quizlet.database.QuizPersistence;
import edu.umsl.quizlet.database.UserInfoPersistence;
import android.content.Context;
import android.provider.ContactsContract;
import android.util.Log;

import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;


/**
 * Created by klkni on 5/8/2017.
 */

public class ResultsModel {

    private UserInfoPersistence sPersistence;
    private QuizPersistence sQuizPersistence;
    private BarGraphSeries<DataPoint> mMyData;
    private BarGraphSeries<DataPoint> mGroupData;
    private String mQuizId = "temp";

    ResultsModel(Context context){
        sPersistence = UserInfoPersistence.sharedInstance(context);
        sQuizPersistence = QuizPersistence.sharedInstance(context);
        mQuizId = sQuizPersistence.getSession().getQuizId();
    }

    public String getGroupResults(){
        return Integer.toString(sQuizPersistence.getQuiz(mQuizId).scoreGroupQuiz()) ;
    }
    public String getMyResults(){
        return Integer.toString(sQuizPersistence.getQuiz(mQuizId).scoreSingleUserQuiz());
    }
    public BarGraphSeries<DataPoint> getMyData() {
        ArrayList<DataPoint> points = new ArrayList<>();
        Quiz quiz = sQuizPersistence.getQuiz(mQuizId);
        for (int i=0; i<quiz.getQuestions().size(); i++) {
            if (quiz.getQuestion(i).getCorrectAnswer() != -1) {
                points.add(new DataPoint(i+1, quiz.getQuestion(i).getAvailableAnswers().get(quiz.getQuestion(i).getCorrectAnswer()).getConfidence()));
            } else {
                points.add(new DataPoint(i,0));
            }
        }
        mMyData = new BarGraphSeries<DataPoint>(points.toArray(new DataPoint[points.size()]));
        mMyData.setSpacing(50);
        mMyData.setAnimated(true);
        return mMyData;
    }

    public BarGraphSeries<DataPoint> getGroupData() {
        ArrayList<DataPoint> points = new ArrayList<>();
        Quiz quiz = sQuizPersistence.getQuiz(mQuizId);
        for (int i=0; i<quiz.getQuestions().size(); i++) {
            points.add(new DataPoint(i+1,quiz.getQuestion(i).getGroupScore()));
        }
        mGroupData = new BarGraphSeries<DataPoint>(points.toArray(new DataPoint[points.size()]));
        mGroupData.setSpacing(50);
        mGroupData.setAnimated(true);
        return mGroupData;
    }

    public double getMaxX() {
        return (double)sQuizPersistence.getQuiz(mQuizId).getQuestions().size() + 0.5;
    }

    public double getMaxY() {
        int max = 0;
        ArrayList<Question> questions = sQuizPersistence.getQuiz(mQuizId).getQuestions();
        for (int i=0; i<questions.size(); i++) {
            if (questions.get(i).getPointsPossible() > max) {
                max = questions.get(i).getPointsPossible();
            }
        }
        return max;
    }
}
