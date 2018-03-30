package edu.umsl.quizlet.SingleUserQuiz;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import edu.umsl.quizlet.R;
import edu.umsl.quizlet.dataClasses.Answer;
import edu.umsl.quizlet.dataClasses.Question;

/**
 * Created by landon on 3/19/17.
 */

public class SubmitFragment extends Fragment implements View.OnClickListener {
    public TextView mSubmitTextView;
    public Button mSubmitButton;
    public ArrayList<Question> mQuestions;
    private RecyclerView mUnfinishedQuestionsRecyclerView;
    private UnfinishedAdapter mUnfinishedAdapter;
    private WeakReference<unfinishedQuestionClick> unfinishedQuestionClickListener;

    public interface unfinishedQuestionClick {
        void onUnfinishedQuestionClick(int position);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_single_user_quiz_confirmation, container, false);
        mUnfinishedQuestionsRecyclerView = (RecyclerView) view.findViewById(R.id.singleUserQuizSubmitUnfinishedQuestionsContainer);
        mUnfinishedQuestionsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mQuestions = QuestionSingleton.get(getActivity()).getQuestions();
        mSubmitTextView = ((TextView)view.findViewById(R.id.singleUserQuizSubmitTextView));
        mSubmitButton = ((Button)view.findViewById(R.id.singleUserQuizSubmitButton));
        mSubmitButton.setOnClickListener(this);
        updateUI();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    public void updateUI() {
        if (mUnfinishedAdapter == null) {
            mUnfinishedAdapter = new UnfinishedAdapter(getUnfinishedQuestions(), this.getContext());
            mUnfinishedQuestionsRecyclerView.setAdapter(mUnfinishedAdapter);
        } else {
            mUnfinishedAdapter.setmUnfinishedQuestions(getUnfinishedQuestions());
            mUnfinishedAdapter.notifyDataSetChanged();
        }
        if (mSubmitTextView != null) {
            if (getUnfinishedQuestions().size() == 0) {
                mSubmitTextView.setText("Looks like every question is complete!");
            } else {
                mSubmitTextView.setText("Unfinished questions:");
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mSubmitButton) {
            DialogFragment dialog = new SubmitDialog();
            dialog.show(getFragmentManager(), "SubmitDialog");
        }
    }

    public class UnfinishedAdapter extends RecyclerView.Adapter<UnfinishedAdapter.UnfinishedViewHolder> {
        private ArrayList<Integer> mUnfinishedQuestions;
        private Context mContext;

        public UnfinishedAdapter(ArrayList<Integer>mUnfinishedQuestions, Context mContext) {
            this.mUnfinishedQuestions = mUnfinishedQuestions;
            Log.e("UNFINISHEDCOUNT", Integer.toString(mUnfinishedQuestions.size()));
            this.mContext = mContext;
        }

        public void setmUnfinishedQuestions(ArrayList<Integer> mUnfinishedQuestions) {
            this.mUnfinishedQuestions = mUnfinishedQuestions;
        }

        @Override
        public UnfinishedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            View view = inflater.inflate(R.layout.fragment_single_user_submit_unfinished_question, parent, false);
            UnfinishedViewHolder viewHolder = new UnfinishedViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(UnfinishedViewHolder holder, int position) {
            holder.mQuestion = mQuestions.get(mUnfinishedQuestions.get(position));
            holder.setmPosition(mUnfinishedQuestions.get(position));
            holder.mUnfinishedQuestionNumberTextView.setText("Question " + (mUnfinishedQuestions.get(position) + 1));
            ArrayList<Answer> answers = mQuestions.get(mUnfinishedQuestions.get(position)).getAvailableAnswers();
            int possible = mQuestions.get(mUnfinishedQuestions.get(position)).getPointsPossible();
            int total = 0;
            for (Answer a : answers) {
                total += a.getConfidence();
            }
            holder.mUnfinishedQuestionCountTextView.setText(total + "/" + possible);
        }

        @Override
        public int getItemCount() {
            if (mUnfinishedQuestions != null)
                return mUnfinishedQuestions.size();
            else
                return 0;
        }

        public class UnfinishedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public TextView mUnfinishedQuestionNumberTextView;
            public TextView mUnfinishedQuestionCountTextView;
            private Question mQuestion;
            private int mPosition;
            public CardView mCardView;
            public UnfinishedViewHolder(View itemView) {
                super(itemView);
                mUnfinishedQuestionNumberTextView = (TextView)itemView.findViewById(R.id.singleUserQuizSubmitUnfinishedTextView);
                mUnfinishedQuestionCountTextView = (TextView)itemView.findViewById(R.id.singleUserQuizSubmitUnfinishedCountTextView);
                mCardView = (CardView)itemView.findViewById(R.id.singleUserQuizSubmitUnfinishedCardView);
                mCardView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                SubmitFragment.this.updateUI();
                unfinishedQuestionClickListener.get().onUnfinishedQuestionClick(mPosition);
            }

            public void setmPosition(int mPosition) {
                this.mPosition = mPosition;
            }
        }
    }

    public ArrayList<Integer> getUnfinishedQuestions() {
        ArrayList<Integer> unfinishedQuestions = new ArrayList<>();
        for (int i=0; i<mQuestions.size(); i++) {
            Question question = mQuestions.get(i);
            int total=0;
            for (Answer answer: question.getAvailableAnswers()) {
                total += answer.getConfidence();
            }
            if (total < question.getPointsPossible()) {
                Log.e("UNFINISHED", Integer.toString(i));
                unfinishedQuestions.add(i);
            }
        }
        return unfinishedQuestions;
    }

    public void setUnfinishedQuestionClickListener(unfinishedQuestionClick unfinishedQuestionClickListener) {
        this.unfinishedQuestionClickListener = new WeakReference<unfinishedQuestionClick>(unfinishedQuestionClickListener);
    }

    public void setmQuestions(ArrayList<Question> mQuestions) {
        this.mQuestions = mQuestions;
    }
}
