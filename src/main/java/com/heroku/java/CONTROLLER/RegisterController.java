package com.heroku.java.CONTROLLER;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.heroku.java.MODEL.Users;
import com.heroku.java.SERVICES.AccountServices;

import jakarta.servlet.http.HttpSession;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Map;

@Controller
public class RegisterController {

  private final DataSource dataSource;
  private AccountServices accountServices;

  @Autowired
  public RegisterController(DataSource dataSource, AccountServices accountServices) {
    this.dataSource = dataSource;
    this.accountServices = accountServices;
  }

  @GetMapping("/register")
  public String signUp() {
    return "register";
  }

  @PostMapping("/create-account")
  public String createAccount(@ModelAttribute("user") Users users) {

    boolean status = accountServices.createAccountMember(users);

    if(status){
      return "redirect:/";
    }else{
      return "redirect:/register";
    }
  }
}
