package com.example.myawesomequiz;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.net.PortUnreachableException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class QuizDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "quize.db";
    private static final int DATABASE_VERSION = 26;

    private static QuizDbHelper instance;

    private SQLiteDatabase db;

    private QuizDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized QuizDbHelper getInstance(Context context){

        if (instance==null){
            instance=new QuizDbHelper(context.getApplicationContext());
        }
        return instance;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        this.db = db;

        final String SQL_CREATE_CATEGORIES_TABLE=" CREATE TABLE " +
                QuizContract.CategoriesTable.TABLE_NAME +"( "+
                QuizContract.CategoriesTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                QuizContract.CategoriesTable.COLUM_NAME + " TEXT " +
                ")";


        final String SQL_CREATE_TABLE = "CREATE TABLE " +
                QuizContract.QuestionTable.TABLE_NAME + "(" +
                QuizContract.QuestionTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                QuizContract.QuestionTable.COLUM_QUESTION + " TEXT, " +
                QuizContract.QuestionTable.COLUM_OPTION1 + " TEXT, " +
                QuizContract.QuestionTable.COLUM_OPTION2 + " TEXT, " +
                QuizContract.QuestionTable.COLUM_OPTION3 + " TEXT, " +
                QuizContract.QuestionTable.COLUM_ANSWER_NUMBER + " INTEGER, " +
                QuizContract.QuestionTable.COLUM_DIFFICULTY + " TEXT, " +
        QuizContract.QuestionTable.COLUM_CATEGORY_ID +" INTEGER, "+
                "FOREIGN KEY(" + QuizContract.QuestionTable.COLUM_CATEGORY_ID+ ") REFERENCES " +
                QuizContract.CategoriesTable.TABLE_NAME +"(" + QuizContract.CategoriesTable._ID + ")"+" ON DELETE CASCADE "+
                ")";

        db.execSQL(SQL_CREATE_CATEGORIES_TABLE);
        db.execSQL(SQL_CREATE_TABLE);
        fillCategoriesTable();
        fillQuestionTable();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        this.db=db;
        db.execSQL("DROP TABLE IF EXISTS  " + QuizContract.CategoriesTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS  " + QuizContract.QuestionTable.TABLE_NAME);
        onCreate(db);

    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    private void fillCategoriesTable(){
        Category c1= new Category("Programing");
        addCategory(c1);
        Category c2= new Category("Geography");
        addCategory(c2);
        Category c3= new Category("Math");
        addCategory(c3);
    }
    private void addCategory(Category category){

        ContentValues contentValues= new ContentValues();
        contentValues.put(QuizContract.CategoriesTable.COLUM_NAME,category.getName());
        long d= db.insert(QuizContract.CategoriesTable.TABLE_NAME,null,contentValues);
        Log.d(TAG, "addQuestion: "+d);
    }

    private void fillQuestionTable() {

        Question q1 = new Question("Programing,Easy : A is correct",
                "A","B","C",1,
                Question.DIFFICULTY_EASY,Category.PROGRAMING);
        addQuestion(q1);
        Question q2 = new Question("Geography,Medium : B is correct",
                "A","B","C",2,
                Question.DIFFICULTY_MEDIUM,Category.GEOGRAPHY);
        addQuestion(q2);
        Question q3= new Question("Math,Hard : C is correct",
                "A","B","C",3,
                Question.DIFFICULTY_HARD,Category.MATH);
        addQuestion(q3);
        Question q4= new Question("Math,Easy : A is correct",
                "A","B","C",1,
                Question.DIFFICULTY_EASY,Category.MATH);
        addQuestion(q4);
        Question q5= new Question("Non existing,Easy : A is correct",
                "A","B","C",1,
                Question.DIFFICULTY_EASY,4);
        addQuestion(q5);
         Question q6= new Question("Non existing,Medium : B is correct",
                "A","B","C",2,
                Question.DIFFICULTY_MEDIUM,5);
        addQuestion(q6);

    }
    private void addQuestion(Question question) {
//        db=this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(QuizContract.QuestionTable.COLUM_QUESTION, question.getQuestion());
        contentValues.put(QuizContract.QuestionTable.COLUM_OPTION1, question.getOption1());
        contentValues.put(QuizContract.QuestionTable.COLUM_OPTION2, question.getOption2());
        contentValues.put(QuizContract.QuestionTable.COLUM_OPTION3, question.getOption3());
        contentValues.put(QuizContract.QuestionTable.COLUM_ANSWER_NUMBER, question.getAsnwerNr());
        contentValues.put(QuizContract.QuestionTable.COLUM_DIFFICULTY, question.getDifficulty());
        contentValues.put(QuizContract.QuestionTable.COLUM_CATEGORY_ID, question.getCategoryID());
       db.insert(QuizContract.QuestionTable.TABLE_NAME, null, contentValues);

    }
    @SuppressLint("Range")
    public List<Category> getAllCategories(){
        List<Category> categoryList = new ArrayList<>();
        db=getReadableDatabase();
        Cursor cursor=db.rawQuery("SELECT * FROM "+ QuizContract.CategoriesTable.TABLE_NAME,null);

       if (cursor.moveToFirst()){
           do{
               Category category=new Category();
               category.setId(cursor.getInt(cursor.getColumnIndex(QuizContract.CategoriesTable._ID)));
               category.setName(cursor.getString(cursor.getColumnIndex(QuizContract.CategoriesTable.COLUM_NAME)));
               categoryList.add(category);
           }while (cursor.moveToNext());
       }
       cursor.close();
       return categoryList;
    }
    @SuppressLint("Range")
    public ArrayList<Question> getAllQuestions(){
        ArrayList<Question> questionList= new ArrayList<>();
        db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery("SELECT * FROM "+ QuizContract.QuestionTable.TABLE_NAME,null);

        if (cursor.moveToFirst()){
            do{
                Question question = new Question();
                question.setId(cursor.getInt(cursor.getColumnIndex(QuizContract.QuestionTable._ID)));
                question.setQuestion(cursor.getString(cursor.getColumnIndex(QuizContract.QuestionTable.COLUM_QUESTION)));
                question.setOption1(cursor.getString(cursor.getColumnIndex(QuizContract.QuestionTable.COLUM_OPTION1)));
                question.setOption2(cursor.getString(cursor.getColumnIndex(QuizContract.QuestionTable.COLUM_OPTION2)));
                question.setOption3(cursor.getString(cursor.getColumnIndex(QuizContract.QuestionTable.COLUM_OPTION3)));
                question.setAsnwerNr(cursor.getInt(cursor.getColumnIndex(QuizContract.QuestionTable.COLUM_ANSWER_NUMBER)));
                question.setCategoryID(cursor.getInt(cursor.getColumnIndex(QuizContract.QuestionTable.COLUM_CATEGORY_ID)));
                questionList.add(question);
            }while (cursor.moveToNext());
        }

        cursor.close();
        return  questionList;
    }

    @SuppressLint("Range")
    public ArrayList<Question> getQuestions(int categories,String difficulty){
        ArrayList<Question> questionList= new ArrayList<>();
        db=this.getReadableDatabase();

       String selections= QuizContract.QuestionTable.COLUM_CATEGORY_ID+ " = ? "+
               " AND "+ QuizContract.QuestionTable.COLUM_DIFFICULTY + " = ? ";
       String[] SelectionArgs =new String[]{String.valueOf(categories),difficulty};

       Cursor cursor=db.query(
               QuizContract.QuestionTable.TABLE_NAME,
               null,
               selections,
               SelectionArgs,
               null,
               null,
               null
       );
        if (cursor.moveToFirst()){
            do{
                Question question = new Question();
              //  question.setId(cursor.getInt(cursor.getColumnIndex(QuizContract.QuestionTable._ID)));
                question.setQuestion(cursor.getString(cursor.getColumnIndex(QuizContract.QuestionTable.COLUM_QUESTION)));
                question.setOption1(cursor.getString(cursor.getColumnIndex(QuizContract.QuestionTable.COLUM_OPTION1)));
                question.setOption2(cursor.getString(cursor.getColumnIndex(QuizContract.QuestionTable.COLUM_OPTION2)));
                question.setOption3(cursor.getString(cursor.getColumnIndex(QuizContract.QuestionTable.COLUM_OPTION3)));
                question.setAsnwerNr(cursor.getInt(cursor.getColumnIndex(QuizContract.QuestionTable.COLUM_ANSWER_NUMBER)));
                question.setCategoryID(cursor.getInt(cursor.getColumnIndex(QuizContract.QuestionTable.COLUM_CATEGORY_ID)));
                questionList.add(question);
            }while (cursor.moveToNext());
        }

        cursor.close();
        return  questionList;
    }
}
