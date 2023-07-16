package com.heroku.java.SERVICES;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.heroku.java.HELPER.SQLEx;
import com.heroku.java.MODEL.Payment;

import jakarta.servlet.http.HttpSession;

@Service
public class PaymentServices {

  private final DataSource dataSource;
  private final SQLEx database;
  private final HttpSession session;

  @Autowired
  private BCryptPasswordEncoder passwordEncoder;

  @Autowired
  public PaymentServices(DataSource dataSource, SQLEx database, HttpSession session) {
    this.dataSource = dataSource;
    this.database = database;
    this.session = session;
  }

  public boolean inserPayment(Payment payment) {
    boolean status = false;
    try {
      Connection connection = dataSource.getConnection();
      String sql = "INSERT INTO payment (paymentmethod, date, paymenttime, receipt, userid) VALUES (?,?,?,?,?)";
      PreparedStatement statement = connection.prepareStatement(sql);
      System.out.println("Time : " + payment.getPaymenttime());

      statement.setString(1, payment.getPaymentmethod());
      statement.setDate(2, payment.getDate());
      statement.setTime(3, payment.getPaymenttime());
      statement.setBytes(4, payment.getReceiptimagebyte());
      statement.setInt(5, (int) session.getAttribute("userid"));

      statement.executeUpdate();
      status = true;
      connection.close();

      // return "redirect:/viewSedan";
    } catch (SQLException sqe) {
      System.out.println("Error Code = " + sqe.getErrorCode());
      System.out.println("SQL state = " + sqe.getSQLState());
      System.out.println("Message = " + sqe.getMessage());
      System.out.println("printTrace /n");
      sqe.printStackTrace();

      // return "redirect:/";
    }

    return status;
  }
}
