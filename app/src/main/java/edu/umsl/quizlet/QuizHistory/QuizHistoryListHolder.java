package edu.umsl.quizlet.QuizHistory;

/**
 * Created by klkni on 3/14/2017.
 */
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import edu.umsl.quizlet.R;


public class QuizHistoryListHolder extends RecyclerView.ViewHolder{
    private TextView mQuizTitleTextView;
    private TextView mQuizScoreTextView;
    private TextView mQuizClassTextView;

    public QuizHistoryListHolder(View itemView){
        super(itemView);
        mQuizClassTextView = (TextView) itemView.findViewById(R.id.quiz_history_class_text_view);
        mQuizScoreTextView = (TextView) itemView.findViewById(R.id.quiz_history_score_text_view);
        mQuizTitleTextView = (TextView) itemView.findViewById(R.id.quiz_history_title_text_view);
    }

    public void bindQuizHistory(String quizTitle, String quizScore, String quizClass) {
        mQuizTitleTextView.setText(quizTitle);
        mQuizScoreTextView.setText(quizScore);
        mQuizClassTextView.setText(quizClass);
    }



}
