package edu.umsl.quizlet.QuizHistory;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;
import java.util.List;

import edu.umsl.quizlet.R;

/**
 * Created by klkni on 3/2/2017.
 */

public class QuizHistoryListingViewFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private WeakReference<QuizHistoryListingViewDataSource> mDataSource;

    public void setmDataSource(QuizHistoryListingViewDataSource dataSource){
        mDataSource = new WeakReference<>(dataSource);
    }
    public interface QuizHistoryListingViewDataSource{
        List<String> getQuizHistory();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.quiz_history_listing_view_fagment, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.quiz_history_list_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
      //  mRecyclerView.setAdapter(new QuizHistoryListAdaptor());
        return view;
    }

//    private class QuizHistoryListAdaptor extends RecyclerView.Adapter<QuizHistoryListHolder> {
//
//        @Override
//        public QuizHistoryListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            LayoutInflater inflater = LayoutInflater.from(getActivity());
//            View view = inflater.inflate(R.layout.quiz_history_item_layout, parent, false);
//            return new QuizHistoryListHolder(view);
//        }
//
//        @Override
//        public void onBindViewHolder(QuizHistoryListHolder holder, int position) {
//            if (mDataSource != null) {
//                holder.bindQuizHistory(mDataSource.get().getQuizHistory().get(position));
//            }
//        }
//
//        @Override
//        public int getItemCount() {
//            if (mDataSource != null) {
//                return mDataSource.get().getQuizHistory().size();
//            }
//            return 0;
//        }
//    }

}
