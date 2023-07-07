package com.heroku.java.MODEL;

public class Users {
    private int userid;
    private String name;
    private String ic;
    private String email;
    private String password;
    private String confirm_password;

    public Users(int userid, String name, String ic, String email, String password) {
        this.userid = userid;
        this.name = name;
        this.ic = ic;
        this.email = email;
        this.password = password;
    }

    public Users() {
      
    }
    
    
    public Users(int userid, String name, String ic, String email) {
        this.userid = userid;
        this.name = name;
        this.ic = ic;
        this.email = email;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getIc() {
        return ic;
    }
    public void setIc(String ic) {
        this.ic = ic;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public int getUserid() {
        return userid;
    }
    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getConfirm_password() {
      return confirm_password;
    }

    public void setConfirm_password(String confirm_password) {
      this.confirm_password = confirm_password;
    }

    
}
