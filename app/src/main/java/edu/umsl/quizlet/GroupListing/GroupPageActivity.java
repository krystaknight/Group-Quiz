package edu.umsl.quizlet.GroupListing;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import java.util.ArrayList;

import edu.umsl.quizlet.API;
import edu.umsl.quizlet.GroupQuiz.GroupQuizActivity;
import edu.umsl.quizlet.GroupService;
import edu.umsl.quizlet.HttpWorkerFragment;
import edu.umsl.quizlet.R;
import edu.umsl.quizlet.SingleUserQuiz.QuestionSingleton;
import edu.umsl.quizlet.dataClasses.Group;
import edu.umsl.quizlet.dataClasses.GroupUser;
import edu.umsl.quizlet.dataClasses.User;
import edu.umsl.quizlet.database.QuizPersistence;

import static edu.umsl.quizlet.HttpWorkerFragment.HTTP_WORKER_FRAG;

/**
 * Created by Austin Edwards on 4/25/2017.
 */


public class GroupPageActivity extends AppCompatActivity implements GroupFragment.GroupListingViewDataSource, API.APIGroupListener {
    public static final String BROADCASTGROUPSTATUS = "GetGroupStatus";
    public static String QUIZ_ID_EXTRA = "QID_EXTRA";
    private String mQuizID;
    private TextView mGroupName;
    private GroupPageModel mGroupModel;
    private boolean groupInDB = false;
    private GroupFragment mGroupFrag;
    private API mApi;
    private IntentFilter mIntentFilter;
    private QuestionSingleton mSingleton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_listing_activity);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        mGroupFrag = (GroupFragment) manager.findFragmentById(R.id.fragmentContainer);
        if (mGroupFrag == null) {
            mGroupFrag = new GroupFragment();
            transaction.add(R.id.fragmentContainer, mGroupFrag);
        }

        if (mGroupModel == null) {
            mGroupModel = new GroupPageModel(this);
        }
        if( mSingleton == null){
            mSingleton = QuestionSingleton.get(this);
        }
        mQuizID = getIntent().getStringExtra(QUIZ_ID_EXTRA);
        mSingleton.setQuizId(mQuizID);

        transaction.commit();

        HttpWorkerFragment mHttpWorkerFragment = (HttpWorkerFragment) manager.findFragmentByTag(HTTP_WORKER_FRAG);
        if (mHttpWorkerFragment == null) {
            mHttpWorkerFragment = new HttpWorkerFragment();
            mHttpWorkerFragment.setmContext(this);
            transaction.add(mHttpWorkerFragment, HTTP_WORKER_FRAG);
        }

        //Setting up API
        mApi = new API(mHttpWorkerFragment, this);
        mApi.setGroupListener(this);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(BROADCASTGROUPSTATUS);
        registerReceiver(mReceiver, mIntentFilter);

        //Intent Service
        Intent service = new Intent(this, GroupService.class);
        startService(service);

        //If the group in the db -- please build everything here
        mGroupName = (TextView) findViewById(R.id.groupName);
        mGroupName.setText(mGroupModel.getGroupName());

        mGroupFrag.setDataSource(this);

        ArrayList<GroupUser> mGroupUsers = mGroupModel.getGroup().getGroupUsers();
        mGroupUsers.get(0).getFirst();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String data = intent.getStringExtra(BROADCASTGROUPSTATUS);
            switch (data) {
                case "GroupStatus":
                    mApi.getGroupStatus();
                    break;
            }
        }
    };


    @Override
    public Group getGroup() {
        return mGroupModel.getGroup();
    }

    //You can use this for a Toast to user if something happens in the API
    // (example token no found api line 182 ? profilePageActivity line 136
    public void error(String e) {
    }

    @Override
    public void groupStatus() {
        Log.e("Group Status", "Im getting called");
        mGroupFrag.changeView();
    }

    public void startGroupQuiz(View v) {
        Intent intent = new Intent(this, GroupQuizActivity.class);
        intent.putExtra(QUIZ_ID_EXTRA, QuizPersistence.sharedInstance(this).getSession().getQuizId());
        startActivity(intent);
        QuizPersistence.sharedInstance(this).setUserLocation(2);
        QuizPersistence.sharedInstance(this).setCurrentQuestion(0);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(mReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}
