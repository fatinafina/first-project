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
   @GetMapping("/home")
  String showHome(){
    return "dashboard";
  }
  @GetMapping("/contact-s")
  String showContactStaff(){
    return "contact-s";
  }

  @GetMapping("/about-us-s")
  String showAboutUseStaff(){
    return "about-us-s";
  }
  @GetMapping("/contact-m")
  String showContactMember(){
    return "contact-m";
  }

  @GetMapping("/about-us-m")
  String showAboutUsMember(){
    return "about-us-m";
  }
   @GetMapping("/account-s")
  String showStaff(){
    return "account-s";
  }
    @GetMapping("/account-m")
  String showMember(){
    return "account-m";
  }
   @GetMapping("/create-staff")
  String createStaff(){
    return "create-staff";
  }
   @GetMapping("/create-package-s")
  String createPackage(){
    return "create-package-s";
  }
    @GetMapping("/update-staff-s")
  String updateStaff(){
    return "update-staff-s";
  }
     @GetMapping("/package-s")
  String showPackage(){
    return "package-s";
  }
  @GetMapping("/home-m")
  String homeMember(){
    return "dashboard-m";
  }
   @GetMapping("/payment-history-m")
  String paymentHistory(){
    return "payment-history-m";
  }
    @GetMapping("/make-payment-m")
  String makePayment(){
    return "make-payment-m";
  }
   @GetMapping("/view-payment-s")
  String viewPayment(){
    return "view-payment-s";
  }
    @GetMapping("/receipt")
  String showReceipt(){
    return "receipt";
  }
  
  @GetMapping("/profile-m")
  String showMemberProfile(){
    return "profile-m";
  }
    @GetMapping("/update-package-s")
  String updatePackage(){
    return "update-package-s";
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
