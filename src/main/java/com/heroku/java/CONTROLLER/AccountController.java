package com.heroku.java.CONTROLLER;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import com.heroku.java.MODEL.Users;

import jakarta.servlet.http.HttpSession;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Map;

@Controller
public class AccountController {

  private final DataSource dataSource;

  @Autowired
  public AccountController(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @GetMapping("/profile")
  public String showAccount(Model model) {
    try {
      Connection connection = dataSource.getConnection();
      String sql = "SELECT * FROM khairatuser where userid=?";
      var statement = connection.prepareStatement(sql);
      int id = 1;
      statement.setInt(1, id);
      final var resultSet = statement.executeQuery();

      while (resultSet.next()) {
        int userid = resultSet.getInt("userid");
        String name = resultSet.getString("name");
        String ic = resultSet.getString("ic");
        String email = resultSet.getString("email");
        String password = resultSet.getString("password");
        String roles = resultSet.getString("role");
        System.out.println(">>>> " + name);
        model.addAttribute("account", new Users(userid, name, ic, email, password));
      }

      return "account_s";
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return "error";
    }

  }

  @PostMapping("/update-account")
  public String updateAccount(@ModelAttribute("profileAcc") Users users,
      @RequestParam(name = "userid") int id) {
    System.out.println("id:" + id);
    try (Connection connection = dataSource.getConnection()) {
      String sql = "UPDATE khairatuser SET name=?, ic=?, email=?, password=? WHERE userid=?";
      var pstatement = connection.prepareStatement(sql);
      System.out.println(users.getName());
      pstatement.setString(1, users.getName());
      pstatement.setString(2, users.getIc());
      pstatement.setString(3, users.getEmail());
      pstatement.setString(4, users.getPassword());
      pstatement.setInt(5, id);

      pstatement.executeUpdate();

      connection.close();

      return "redirect:/profile";

    } catch (Throwable t) {
      System.out.println("message" + t.getMessage());
      return "error";
    }
  }
}
