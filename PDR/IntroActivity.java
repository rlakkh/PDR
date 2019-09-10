package com.example.rlakkh.pdr;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import static com.example.rlakkh.pdr.MainActivity.name;
/**
 * Created by RLAKKH on 2018-05-12.
 */

public class IntroActivity extends AppCompatActivity {
    TextView textView;
    TextView nameView;
    EditText nameEdit;
    Button registBtn;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        getSupportActionBar().hide();

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

        nameView = (TextView) findViewById(R.id.nameView);
        textView = (TextView) findViewById(R.id.app_name);
        nameEdit = (EditText) findViewById(R.id.name);
        registBtn = (Button) findViewById(R.id.submit);

        nameEdit.setTextColor(Color.WHITE);
        nameView.setTextSize(20);
        nameView.setText("\n");
        nameView.setTextColor(Color.WHITE);
        nameView.setTextSize(20);
        nameView.setText("이름:");
        textView.setText("PDR");
        registBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = nameEdit.getText().toString();
                Intent intent = new Intent(IntroActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
