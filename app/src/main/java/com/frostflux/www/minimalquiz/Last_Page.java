package com.frostflux.www.minimalquiz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Last_Page extends AppCompatActivity {

    TextView mGrade, mFinalScore;
    Button mRetryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        mGrade = (TextView) findViewById(R.id.grade);
        mFinalScore = (TextView) findViewById(R.id.outOf);
        mRetryButton = (Button) findViewById(R.id.retry);

        Bundle bundle = getIntent().getExtras();
        int score = bundle.getInt("finalScore");

        mFinalScore.setText("You scored " + score + " out of " + Database.questions.length);

        if(score==3){
            mGrade.setText("Outstanding!");
        }
        else if(score==2){
            mGrade.setText("Good Work!");
        }else{
            mGrade.setText("Go over and try again");
        }

        mRetryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Last_Page.this, Mid_Page.class));
                Last_Page.this.finish();
            }
        });
    }
}
