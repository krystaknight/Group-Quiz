package edu.umsl.quizlet;

import android.content.Context;

import edu.umsl.quizlet.StudentCourseListing.StudentCourseModel;
import edu.umsl.quizlet.database.UserInfoPersistence;


/**
 * Created by klkni on 4/14/2017.
 */


public class ProfilePageModel {
    private UserInfoPersistence mPersistence;
    private StudentCourseModel mModel;
    private String firstName = "";
    private String lastName = "";

    ProfilePageModel(Context context){
        mPersistence = UserInfoPersistence.sharedInstance(context);
    }



    public String getFirstName(){
       if(firstName == ""){
           firstName = mPersistence.getUser().getFirst();
       }
       return firstName;

    }
    public String getLastName() {
        if (lastName == "") {
            lastName = mPersistence.getUser().getLast();
        }
        return lastName;
    }

    public void deleteUser(){
        mPersistence.deleteUser();
    }

}
