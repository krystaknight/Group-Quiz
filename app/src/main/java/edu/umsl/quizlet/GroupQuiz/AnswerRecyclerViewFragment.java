package edu.umsl.quizlet.GroupQuiz;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import edu.umsl.quizlet.R;
import edu.umsl.quizlet.dataClasses.Answer;
import edu.umsl.quizlet.database.QuizPersistence;

/**
 * Created by landon on 3/19/17.
 */

public class AnswerRecyclerViewFragment extends Fragment {
    private RecyclerView mAnswerRecyclerView;
    private AnswerAdapter mAnswerAdapter;
    private ArrayList<Answer> mAnswers;
    private QuestionSingleton sQuestions;
    private WeakReference<groupQuestionScratch> mListener;
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

            View view = inflater.inflate(R.layout.fragment_group_user_quiz_answer, parent, false);
            view.setSaveEnabled(true);
            AnswerViewHolder viewHolder = new AnswerViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(AnswerViewHolder holder, int position) {
            Answer a = mAnswers.get(position);
            holder.mAnswerTextView.setText(a.getText());
            holder.setmPosition(position);
            holder.mPointsAllocatedTextView.setText(Integer.toString(a.getConfidence()));
            Log.e("CANVASID", String.valueOf(holder.mErasableCanvas.getId()));
//            if (holder.mErasableCanvas == null) {
//                View v = new ErasableCanvas(getContext());
//                v.setId(View.generateViewId());
//                Log.e("ERASABLE ID", String.valueOf(v.getId()));
//                holder.mCanvasContainer.addView(v);
//                holder.mErasableCanvas = (ErasableCanvas)v;
//                holder.setmPosition(position);
//                holder.setmAnswer(a);
//            }
        }

        @Override
        public int getItemCount() {
            if (mAnswers != null)
                return mAnswers.size();
            else
                return 0;
        }

        public class AnswerViewHolder extends RecyclerView.ViewHolder implements ErasableCanvas.OnCustomClickListener {
            public TextView mAnswerTextView;
            public TextView mPointsAllocatedTextView;
            public ErasableCanvas mErasableCanvas;
            public ImageView mCorrectImageView;
            public ImageView mIncorrectImageView;
            public Parcelable mErasableCanvasSaveState;
            public boolean hasBeenClicked;
            private int mPosition;
            private Answer mAnswer;
            public AnswerViewHolder(View itemView) {
                super(itemView);
                hasBeenClicked = false;
                mAnswerTextView = (TextView)itemView.findViewById(R.id.groupQuizAnswerTextView);
                mPointsAllocatedTextView = (TextView)itemView.findViewById(R.id.groupQuizAnswerPointsAllocatedTextView);
                mAnswerTextView = (TextView) itemView.findViewById(R.id.groupQuizAnswerTextView);
                mPointsAllocatedTextView = (TextView) itemView.findViewById(R.id.groupQuizAnswerPointsAllocatedTextView);
                mErasableCanvas = (ErasableCanvas)itemView.findViewById(R.id.groupQuizScratchOffCanvas);
                mCorrectImageView = (ImageView)itemView.findViewById(R.id.groupQuizScratchOffCorrectImage);
                mIncorrectImageView = (ImageView)itemView.findViewById(R.id.groupQuizScratchOffIncorrectImage);
                mErasableCanvas.setmClickListener(this);
                if (!QuizPersistence.sharedInstance(mContext).getSession().isLeader()) {
                    mErasableCanvas.setVisibility(View.GONE);
                    itemView.findViewById(R.id.groupQuizScratchOffLoadingContainer).setVisibility(View.GONE);
                }
            }

            public void setmPosition(int mPosition) {
                this.mPosition = mPosition;
            }

            public void setmAnswer(Answer mAnswer) {
                this.mAnswer = mAnswer;
            }

            @Override
            public void OnClick(View v) {
                Log.e("ERASEABLE", "Click on answer " + mPosition);
                if (mListener != null && !hasBeenClicked) {
                    mListener.get().groupQuestionScratch(mPosition, v);
                    hasBeenClicked = true;
                }
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

    public void setmListener(groupQuestionScratch mListener) {
        this.mListener = new WeakReference<groupQuestionScratch>(mListener);
    }

    interface groupQuestionScratch {
        void groupQuestionScratch(int number, View view);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    public void setAnswerStatus(int num, boolean isCorrect) {
        AnswerAdapter.AnswerViewHolder holder = (AnswerAdapter.AnswerViewHolder)mAnswerRecyclerView.findViewHolderForAdapterPosition(num);
        if (isCorrect) {
            holder.mCorrectImageView.setVisibility(View.VISIBLE);
        } else {
            holder.mIncorrectImageView.setVisibility(View.VISIBLE);
        }
    }
}
