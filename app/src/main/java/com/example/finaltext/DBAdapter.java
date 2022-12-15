package com.example.finaltext;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

/**
 * @author
 *
 */
public class DBAdapter {
    private static final String DB_NAME = "student.db";// 数据库名
    public static final String STUDENT_TABLE = "student";// 学生表、
    public static final String COURSE_TABLE = "course";// 课程表
    public static final String RELATION_TABLE = "relation";// 学生、课程关系表
    private static final int DB_VERSION = 1;// 数据库版本号

    public static final String STUDENT_ID = "studentId";// 学生的学号
    public static final String STUDENT_NAME = "myName";// 学生姓名
    public static final String STUDENT_CLASS = "myClass";// 学生班级

    public static final String COURSE_ID = "courseId";
    public static final String COURSE_NAME = "courseName";
    public static final String COURSE_OBJ = "courseObj";
    public static final String COURSE_PHONE = "phone";

    public static final String RELATION_ID = "relationId";

    private SQLiteDatabase db;
    private Context mcontext;
    private DBOpenHelper dbOpenHelper;

    public DBAdapter(Context context) {
        mcontext = context;
    }

    public void open() throws SQLiteException {
        // 创建一个DBOpenHelper实例
        dbOpenHelper = new DBOpenHelper(mcontext, DB_NAME, null, DB_VERSION);
        // 通过dbOpenHelper.getWritableDatabase()或者dbOpenHelper.getReadableDatabase()
        //创建或打开一个数据库SQLiteDatabase实例，其中dbOpenHelper.getWritableDatabase()
        //得到的数据库具有读写的权限，而dbOpenHelper.getReadableDatabase()得到的数据库则具有只读的权限。
        try {
            db = dbOpenHelper.getWritableDatabase();
        } catch (SQLiteException e) {
            db = dbOpenHelper.getReadableDatabase();
        }
    }

    // 关闭数据库
    public void close() {
        if (db != null) {
            db.close();
            db = null;
        }
    }

    //修改课程
    public long updatecourse(String table, long id, Course course) {
        ContentValues newValues = new ContentValues();

        newValues.put(COURSE_NAME, course.name);
        newValues.put(COURSE_OBJ, course.obj);
        newValues.put(COURSE_PHONE, course.phone);

        return db.update(table, newValues, COURSE_ID + "=" + id, null);
    }

    // 查询学生的选课信息
    public Relation[] queryData(int id) {
        Cursor result = db
                .rawQuery(
                        "select courseId from relation  where studentId="
                                + id + ";", null);

        return ConvertToRelation(result,id,getstuname(id));
    }

    public String getstuname(int id){
        Cursor result = db.rawQuery("select myName from student  where studentId=" + id + ";", null);
        int resultCounts = result.getCount();
        if (resultCounts == 0 || !result.moveToFirst()) {
            return null;
        }
        String name = result.getString(result.getColumnIndex(STUDENT_NAME));
        return name;
    }

    // 用cursor操作将查询到的数据放入相应的数组中
    private Relation[] ConvertToRelation(Cursor cursor,int id,String stuid) {
        // cursor.getCount()获得用户查询得到的信息条数
        int resultCounts = cursor.getCount();
        if (resultCounts == 0 || !cursor.moveToFirst()) {
            return null;
        }
        Relation[] relation = new Relation[resultCounts];
        for (int i = 0; i < resultCounts; i++) {
            relation[i] = new Relation();
            //relation[i].id = cursor.getInt(cursor.getColumnIndex(STUDENT_ID));
            relation[i].id = id;
            relation[i].courseId = cursor.getInt(cursor.getColumnIndex(COURSE_ID));
            //relation[i].studentId = cursor.getInt(cursor.getColumnIndex(STUDENT_ID));
            relation[i].studentId =stuid;
            cursor.moveToNext();
        }
        return relation;
    }





    /**
     * 向表中添加一条数据
     *
     * @param people
     * @return
     */
    public long insert(Person person, int id) {
        // ContentValues类存储了一组键值对
        ContentValues newValues = new ContentValues();

        newValues.put(STUDENT_ID, person.id);
        newValues.put(STUDENT_NAME, person.myName);
        newValues.put(STUDENT_CLASS, person.myClass);

        ContentValues values = new ContentValues();

        values.put(COURSE_ID, id);
        values.put(STUDENT_ID, person.id);

        db.insert(RELATION_TABLE, null, values);

        return db.insert(STUDENT_TABLE, null, newValues);
    }

    // 添加课程
    public long insert(Course course) {
        ContentValues newValues = new ContentValues();

        newValues.put(COURSE_ID, course.id);
        newValues.put(COURSE_NAME, course.name);
        newValues.put(COURSE_OBJ, course.obj);
        newValues.put(COURSE_PHONE, course.phone);

        return db.insert(COURSE_TABLE, null, newValues);
    }

    // 通过id删除一条学生信息
    public long deleteOneData(long id) {
        return db.delete(STUDENT_TABLE, STUDENT_ID + "=" + id, null);
    }

    // 通过id删除一条课程信息
    public long deleteOneCourse(long id) {
        return db.delete(COURSE_TABLE, COURSE_ID + "=" + id, null);
    }





    // 删除课程ID为id的全部课程学生
    public void deleteAllData(int id) {
        db.delete(RELATION_TABLE, COURSE_ID + "=?",
                new String[] { String.valueOf(id) });
    }

