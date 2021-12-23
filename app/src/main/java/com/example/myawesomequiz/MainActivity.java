package com.example.myawesomequiz;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_QUIZ=1;
    public static final String EXTRA_CATEGORY_ID="extraCategoryID";
    public static final String EXTRA_CATEGORY_NAME="extraCategoryName";
    public static final String EXTRA_DIFFICULTY="extraDifficulty";

    public static final String SHARED_PREFS="sharedPrefs";
    public static final String KEY_HIGHSCORE="keyHighscore";

    private TextView highTextView;
    private Button quizButton;
    private Spinner spinnerDifficulty;
    private Spinner spinnerCategory;
    private int highscore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        highTextView=findViewById(R.id.highscoreTextViewId);
        spinnerCategory=findViewById(R.id.spinner_categories);
        spinnerDifficulty=findViewById(R.id.spinner_difficulty);


        loadCategories();
        loadDifficultyLevels();
        loadHighscore();
        quizButton=findViewById(R.id.startQuizButtonId);
        quizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startQuiz();
            }
        });
    }
    public void startQuiz(){
        Category selectedCategory=(Category) spinnerCategory.getSelectedItem();
        int categoryID = selectedCategory.getId();
        String categoryName=selectedCategory.getName();
        String difficulty=spinnerDifficulty.getSelectedItem().toString();

        Intent intent= new Intent(MainActivity.this,QuizActivity.class);
        intent.putExtra(EXTRA_CATEGORY_ID,categoryID);
        intent.putExtra(EXTRA_CATEGORY_NAME,categoryName);
        intent.putExtra(EXTRA_DIFFICULTY,difficulty);
        startActivityForResult(intent,REQUEST_CODE_QUIZ);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==REQUEST_CODE_QUIZ);{
            if (resultCode==RESULT_OK){
                int score=data.getIntExtra(QuizActivity.EXTRA_SCORE,0);
                if (score>highscore){
                    updateHighscore(score);
                }
            }
        }
    }
    private void loadCategories(){

      QuizDbHelper dbHelper=QuizDbHelper.getInstance(this);
        List<Category>categoryList=dbHelper.getAllCategories();

        ArrayAdapter<Category> adapterCategories = new ArrayAdapter<Category>(this,
                android.R.layout.simple_spinner_item,categoryList);
        adapterCategories.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinnerCategory.setAdapter(adapterCategories);
    }
    private void loadDifficultyLevels()
    {
        String[] difficultyLevels=Question.getAllDifficultyLevel();
        ArrayAdapter<String>adapterDifficulty= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,difficultyLevels);
        adapterDifficulty.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDifficulty.setAdapter(adapterDifficulty);
    }
    private void loadHighscore(){
        SharedPreferences prefs= getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        highscore=prefs.getInt(KEY_HIGHSCORE,0);
        highTextView.setText("Highscore : " + highscore);

    }
    private void updateHighscore(int highscoreNew){

        highscore = highscoreNew;
        highTextView.setText("Highscore : " + highscore);

        SharedPreferences prefs= getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editor=prefs.edit();
        editor.putInt(KEY_HIGHSCORE,highscore);
        editor.apply();


    }
}