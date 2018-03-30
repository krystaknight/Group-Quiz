package edu.umsl.quizlet;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.InetAddress;

import edu.umsl.quizlet.database.QuizPersistence;
import edu.umsl.quizlet.database.UserInfoPersistence;
import edu.umsl.quizlet.dataClasses.Course;
import edu.umsl.quizlet.dataClasses.Group;
import edu.umsl.quizlet.dataClasses.Quiz;
import edu.umsl.quizlet.dataClasses.Session;
import edu.umsl.quizlet.dataClasses.User;

import static android.content.ContentValues.TAG;


/**
 * Created by harprabh on 3/30/2017.
 */

public class API implements HttpWorkerFragment.MainFragmentListener {
    private HttpWorkerFragment mHttpWorkerFragment;
    private boolean httpWorkerDone = false;
    private UserInfoPersistence sPersistence;
    private QuizPersistence qPersistence;
    private requestType request = null;
    private WeakReference<APIListener> mListener;
    private WeakReference<APIGroupListener> mGroupListener;
    private WeakReference<APIQuizListener> mListenerQuiz;
    private WeakReference<APIGroupQuizListener> mGroupQuizListener;

    public API(HttpWorkerFragment frag, Context ctx) {
        mHttpWorkerFragment = frag;
        mHttpWorkerFragment.setListener(this);
        sPersistence = UserInfoPersistence.sharedInstance(ctx);
        qPersistence = QuizPersistence.sharedInstance(ctx);
    }

    public void setListener(APIListener listener) {
        mListener = new WeakReference<>(listener);
    }

    public void setQuizListener(APIQuizListener listener2) {
        mListenerQuiz = new WeakReference<APIQuizListener>(listener2);
    }

    public void setGroupListener(APIGroupListener listener3) {
        mGroupListener = new WeakReference<APIGroupListener>(listener3);
    }

    public void setGroupQuizListener(APIGroupQuizListener listener){
        mGroupQuizListener = new WeakReference<APIGroupQuizListener>(listener);
    }

    /*--------------------------------User API Calls --------------------------------------------*/

    //Called by the Main activity to see if the user info is in the table (ie user is logged in)
    public User checkForUser() {
        User usr = sPersistence.getUser();
        if (usr != null) { // if user is found, return true
            return usr;
        } else {
            return null;
        }
    }
    // Get's user information from David's api
    public void getUser(String userID) {
        request = requestType.GETUSER;
        String url = "http://tblearn-api.vigilantestudio.com/v1/users/" + userID;
        mHttpWorkerFragment.startDownloadTask(url, "GET");
    }

    public void updateCourses(String userID) {
        request = requestType.UPDATECOURSE;
        String url = "http://tblearn-api.vigilantestudio.com/v1/users/" + userID;
        mHttpWorkerFragment.startDownloadTask(url, "GET");
    }

    /*------------------------------------- Getting Quiz API Calls ------------------------------*/

    // Calling Quizzes endpoint to get quiz information11
    public void getQuizzes(String userID) {
        request = requestType.GETQUIZZES;
        String url = "http://tblearn-api.vigilantestudio.com/v1/quizzes/" + userID;
        mHttpWorkerFragment.startDownloadTask(url, "GET");
    }

    // Getting Quiz question and answers
    public void getQuiz(String token, String courseID) {
        request = requestType.GETQUIZ;
        String userID = sPersistence.getUser().getUserID();
        String quiz_id = qPersistence.getQuizIdFromCourseId(courseID);
        //Log.e("getQuiz", "courseID "+ courseID + " userID: " + userID + " quiz_id " + quiz_id );

        String url = "http://tblearn-api.vigilantestudio.com/v1/quiz/?user_id=" + userID +"&course_id=" + courseID + "&quiz_id=" + quiz_id + "&token=" + token.toUpperCase();

        Log.e("TAG", url);
        mHttpWorkerFragment.startDownloadTask(url, "GET");
    }

    // Submitting User taken quiz
    public void postQuizForGrading(JSONObject jObj) {
        request = requestType.POSTSINGLEUSERQUIZ;
        String courseID = qPersistence.getCourseIdFromQuizId(qPersistence.getSession().getQuizId());
        String url = "http://tblearn-api.vigilantestudio.com/v1/quiz/?course_id=" +
                qPersistence.getCourseIdFromQuizId(qPersistence.getSession().getQuizId()) +
        "&user_id=" + qPersistence.getSession().getUserId() +
                "&session_id=" + qPersistence.getSession().getSessionId();
        Log.e("Url ", url);
        Log.e("POST ", jObj.toString());
        mHttpWorkerFragment.startDownloadTask(url, "POST", jObj.toString());
    }

    /*-------------------------------------------Group API calls ---------------------------------*/

