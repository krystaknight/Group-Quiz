package edu.umsl.quizlet;

import android.content.Context;

import edu.umsl.quizlet.database.QuizPersistence;
import edu.umsl.quizlet.database.UserInfoPersistence;
import edu.umsl.quizlet.dataClasses.Session;
import edu.umsl.quizlet.dataClasses.User;

import static edu.umsl.quizlet.HttpWorkerFragment.HTTP_WORKER_FRAG;

/**
 * Created by hsswx7 on 5/4/2017.
 */

public class LoginPageModel {
    private UserInfoPersistence sPersistence; //Allows Model access to User's info stored in the PhoneDb
    private QuizPersistence sQuizPersistence; //Allows Model access to Quiz's info stored in the PhoneDb
    private HttpWorkerFragment mHttpWorkerFragment;

    LoginPageModel(Context context){
        sPersistence = UserInfoPersistence.sharedInstance(context);
        sQuizPersistence = QuizPersistence.sharedInstance(context);
    }

    public User checkIfUserHasLoggedIn(){
        User usr = sPersistence.getUser();
        if(usr != null){
            return usr;
        }else {
            return null;
        }
    }

    public Session checkIfQuizInProgress(){
        Session session = sQuizPersistence.getSession();
        if(session !=null){
            return session;
        }else{
            return null;
        }
    }

}
