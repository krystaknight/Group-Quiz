package edu.umsl.quizlet.SingleUserQuiz;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.umsl.quizlet.R;
import edu.umsl.quizlet.dataClasses.Answer;
import edu.umsl.quizlet.dataClasses.Question;

/**
 * Created by landon on 4/18/17.
 */

public class QuestionFragment extends Fragment implements AnswerRecyclerViewFragment.pointsAllocatedUpdated {
    private Context mContext;
    public View mQuestionFragment;
    private Question mQuestionModel;
    private int mQuestionNumber;
    private static String QUESTION_NUMBER = "questionNumber";

    public Question getQuestionModel() {
        return mQuestionModel;
    }

    public void setQuestionModel(Question questionModel) {
        this.mQuestionModel = questionModel;
    }

    public void setQuestionNumber(int num) {
        mQuestionNumber = num;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mQuestionFragment =  inflater.inflate(R.layout.fragment_single_user_quiz_question, container, false);
        return mQuestionFragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            mQuestionNumber = (int) savedInstanceState.getSerializable(QUESTION_NUMBER);
            mContext = getActivity();
            mQuestionModel = QuestionSingleton.get(mContext).getQuestion(mQuestionNumber);
        }

        ((TextView)mQuestionFragment.findViewById(R.id.singleUserQuizQuestionTextView)).setText(mQuestionModel.getText());
        int pointsRemaining = mQuestionModel.getPointsPossible();
        for (Answer a : mQuestionModel.getAvailableAnswers()) {
            pointsRemaining -= a.getConfidence();
        }
        ((TextView)mQuestionFragment.findViewById(R.id.singleUserQuizPointsRemainingTextView)).setText(Integer.toString(pointsRemaining)+ " points left");
//        ((TextView)mQuestionFragment.findViewById(R.id.singleUserQuizQuestionTitleTextView)).setText(mQuestionModel.getTitle());
        ((TextView)mQuestionFragment.findViewById(R.id.singleUserQuizPointsPossibleTextView)).setText(mQuestionModel.getPointsPossible() + " points");
        ((TextView)mQuestionFragment.findViewById(R.id.singleUserQuizQuestionNumberTextView)).setText("Question " +Integer.toString(mQuestionNumber + 1));

        FragmentManager fman = getChildFragmentManager();
        AnswerRecyclerViewFragment afrag = new AnswerRecyclerViewFragment();
        afrag.setmAnswers(mQuestionModel.getAvailableAnswers());
        afrag.setmQuestionNumber(mQuestionNumber);
        afrag.setQuestionValue(mQuestionModel.getPointsPossible());
        afrag.setmListener(QuestionFragment.this);
        FragmentTransaction ftran = fman.beginTransaction();
        ftran.add(R.id.singleUserQuizAnswerContainer, afrag, "AnswerRecyclerViewTag");
        ftran.commit();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void pointsUpdated(int total) {
        if (total != -1)
            ((TextView)mQuestionFragment.findViewById(R.id.singleUserQuizPointsRemainingTextView)).setText(Integer.toString(mQuestionModel.getPointsPossible() - total)+ " points left");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mQuestionNumber = (int) savedInstanceState.getSerializable(QUESTION_NUMBER);
            mContext = getActivity();
            mQuestionModel = QuestionSingleton.get(mContext).getQuestion(mQuestionNumber);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(QUESTION_NUMBER, mQuestionNumber);
        super.onSaveInstanceState(outState);
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }
}