    // Getting Group for user
    public void getGroupForUser() {
        request = requestType.GETGROUPFORUSER;
        String url = "http://tblearn-api.vigilantestudio.com/v1/groupForUser/?user_id=" + sPersistence.getUser().getUserID()
                + "&course_id=" + qPersistence.getCourseIdFromQuizId(qPersistence.getSession().getQuizId());
        Log.e("URL", "Get Group for User " + url);
        mHttpWorkerFragment.startDownloadTask(url, "GET");
    }

    public void getGroupStatus() {
        request = requestType.GROUPSTATUS;

        Session session = qPersistence.getSession();
        String sessionID = session.getSessionId();
        String quiz_id = session.getQuizId();
        String courseID = qPersistence.getCourseIdFromQuizId(quiz_id);
        String groupID = sPersistence.getGroup().getId();

        String url = "http://tblearn-api.vigilantestudio.com/v1/groupStatus/?group_id="+ groupID +"&course_id=" +courseID+ "&quiz_id="+quiz_id+"&session_id="+ sessionID;

        Log.e("TAG", url);
        mHttpWorkerFragment.startDownloadTask(url, "GET");
    }

    public void getGroupQuizProgress(){
        request = requestType.GETGROUPQUIZPROGRESS;
        String url = "http://tblearn-api.vigilantestudio.com/v1/groupQuizProgress?quiz_id="+ qPersistence.getSession().getQuizId() +
                "&group_id=" + sPersistence.getGroup().getId() + "&session_id=" + qPersistence.getSession().getSessionId();
        mHttpWorkerFragment.startDownloadTask(url, "GET");
    }

           /*------------------------------------Group Quiz Api Calls ---------------------*/

    public void postGroupQuizQuestion(String questionId, String answerValue){
        request = requestType.POSTGROUPQUIZQUESTION;
        JSONObject jObj = new JSONObject();
        try {
            jObj.put("question_id", questionId);
            jObj.put("answerValue", answerValue);
        } catch (JSONException e) {
            Log.e("postGroupQuizQuestion", "JSONObject Error!");
            e.printStackTrace();
        }

        Log.e("JsonObject", jObj.toString());
        String url = "http://tblearn-api.vigilantestudio.com/v1/groupQuiz/?quiz_id=" + qPersistence.getSession().getQuizId() +"&group_id=" +sPersistence.getGroup().getId()+"&session_id="+qPersistence.getSession().getSessionId();
        Log.e("postGroupQuestion URL", url);
        mHttpWorkerFragment.startDownloadTask(url,"POST",jObj.toString());
    }

