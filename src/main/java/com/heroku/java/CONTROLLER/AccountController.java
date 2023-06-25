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

    @GetMapping("/account")
    public String showAccount(Model model) {
        try{
            Connection connection = dataSource.getConnection();
            String sql = "SELECT * FROM khairatuser where userid=?";
            var statement = connection.prepareStatement(sql);
            int id = 1;
            statement.setInt(1, id);
            final var resultSet = statement.executeQuery();

            while(resultSet.next()){
                int userid = resultSet.getInt("userid");
                String name = resultSet.getString("name");
                String ic = resultSet.getString("ic");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");
                String role = resultSet.getString("role");
                System.out.println(">>>> " + name);
                model.addAttribute("account", new Users(userid,name,ic,email,password));
            }

            return "account_m";
        }catch(Exception e){
            System.out.println(e.getMessage());
            return "error";
        }
        
    }

    @PostMapping("/update-account")
    public String updateAccount(@ModelAttribute("user") Users users, @RequestAttribute(name="id") int id) {

        try (Connection connection = dataSource.getConnection()) {
        //    String sql = "SELECT * FROM khairatuser WHERE userid=?";
        //    var statement = connection.prepareStatement(sql);

            System.out.println(users.getEmail());
            // String name = users.getName();
            // String ic = users.getIc();
            // String email = users.getEmail();
            // String password = users.getPassword();

            // prepareStatement.setString(1, users.getName());
            // prepareStatement.setString(2, users.getIc());
            // prepareStatement.setString(3, users.getEmail());
            // prepareStatement.setString(4, users.getPassword());
            // prepareStatement.executeUpdate();
            connection.close();
            
            return "account_m";

        } catch (Throwable t) {
            System.out.println("message" + t.getMessage());
            return "error";
        }
    }
}

