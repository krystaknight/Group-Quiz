package edu.umsl.quizlet.StudentCourseListing;

import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.support.v4.app.FragmentManager;

import edu.umsl.quizlet.TokenDialog;
import edu.umsl.quizlet.R;
import edu.umsl.quizlet.dataClasses.Course;

/**
 * Created by klkni on 4/11/2017.
 */

public class CourseHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView mCourseTextView;
    private StudentCourseModel mModel;
    FragmentManager mManager;

    public CourseHolder(View itemView , FragmentManager manager){
    super(itemView);
        mCourseTextView = (TextView) itemView.findViewById(R.id.student_course_text_view);
        mManager = manager;
        itemView.setOnClickListener(this);
    }

    public void bindCourse(Course c){
        mCourseTextView.setText(c.getName());
    }

    public void onClick(View v){
        Context context = v.getContext();
        if(mModel==null){
            mModel = new StudentCourseModel(context);
        }
        String courseName = mCourseTextView.getText().toString();
        mModel.setCourse(courseName);

        DialogFragment dialog = new TokenDialog();
        dialog.show(mManager, courseName);

        Log.e("COURSE LIST", "Clicked: " + this + " at position: " + getAdapterPosition());

    }

}
