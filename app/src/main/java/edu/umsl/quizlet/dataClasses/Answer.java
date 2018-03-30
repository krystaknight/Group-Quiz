package edu.umsl.quizlet.dataClasses;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Austin on 3/7/2017.
 */

public class Answer {
    private String value;
    private String text;
    private int sortOrder;
    private int confidence;

   public Answer(String value, String text, int sortOrder, int confidence) {
        this.value = value;
        this.text = text;
        this.sortOrder = sortOrder;
        this.confidence = confidence;
    }

    public Answer(JSONObject jsonObject) {
        try {
            this.value = jsonObject.getString("value");
            this.text = jsonObject.getString("text");
            this.sortOrder = jsonObject.getInt("sortOrder");
        } catch (JSONException e) {
            Log.e("JSON Exception", e.toString());
            e.printStackTrace();
        }
    }

    public String getId() {
        return value + text + Integer.toString(sortOrder);
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public int getConfidence() {
        return confidence;
    }

    public void setConfidence(int confidence) {
        if (confidence >= 0) {
            this.confidence = confidence;
        } else {
            this.confidence = 0;
        }
    }

    @Override
    public String toString() {
        String str = "ID: " + this.getId() + '\n';
        str += "Value: " + this.value + '\n';
        str += "Text: " + this.text + '\n';
        str += "Sort Order: " + this.sortOrder + '\n';
        str += "Confidence: " + this.confidence + '\n';
        return str;
    }
}
