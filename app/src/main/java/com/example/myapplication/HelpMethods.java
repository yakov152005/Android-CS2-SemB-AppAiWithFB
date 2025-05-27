package com.example.myapplication;

public class HelpMethods {

    public static boolean isValidPass(String password){
        return password.length() > 6;
    }

    public static boolean isValidPhoneNumber(String phone){
        boolean length = phone.length() == 10;
        boolean startWith = phone.startsWith("05");

        return (length && startWith);
    }

}
