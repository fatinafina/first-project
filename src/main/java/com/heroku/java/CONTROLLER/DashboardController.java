package com.heroku.java.CONTROLLER;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.heroku.java.MODEL.Packages;
import com.heroku.java.MODEL.Users;
import com.heroku.java.SERVICES.AccountServices;
import com.heroku.java.SERVICES.PackageServices;

import jakarta.servlet.http.HttpSession;

@Controller
public class DashboardController {

  private final DataSource dataSource;
  private AccountServices accountServices;
  private PackageServices packageServices;

  @Autowired
  public DashboardController(DataSource dataSource, AccountServices accountServices, PackageServices packageServices) {
    this.dataSource = dataSource;
    this.accountServices = accountServices;
    this.packageServices = packageServices;
  }

  @GetMapping("/home-m")
  String homeMember(HttpSession session, Model model) {
    // if (accountServices.checkSession(session)) {
    // return "dashboard-m";
    // } else {
    // return "redirect:/";
    // }
    Users users = new Users();
    Packages pg = new Packages();
    pg.setPackageid((int) session.getAttribute("packageid"));
    ArrayList<Packages> packages = packageServices.getAllPackages();
    ArrayList<Packages> itemPackages = packageServices.getAllItem();

    model.addAttribute("packages", packages);
    model.addAttribute("items", itemPackages);
    model.addAttribute("pg", pg);

    return "dashboard-m";
  }

  @PostMapping("/insert-package")
  String selectPackage(HttpSession session, Model model, @RequestParam(value = "pid") int pid) {
    // if (accountServices.checkSession(session)) {
    // return "dashboard-m";
    // } else {
    // return "redirect:/";
    // }
    int userid = (int) session.getAttribute("userid");

    String url = packageServices.insertPackageMember(userid, pid);
    // ArrayList<Packages> packages = packageServices.getAllPackages();
    // ArrayList<Packages> itemPackages = packageServices.getAllItem();

    // model.addAttribute("packages", packages);
    // model.addAttribute("items", itemPackages);

    return url;
  }

  @GetMapping("/home-s")
  public String homeStaff(HttpSession session, Model model) {
    Users users = new Users();
    if (accountServices.checkSession(session)) {
      int staffCount = accountServices.getTotalStaff();
      int memberCount = accountServices.getTotalMember();
      int countRegister = accountServices.getTotalRegister();
      int countUnregister = accountServices.getTotalUnregister();
      
      users.setCountRegister(countRegister);
      users.setCountUnregister(countUnregister);
      users.setCountStaff(staffCount);
      users.setCountMember(memberCount);

      model.addAttribute("users", users);

      return "dashboard-s";
    } else {
      return "redirect:/";
    }
  }

}
