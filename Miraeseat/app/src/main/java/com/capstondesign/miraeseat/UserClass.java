package com.capstondesign.miraeseat;

public class UserClass {
    String Email;
    String Nick;
    // imagepath

    public UserClass(String email, String nick) {
        this.Email = email;
        this.Nick = email;
        // imagepath
    }

    public String getEmail() { return Email; }

    public String getNick() { return Nick; }

    public void setNick(String nick) { this.Nick = nick; }

    // imagepath 관련 함수
}