    /*-------------------------------------------API calls Response -----------------------------*/
    @Override
    public void dataDownloadComplete(String downloadText) {
        if (downloadText != null) {
            switch (request) {
                // Create a user with downloadText
                case GETUSER:
                    try {
                        JSONObject jObj = new JSONObject(downloadText);
                        addUserInfoToDB(jObj); // Handling All User information
                    } catch (Exception e) {
                        Log.e("ERROR ", "GetUser: " + e.toString());
                        mListener.get().error("Incorrect UserName");
                    }
                    break;
                case GETQUIZZES: //Getting Quiz information
                    try {
                        JSONArray jObj = new JSONArray(downloadText); // Professor stores it as an array

                        for (int i = 0; i < jObj.length(); i++) {
                            JSONObject object = jObj.getJSONObject(i).getJSONObject("quiz"); //getting the Quiz object from the JSON Array
                            Quiz quiz = new Quiz(object);
                            qPersistence.setQuiz(quiz);
                            qPersistence.setCourseToQuiz(quiz, jObj.optJSONObject(i).getString("courseId"));
                        }

                    } catch (Exception e) {
                        Log.e("ERROR1", e.toString());
                    }
                    break;
                case GETQUIZ:  // Getting Quiz questions  Y74BUDCM
                    try {
                        Log.e("TAG", "GETQUIZ DownloadText: " + downloadText);
                        JSONObject jObj = new JSONObject(downloadText);

                        //Adding Quiz to the Phone's table
                        addQuizToDB(jObj);

                        //Creating The Session Table
                        addSessionToDB(jObj);

                        //Calling ProfilePageActivity to Start Quiz
                        mListener.get().quizDownloaded(qPersistence.getSession().getQuizId());

                    } catch (Exception e) {
                        Log.e("ERROR", "GetQUIZ " + e.toString());
                    }

                    break;
                case UPDATECOURSE:
                    try {
                        JSONObject jObj = new JSONObject(downloadText);
                        addCoursesToDB(jObj);
                    } catch (Exception e){
                        Log.e("ERROR", "UPDATECOURSES : " + e);
                    }
                    break;
                case GROUPSTATUS:
                    try {
                        JSONObject jsonObject = new JSONObject(downloadText);
                        updateGroupStatus(jsonObject);
                        mGroupListener.get().groupStatus();
                    }  catch (Exception e) {
                        Log.e("ERROR", "UPDATECOURSES : " + e);
                    }
                    break;
                case GETGROUPFORUSER:
                    try {
                        Log.e("TAG", "GetGroupFrUser " + downloadText);
                        JSONObject jObj = new JSONObject(downloadText);
                        setUpGroup(jObj);
                        mGroupListener.get().groupStatus();

                    } catch (Exception e) {
                        Log.e("ERROR", "GETGROUPFORUSER: " + e);
                    }
                    break;
                case POSTSINGLEUSERQUIZ:
                    mListenerQuiz.get().quizSubmitted(true);
                    //Log.e("POSTRESPONSE", downloadText);
                    processPostSingleUserQuizResults(downloadText);
                    Log.e(TAG, "Getting Correct questions done");
                    break;
                case POSTGROUPQUIZQUESTION:
                    try{
                        Log.e("POSTGROUPQUIZQUESTION", downloadText);
                        JSONObject jObj = new JSONObject(downloadText);
                        processPostGroupQuizResponse(jObj);
                    }catch (Exception e){
                        Log.e("ERROR" , "POSTGROUPQUESTIONQUESTION: " + e);
                    }
                    break;
                case GETGROUPQUIZPROGRESS:
                    try {
                        JSONObject jObj = new JSONObject(downloadText);
                        mGroupQuizListener.get().getGroupQuizProgress(jObj);
                    }catch (Exception e){
                        Log.e("ERROR", "GETGROUPQUIZPROGRESS " + e);
                    }
                    break;

            }
        } else { //This else triggers if there is an Issue calling David's API
            switch (request) {
                case GETUSER:
                    mListener.get().error("No user was found!");
                    break;
                case GETQUIZZES:
                    mListener.get().error("Error Getting Available Quizzes for User");
                    break;
                case GETQUIZ: // IF the user inputs a Incorrect quizID
                    mListener.get().error("Incorrect Token, Try Again!");
                    break;
                case GETGROUPFORUSER:
                    Log.e("ERROR", "GETGROUPFORUSER did not get Json");
                    mGroupListener.get().error("Getting Group UnSuccessful");
//                    mGroupListener.get().groupStored(false);
                    break;
                case POSTSINGLEUSERQUIZ:
                    Log.e("ERROR", "POSTSINGLEUSERQUIZ downland text came back as null");
                    mListenerQuiz.get().quizSubmitted(false);
                    break;
                case POSTGROUPQUIZQUESTION:
                    Log.e("ERROR", "POSTGROUPQUIZQUESTION IN API dataDownloadComplete()");
                    mGroupQuizListener.get().error("ERROR In posting Group Quiz Question");
                    break;
                case GETGROUPQUIZPROGRESS:
                    Log.e("ERROR", "GETGROUPQUIZPROGRESS in API dataDownloadComplete is null");
            }
        }
    }


    //post group quiz answer

    // Adding Downloaded Text to DB
    private void addUserInfoToDB(JSONObject jObj) {
        /* Adding User to User.sqlite table */
        User usr = new User(jObj);
        sPersistence.addUser(usr);
        addCoursesToDB(jObj);
        //Log.e("TAG", "Adding Course");
        mListener.get().userFound(usr);
    }

    /* Adding Courses */
    private void addCoursesToDB(JSONObject jObj) {
        try {
            //Log.e("TAG", "JSONArray Size " + jObj.getJSONArray("enrolledCourses").length());
            // Making sure each Courses is added to the table one by one
            for (int i = 0; i < jObj.getJSONArray("enrolledCourses").length(); i++) {
                Course course = new Course(jObj, i);
                sPersistence.addCourse(course); //Adding course to the User.sqlite lite Course table
            }
            mListener.get().coursesUpdated(jObj.getString("userID"));
        } catch (Exception e) {
            Log.e("AddUserInfoToDB", e.toString());
        }
    }

    /* Adding Quiz to DB */
    private void addQuizToDB(JSONObject jObj) {
        try {
            Quiz quiz = qPersistence.getQuiz(jObj.getJSONObject("quiz").getString("_id"));
            //Log.e("TEST 1","Fun");
            quiz.setQuestions(jObj.getJSONObject("quiz").getJSONArray("questions"));
            //Log.e("TEST 2","Funn");
            qPersistence.setQuiz(quiz);
            //Log.e("TEST 3","Funnn");
            //Log.e("GETQUIZTEST", quiz.getPostJson().toString());
        } catch (Exception e) {
            Log.e("ERROR", "GetQUIZ1 " + e.toString());
        }
    }

