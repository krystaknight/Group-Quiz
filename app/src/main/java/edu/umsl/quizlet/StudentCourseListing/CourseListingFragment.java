package edu.umsl.quizlet.StudentCourseListing;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;
import java.util.List;

import edu.umsl.quizlet.R;
import edu.umsl.quizlet.dataClasses.Course;

/**
 * Created by klkni on 3/23/2017.
 *
 * Get classes that student it enrolled in and output to recycler view
 *
 */

public class CourseListingFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private WeakReference<StudentCourseListingViewDataSource> mDataSource;
    FragmentManager mManager;

    public void setDataSource(StudentCourseListingViewDataSource dataSource) {
        mDataSource = new WeakReference<>(dataSource);
    }
    public interface StudentCourseListingViewDataSource {
        List<Course> getCourses();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.course_listing_frag, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.student_class_list_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(new StudentCourseAdapter());
        return view;
    }

    private class StudentCourseAdapter extends RecyclerView.Adapter<CourseHolder> {

        @Override
        public CourseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            mManager = getFragmentManager();
            View view = inflater.inflate(R.layout.course_item_layout, parent, false);
            return new CourseHolder(view , mManager);
        }
        @Override
        public void onBindViewHolder(CourseHolder holder, int position) {
            if (mDataSource != null) {
                holder.bindCourse(mDataSource.get().getCourses().get(position));
            }
        }

        @Override
        public int getItemCount() {
            if (mDataSource != null) {
                return mDataSource.get().getCourses().size();

            }
            Log.e("NO COURSES", "This User has no courses.");
            return 0;
        }

    }

}
