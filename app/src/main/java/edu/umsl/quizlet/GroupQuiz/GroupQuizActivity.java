package edu.umsl.quizlet.GroupQuiz;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import edu.umsl.quizlet.API;
import edu.umsl.quizlet.HttpWorkerFragment;
import edu.umsl.quizlet.R;
import edu.umsl.quizlet.ResultsActivity;
import edu.umsl.quizlet.dataClasses.Answer;
import edu.umsl.quizlet.dataClasses.Question;
import edu.umsl.quizlet.database.QuizPersistence;
import edu.umsl.quizlet.database.UserInfoPersistence;

import static edu.umsl.quizlet.HttpWorkerFragment.HTTP_WORKER_FRAG;

/**
 * Created by landon on 4/18/17.
 */

public class GroupQuizActivity extends FragmentActivity implements View.OnClickListener, API.APIGroupQuizListener, QuestionFragment.groupQuestionScratch {
    public static String QUIZ_ID_EXTRA = "QID_EXTRA";
    private String mQuizID;
    private ArrayList<Question> mQuestions;
    private int mPosition;
    private LinearLayout mAnswerContainer;
    private QuestionFragment mQfrag;
    private HttpWorkerFragment mHttpWorkerFragment;
    private API mApi;
    private FrameLayout mLastCanvasParent;
    private boolean correctAnswerChosen;
    private int mCurrentAnswer;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private WeakReference<disableUnchosenCanvases> mCorrectAnswerChosenListener;

    interface disableUnchosenCanvases {
        void correctAnswerChosen();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_quiz);

        mQuizID = getIntent().getStringExtra(QUIZ_ID_EXTRA);
        Log.e("QUIZID", mQuizID);

        QuestionSingleton questionSingleton = QuestionSingleton.get(this);
        questionSingleton.setQuizId(mQuizID);
        mQuestions = questionSingleton.getQuestions();
        mPosition = 0;
        mQuestions.get(mPosition).setGroupScore(mQuestions.get(mPosition).getPointsPossible());
        if (QuizPersistence.sharedInstance(this).getSession().isLeader()) {
            Log.e("LEADER", "YES");
        } else {
            Log.e("LEADER", "NO");
        }
        if (!QuizPersistence.sharedInstance(this).getSession().isLeader()) {
            if (QuizPersistence.sharedInstance(this).getSession().getCurrentQuestion() > 0 &&
                    QuizPersistence.sharedInstance(this).getSession().getCurrentQuestion() < mQuestions.size()) {
                mPosition = QuizPersistence.sharedInstance(this).getSession().getCurrentQuestion();
            }
        }

        mQfrag = (QuestionFragment)getSupportFragmentManager().findFragmentByTag("QUESTIONFRAGTAG");

