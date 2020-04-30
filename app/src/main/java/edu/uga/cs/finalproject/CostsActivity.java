package edu.uga.cs.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class CostsActivity extends AppCompatActivity {

    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_costs);

        button = findViewById(R.id.backHome);
        Log.d("tester", "inside pastResultActivity ");
        ////quizData = new QuizData(getApplicationContext());
        //quizList = quizData.retrieveQuiz();
        TableLayout tableLayout = findViewById(R.id.table);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CostsActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });


        /**
        index++;

        //This loops goes through the quizlist in the database and prints them out with their corresponding
        //date and score in a table format. We had no clue how to use recycler after trying a couple of times
        for (int i = quizList.size() - 1; i > 0; i--) {
            if (quizData.retrieveId((int) quizList.get(i).getId()).getScore() < 0) {}
            row = new TableRow(this);
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(20, 10, 20, 10);
            row.setLayoutParams(layoutParams);

            date = new TextView(this);
            date.setText(quizData.retrieveId((int) quizList.get(i).getId()).getDate());

            score = new TextView(this);
            String scoreText = "" + quizData.retrieveId((int) quizList.get(i).getId()).getScore();


            row.addView(date, 0);
            row.addView(score, 1);
            tableLayout.addView(row, index);
            index++;
        }
    }

**/
    }
}
