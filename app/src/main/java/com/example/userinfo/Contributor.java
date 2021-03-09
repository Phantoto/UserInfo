package com.example.userinfo;

class Contributor {

    private String login;
    private int contributions;

    // И другие поля
    //String html_url;

    @Override
    public String toString() {
        return login + " (" + contributions + ")";
    }
}
