package edu.umsl.quizlet.dataClasses;


import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by landon on 4/14/17.
 */

public class Session {
    private String sessionId;
    private String userId;
    private String quizId;
    private String token; // I don't think we need this
    private int currentQuestion; // holds current question user is at in group or single user quiz
    private int userLocation; // 0 -> Single User Quiz | 1 = > Group Waiting Page | 2 -> Group Quiz 3 -> Results Page
    private Date timeRemaining;
    private boolean isLeader;


    public Session(String sessionId, String userId, String quizId, int currentQuestion, int userLocation) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.quizId = quizId;
        this.currentQuestion = currentQuestion;
        this.userLocation = userLocation;
    }

    public Session(String sessionId, String userId, String quizId, int currentQuestion, Date timeRemaining, int userLocation, boolean isLeader) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.quizId = quizId;
        this.currentQuestion = currentQuestion;
        this.userLocation = userLocation;
        this.timeRemaining = timeRemaining;
        this.isLeader = isLeader;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getQuizId() {
        return quizId;
    }

    public void setQuizId(String quizId) {
        this.quizId = quizId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getCurrentQuestion() {
        return currentQuestion;
    }

    public void setCurrentQuestion(int currentQuestion) {
        this.currentQuestion = currentQuestion;
    }

    public int getUserStatus() {
        return userLocation;
    }

    public void setUserStatus(int userLocation) {
        this.userLocation = userLocation;
    }

    public Date getTimeRemaining() {
        return timeRemaining;
    }

    public void setTimeRemaining(Date timeRemaining) {
        this.timeRemaining = timeRemaining;
    }

    public boolean isLeader() {
        return isLeader;
    }

    public void setLeader(boolean leader) {
        isLeader = leader;
    }

    public String getFormatedTimeRemaining() {
        SimpleDateFormat df = new SimpleDateFormat("hh:mm:ss aa");
        return df.format(timeRemaining);
    }

    public void setTimeRemainingForFirstTime(int timeTotakeQuiz) {
        SimpleDateFormat df = new SimpleDateFormat("hh:mm:ss aa");
        //Getting the current tim3
        Calendar now = Calendar.getInstance();
        now.add(Calendar.MILLISECOND,Calendar.ZONE_OFFSET +  Calendar.DST_OFFSET);
        now.add(Calendar.MINUTE, timeTotakeQuiz);
        this.timeRemaining = now.getTime();
        Log.e("timeRemaining", String.valueOf(df.format(timeRemaining)));
    }
}