    // 删除所有的数据表
    public void deleteAllTable() {
        db.delete(COURSE_TABLE, null, null);
        db.delete(RELATION_TABLE, null, null);
        db.delete(STUDENT_TABLE, null, null);
    }

    // 查询studentID为id的一个同学的信息
    public Person[] queryOneData(long id) {
        Cursor result = null;
        result = db.query(STUDENT_TABLE, null, STUDENT_ID + "=" + id, null,
                null, null, null);
        return ConvertToPeople(result);
    }

    // 查询courseID为id的一门课程信息
    public Course[] queryOneCourse(long id) {
        Cursor result = null;
        result = db.query(COURSE_TABLE, null, COURSE_ID + "=" + id, null, null,
                null, null);
        return ConvertToCourse(result);
    }

    // 查询课程ID为id的所有学生的信息
    public Person[] queryAllData(int id) {
        Cursor result = db
                .rawQuery(
                        "select a.studentId,a.myName,a.myClass from student a "
                                + "where a.studentId in (select b.studentId from relation b where b.courseId="
                                + id + ");", null);
        return ConvertToPeople(result);
    }

    // 查询所有的课程信息
    public Course[] queryAllCourse() {
        Cursor result = db.query(COURSE_TABLE, new String[] { COURSE_ID,
                        COURSE_NAME, COURSE_OBJ, COURSE_PHONE }, null, null, null,
                null, null);
        return ConvertToCourse(result);
    }

    // 修改数据库表的数据
    public long updateOneData(String table, long id, Person people) {
        ContentValues newValues = new ContentValues();

        newValues.put(STUDENT_NAME, people.myName);
        newValues.put(STUDENT_CLASS, people.myClass);
        newValues.put(STUDENT_ID, people.id);

        return db.update(table, newValues, STUDENT_ID + "=" + id, null);
    }

    // 用cursor操作将查询到的数据放入相应的数组中
    private Course[] ConvertToCourse(Cursor cursor) {
        int resultCounts = cursor.getCount();
        if (resultCounts == 0 || !cursor.moveToFirst()) {
            return null;
        }
        Course[] courses = new Course[resultCounts];
        for (int i = 0; i < resultCounts; i++) {
            courses[i] = new Course();
            courses[i].id = cursor.getInt(cursor.getColumnIndex(COURSE_ID));
            courses[i].name = cursor.getString(cursor
                    .getColumnIndex(COURSE_NAME));
            courses[i].obj = cursor
                    .getString(cursor.getColumnIndex(COURSE_OBJ));
            courses[i].phone = cursor.getString(cursor
                    .getColumnIndex(COURSE_PHONE));
            cursor.moveToNext();
        }
        return courses;
    }

    // 用cursor操作将查询到的数据放入相应的数组中
    private Person[] ConvertToPeople(Cursor cursor) {
        // cursor.getCount()获得用户查询得到的信息条数
        int resultCounts = cursor.getCount();
        if (resultCounts == 0 || !cursor.moveToFirst()) {
            return null;
        }
        Person[] peoples = new Person[resultCounts];
        for (int i = 0; i < resultCounts; i++) {
            peoples[i] = new Person();
            peoples[i].id = cursor.getInt(cursor.getColumnIndex(STUDENT_ID));
            peoples[i].myName = cursor.getString(cursor
                    .getColumnIndex(STUDENT_NAME));
            peoples[i].myClass = cursor.getString(cursor
                    .getColumnIndex(STUDENT_CLASS));
            cursor.moveToNext();
        }
        return peoples;
    }

    /**
     * 静态Helper类，用于建立、更新和打开数据库
     */
    private static class DBOpenHelper extends SQLiteOpenHelper {
        // 创建数据库的sql语句
        private static final String STUDENT_CREATE = "CREATE TABLE "
                + STUDENT_TABLE + "(" + STUDENT_ID + " Integer primary key,"
                + STUDENT_NAME + " text not null," + STUDENT_CLASS + " text);";
        private static final String COURSE_CREATE = "CREATE TABLE "
                + COURSE_TABLE + "(" + COURSE_ID + " Integer primary key , "
                + COURSE_NAME + " text not null," + COURSE_OBJ + " text,"
                + COURSE_PHONE + " integer);";
        private static final String RELATION_CREATE = "CREATE TABLE "
                + RELATION_TABLE + "(" + STUDENT_ID + " Integer not null,"
                + COURSE_ID + " Integer not null);";

        // 在用户创建DBOpenHelper的构造函数，其自动调用自身的onCreate(SQLiteDatabase db)函数
        public DBOpenHelper(Context context, String name,
                            CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // 执行sql语句，创建数据库
            db.execSQL(STUDENT_CREATE);
            db.execSQL(COURSE_CREATE);
            db.execSQL(RELATION_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase _db, int oldVersion, int newVersion) {
            // 函数在数据库需要升级时被调用，
            // 一般用来删除旧的数据库表，
            // 并将数据转移到新版本的数据库表中
            _db.execSQL("DROP TABLE IF EXISTS " + STUDENT_TABLE);
            _db.execSQL("DROP TABLE IF EXISTS " + COURSE_TABLE);
            _db.execSQL("DROP TABLE IF EXISTS " + RELATION_TABLE);
            onCreate(_db);
        }
    }
}
