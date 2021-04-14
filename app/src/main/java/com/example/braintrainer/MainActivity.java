package com.example.braintrainer;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private ArrayList<TextView> options = new ArrayList<>();
    private ArrayList<Integer> opinionIndex;
    private TextView textViewTimer;
    private TextView textViewScore;
    private TextView textViewQuestion;

    private String question;
    private int rightAnswer;
    private int rightAnswerPosition;
    private boolean isPositive; //положительное число
    private int min = 5;
    private int max = 30;

    private int countOfQuestions = 0;
    private int countOfRightAnswers = 0;
    private boolean gameOver = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        opinionIndex = new ArrayList<>();
        textViewTimer = findViewById(R.id.textViewTimer);
        textViewScore = findViewById(R.id.textViewScore);
        textViewQuestion = findViewById(R.id.textViewQuestion);
        options.add(findViewById(R.id.textViewOpinion0));
        options.add(findViewById(R.id.textViewOpinion1));
        options.add(findViewById(R.id.textViewOpinion2));
        options.add(findViewById(R.id.textViewOpinion3));
        for (int i = 0; i < options.size(); i++)
            opinionIndex.add(i);
        playNext();
        CountDownTimer timer = new CountDownTimer(20000, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                textViewTimer.setText(getTime(millisUntilFinished));
                if (millisUntilFinished < 5000) {
                    textViewTimer.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                }
            }

            @Override
            public void onFinish() {
                gameOver = true;
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                int max = preferences.getInt("max", 0);
                if (countOfRightAnswers >= max) {
                    preferences.edit().putInt("max", countOfRightAnswers).apply();
                }
                Intent intent = new Intent(MainActivity.this, ScoreActivity.class);
                intent.putExtra("result", countOfRightAnswers);
                startActivity(intent);
            }
        };
        timer.start();
    }

    public void playNext() {
        generateQuestion();
        for (int i = 0; i < options.size(); i++) {
            int answer;
            if (i == rightAnswerPosition) {
                answer = rightAnswer;
                //options.get(i).setText(Integer.toString(rightAnswer));
            } else {
                do {
                    answer = (int) (Math.random() * max * 2 + 1) - (max - min);
                    if (answer == rightAnswer) continue;
                    int flag = 0;
                    for (int j = 0; j < i; j++) {
                        if (answer == opinionIndex.get(j)) {
                            flag = 1;
                        }
                    }
                    if (flag == 0) break;
                } while (true);
            }
            opinionIndex.set(i, answer);
            options.get(i).setText(Integer.toString(answer));
            String score = String.format("%s / %s", countOfRightAnswers, countOfQuestions);
            textViewScore.setText(score);
        }
    }

    private String getTime(long millis) {
        int seconds = (int) millis / 1000;
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    private void generateQuestion() {
        int a = (int) (Math.random() * (max - min + 1) + min);
        int b = (int) (Math.random() * (max - min + 1) + min);
        //Устанавливаем значение знака ( + или - оно будет)
        int mark = (int) (Math.random() * 2);
        isPositive = mark == 1;
        if (isPositive) {
            rightAnswer = a + b;
            question = String.format("%s + %s", a, b);
        } else {
            rightAnswer = a - b;
            question = String.format("%s - %s", a, b);
        }
        textViewQuestion.setText(question);
        rightAnswerPosition = (int) (Math.random() * 4);
    }

    public void onClickAnswer(View view) {
        if (!gameOver) {
            TextView textView = (TextView) view;
            String answer = textView.getText().toString();
            int chosenAnswer = Integer.parseInt(answer);
            if (chosenAnswer == rightAnswer) {
                countOfRightAnswers++;
                Toast.makeText(this, "Верно", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Неверно", Toast.LENGTH_SHORT).show();
            }
            countOfQuestions++;
            playNext();
        }
    }
}
