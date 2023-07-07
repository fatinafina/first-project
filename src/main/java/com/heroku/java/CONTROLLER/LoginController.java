package com.heroku.java.CONTROLLER;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.heroku.java.MODEL.Login;
import com.heroku.java.MODEL.Users;
import com.heroku.java.SERVICES.AccountServices;

import jakarta.servlet.http.HttpSession;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Map;

@Controller
public class LoginController {
  private DataSource dataSource;
  private AccountServices accountServices;

  @Autowired
  public LoginController(DataSource dataSource, AccountServices accountServices) {
    this.dataSource = dataSource;
    this.accountServices = accountServices;
  }

  @GetMapping("/")
  public String login(HttpSession session) {
    if (accountServices.checkSession(session)) {
      return "redirect:/home-s";
    } else {
      System.out.println("Session expired or invalid...");
      return "login";
    }
    // return "login";
  }

  @GetMapping("/logout")
  public String logout(HttpSession session) {
    session.invalidate();
    System.out.println("Successfully logged out...");
    return "redirect:/";
  }

  @GetMapping("/home-s")
  public String homeStaff(HttpSession session) {
    if (accountServices.checkSession(session)) {
      return "dashboard-s";
    } else {
      return "redirect:/";
    }
  }

   @GetMapping("/home-m")
  String homeMember(HttpSession session) {
    if (accountServices.checkSession(session)) {
      return "dashboard-m";
    } else {
      return "redirect:/";
    }
  }

  @PostMapping("/login-account")
  String homepage(HttpSession session, @ModelAttribute("login") Users users, Model model) {

    String loginAcc = accountServices.loginAccount(users);
    System.out.println("loginAcc : " + loginAcc);
    System.out.println("testing...");

    if (loginAcc.equals("member")) { // success login as member
      return "redirect:/home-m";
    } else if (loginAcc.equals("staff")) { // success login as staff
      return "redirect:/home-s";
    } else if (loginAcc.equals("none")) { // wrong password or email
      System.out.println(">>>> Wrong password or email");
      return "redirect:/  ";
    } else {
      return "redirect:/  ";
    }

  }
}
