package com.heroku.java.CONTROLLER;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import com.heroku.java.MODEL.Users;
import com.heroku.java.SERVICES.AccountServices;

import jakarta.servlet.http.HttpSession;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Map;

@Controller
public class AccountController {

  private final DataSource dataSource;
  private AccountServices accountServices;

  @Autowired
  public AccountController(DataSource dataSource, AccountServices accountServices) {
    this.dataSource = dataSource;
    this.accountServices = accountServices;
  }

  // public void init() {
  // accountServices = new AccountServices(dataSource);
  // }

  @GetMapping("/profile-s")
  public String showStaffProfile(Model model, Users users, HttpSession session) {
    if (accountServices.checkSession(session)) {
      users.setUserid((int) session.getAttribute("userid"));
      model.addAttribute("account", accountServices.getUserInformation(users));

      return "profile-s";
    } else {
      return "redirect:/";
    }
  }

  @GetMapping("/profile-m")
  String showMemberProfile(Model model, Users users, HttpSession session) {
    if (accountServices.checkSession(session)) {
      users.setUserid((int) session.getAttribute("userid"));
      model.addAttribute("account", accountServices.getUserInformation(users));
      return "profile-m";
    } else {
      return "redirect:/";
    }
  }

  @PostMapping("/createAccountStaff")
  public String createAccountStaff(@ModelAttribute("acccountStaff") Users users, HttpSession session) {
    if (accountServices.checkSession(session)) {
      users.setUserid((int) session.getAttribute("userid"));
      String password = users.getPassword();
      String confirm_password = users.getConfirm_password();
      boolean status = false;

      if (password.equals(confirm_password)) {
        status = accountServices.createAccountStaff(users);
        System.out.println("(Status) create account staff : " + status);
        if (status == true) {
          return "redirect:/account-s";
        } else {
          return "redirect:/create-staff";
        }
      } else {
        status = false;
        System.out.println("(Status) create account staff : " + status);
        System.out.println(">>> Confirm password is incorrect...");
        return "redirect:/create-staff";
      }
    } else {
      return "redirect:/";
    }
  }

  @GetMapping("/account-s")
  String showStaff(Model model, HttpSession session) {

    // if (accountServices.checkSession(session)) {
      ArrayList<Users> users = new ArrayList<>();
      users = accountServices.getAllStaff();
      model.addAttribute("users", users);

      return "account-s";
    // } else {
    //   return "redirect:/";
    // }
  }

  @GetMapping("/account-m")
  String showMember(Model model, HttpSession session) {
    ArrayList<Users> users = new ArrayList<>();
    users = accountServices.getAllMember();
    model.addAttribute("users", users);
    return "account-m";
  }

  @GetMapping("/update-staff")
  String displayOneStaff(@RequestParam(value = "userid") int userid, Model model, HttpSession session) {
    // System.out.println(userid);
    if (accountServices.checkSession(session)) {
      Users users = new Users();
      users.setUserid(userid);
      System.out.println(users.getPassword());
      users = accountServices.getUserInformation(users);
      model.addAttribute("users", users);
      return "update-staff-s";
    } else {
      return "redirect:/";
    }
  }

  @GetMapping("/update-member")
  String displayOneMember(@RequestParam(value = "userid") int userid, Model model, HttpSession session) {
    // System.out.println(userid);
    // if (accountServices.checkSession(session)) {
    Users users = new Users();
    users.setUserid(userid);
    System.out.println(users.getPassword());
    users = accountServices.getUserInformation(users);
    model.addAttribute("users", users);
    return "update-member-m";
    // } else {
    // return "redirect:/";
    // }
  }

  @PostMapping("/update-staff")
  String updateStaff(Model model, @ModelAttribute("updateStaff") Users users) {
    boolean status = accountServices.updateUsers(users);
    System.out.println("Update staff status : " + status);
    return "redirect:/account-s";
  }

  @PostMapping("/update-member")
  String updateMember(Model model, @ModelAttribute("updateMember") Users users) {
    boolean status = accountServices.updateUsers(users);
    System.out.println("Update member status : " + status);
    return "redirect:/account-m";
  }

  @PostMapping("/update-account-s")
  public String updateAccount(@ModelAttribute("profileAcc") Users users,
      @RequestParam(name = "userid") int id) {
    // initialize profile id
    users.setUserid(id);
    boolean status = accountServices.updateUsers(users);
    System.out.println("Update profile staff status : " + status);
    // Return page
    return "redirect:/profile-s";
  }

  @PostMapping("/update-account-m")
  public String updateAccountM(@ModelAttribute("profileAcc") Users users,
      @RequestParam(name = "userid") int id) {
    System.out.println("id:" + id);
    users.setUserid(id);
    boolean status = accountServices.updateUsers(users);
    System.out.println("Update profile member status : " + status);
    return "redirect:/profile-m";
  }

  @PostMapping("/delete-member")
  public String deleteAccountMember(@ModelAttribute("deleteMember") Users users) {
    System.out.println("member id : " +users.getUserid());
    boolean status = accountServices.deleteUsers(users);
    System.out.println("status delete : " +  status);
    return "redirect:/account-m";
  }

   @PostMapping("/delete-staff")
  public String deleteAccountStaff(@ModelAttribute("deleteStaff") Users users) {
    System.out.println("staff id : " + users.getUserid());
    boolean status = accountServices.deleteUsers(users);
    System.out.println("status delete : " +  status);
    return "redirect:/account-s";
  }

}
