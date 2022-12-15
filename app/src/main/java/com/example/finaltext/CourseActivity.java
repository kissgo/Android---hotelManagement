package com.example.finaltext;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.io.*;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

public class CourseActivity extends Activity {
    // 声明对数据库进行增删改查操作的DBAdapter类
    private DBAdapter dbAdapter;
    // 声明课程activity中的按钮控件：添加课程、ID查询、ID删除
    private Button btnDataAdd, btnIDQuery, btnIdDelete,btnMusic;
    // 声明课程activity中各个属性对应编辑框
    private EditText courseIdEdit, nameEdit, objEdit, phoneEdit, IdEdit;
    // 声明提示显示框lableview和数据显示框display
    private TextView lableView;
    private ListView display;

    private conn myconn;
    private Music.binder binder;
    private Intent intent;

    public int x=0;

    public static boolean music_start = true;
    public static boolean isFirst= true;

    private int i =1;


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course);

        Bundle bundle =getIntent().getExtras();
        Toast.makeText(getApplicationContext(),bundle.getString("name")+"欢迎进入系统",Toast.LENGTH_LONG).show();

        myconn = new conn();
        intent= new Intent(CourseActivity.this,Music.class);
        IntentFilter inf = new IntentFilter();
        inf.addAction("music");
        registerReceiver(broad,inf);






        // 对activity进行初始化，获得相应的控件
        setupView();
        // 获得数据库实例
        dbAdapter = new DBAdapter(this);
        dbAdapter.open();
        // 显示数据库中所有的course
        showAll();

        this.registerForContextMenu(findViewById(R.id.course_display));




        // 添加一条数据.按钮
        btnDataAdd.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (!isRight()) {
                    return;
                }
                Course course = new Course();
                // 从编辑框中获得相应的属性值
                course.id = Integer.parseInt(courseIdEdit.getText().toString());
                course.name = nameEdit.getText().toString();
                course.obj = objEdit.getText().toString();
                course.phone = phoneEdit.getText().toString();
                // 插入一个数据，并获得插入的row标识
                long colunm = dbAdapter.insert(course);
                if (colunm == -1) {
                    lableView.setText("添加错误");
                } else {
                    lableView.setText("成功添加数据 , ID: " + String.valueOf(colunm));
                }
                showAll();
            }
        });
        // ID查询操作
        btnIDQuery.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (!right()) {
                    return;
                }
                int id = Integer.parseInt(IdEdit.getText().toString());
                // 定义intent，实现activity跳转，并通过bundle携带数据将对应的课程id传入PersonActivity.java中
                Intent intent = new Intent();
                intent.setClass(CourseActivity.this, PersonActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("courseID", id);
                intent.putExtras(bundle);

                //unbindService(myconn);
                startActivity(intent);
            }
        });
        // ID删除
        btnIdDelete.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (!right()) {
                    return;
                }
                final AlertDialog mydialog =new AlertDialog.Builder(CourseActivity.this)
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
                                // 删除对应id的课程
                                dbAdapter.deleteOneCourse(id);
                                showAll();
                                Toast.makeText(getApplicationContext(),"已删除房间号为:" + id + "的房间",Toast.LENGTH_LONG).show();
                            }
                        }).create();
                mydialog.show();
            }
        });

        //广播音乐
        btnMusic.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                if (music_start ){
                    startService(intent);
                    bindService(intent,myconn,BIND_AUTO_CREATE);
                    btnMusic.setText("暂停");
                    music_start=false;
                    isFirst=false;

                }
                else{
                    //intent.setAction("music");
                    //if (i==0)
                       // i=1;
                    //else if (i==1)
                        //i=0;
                    //intent.putExtra("status",i);
                    //binder.Pause(intent);
                    binder.Pause();
                }
            }
        });

        display.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                LayoutInflater updatedialog =LayoutInflater.from(CourseActivity.this);
                 View image = updatedialog.inflate(R.layout.image,null);

                Course[] courses = dbAdapter.queryAllCourse();
                Course c = courses[i];

                ImageView iv = image.findViewById(R.id.imageView);

                String f = "/sdcard/p/" +c.id + ".jpg";


                Bitmap bitmap = getLoacalBitmap(f);
                ContentResolver cr = getContentResolver();

                iv.setImageBitmap(bitmap);





                AlertDialog image1  =new AlertDialog.Builder(CourseActivity.this)
                        .setTitle("房间样式")
                        .setView(image)
                        //.setMessage(String.valueOf(c.id))

                        .create();

                image1.show();
            }


        });



    }



    /**
     * 加载本地图片
     * @param url
     * @return
     */
    public static Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);  ///把流转化为Bitmap图片

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Activity.DEFAULT_KEYS_DIALER:
                    // 解析返回的图片成bitmap
                    Bitmap bmp = (Bitmap) intent.getExtras().get("data");


                    String f = x + ".jpg";
                    System.out.println(f);
                    File file = new File("/sdcard/p/" + f);
                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(file);
                        bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    } catch (Exception x) {
                        Log.e(TAG, "save Bitmap error=" + x);
                    } finally {
                        try {
                            fos.flush();
                            fos.close();
                        } catch (Exception x) {
                            Log.e(TAG, "save Bitmap error=" + x);
                        }
                    }
                    break;
                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }



    private boolean right() {
        if (IdEdit.getText().length() == 0) {
            lableView.setText("请输入符合场常理的数据");
            return false;
        } else {
            return true;
        }
    }

    private boolean isRight() {
        if (courseIdEdit.getText().length() == 0
                || nameEdit.getText().length() == 0
                || objEdit.getText().length() == 0
                || phoneEdit.getText().length() == 0) {
            lableView.setText("请输入符合场常理的数据");
            return false;
        } else {
            return true;
        }
    }

    // 对activity进行初始化
    private void setupView() {
        btnDataAdd = (Button) findViewById(R.id.course_btn_dataAdd);
        btnIDQuery = (Button) findViewById(R.id.course_ID_query);
        btnIdDelete = (Button) findViewById(R.id.course_ID_delete);

        nameEdit = (EditText) findViewById(R.id.course_nameEdit);
        courseIdEdit = (EditText) findViewById(R.id.course_idEdit);
        objEdit = (EditText) findViewById(R.id.course_objEdit);
        phoneEdit = (EditText) findViewById(R.id.course_phoneEdit);
        IdEdit = (EditText) findViewById(R.id.course_ID_entry);

        lableView = (TextView) findViewById(R.id.course_lable);
        //display = (TextView) findViewById(R.id.course_display);
        display = findViewById(R.id.course_display);

        btnMusic= findViewById(R.id.music_course);
    }

    private void showAll() {
        Course[] courses = dbAdapter.queryAllCourse();
        if (courses == null) {
            lableView.setText("数据库里一个course也没有");
            //display.setText("");
            display.setAdapter(null);
            return;
        }
        lableView.setText("数据库:");
        String[] result = new String[courses.length];
        for (int i = 0; i < courses.length; i++) {
            result[i] = courses[i].toString();

        }
        //display.setText(result);
        display.setAdapter(new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,result));
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0,1,0,"修改");
        menu.add(0,2,1,"删除");
        menu.add(0,3,2,"拍摄");
    }

    
    



    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        if (item.getMenuInfo() instanceof AdapterView.AdapterContextMenuInfo) {
            final AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
           final Course[] courses = dbAdapter.queryAllCourse();

            final Course c = courses[menuInfo.position];

            x=c.id;
            switch (item.getItemId()) {
                //修改
                case 1:
                    LayoutInflater updatedialog =LayoutInflater.from(CourseActivity.this);
                    final View myupdate = updatedialog.inflate(R.layout.update,null);



                    final EditText e1 = myupdate.findViewById(R.id.editText1);
                    e1.setText(String.valueOf(c.id));

                    AlertDialog update  =new AlertDialog.Builder(this)
                            .setTitle("警告！")
                            .setView(myupdate)
                            .setMessage("确认修改吗？")
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            })
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {


                                    EditText e2 = myupdate.findViewById(R.id.editText2);
                                    EditText e3 = myupdate.findViewById(R.id.editText3);
                                    EditText e4 = myupdate.findViewById(R.id.editText4);

                                    //Course courses = new Course();
                                    //courses.id = Integer.parseInt(e1.getText().toString());
                                    //courses.name= e2.getText().toString();
                                    //courses.obj = e3.getText().toString();
                                    //courses.phone = e4.getText().toString();




                                    c.name= e2.getText().toString();
                                    c.obj = e3.getText().toString();
                                    c.phone = e4.getText().toString();





                                    long a =0;
                                    a=dbAdapter.updatecourse(DBAdapter.COURSE_TABLE,c.id,c);



                                    if (a!=0){
                                        Toast.makeText(getApplicationContext(),"修改房间:" + c.id + "成功",Toast.LENGTH_LONG).show();
                                        showAll();
                                    }

                                }
                            }).create();

                    update.show();
                break;
                    //删除
                case 2:
                    AlertDialog mydialog =new AlertDialog.Builder(this)
                            .setTitle("警告！")
                            .setMessage("确认删除吗？")
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    closeContextMenu();
                                }
                            })
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //Course[] courses = dbAdapter.queryAllCourse();
                                    //Course c = courses[menuInfo.position];
                                    dbAdapter.deleteOneCourse(c.id);
                                    showAll();
                                }
                            }).create();
                    mydialog.show();
                    break;

                case 3:
                    Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    ActivityCompat.requestPermissions(CourseActivity.this, new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},1);

                    //Bundle bundle = new Bundle();
                    //bundle.putInt("p",c.id);
                    //intent.putExtras(bundle);
                    //intent.putExtra("p",c.id);
                    startActivityForResult(intent, Activity.DEFAULT_KEYS_DIALER);

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

                    isFirst = true;
                    break;
                case 0:
                    btnMusic.setText("暂停");
                    Toast.makeText(getApplicationContext(),"收到广播：继续播放",Toast.LENGTH_SHORT).show();

                    isFirst = false;
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
