package com.example.myawesomequiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class QuizActivity extends AppCompatActivity {

    public static final String EXTRA_SCORE="extraScore";
    private static  final long COUNTDOWN_IN_MILLIS=30000;

    private static final String KEY_SCORE ="keyScore";
    private static final String KEY_QUESTION_COUNT="keyQuestionCount";
    private static final String KEY_MILLIS_LEFT="keyMillisLeft";
    private static final String KEY_ANSWER="keyAnswered";
    private static final String KEY_QUESTION_LIST="keyQuestionList";

    private TextView textViewQuestion;
    private TextView textViewScore;
    private TextView textViewQuestionCount;
    private TextView textViewCategory;
    private TextView textViewDifficulty;
    private TextView textViewCountDown;
    private RadioGroup radioGroup;
    private RadioButton radioButton1;
    private RadioButton radioButton2;
    private RadioButton radioButton3;
    private Button buttonConfirmNext;

    private ColorStateList textColorDefaultRb;
    private ColorStateList textColorDefaultCD;

    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;

    private ArrayList<Question> questionList;
    private int questionCounter;
    private int questionCounterTotal;
    private Question currentQueston;

    private int score;
    private boolean answer;

    private long BackPressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        textViewQuestion=findViewById(R.id.text_view_question);
        textViewScore=findViewById(R.id.scoreTextViewId);
        textViewQuestionCount=findViewById(R.id.questionCountTextId);
        textViewCategory=findViewById(R.id.text_view_categories);
        textViewDifficulty=findViewById(R.id.text_view_difficulty);
        textViewCountDown=findViewById(R.id.text_view_countdown);
        radioGroup=findViewById(R.id.radio_group);
        radioButton1=findViewById(R.id.radio_button1);
        radioButton2=findViewById(R.id.radio_button2);
        radioButton3=findViewById(R.id.radio_button3);
        buttonConfirmNext=findViewById(R.id.button_comfirm_next);

        textColorDefaultRb=radioButton1.getTextColors();
        textColorDefaultCD=textViewCountDown.getTextColors();

        Intent intent = getIntent();
        int categoryID=intent.getIntExtra(MainActivity.EXTRA_CATEGORY_ID,0);
        String categoryName=intent.getStringExtra(MainActivity.EXTRA_CATEGORY_NAME);
        String difficulty = intent.getStringExtra(MainActivity.EXTRA_DIFFICULTY);

        textViewCategory.setText("Category : "+categoryName);
        textViewDifficulty.setText("Diffficulty : "+difficulty);

        if (savedInstanceState==null) {
            QuizDbHelper quizDbHelper = QuizDbHelper.getInstance(this);
            questionList = quizDbHelper.getQuestions(categoryID,difficulty);
            questionCounterTotal = questionList.size();
            Collections.shuffle(questionList);

            showNextQuestion();
        }else{

            questionList = savedInstanceState.getParcelableArrayList(KEY_QUESTION_LIST);
            if (questionList == null){
                finish();
            }
            questionCounterTotal=questionList.size();
            questionCounter= savedInstanceState.getInt(KEY_QUESTION_COUNT);
            currentQueston = questionList.get(questionCounter -1);
            score = savedInstanceState.getInt(KEY_SCORE);
            timeLeftInMillis = savedInstanceState.getLong(KEY_MILLIS_LEFT);
            answer = savedInstanceState.getBoolean(KEY_ANSWER);

            if (!answer){
                startCundDown();
            }else{
                updateCoundDownText();
                showSolution();
            }
        }

        buttonConfirmNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!answer){
                    if (radioButton1.isChecked() || radioButton2.isChecked() || radioButton3.isChecked()){

                        checkAnswer();

                    }else {
                        Toast.makeText(QuizActivity.this,"please select an answer",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    showNextQuestion();
                }
            }
        });
    }

    private void showNextQuestion(){
        radioButton1.setTextColor(textColorDefaultRb);
        radioButton2.setTextColor(textColorDefaultRb);
        radioButton3.setTextColor(textColorDefaultRb);
        radioGroup.clearCheck();

        if (questionCounter<questionCounterTotal)
        {
            currentQueston=questionList.get(questionCounter);

            textViewQuestion.setText(currentQueston.getQuestion());
            radioButton1.setText(currentQueston.getOption1());
            radioButton2.setText(currentQueston.getOption2());
            radioButton3.setText(currentQueston.getOption3());

            questionCounter++;
            textViewQuestionCount.setText("Question : "+questionCounter+"/"+questionCounterTotal);
            answer= false;
            buttonConfirmNext.setText("confirm");

            timeLeftInMillis=COUNTDOWN_IN_MILLIS;
            startCundDown();

        }else {
            finishQuiz();
        }
    }
    private void startCundDown(){
        countDownTimer= new CountDownTimer(timeLeftInMillis,100) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis=millisUntilFinished;
                updateCoundDownText();

            }

            @Override
            public void onFinish() {
                timeLeftInMillis=0;
                updateCoundDownText();
                checkAnswer();

            }
        }.start();
    }
    private void updateCoundDownText(){
        int minutes= (int) (timeLeftInMillis/1000) / 60;
        int seconds=(int) (timeLeftInMillis /1000) % 60;

        String timeFormated = String.format(Locale.getDefault(),"%02d:%02d",minutes,seconds);

        textViewCountDown.setText(timeFormated);

        if (timeLeftInMillis <10000){
            textViewCountDown.setTextColor(Color.RED);
        }else{
            textViewCountDown.setTextColor(textColorDefaultCD);
        }
    }

    private void checkAnswer(){
         answer=true;

         countDownTimer.cancel();

         RadioButton rbSelected= findViewById(radioGroup.getCheckedRadioButtonId());
         int answerNr = radioGroup.indexOfChild(rbSelected)+1;

         if (answerNr == currentQueston.getAsnwerNr()){
             score++;
             textViewScore.setText("Score : "+score);
         }

         showSolution();
    }

    private void showSolution(){
        radioButton1.setTextColor(Color.RED);
        radioButton2.setTextColor(Color.RED);
        radioButton3.setTextColor(Color.RED);

        switch (currentQueston.getAsnwerNr()){
            case 1:
                radioButton1.setTextColor(Color.GREEN);
                textViewQuestion.setText("Answer 1 is correct");
                break;
                case 2:
                radioButton2.setTextColor(Color.GREEN);
                textViewQuestion.setText("Answer 2 is correct");
                break; case 3:
                radioButton3.setTextColor(Color.GREEN);
                textViewQuestion.setText("Answer 3 is correct");
                break;

        }
        if (questionCounter < questionCounterTotal){
            buttonConfirmNext.setText("Next");


        }else {
            buttonConfirmNext.setText("finish");
        }

    }

    private void finishQuiz()
    {
        Intent resultIntent= new Intent();
        resultIntent.putExtra(EXTRA_SCORE,score);
        setResult(RESULT_OK,resultIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (BackPressedTime + 2000 >System.currentTimeMillis()){
            finishQuiz();
        }else {
            Toast.makeText(this,"Press back again to finigh",Toast.LENGTH_SHORT).show();

        }
        BackPressedTime=System.currentTimeMillis();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null){
            countDownTimer.cancel();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_SCORE,score);
        outState.putInt(KEY_QUESTION_COUNT,questionCounter);
        outState.putLong(KEY_MILLIS_LEFT,timeLeftInMillis);
        outState.putBoolean(KEY_ANSWER,answer);
        outState.putParcelableArrayList(KEY_QUESTION_LIST,questionList);
    }
}