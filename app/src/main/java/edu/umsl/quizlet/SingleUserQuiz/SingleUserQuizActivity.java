package edu.umsl.quizlet.SingleUserQuiz;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.format.Time;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import edu.umsl.quizlet.API;
import edu.umsl.quizlet.GroupListing.GroupPageActivity;
import edu.umsl.quizlet.GroupQuiz.GroupQuizActivity;
import edu.umsl.quizlet.HttpWorkerFragment;
import edu.umsl.quizlet.R;
import edu.umsl.quizlet.dataClasses.Question;
import edu.umsl.quizlet.database.QuizPersistence;

import static edu.umsl.quizlet.HttpWorkerFragment.HTTP_WORKER_FRAG;

/**
 * Created by landon on 4/18/17.
 */

public class SingleUserQuizActivity extends FragmentActivity implements SubmitDialog.NoticeDialogListener, ViewPager.OnPageChangeListener, QuestionAdapter.unfinishedQuestionClick, API.APIQuizListener, API.APIGroupListener {
    public static String QUIZ_ID_EXTRA = "QID_EXTRA";
    public static String FRAGMENT_KEY = "mPager";
    private String mQuizID;

    private int mCurrentQuestion;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private TextView mTimerTextView;
    private ProgressBar mTimerProgressBar;
    private Date mTimeToEnd;
    private CountDownTimer mTimer;
    private HttpWorkerFragment mHttpWorkerFragment;
    private API mApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        setContentView(R.layout.single_user_quiz_viewpager);

        mQuizID = getIntent().getStringExtra(QUIZ_ID_EXTRA);

        mCurrentQuestion = QuizPersistence.sharedInstance(this).getSession().getCurrentQuestion();

        mPager = (ViewPager) findViewById(R.id.singleUserQuizQuestionListRecycler);
        mTimerTextView = (TextView) findViewById(R.id.singleUserQuizTimerTextView);
        mTimerProgressBar = (ProgressBar) findViewById(R.id.singleUserQuizTimerProgressBar);
        mTimeToEnd = QuizPersistence.sharedInstance(this).getSession().getTimeRemaining();
        startTimer();

        QuestionSingleton questionSingleton = QuestionSingleton.get(this);
        questionSingleton.setQuizId(mQuizID);
        ArrayList<Question> questions = questionSingleton.getQuestions();

        mTimerProgressBar.setMax(questions.size());
        mTimerProgressBar.setProgress(0);
        mPagerAdapter = new QuestionAdapter(questions, this, getSupportFragmentManager());
        ((QuestionAdapter)mPagerAdapter).setmUnfinishedQuestionClickListener(this);
        mPager.setAdapter(mPagerAdapter);
        mPager.addOnPageChangeListener(this);
        mPager.setCurrentItem(mCurrentQuestion);

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
        mApi.setQuizListener(this);
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // Exit the activity
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    public void startGroupPage(){
        Intent intent = new Intent(this, GroupPageActivity.class);
        intent.putExtra(GroupPageActivity.QUIZ_ID_EXTRA, QuestionSingleton.get(this).getQuizId());
        startActivity(intent);
    }

    @Override
    public void submitQuiz() {
        if (mTimer != null) {
            mTimer.cancel();
        }
        Log.e("SUBMIT", "Submitting here in theory");
        Log.e("POSTING", QuizPersistence.sharedInstance(this).getQuiz(QuestionSingleton.get(this)
                .getQuizId()).getPostJson().toString());
//        startGroupPage();



        // Temporary to allow for testing group quiz
//        Intent intent = new Intent(this, GroupQuizActivity.class);
//        intent.putExtra(QUIZ_ID_EXTRA, QuestionSingleton.get(this).getQuizId());
//        startActivity(intent);
        mApi.postQuizForGrading(QuizPersistence.sharedInstance(this).getQuiz(QuestionSingleton.get(this)
                .getQuizId()).getPostJson());
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position == mPagerAdapter.getCount() - 1) {
            ((QuestionAdapter)mPagerAdapter).setSubmitFragmentText();
        }
        mTimerProgressBar.setProgress(position);
        QuizPersistence.sharedInstance(this).setCurrentQuestion(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    public void openSubmitDialog() {
        DialogFragment dialog = new SubmitDialog();
        dialog.show(getSupportFragmentManager(), "SubmitDialogNoCancel");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mTimer != null)
            mTimer.cancel();
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (mTimeToEnd == null) {
            mTimeToEnd = QuizPersistence.sharedInstance(this).getSession().getTimeRemaining();
        }
        startTimer();
    }

    @Override
    protected void onDestroy() {
        if (mTimer != null)
            mTimer.cancel();
        super.onDestroy();
    }

    @Override
    public void onUnfinishedQuestionClick(int position) {
        mPager.setCurrentItem(position);
    }

    @Override
    public void quizSubmitted(boolean quizSubmitted) {
        // After quiz is posted
        QuizPersistence.sharedInstance(this).setCurrentQuestion(0);
        QuizPersistence.sharedInstance(this).setUserLocation(1);
        mApi.setGroupListener(this);
        mApi.getGroupStatus(); //getting group status before user goes into GroupPageActivity
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mTimer != null) {
            mTimer.cancel();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mTimeToEnd == null) {
            mTimeToEnd = QuizPersistence.sharedInstance(this).getSession().getTimeRemaining();
        }
        startTimer();
    }

    private void startTimer() {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.MILLISECOND, Calendar.ZONE_OFFSET + Calendar.DST_OFFSET);
        if (mTimeToEnd.getTime() > now.getTime().getTime()) {
            mTimer = new CountDownTimer((mTimeToEnd.getTime() - now.getTime().getTime()), 500) {
                boolean flashing = false, red = false, yellow = false;

                public void onTick(long millisUntilFinished) {
                    long seconds = millisUntilFinished / 1000;
                    long minutes = seconds / 60;
                    seconds = seconds % 60;
                    if (!yellow && minutes < 5) {
                        mTimerTextView.setTextColor(getResources().getColor(R.color.single_user_quiz_timer_med));
                        yellow = true;
                    }
                    if (!red && minutes < 1) {
                        mTimerTextView.setTextColor(getResources().getColor(R.color.single_user_quiz_timer_high));
                        red = true;
                    }
                    if (!flashing && minutes < 1 && seconds < 15) {
                        Log.e("COLOR", "Flashing");
                        Animation anim = new AlphaAnimation(0.0f, 1.0f);
                        anim.setDuration(300);
                        anim.setStartOffset(0);
                        anim.setRepeatMode(Animation.REVERSE);
                        anim.setRepeatCount(Animation.INFINITE);
                        mTimerTextView.startAnimation(anim);
                        flashing = true;
                    }
                    mTimerTextView.setText(minutes + ":" + String.format("%02d", seconds));
                }

                ;

                public void onFinish() {
                    mTimerTextView.setText("TIME");
                    mTimerTextView.setTextColor(Color.BLACK);
                    mTimerTextView.clearAnimation();
                    openSubmitDialog();
                }
            }.start();
        } else {
            mTimerTextView.setText("TIME");
            openSubmitDialog();
        }
    }

    @Override
    public void error(String error) {

    }

    @Override
    public void groupStatus() {
        startGroupPage();
        finish();
    }
}