        if (mQfrag == null) {
            mQfrag = new QuestionFragment();
            mQfrag.setQuestionModel(mQuestions.get(mPosition));
            mQfrag.setQuestionNumber(mPosition);
            mQfrag.setmListener(this);
            mQfrag.setmContext(this);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.groupQuizAnswerContainer, mQfrag, "QUESTIONFRAGTAG")
                    .commit();
        }

        ((Button)findViewById(R.id.groupQuizNextButton)).setOnClickListener(this);
        ((Button)findViewById(R.id.groupQuizNextButton)).setEnabled(false);
        correctAnswerChosen = false;

        // Setup for API Stuff

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();


        //Setting up mHttpWorkerFragment and API
        mHttpWorkerFragment = (HttpWorkerFragment) manager.findFragmentByTag(HTTP_WORKER_FRAG);
        if (mHttpWorkerFragment == null) {
            mHttpWorkerFragment = new HttpWorkerFragment();
            mHttpWorkerFragment.setmContext(this);
            transaction.add(mHttpWorkerFragment, HTTP_WORKER_FRAG);
        }
        mApi = new API(mHttpWorkerFragment, this);
        mApi.setGroupQuizListener(this);
        if (!QuizPersistence.sharedInstance(this).getSession().isLeader()) {
            startTimer();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {
        if (mPosition + 1 < mQuestions.size()) {
            if (mTimer != null) {
                mTimer.cancel();
            }
            mPosition++;
            correctAnswerChosen = false;
            mQuestions.get(mPosition).setGroupScore(mQuestions.get(mPosition).getPointsPossible());
            getSupportFragmentManager().beginTransaction()
                    .remove(mQfrag).commit();
            mQfrag.setQuestionNumber(mPosition);

            mQfrag = new QuestionFragment();
            mQfrag.setQuestionModel(mQuestions.get(mPosition));
            mQfrag.setQuestionNumber(mPosition);
            mQfrag.setmListener(this);
            mQfrag.setmContext(this);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.groupQuizAnswerContainer, mQfrag, "QUESTIONFRAGTAG")
                    .commit();
            QuizPersistence.sharedInstance(this).setCurrentQuestion(mPosition);
            ((Button)findViewById(R.id.groupQuizNextButton)).setEnabled(false);
            startTimer();
        } else {
            if (mTimer != null) {
                mTimer.cancel();
            }
            Intent intent = new Intent(this, ResultsActivity.class);
            intent.putExtra(QUIZ_ID_EXTRA, mQuizID);
            startActivity(intent);
            QuizPersistence.sharedInstance(this).setCurrentQuestion(0);
            QuizPersistence.sharedInstance(this).setUserLocation(3);
            finish();
        }
        if (mPosition + 1 == mQuestions.size()) {
            ((Button)this.findViewById(R.id.groupQuizNextButton)).setText("Done");
        }
    }

    @Override
    public void groupQuestionResponse(boolean isCorrect, int points) {
        Log.e("ISCORRECT",Boolean.toString(isCorrect));
        Log.e("POINTS", Integer.toString(points));
        Log.e("mLastCanvasParent", Boolean.toString(mLastCanvasParent == null));
        if (mLastCanvasParent != null) {
            ImageView correct = (ImageView) mLastCanvasParent.findViewById(R.id.groupQuizScratchOffCorrectImage);
            ImageView incorrect = (ImageView) mLastCanvasParent.findViewById(R.id.groupQuizScratchOffIncorrectImage);
            RelativeLayout loader = (RelativeLayout) mLastCanvasParent.findViewById(R.id.groupQuizScratchOffLoadingContainer);
            if (isCorrect) {
                correct.setVisibility(View.VISIBLE);
                correctAnswerChosen = true;
                ((TextView)this.findViewById(R.id.singleUserQuizPointsRemainingTextView)).setText(
                        mQuestions.get(mPosition).getGroupScore() + " points left"
                );
//                ((ErasableCanvas)mLastCanvasParent.findViewById(R.id.groupQuizScratchOffCanvas)).setTouchDisabled(true);
                ((Button)findViewById(R.id.groupQuizNextButton)).setEnabled(true);
                if (mPosition + 1 < mQuestions.size()) {
                    QuizPersistence.sharedInstance(this).setCurrentQuestion(mPosition + 1);
                } else {
                    QuizPersistence.sharedInstance(this).setCurrentQuestion(0);
                    QuizPersistence.sharedInstance(this).setUserLocation(3);
                }
            } else {
                incorrect.setVisibility(View.VISIBLE);
                mQuestions.get(mPosition).decrementPossiblePoints();
                ((TextView)this.findViewById(R.id.singleUserQuizPointsRemainingTextView)).setText(
                        mQuestions.get(mPosition).getGroupScore() + " points left"
                );
            }
            loader.setVisibility(View.GONE);
        }
    }

    @Override
    public void getGroupQuizProgress(JSONObject jObj) {
        try {
            JSONArray jsonArray;
            if (jObj.getInt("questionsAnswered") < mQuestions.size()) {
                Log.e("POSITION", Integer.toString(mPosition));
                Log.e("QANS", jObj.toString());
            } else {
                mPosition = mQuestions.size();
                for (int i=0; i<jObj.getJSONArray("givenAnswers").length(); i++) {
                    JSONObject jObj2 = jObj.getJSONArray("givenAnswers").getJSONObject(i);
                    int index = jObj2.getJSONArray("submittedAnswers").length() - 1;
                    Log.e("INDEX", Integer.toString(index));

                    if(jObj2.getJSONArray("submittedAnswers").getJSONObject(index).getBoolean("isCorrect")){
                        QuizPersistence.sharedInstance(this).updateGroupScore(jObj2.getString("question"),
                                jObj2.getJSONArray("submittedAnswers").getJSONObject(index).getInt("points"));
                    }
                }
                ((Button)findViewById(R.id.groupQuizNextButton)).setEnabled(true);
                ((Button)findViewById(R.id.groupQuizNextButton)).performClick();
                Log.e("POSITION", Integer.toString(mPosition));
                Log.e("QANS", jObj.toString());
            }
            if (mPosition < jObj.getJSONArray("givenAnswers").length()) {
                ((Button)findViewById(R.id.groupQuizNextButton)).setEnabled(true);
                jsonArray = jObj.getJSONArray("givenAnswers").getJSONObject(mPosition).getJSONArray("submittedAnswers");
                for (int i = 0; i < jsonArray.length(); i++) {
                    String qVal = jsonArray.getJSONObject(i).getString("value");
                    boolean correct = jsonArray.getJSONObject(i).getBoolean("isCorrect");
                    int score = jsonArray.getJSONObject(i).getInt("points");
                    int ansNum = 0;
                    ArrayList<Answer> answers = mQuestions.get(i).getAvailableAnswers();
                    for (int j = 0; j < answers.size(); j++) {
                        if (answers.get(j).getValue().equals(qVal)) {
                            ansNum = j;
                            Log.e("VALUE", Integer.toString(ansNum));
                            break;
                        }
                    }
                    mQfrag.setAnswerStatus(ansNum, correct);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ERROR", jObj.toString());
            Toast toast = Toast.makeText(this, "Error getting group progress", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    @Override
    public void error(String error) {
        Toast toast = Toast.makeText(this, error, Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    public void groupQuestionScratched(int num, View view) {
        mLastCanvasParent = (FrameLayout)view.getParent();
        mCurrentAnswer = num;
        if (!correctAnswerChosen) {
            mApi.postGroupQuizQuestion(
                    mQuestions.get(mPosition).getId(),
                    mQuestions.get(mPosition).getAvailableAnswers().get(num).getValue()
            );
        } else {
            ImageView incorrect = (ImageView) mLastCanvasParent.findViewById(R.id.groupQuizScratchOffIncorrectImage);
            RelativeLayout loader = (RelativeLayout) mLastCanvasParent.findViewById(R.id.groupQuizScratchOffLoadingContainer);
            loader.setVisibility(View.GONE);
            incorrect.setVisibility(View.VISIBLE);

        }
    }

    private void startTimer() {
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                mApi.getGroupQuizProgress();
            }
        };
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(mTimerTask, 0, 5000);
    }
}