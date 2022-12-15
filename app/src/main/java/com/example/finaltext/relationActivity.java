package com.example.finaltext;

import android.content.Intent;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class relationActivity extends AppCompatActivity {

    // 声明对数据库进行增删改查操作的DBAdapter类
    private DBAdapter dbAdapter;

    ListView display;

    private int id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relation);

        //接收CourseActivity传入的课程id数据
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        id = bundle.getInt("courseID");

        display = findViewById(R.id.lv);
        // 获得实例
        dbAdapter = new DBAdapter(this);
        // 打开数据库
        dbAdapter.open();

        TextView tv = findViewById(R.id.textView);
        tv.setText("身份证号为“" + id + "“ 的顾客住过的房间" );

        showAll();

    }



    // 显示课程号为id的所有学生信息
    private void showAll() {
        Relation[] relation = dbAdapter.queryData(id);
        if (relation == null) {
            //lableView.setText("数据库里一个person也没有");

            return;
        }
        //lableView.setText("数据库:");
        String[] result = new String[relation.length];
        for (int i = 0; i < relation.length; i++) {
            result[i] = relation[i].toString();

        }
        //display.setText(result);
        display.setAdapter(new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,result));
    }

}


