package com.example.myawesomequiz;

import android.provider.BaseColumns;

public class QuizContract {

     private  QuizContract(){}

     public static class CategoriesTable implements BaseColumns{
         public static final String TABLE_NAME="quiz_categories";
         public static final String COLUM_NAME="name";
     }
    public static class  QuestionTable implements BaseColumns {
        public static final String TABLE_NAME="quiz_questions";
        public static final String COLUM_QUESTION = "question";
        public static final String COLUM_OPTION1 ="option1";
        public static final String COLUM_OPTION2 ="option2";
        public static final String COLUM_OPTION3 ="option3";
        public static final String COLUM_ANSWER_NUMBER= "answer_nr";
        public static final String COLUM_DIFFICULTY= "difficulty";
        public static final String COLUM_CATEGORY_ID="category_id";
    }
}
