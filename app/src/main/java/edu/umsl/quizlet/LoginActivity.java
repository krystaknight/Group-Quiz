package edu.umsl.quizlet;

import android.content.Intent;
import android.os.Bundle;
import android.support.transition.TransitionManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import edu.umsl.quizlet.GroupListing.GroupPageActivity;
import edu.umsl.quizlet.GroupQuiz.GroupQuizActivity;
import edu.umsl.quizlet.SingleUserQuiz.SingleUserQuizActivity;
import edu.umsl.quizlet.dataClasses.Course;
import edu.umsl.quizlet.dataClasses.Group;
import edu.umsl.quizlet.dataClasses.Quiz;
import edu.umsl.quizlet.dataClasses.Session;
import edu.umsl.quizlet.dataClasses.User;

import edu.umsl.quizlet.dataClasses.User;


import static edu.umsl.quizlet.HttpWorkerFragment.HTTP_WORKER_FRAG;

public class LoginActivity extends AppCompatActivity implements API.APIListener {

    private Button mLoginButton; //Test
    private EditText mUserNameEditText;
    private EditText mUserPassEditText;
    private LoginPageModel mLoginPageModel;
    private HttpWorkerFragment mHttpWorkerFragment;
    private API mApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(mLoginPageModel == null){
            mLoginPageModel = new LoginPageModel(this);
        }

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
        mApi.setListener(this);

        transaction.commit();

        mLoginButton = (Button) findViewById(R.id.loginButton);
        mLoginButton.setOnClickListener(new View.OnClickListener () {
            boolean visible;
            @Override
            public void onClick(View view) {
                validateUser();
            }
        });

        mUserNameEditText = (EditText) findViewById(R.id.userNameEditText);
//        mUserPassEditText = (EditText) findViewById(R.id.passwordEditText);


        //mUserPassEditText = (EditText) findViewById(R.id.passwordEditText);
        mHttpWorkerFragment.setmContext(this);
        mApi = new API(mHttpWorkerFragment, this);
        mApi.setListener(this);

        // Checking if the user has already logged in, If they are then sending user to StartProfile
        User usr = null;
        if ((usr = mLoginPageModel.checkIfUserHasLoggedIn()) != null) {
            // user exists, skip login screen
            Toast.makeText(this, "Welcome back "+ usr.getFirst() + " " + usr.getLast(), Toast.LENGTH_SHORT).show();
            goToUsersCurrentActivity(usr);
        } else {
            Toast.makeText(this, "Welcome!", Toast.LENGTH_SHORT).show();
        }
    }

    private void goToUsersCurrentActivity(User usr){
        Session session = null;
        //if the user has started a quiz and moved to new windows
        if((session = mLoginPageModel.checkIfQuizInProgress()) != null){
            //0 -> Single User Quiz | 1 = > Group Waiting Page | 2 -> Group Quiz
            int userStatus = session.getUserStatus();

            Intent intent;
            switch(userStatus){
                case 0: //User was in the middle of the quiz and exited out of the app
                    intent = new Intent(this, SingleUserQuizActivity.class);
                    intent.putExtra(SingleUserQuizActivity.QUIZ_ID_EXTRA, session.getQuizId());
                    startActivity(intent);
                    break;
                case 1:
                    intent = new Intent(this, GroupPageActivity.class);
                    startActivity(intent);
                    break;
                case 2:
                    intent = new Intent(this, GroupQuizActivity.class);
                    intent.putExtra(SingleUserQuizActivity.QUIZ_ID_EXTRA, session.getQuizId());
                    startActivity(intent);
                    break;
                case 3:
                    intent = new Intent(this, ResultsActivity.class);
                    startActivity(intent);
                    break;
                default: //if any shenanigan's happen then user goes back to profile page
                    mApi.updateCourses(usr.getUserID()); //Updates Courses
                    startProfile();
                    break;
            }
        }
        else{
            mApi.updateCourses(usr.getUserID()); //Updates Courses
            startProfile();
        }
        finish(); // This way the user can't go to the Login screen by hitting the back button
    }

    public void startProfile(){
        startActivity(new Intent(this, ProfilePageActivity.class));
    }

    public void validateUser() {
        String userID = mUserNameEditText.getEditableText().toString();

        if (userID.equals("")){ // no user entered
            Toast.makeText(this, "UserName Cannot Be Blank!", Toast.LENGTH_SHORT).show();
        } else {
            mApi.getUser(userID);

        }
    }

    //private void set

    @Override
    public void userFound(User usr) {
        Log.e("LoginActivity", usr.getFirst());
        Toast.makeText(this, "User Created: " + usr.getFirst() + " " + usr.getLast(), Toast.LENGTH_SHORT).show();
        mApi.getQuizzes(usr.getUserID());
        startProfile();
        finish(); // This way the user can't go to the Login screen by hitting the back button
    }

    @Override
    public void coursesUpdated(String userID) {
        Log.e("TAG", "CoursesUpdated UserID Passed " + userID);
        mApi.getQuizzes(userID);
    }


    public void quizDownloaded(String quizID) {

    }

    @Override
    public void error(String e) {
        Toast.makeText(this, e, Toast.LENGTH_SHORT).show();
    }
}