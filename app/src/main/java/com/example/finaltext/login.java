package com.example.finaltext;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText e1 = findViewById(R.id.editText);
        final EditText e2 = findViewById(R.id.editTextTextPassword);
        Button bt = findViewById(R.id.button);

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (e1.getText().toString().equals("gyf") && e2.getText().toString().equals("123")){
                    Intent intent = new Intent(login.this, CourseActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("name", e1.getText().toString());
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                }
                else
                    Toast.makeText(getApplicationContext(),"用户名或密码不正确！请重新输入。",Toast.LENGTH_LONG)
                            .show();
            }
        });


    }
}