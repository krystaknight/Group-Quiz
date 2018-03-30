package edu.umsl.quizlet.SingleUserQuiz;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import edu.umsl.quizlet.dataClasses.Question;

/**
 * Created by landon on 4/27/17.
 */

public class QuestionAdapter extends FragmentStatePagerAdapter implements SubmitFragment.unfinishedQuestionClick {
    private ArrayList<Question> mQuestions;
    private QuestionSingleton sQuestionSingleton;
    private Context mContext;
    private SubmitFragment mSubmitFragment;
    private FragmentManager mFMan;

    private WeakReference<unfinishedQuestionClick> mUnfinishedQuestionClickListener;

    public interface unfinishedQuestionClick {
        void onUnfinishedQuestionClick(int position);
    }

    public QuestionAdapter(FragmentManager fm) {
        super(fm);
        mFMan = fm;
    }

    public QuestionAdapter(ArrayList<Question> mQuestions, Context mContext, FragmentManager fm) {
        super(fm);
        this.sQuestionSingleton = QuestionSingleton.get(mContext);
        this.mQuestions = sQuestionSingleton.getQuestions();
        this.mContext = mContext;
        this.mFMan = fm;
    }

    public void setmQuestions(ArrayList<Question> mQuestions) {
        this.mQuestions = mQuestions;
    }

    @Override
    public int getCount() {
        return mQuestions.size() + 1;
    }

    @Override
    public Fragment getItem(int position) {
        if (position < mQuestions.size()) {
            QuestionFragment qfrag = new QuestionFragment();
            qfrag.setQuestionModel(mQuestions.get(position));
            qfrag.setQuestionNumber(position);
            qfrag.setmContext(mContext);
            return qfrag;
        } else {
            SubmitFragment sfrag = new SubmitFragment();
            mSubmitFragment = sfrag;
            sfrag.setUnfinishedQuestionClickListener(this);
            return sfrag;
        }
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
        super.restoreState(state, loader);
    }

    @Override
    public Parcelable saveState() {
        sQuestionSingleton.saveAnswers();
        return super.saveState();
    }

    public void setSubmitFragmentText() {
        if (mSubmitFragment != null) {
            mSubmitFragment.updateUI();
        }
    }

    public void setmUnfinishedQuestionClickListener(unfinishedQuestionClick mUnfinishedQuestionClickListener) {
        this.mUnfinishedQuestionClickListener = new WeakReference<unfinishedQuestionClick>(mUnfinishedQuestionClickListener);
    }

    @Override
    public void onUnfinishedQuestionClick(int position) {
        this.mUnfinishedQuestionClickListener.get().onUnfinishedQuestionClick(position);
    }
}
