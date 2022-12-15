package com.example.finaltext;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
import android.os.Bundle;
import android.os.IBinder;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import androidx.annotation.NonNull;

public class PersonActivity extends Activity {
    // 声明对数据库进行增删改查操作的DBAdapter类
    private DBAdapter dbAdapter;
    // 声明学生activity中的按钮控件：添加课程、ID查询、ID删除
    private Button btnDataAdd, btnDataDeleteAll, btnIdDelete,btnUpdate,btncheck,btnMusic;
    // 声明学生activity中各个属性对应编辑框
    private EditText nameEdit, classEdit, personIdEdit, IdEdit;
    // 声明提示显示框lableview和数据显示框display
    private TextView lableView;
    private ListView display;

    private int id;


    private conn myconn;
    private Music.binder binder;
    private Intent intent1;


    private int i ;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.person);
        //接收CourseActivity传入的课程id数据
        Intent intent = PersonActivity.this.getIntent();
        Bundle bundle = intent.getExtras();
        id = bundle.getInt("courseID");
        //初始化
        setupView();
        // 获得实例
        dbAdapter = new DBAdapter(this);
        // 打开数据库
        dbAdapter.open();
        showAll();



        myconn = new conn();
        intent1= new Intent(PersonActivity.this,Music.class);
        IntentFilter inf = new IntentFilter();
        inf.addAction("music");
        registerReceiver(broad,inf);

        bindService(intent1,myconn,BIND_AUTO_CREATE);

        this.registerForContextMenu(findViewById(R.id.person_display));

        if (!CourseActivity.music_start) {
            if (!CourseActivity.isFirst) {
                btnMusic.setText("暂停");
            } else if (CourseActivity.isFirst) {
                btnMusic.setText("继续");
            }
        }
        //广播音乐
        btnMusic.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                if (CourseActivity.music_start ){
                    startService(intent1);

                    btnMusic.setText("暂停");
                    CourseActivity.music_start=false;
                    CourseActivity.isFirst=false;
                }

                else{

                    binder.Pause();
                }
            }
        });


        // 添加一条数据.按钮
        btnDataAdd.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (!isRight()) {
                    return;
                }
                Person person = new Person();
                person.id = Integer.parseInt(personIdEdit.getText().toString());
                person.myName = nameEdit.getText().toString();
                person.myClass = classEdit.getText().toString();
                //对相应的课程插入数据
                long colunm = dbAdapter.insert(person, id);
                if (colunm == -1) {
                    lableView.setText("添加错误");
                } else {
                    lableView.setText("成功添加数据 , ID: " + String.valueOf(colunm));
                }
                showAll();
            }
        });

        btnUpdate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRight()) {
                    return;
                }
                AlertDialog update  =new AlertDialog.Builder(PersonActivity.this)
                        .setTitle("警告！")
                        .setMessage("确认修改吗？")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Person person = new Person();
                                person.id = Integer.parseInt(personIdEdit.getText().toString());
                                person.myName=nameEdit.getText().toString();
                                person.myClass=classEdit.getText().toString();
                                dbAdapter.updateOneData("student",person.id,person);
                                showAll();
                            }
                        }).create();
                update.show();

            }
        });

        btncheck.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!right()){
                    return;
                }
                long pid = Integer.parseInt(IdEdit.getText().toString());
                Person[] p = dbAdapter.queryOneData(pid);
                String s = p[0].toString();
                Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();



                int id = Integer.parseInt(IdEdit.getText().toString());
                Intent intent = new Intent();
                intent.setClass(PersonActivity.this, relationActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("courseID", id);
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });

        // 删除全部数据
        btnDataDeleteAll.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {

                final AlertDialog mydialog =new AlertDialog.Builder(PersonActivity.this)
                        .setTitle("警告！")
                        .setMessage("确认删除吗？")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dbAdapter.deleteAllData(id);
                                lableView.setText("数据全部删除");
                                //display.setText("");
                                display.setAdapter(null);
                            }
                        }).create();
                mydialog.show();
            }
        });
        // ID删除
        btnIdDelete.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (!right()) {
                    return;
                }
                final AlertDialog mydialog =new AlertDialog.Builder(PersonActivity.this)
                        .setTitle("警告！")
                        .setMessage("确认删除吗？")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                int id = Integer.parseInt(IdEdit.getText().toString());
                                dbAdapter.deleteOneData(id);
                                Toast.makeText(getApplicationContext(),"已删除身份证号为:" + id + "的客户",Toast.LENGTH_LONG).show();
                                showAll();
                            }
                        }).create();
                mydialog.show();


            }
        });
    }
    //判断idedit是否输入数据
    private boolean right() {
        if (IdEdit.getText().length() == 0) {
            lableView.setText("请输入符合场常理的数据");
            return false;
        } else {
            return true;
        }
    }

    //判断person相应的属性输入框是否输入数据
    private boolean isRight() {
        if (classEdit.getText().length() == 0
                || nameEdit.getText().length() == 0
                || personIdEdit.getText().length() == 0) {
            lableView.setText("请输入符合场常理的数据");
            return false;
        } else {
            return true;
        }
    }

    // 对学生activity进行初始化，获得xml中定义的相应的控件
    private void setupView() {
        btnDataAdd = (Button) findViewById(R.id.person_btn_dataAdd);
        btnDataDeleteAll = (Button) findViewById(R.id.person_btn_dataDeleteAll);

        btnIdDelete = (Button) findViewById(R.id.person_ID_delete);
        btnUpdate=findViewById(R.id.update);
        btncheck=findViewById(R.id.check);

        nameEdit = (EditText) findViewById(R.id.person_nameEdit);
        classEdit = (EditText) findViewById(R.id.person_classEdit);
        personIdEdit = (EditText) findViewById(R.id.person_idEdit);
        IdEdit = (EditText) findViewById(R.id.person_ID_entry);

        lableView = (TextView) findViewById(R.id.person_lable);
        display = findViewById(R.id.person_display);

        btnMusic=findViewById(R.id.music_person);
    }

    // 显示课程号为id的所有学生信息
    private void showAll() {
        Person[] peoples = dbAdapter.queryAllData(id);
        if (peoples == null) {
            lableView.setText("数据库里一个person也没有");
            display.setAdapter(null);
            return;
        }
        lableView.setText("数据库:");
        String[] result = new String[peoples.length];
        for (int i = 0; i < peoples.length; i++) {
            result[i] = peoples[i].toString();

        }
        //display.setText(result);
        display.setAdapter(new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,result));
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0,3,1,"修改");
        menu.add(0,4,2,"删除");
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        if (item.getMenuInfo() instanceof AdapterView.AdapterContextMenuInfo) {
            final AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

            final Person[] person = dbAdapter.queryAllData(id);

            final Person p = person[menuInfo.position];


            switch (item.getItemId()) {
                //修改
                case 3:
                    LayoutInflater updatedialog =LayoutInflater.from(PersonActivity.this);
                    final View myupdate = updatedialog.inflate(R.layout.updateperson,null);

                    EditText e1 = myupdate.findViewById(R.id.editText1);
                    e1.setText(String.valueOf(p.id));

                    AlertDialog update  =new AlertDialog.Builder(this)
                            .setTitle("警告！")
                            .setView(myupdate)
                            .setMessage("确认修改吗？")
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    closeContextMenu();
                                }
                            })
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {


                                    EditText e2 = myupdate.findViewById(R.id.editText2);
                                    EditText e3 = myupdate.findViewById(R.id.editText3);

                                    //Person person = new Person();
                                    //person.id=Integer.parseInt(e1.getText().toString());
                                    p.myName= e2.getText().toString();
                                    p.myClass=e3.getText().toString();


                                    long a =0;
                                    a=dbAdapter.updateOneData(DBAdapter.STUDENT_TABLE,p.id,p);
                                    if (a!=0){
                                        Toast.makeText(getApplicationContext(),"修改客户:" + p.myClass + "成功",Toast.LENGTH_LONG).show();
                                        showAll();
                                    }

                                }
                            }).create();
                    update.show();
                    break;
                //删除
                case 4:
                    AlertDialog mydialog =new AlertDialog.Builder(this)
                            .setTitle("警告！")
                            .setMessage("确认删除吗？")
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            })
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //Person[] person = dbAdapter.queryAllData(id);
                                    //Person p = person[menuInfo.position];
                                    dbAdapter.deleteOneData(p.id);
                                    showAll();
                                }
                            }).create();
                    mydialog.show();
                    break;


            }
        }
        return true;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();


        unregisterReceiver(broad);

    }

    public BroadcastReceiver broad = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            int i = intent.getIntExtra("status",-1);
            switch (i){
                case 1:
                    btnMusic.setText("继续");

                    Toast.makeText(getApplicationContext(),"收到广播：暂停播放",Toast.LENGTH_SHORT).show();
                    break;
                case 0:
                    btnMusic.setText("暂停");
                    Toast.makeText(getApplicationContext(),"收到广播：继续播放",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private class conn implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder= (Music.binder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }


}