package com.capstondesign.miraeseat;

import com.google.firebase.firestore.auth.User;

public class UserClass {
    private String email;
    private String nick;
    private String imagepath;

    public UserClass() { }

    public UserClass(String email, String nick, String imagepath) {
        this.email = email;
        this.nick = nick;
        this.imagepath = imagepath;
    }

    public String getEmail() { return email; }

    public String getNick() { return nick; }

    public String getImagepath() { return imagepath; }

    public void setNick(String nick) { this.nick = nick; }

    // imagepath 관련 함수
}
