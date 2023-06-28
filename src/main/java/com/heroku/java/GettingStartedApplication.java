package com.heroku.java;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Map;

/*import org.jscience.physics.amount.Amount;
import org.jscience.physics.model.RelativisticModel;
import javax.measure.unit.SI;
*/
@SpringBootApplication
@Controller
public class GettingStartedApplication {
  private final DataSource dataSource;

  @Autowired
  public GettingStartedApplication(DataSource dataSource) {
    this.dataSource = dataSource;
  }
  @GetMapping("/contact")
  String showContact(){
    return "contact";
  }

  @GetMapping("/fees")
  String showFees(){
    return "fees";
  }

  @GetMapping("/about-us")
  String showAboutUse(){
    return "about-us";
  }
   @GetMapping("/account-staff")
  String showStaff(){
    return "account-staff";
  }
   @GetMapping("/create-staff")
  String createStaff(){
    return "create-staff";
  }
  @GetMapping("/database")
  String database(Map<String, Object> model) {
    try (Connection connection = dataSource.getConnection()) {
      final var statement = connection.createStatement();
      statement.executeUpdate("CREATE TABLE IF NOT EXISTS ticks (tick timestamp)");
      statement.executeUpdate("INSERT INTO ticks VALUES (now())");

      final var resultSet = statement.executeQuery("SELECT tick FROM ticks");
      final var output = new ArrayList<>();
      while (resultSet.next()) {
        output.add("Read from DB: " + resultSet.getTimestamp("tick"));
      }

      model.put("records", output);
      return "database";

    } catch (Throwable t) {
      model.put("message", t.getMessage());
      return "error";
    }
  }

  @GetMapping("/error")
  String index() {
    return "error";
  }

  public static void main(String[] args) {
    SpringApplication.run(GettingStartedApplication.class, args);
  }
}
