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

import jakarta.servlet.http.HttpSession;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Map;

@Controller
public class LoginController {
    private final DataSource dataSource;

    @Autowired
    public LoginController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/")
    public String login(HttpSession session) {
        if (session.getAttribute("email") != null) {
            return "redirect:/home";
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

    @GetMapping("/home")
    public String home(HttpSession session) {
        // if (session.getAttribute("email") != null) {
        //     return "index";
        // } else {
        //     System.out.println("Session expired or invalid...");
        //     return "redirect:/";
        // }
        return "dashboard";
    }

    @PostMapping("/login-account")
    String homepage(HttpSession session, @ModelAttribute("login") Login login, Model model) {
        try (Connection connection = dataSource.getConnection()) {
            final var statement = connection.createStatement();

            final var resultSet = statement.executeQuery("SELECT userid, email, password, role FROM khairatuser");

            String returnPage = "";

            while (resultSet.next()) {
                int userid = resultSet.getInt("userid");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");
                String roles = resultSet.getString("role");

                System.out.println("Email : " + login.getEmail() + " || " + email);
                System.out.println("password : " + login.getPassword() + " || " + password);
                System.out.println("User id : " + userid);
                System.out.println("Role : " + roles);

                if (login.getEmail() != "" && login.getPassword() != "") {
                    if (email.equals(login.getEmail()) && login.getPassword().equals(password)) {

                        session.setAttribute("userid", userid);
                        session.setAttribute("email", login.getEmail());
                        session.setAttribute("role", roles);
                        // session.setMaxInactiveInterval(1440 * 60);

                        System.out.println("Successfully logged in...");
                        returnPage = "redirect:/home";
                        break;
                    } else {
                        System.out.println("Email and password cannot find the database...");
                        returnPage = "redirect:/error";
                    }

                } else {
                    System.out.println("Email and password is null...");
                    returnPage = "redirect:/error";

                }
            }
            connection.close();
            return returnPage;

        } catch (Throwable t) {
            System.out.println("message : " + t.getMessage());
            return "redirect:/";
        }

    }
}
