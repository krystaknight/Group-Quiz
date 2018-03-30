package edu.umsl.quizlet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import edu.umsl.quizlet.GroupListing.GroupPageActivity;

import edu.umsl.quizlet.SingleUserQuiz.SingleUserQuizActivity;
import edu.umsl.quizlet.StudentCourseListing.CourseListingFragment;
import edu.umsl.quizlet.StudentCourseListing.StudentCourseModel;
import edu.umsl.quizlet.dataClasses.Course;
import edu.umsl.quizlet.dataClasses.User;

import static edu.umsl.quizlet.HttpWorkerFragment.HTTP_WORKER_FRAG;


/**
 * Created by Krysta Knight on 3/2/2017.

 */

public class ProfilePageActivity extends AppCompatActivity implements TokenDialog.NoticeDialogListener,CourseListingFragment.StudentCourseListingViewDataSource,API.APIListener {

    private StudentCourseModel mCourseModel;
    private CourseListingFragment mCourseListFrag;
    private HttpWorkerFragment mHttpWorkerFragment;
    private TextView mUserName;
    private ProfilePageModel mProfileModel;
    private API mApi;
    private View mLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        setContentView(R.layout.profile_page_activity);

        if (mCourseModel == null) {
            mCourseModel = new StudentCourseModel(this);
        }
        if (mProfileModel == null) {
            mProfileModel = new ProfilePageModel(this);
        }

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        mCourseListFrag = (CourseListingFragment) manager.findFragmentById(R.id.fragmentContainer);
        if (mCourseListFrag == null) {
            mCourseListFrag = new CourseListingFragment();
            transaction.add(R.id.fragmentContainer, mCourseListFrag);
        }

        mHttpWorkerFragment = (HttpWorkerFragment) manager.findFragmentByTag(HTTP_WORKER_FRAG);
        if (mHttpWorkerFragment == null) {
            mHttpWorkerFragment = new HttpWorkerFragment();
            transaction.add(mHttpWorkerFragment, HTTP_WORKER_FRAG);
        }
        transaction.commit();

        mLoading = (View) findViewById(R.id.loadingPanel);

        mUserName = (TextView) findViewById(R.id.userName);
        mUserName.setText("Hey " + mProfileModel.getFirstName() + "!");

        mCourseListFrag.setDataSource(this);

        mApi = new API(mHttpWorkerFragment, this);
        mHttpWorkerFragment.setmContext(this);
        mApi.setListener(this);

        User usr = mApi.checkForUser();


    }

    @Override
    public List<Course> getCourses() {
        return mCourseModel.getCourses();
    }

    public void deleteUser(View v) {
        mProfileModel.deleteUser();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    public void showGroup(View v) {
        startActivity(new Intent(this, GroupPageActivity.class));
    }

    /* Implementing Token input from the user Using Dialog
    *  OpenStartQuiz Creates the instance of the dialog and shows it.
    *  Token input verification is Handled in TokenDialog.java
    */
    @Override
    public void getQuiz(String token) {
        String id = mCourseModel.getCourseClicked();
//        Log.e("Course ID", "Id=" + id);
        mApi.getQuiz(token, id); // Getting Quiz
        mLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void userFound(User user) {
    }

    @Override
    public void coursesUpdated(String userID) {
    }


    public void quizDownloaded(String quizID) {
        mApi.getGroupForUser();
        Intent intent = new Intent(this, SingleUserQuizActivity.class);
        intent.putExtra(SingleUserQuizActivity.QUIZ_ID_EXTRA, quizID);
        startActivity(intent);
        finish();
    }

    // Displaying Error message to the user if Invalid QuizID
    @Override
    public void error(String e) {
        Toast.makeText(this, e, Toast.LENGTH_SHORT).show();
        mLoading.clearAnimation();
        mLoading.setVisibility(View.INVISIBLE);
        recreate();
    }
}
