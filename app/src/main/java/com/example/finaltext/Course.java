package com.example.finaltext;

public class Course {
    public int id ;
    public String name  ;
    public String obj ;
    public String phone ;
    @Override
    public String toString()
    {
        String result = "房间号 : "+this.id+","
                +"房间类型:"+this.name+","
                +"价格:"+this.obj+","
                +"房间描述:"+this.phone;
        return result;
    }

}
