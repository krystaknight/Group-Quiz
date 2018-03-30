package edu.umsl.quizlet.dataClasses;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by landon on 3/7/17.
 */

public class Quiz {
    private String id;
    private String description;
    private String text;
    private Date availableDate;
    private Date expiryDate;
    private int weightedPoints;
    private ArrayList<Question> questions;
    private Boolean timed;
    private int timedLength;

    public Quiz(String id, String description, String text, Date availableDate, Date expiryDate,Boolean timed, int timedLength) {
        this.id = id;
        this.description = description;
        this.text = text;
        this.availableDate = availableDate;
        this.expiryDate = expiryDate;
        this.timed = timed;
        this.timedLength = timedLength;
    }

    public Quiz(String id, String description, String text, Date availableDate, Date expiryDate, int weightedPoints, Boolean timed, int timedLength) {
        this.id = id;
        this.description = description;
        this.text = text;
        this.availableDate = availableDate;
        this.expiryDate = expiryDate;
        this.weightedPoints = weightedPoints;
        this.timed = timed;
        this.timedLength = timedLength;
    }

    public Quiz(JSONObject jsonObject) {
        try {
            this.id = jsonObject.getString("_id");
            this.description = jsonObject.getString("description");
            this.text = jsonObject.getString("text");
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
            this.availableDate = (Date)df.parse(jsonObject.getString("availableDate"));
            this.expiryDate = (Date)df.parse(jsonObject.getString("expiryDate"));
            this.timed = jsonObject.getBoolean("timed");
            if (this.timed) {
                this.timedLength = jsonObject.getInt("timedLength");
            }
            //this.questions = getQuestionsFromJson(jsonObject.getJSONArray("questions"));
        } catch (JSONException e) {
            Log.e("JSON Exception", e.toString());
            e.printStackTrace();
        } catch (ParseException e) {
            Log.e("Parse Exception", e.toString());
            e.printStackTrace();
        }
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getText() {
        return text;
    }

    public Date getAvailableDate() {
        return availableDate;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public ArrayList<Question> getQuestions() {
        return questions;
    }

    public Question getQuestion(int index) {
        return questions.get(index);
    }

    public void setQuestion(ArrayList<Question> questions) { this.questions = questions; }

    public void addQuestion(Question question) {
        this.questions.add(question);
    }

    public Boolean getTimed() {
        return timed;
    }

    public int getTimedLength() {
        return timedLength;
    }

    public void setWeightedPoints(Integer weightedPoints) {
        this.weightedPoints = weightedPoints;
    }

    public void SetAllQuestion (JSONObject jsonObject){
        try{
            this.questions = getQuestionsFromJson(jsonObject.getJSONArray("questions"));
        }catch (Exception e){
            Log.e("Parse Exception", e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        String str = "ID: " + this.id + '\n';
        str += "Description: " + this.description + '\n';
        str += "Text: " + this.text + '\n';
        str += "Available Date: " + this.availableDate + '\n';
        str += "Expiry Date: " + this.expiryDate + '\n';
        str += "Timed: " + this.timed + '\n';
        if (this.timed) {
            str += "Timed Length: " + Integer.toString(this.timedLength) + " min\n";
        }
        str += "Questions: \n" + this.questions.toString() + '\n';
        return str;
    }

    private ArrayList<Question> getQuestionsFromJson(JSONArray jsonArray) {
        ArrayList<Question> questions = new ArrayList<>();
        for (int i=0; i<jsonArray.length(); i++) {
            try {
                JSONObject question = jsonArray.getJSONObject(i);
                questions.add(new Question( question));
            } catch (JSONException e) {
                Log.e("JSON Exception", e.toString());
                e.printStackTrace();
            }
        }
        return questions;
    }

    public void setQuestions(JSONArray jsonArray) {
        ArrayList<Question> questions = new ArrayList<>();
        for (int i=0; i<jsonArray.length(); i++) {
            try {
                JSONObject question = jsonArray.getJSONObject(i);
                questions.add(new Question(question));
            } catch (JSONException e) {
                Log.e("JSON Exception", e.toString());
                e.printStackTrace();
            }
        }
        this.questions = questions;
    }

    public void setQuestions(ArrayList<Question> questions) {
        this.questions = questions;
    }

    public JSONObject getPostJson() {
        String json = "{\n    \"id\": \"" +
                this.id + "\",\n    \"questions\": [\n      ";
        for (int i=0; i<questions.size(); i++) {
            Question q = questions.get(i);
            String qJson = "{\n        " +
                    "\"id\": \"" + q.getId() + "\",\n        " +
                    "\"submittedAnswers\": [\n          ";
            ArrayList<Answer> answers = q.getAvailableAnswers();
            for (int j = 0; j < answers.size(); j++) {
                Answer a = answers.get(j);
                String aJson = "{\n          " +
                        "\"value\": \"" + a.getValue() +
                        "\",\n         \"allocatedPoints\": " +
                        a.getConfidence() + "\n        }";
                if (j != answers.size() - 1) {
                    aJson +=",";
                }
                qJson += aJson;
            }
            qJson += "\n      ]\n    }";
            if (i != questions.size() -1) {
                qJson +=",";
            }
            json += qJson;
        }
        json += "\n  ]\n}";
        try {
            return new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }

    public int scoreSingleUserQuiz() {
        int score = 0;
        for (Question question: questions) {
            if (question.getCorrectAnswer() == -1) {
                score = 0;
                break;
            }
            score += question.getAvailableAnswers().get(question.getCorrectAnswer()).getConfidence();
        }
        return score;
    }

    public int scoreGroupQuiz() {
        int score = 0;
        for (Question question: questions) {
            score += question.getGroupScore();
        }
        return score;
    }

}