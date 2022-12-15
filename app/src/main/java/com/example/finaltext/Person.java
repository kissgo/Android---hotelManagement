package com.example.finaltext;

public class Person
{
    public int id ;
    public String myClass  ;
    public String myName ;
    @Override
    public String toString()
    {
        String result = "身份证: "+this.id+","
                +"姓名:"+this.myName+","
                +"手机号:"+this.myClass;
        return result;
    }


}