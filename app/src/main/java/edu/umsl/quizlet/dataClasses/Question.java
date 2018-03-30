package edu.umsl.quizlet.dataClasses;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;
/**
 * Created by A on 3/2/2017.
 */

import java.lang.String;

public class Question {
    private String id;
    private String title;
    private String text;
    private int pointsPossible;
    private int groupScore;
    private int groupAnswer;
    private int correctAnswer;
    private ArrayList<Answer> availableAnswers;

    public Question(String id, String title, String text, int pointsPossible, int groupScore, int groupAnswer, int correctAnswer, ArrayList<Answer> availableAnswers) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.pointsPossible = pointsPossible;
        this.groupScore = groupScore;
        this.groupAnswer = groupAnswer;
        this.correctAnswer = correctAnswer;
        this.availableAnswers = availableAnswers;
    }

    public Question(String id, String title, String text, int pointsPossible, int groupScore, int groupAnswer) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.pointsPossible = pointsPossible;
        this.groupScore = groupScore;
        this.groupAnswer = groupAnswer;
    }

    Question(JSONObject jsonObject) {
        this.groupScore = 0;
        this.groupAnswer = -1;
        this.correctAnswer = -1;
        try {
            this.id = jsonObject.getString("_id");
            this.title = jsonObject.getString("title");
            this.text = jsonObject.getString("text");
            this.pointsPossible = jsonObject.getInt("pointsPossible");
            this.availableAnswers = getAnswersFromJson(jsonObject.getJSONArray("availableAnswers"));
        } catch (JSONException e) {
            Log.e("JSON Exception", e.toString());
            e.printStackTrace();
        }

    }

    // I don't think we'll need any setters after the constructor is called.
    // but a decrement function will be needed for group quiz
    public void decrementPossiblePoints() {
        if (groupScore == 1) {groupScore = 0; return;}
        if (groupScore - (groupScore / 2) >= 0) {
            groupScore -= (groupScore / 2);
        }
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public void setGroupScore(int groupScore) {
        this.groupScore = groupScore;
    }

    public int getGroupScore() {
        return groupScore;
    }

    public int getPointsPossible() {
        return pointsPossible;
    }

    public void setGroupAnswer(int groupAnswer) {this.groupAnswer = groupAnswer;}

    public int getGroupAnswer() {
        return groupAnswer;
    }

    public ArrayList<Answer> getAvailableAnswers() {
        return availableAnswers;
    }

    public void setAvailableAnswers(ArrayList<Answer> availableAnswers) {
        this.availableAnswers = availableAnswers;
    }

    public void addAnswer(Answer answer) {
        availableAnswers.add(answer);
    }

    public int getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(int correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public int addConfidencePoint(int answerIndex) {
        Answer a = availableAnswers.get(answerIndex);
        if (! (a.getConfidence() < pointsPossible)) {
            return a.getConfidence();
        } else {
            int total = 0;
            for (Answer ans: availableAnswers) {
                total += ans.getConfidence();
            }
            if ( !(total < pointsPossible)) {
                // Fanciness that wound up being unpredictable for users
//                for (Answer ans:availableAnswers) {
//                    if (ans.getConfidence() > 0 && !ans.equals(a)) {
//                        ans.setConfidence(ans.getConfidence() - 1);
//                        break;
//                    }
//                }
//                a.setConfidence(a.getConfidence() + 1);
                return total;
            } else {
                a.setConfidence(a.getConfidence() + 1);
                return total + 1;
            }
        }
    }

    public int subtractConfidencePoint(int answerIndex) {
        Answer a = availableAnswers.get(answerIndex);
        if (a.getConfidence() > 0) {
            a.setConfidence(a.getConfidence() - 1);
        }
        int total = 0;
        for (Answer ans: availableAnswers) {
            total += ans.getConfidence();
        }
        return total;
    }

    @Override
    public String toString() {
        String str = "ID: " + this.id + '\n';
        str += "Title: " + this.title + '\n';
        str += "Text: " + this.text + '\n';
        str += "Possible Points: " + this.pointsPossible + '\n';
        str += "Group Score: " + this.groupScore + '\n';
        str += "Answers: \n" + availableAnswers.toString() + '\n';
        return str;
    }

    private ArrayList<Answer> getAnswersFromJson(JSONArray jsonArray) {
        ArrayList<Answer> answers = new ArrayList<>();
        for (int i=0; i<jsonArray.length(); i++) {
            try {
                JSONObject answer = jsonArray.getJSONObject(i);
                answers.add(new Answer(answer));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return answers;
    }

    public JSONObject getPostJson() {
        JSONObject jsonObject = new JSONObject();
        String json = "{\n\"question_id\": " +
                "\"" + id +
                "\",\n\"answerValue\":\"" +
                availableAnswers.get(groupAnswer).getValue() +
                "\"\n}";
        try {
            jsonObject = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
