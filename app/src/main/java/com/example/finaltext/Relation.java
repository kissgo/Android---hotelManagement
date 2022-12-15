package com.example.finaltext;

public class Relation {
    public int id ;
    public String studentId  ;
    public long courseId ;
    @Override
    public String toString()
    {
        String result = "身份证号 : "+this.id+","
                +"姓名:"+this.studentId+","
                +"入住房间:"+this.courseId;
        return result;
    }
}