    // Adds session to DB
    private void addSessionToDB(JSONObject jObj) {
        //Creating The Session Table
        try {
            Session session = new Session(
                    jObj.getString("sessionId"),
                    sPersistence.getUser().getUserID(),
                    jObj.getJSONObject("quiz").getString("_id"),
                    0,0 //session currentQuestion and
            );
            session.setTimeRemainingForFirstTime(jObj.getJSONObject("quiz").getInt("timedLength"));
            qPersistence.setSessionTable(session);
//            Log.e("Session Test", qPersistence.getSession().getQuizId() + " " + qPersistence.getSession().getCurrentQuestion());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Adds session to Db
    private void setUpGroup(JSONObject jObj) {

        try {
            Group group = new Group(jObj);
            group.setGroupUsers(jObj.getJSONArray("users"));
            sPersistence.addGroup(group);
            //Log.e("Group", "Test: " + sPersistence.getGroup().getId() + " " + sPersistence.getGroup().getName());
//            Log.e("GroupUser", "Test: \n");
//            for(int i = 0; i < sPersistence.getGroup().getGroupUsers().size(); i++){
//                Log.e("Group User" , sPersistence.getGroup().getGroupUsers().get(i).getFirst() + " " +
//                    sPersistence.getGroup().getGroupUsers().get(i).getLast());
//            }

        } catch (Exception e) {
            Log.e("ERROR", "setUpGroup : " + e.toString());
        }
    }

    private void updateGroupStatus(JSONObject jObj){
        try {
            Log.e(TAG, jObj.toString());
            if(jObj.getJSONObject("leader").getString("userId").equals(sPersistence.getUser().getUserID())){
                qPersistence.setIsLeader(true);
            }else{
                qPersistence.setIsLeader(false);
                Log.e("LEADER", jObj.getJSONObject("leader").getString("userId"));
            }

            if(jObj.getJSONObject("leader") != null){
                sPersistence.updateGroupLeader(jObj.getJSONObject("leader").getString("userId"));
            }

            JSONArray groupStatus = jObj.getJSONArray("status");
            for(int i = 0; i < groupStatus.length(); i++){
                sPersistence.updateGroupUserProgress(groupStatus.getJSONObject(i).getString("userId"), groupStatus.getJSONObject(i).getString("status"));
            }

        }catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "updateGroupStatus jsonArray error" );
        }
    }

    private void processPostSingleUserQuizResults(String downloadText){
        try{
            JSONObject jsonObject = new JSONObject(downloadText);
            JSONArray answers = jsonObject.getJSONArray("answers");

            Quiz quiz = qPersistence.getQuiz(jsonObject.getString("quizId"));

            for(int i = 0; i < answers.length(); i++){
                for(int j = 0; j < answers.getJSONObject(i).getJSONArray("submittedAnswers").length(); j++){
                    boolean correct = answers.getJSONObject(i).getJSONArray("submittedAnswers").getJSONObject(j).getBoolean("isCorrect");
                    if(correct){
                        Log.e("Setting correct answer", "Correct answer = " + j + " for q " + i);
                        quiz.getQuestion(i).setCorrectAnswer(j);
                        break;
                    }
                }
            }

            qPersistence.updateCorrectAnswers(quiz);

        }catch (Exception e){
            Log.e(TAG , " line 367 function : error dealing with JSONObject");
        }
    }

    private void processPostGroupQuizResponse(JSONObject jObj){
        try{

            int index = jObj.getJSONArray("submittedAnswers").length() - 1;
            if(jObj.getJSONArray("submittedAnswers").getJSONObject(index).getBoolean("isCorrect")){
                qPersistence.updateGroupScore(jObj.getString("question"),jObj.getJSONArray("submittedAnswers").getJSONObject(index).getInt("points"));
            }
            mGroupQuizListener.get().groupQuestionResponse(jObj.getJSONArray("submittedAnswers").getJSONObject(index).getBoolean("isCorrect"),
                    jObj.getJSONArray("submittedAnswers").getJSONObject(index).getInt("points"));


        }catch (Exception e){
            Log.e("ERROR" , "processPostGroupQuizResponse: " + e);
        }

    }


    private enum requestType {GETUSER, GETQUIZZES, GETQUIZ, UPDATECOURSE, GETGROUPFORUSER,
        POSTSINGLEUSERQUIZ,GROUPSTATUS,POSTGROUPQUIZQUESTION, GETGROUPQUIZPROGRESS}



    /* ------------------------------ API Model Interfaces ---------------------------------------*/

    interface APIListener {
        void userFound(User user);

        void coursesUpdated(String userID);

        void quizDownloaded(String quizID);

        void error(String e);
    }

    public interface APIQuizListener {
        void quizSubmitted(boolean quizSubmitted);
    }

    public interface APIGroupListener {
        void error(String error);
        void groupStatus();
    }

    public interface APIGroupQuizListener{
        void groupQuestionResponse(boolean isCorrect, int points);
        void getGroupQuizProgress(JSONObject jObj);
        void error(String error);
    }


}
