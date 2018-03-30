package edu.umsl.quizlet.SingleUserQuiz;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import edu.umsl.quizlet.R;
import edu.umsl.quizlet.dataClasses.Answer;

/**
 * Created by landon on 3/19/17.
 */

public class AnswerRecyclerViewFragment extends Fragment {
    private RecyclerView mAnswerRecyclerView;
    private AnswerAdapter mAnswerAdapter;
    private ArrayList<Answer> mAnswers;
    private QuestionSingleton sQuestions;
    private WeakReference<pointsAllocatedUpdated> mListener;
    private int mQuestionValue;
    private int mQuestionNumber;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_single_user_answer_recycler, container, false);
        mAnswerRecyclerView = (RecyclerView) view.findViewById(R.id.singleUserQuizAnswerRecyclerView);
        mAnswerRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        sQuestions = QuestionSingleton.get(getActivity());
        updateUI();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    public void updateUI() {
        if (mAnswerAdapter == null) {
            mAnswerAdapter = new AnswerAdapter(mAnswers, this.getContext());
            mAnswerAdapter.setmQuestionValue(this.mQuestionValue);
            mAnswerRecyclerView.setAdapter(mAnswerAdapter);
        } else {
            mAnswerAdapter.setmAnswers(mAnswers);
            mAnswerAdapter.setmQuestionValue(mQuestionValue);
            mAnswerAdapter.notifyDataSetChanged();
        }
    }

    public class AnswerAdapter extends RecyclerView.Adapter<AnswerAdapter.AnswerViewHolder> {
        private ArrayList<Answer> mAnswers;
        private int mQuestionValue;
        private Context mContext;

        public AnswerAdapter(ArrayList<Answer> mAnswers, Context mContext) {
            this.mAnswers = mAnswers;
            this.mContext = mContext;
        }

        public void setmAnswers(ArrayList<Answer> mAnswers) {
            this.mAnswers = mAnswers;
        }

        public void setmQuestionValue(int mQuestionValue) {
            this.mQuestionValue = mQuestionValue;
        }

        @Override
        public AnswerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            View view = inflater.inflate(R.layout.fragment_single_user_quiz_answer, parent, false);
            AnswerViewHolder viewHolder = new AnswerViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(AnswerViewHolder holder, int position) {
            Answer a = mAnswers.get(position);
            holder.mAnswerTextView.setText(a.getText());
            holder.mPointsAllocatedTextView.setText(Integer.toString(a.getConfidence()));
            holder.mValueTextView.setText(a.getValue());
            holder.mPointsAllocatedProgressBar.setProgress(a.getConfidence());
            holder.mPointsAllocatedProgressBar.setMax(mQuestionValue);
            holder.setmPosition(position);
            holder.setmAnswer(a);
        }

        @Override
        public int getItemCount() {
            if (mAnswers != null)
                return mAnswers.size();
            else
                return 0;
        }

        public class AnswerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public TextView mAnswerTextView;
            public TextView mPointsAllocatedTextView;
            public ProgressBar mPointsAllocatedProgressBar;
            public Button mSubtractPointButton;
            public Button mAddPointButton;
            public TextView mValueTextView;
            private int mPosition;
            private Answer mAnswer;
            public AnswerViewHolder(View itemView) {
                super(itemView);
                mAnswerTextView = (TextView)itemView.findViewById(R.id.singleUserQuizAnswerTextView);
                mPointsAllocatedTextView = (TextView)itemView.findViewById(R.id.singleUserQuizAnswerPointsAllocatedTextView);
                mPointsAllocatedProgressBar = (ProgressBar) itemView.findViewById(R.id.singleUserQuizPercentOfPointsProgressBar);
                mSubtractPointButton = ((Button)itemView.findViewById(R.id.singleUserQuizSubtractPointButton));
                mAddPointButton = ((Button)itemView.findViewById(R.id.singleUserQuizAddPointButton));
                mValueTextView = ((TextView)itemView.findViewById(R.id.singleUserQuizAnswerValueTextView));
                mSubtractPointButton.setOnClickListener(this);
                mAddPointButton.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                int total = -1;
                if (v == mAddPointButton) {
                    total = sQuestions.addPointToAnswer(mQuestionNumber, mPosition);
                } else if (v == mSubtractPointButton) {
                    total = sQuestions.subtractPointFromAnswer(mQuestionNumber, mPosition);
                }
                mListener.get().pointsUpdated(total);
                AnswerRecyclerViewFragment.this.updateUI();
            }

            public void setmPosition(int mPosition) {
                this.mPosition = mPosition;
            }

            public void setmAnswer(Answer mAnswer) {
                this.mAnswer = mAnswer;
            }
        }
    }

    public void setmAnswers(ArrayList<Answer> mAnswers) {
        this.mAnswers = mAnswers;
    }

    public void setQuestionValue(int value) {this.mQuestionValue = value;}

    public void setmQuestionNumber(int mQuestionNumber) {
        this.mQuestionNumber = mQuestionNumber;
    }

    public void setmListener(pointsAllocatedUpdated mListener) {
        this.mListener = new WeakReference<pointsAllocatedUpdated>(mListener);
    }

    interface pointsAllocatedUpdated {
        void pointsUpdated(int total);
    }
}
