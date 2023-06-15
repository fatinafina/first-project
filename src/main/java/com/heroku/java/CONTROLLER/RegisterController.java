package com.heroku.java.CONTROLLER;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.heroku.java.MODEL.Users;

import jakarta.servlet.http.HttpSession;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Map;

@Controller
public class RegisterController {

    private final DataSource dataSource;

    @Autowired
    public RegisterController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/signup")
    public String signUp() {
        return "register";
    }

    @PostMapping("/create-account")
    public String createAccount(@ModelAttribute("user") Users users) {

        try (Connection connection = dataSource.getConnection()) {
            var prepareStatement = connection.prepareStatement("INSERT INTO khairatuser (name, ic, email, password) VALUES (?,?,?,?)");

            // String name = users.getName();
            // String ic = users.getIc();
            // String email = users.getEmail();
            // String password = users.getPassword();

            prepareStatement.setString(1, users.getName());
            prepareStatement.setString(2, users.getIc());
            prepareStatement.setString(3, users.getEmail());
            prepareStatement.setString(4, users.getPassword());
            prepareStatement.executeUpdate();
            connection.close();
            
            return "redirect:/";

        } catch (Throwable t) {
            System.out.println("message" + t.getMessage());
            return "error";
        }
    }
}
