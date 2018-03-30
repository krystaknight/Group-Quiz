package edu.umsl.quizlet.database;

/**
 * Created by landon on 4/15/17.
 */

public class QuizletDbSchema {

    public static final class UserTable {
        public static final String NAME = "user";

        public static final class Columns {
            public static final String _ID = "_id";
            public static final String USER_ID = "userID";
            public static final String EMAIL = "email";
            public static final String FIRST_NAME = "firstName";
            public static final String LAST_NAME = "lastName";
        }
    }

    public static final class CourseTable {
        public static final String NAME = "course";
        
        public static final class Columns {
            public static final String _ID = "_id";
            public static final String COURSE_ID = "courseID";
            public static final String EXTENDED_ID = "extendedID";
            public static final String COURSE_NAME = "courseName";
            public static final String SEMESTER = "semester";
            public static final String INSTRUCTOR = "instructor";
        }
    }

    public static final class CourseToQuizTable {
        public static final String NAME = "courseToQuiz";

        public static final class Columns {
            public static final String _ID = "id";
            public static final String COURSE_ID = "courseID";
            public static final String QUIZ_ID = "quizID";
        }
    }

    public static final class QuizTable {
        public static final String NAME = "quiz";

        public static final class Columns {
            public static final String _ID = "_id";
            public static final String DESCRIPTION = "description";
            public static final String TEXT = "text";
            public static final String AVAILABLE_DATE = "availableDate";
            public static final String EXPIRY_DATE = "expiryDate";
            public static final String TIMED = "timed";
            public static final String TIMED_LENGTH = "timedLength";
        }
    }

    public static final class QuizToQuestionTable {
        public static final String NAME = "quizToQuestion";

        public static final class Columns {
            public static final String _ID = "id";
            public static final String QUIZ_ID = "quizID";
            public static final String QUESTION_ID = "questionID";
        }

    }

    public static final class QuestionTable {
        public static final String NAME = "question ";

        public static final class Columns {
            public static final String _ID = "_id";
            public static final String TITLE = "title";
            public static final String TEXT = "text";
            public static final String POINTS_POSSIBLE = "pointsPossible";
            public static final String GROUP_SCORE = "groupScore";
            public static final String GROUP_ANSWER = "groupAnswer";
            public static final String CORRECT_ANSWER = "correctAnswer";
        }
    }

    public static final class QuestionToAnswerTable {
        public static final String NAME = "questionToAnswer";

        public static final class Columns {
            public static final String _ID = "id";
            public static final String QUESTION_ID = "questionID";
            public static final String ANSWER_ID = "answerID";
        }
    }

    public static final class AnswerTable {
        public static final String NAME = "answers";

        public static final class Columns {
            public static final String _ID = "_id";
            public static final String VALUE = "value";
            public static final String TEXT = "text";
            public static final String SORT_ORDER = "sortOrder";
            public static final String CONFIDENCE = "confidence";
        }
    }

    public static final class QuizHistoryTable {
        public static final String NAME = "quizHistory";

        public static final class Columns {
            public static final String _ID = "_id";
            public static final String COURSE_ID = "courseId";
            public static final String SCORE = "score";
            public static final String TITLE = "title";
        }
    }

    public static final class SessionTable {
        public static final String NAME = "session";

        public static final class Columns {
            public static final String _ID = "_id";
            public static final String USER_ID = "userId";
            public static final String QUIZ_ID = "quizId";
            public static final String CURRENT_QUESTION = "currentQuestion";
            public static final String USER_STATUS = "user_status";
            public static final String IS_LEADER = "isLeader";
            public static final String TIME_REMAINING = "timeRemaining";
        }
    }

    public static final class GroupTable{
        public static final String NAME = "GroupTable";

        public static final class Columns{
            public static final String GROUPID = "_id";
            public static final String GROUPNAME = "name";
        }
    }

    public static final class GroupUserTable{
        public static final String NAME = "GroupUserTable";

        public static final class Columns{
            public static final String USER_ID = "userID";
            public static final String EMAIL = "email";
            public static final String FIRST_NAME = "firstName";
            public static final String LAST_NAME = "lastName";
            public static final String SINGLEQUIZSTATUS = "singleQuizStatus";
            public static final String LEADER = "leader";
        }
    }


}