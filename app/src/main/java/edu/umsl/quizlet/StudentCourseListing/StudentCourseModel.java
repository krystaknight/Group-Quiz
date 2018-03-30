package edu.umsl.quizlet.StudentCourseListing;

import android.content.Context;

import java.util.List;
import edu.umsl.quizlet.database.*;
import edu.umsl.quizlet.dataClasses.*;
/**
 * Created by klkni on 4/6/2017.
 */

public class StudentCourseModel {
    private List<Course> mCourses;
    private UserInfoPersistence mPersistence;

    public StudentCourseModel(Context context){mPersistence = UserInfoPersistence.sharedInstance(context);}

    public List<Course> getCourses(){
        if(mCourses == null){
            mCourses = mPersistence.getCourses();
        }
        return mCourses;
    }


    protected void setCourse(String name){
        mPersistence.setClickedCourse(name);
    }

    public String getCourseClicked(){
        return  mPersistence.getClickedCourseId();
    }


}
