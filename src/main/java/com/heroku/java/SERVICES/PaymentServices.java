package com.heroku.java.SERVICES;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Base64;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.heroku.java.HELPER.SQLEx;
import com.heroku.java.MODEL.Payment;
import com.heroku.java.MODEL.Users;

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

  private final String SELECT_ALL_PAYMENT = "SELECT * FROM payment p JOIN khairatuser k ON p.userid = k.userid ORDER BY p.paymentid;";
  private final String SELECT_PAYMENT_ID = "SELECT * FROM payment WHERE userid=? ORDER BY paymentid DESC;";
  private final String UPDATE_PAYMENT_STATUS = "UPDATE payment SET statuspayment=? WHERE paymentid=?;";

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

  public ArrayList<Payment> getAllPayment() {
    ArrayList<Payment> payment = new ArrayList<>();

    try (Connection connection = dataSource.getConnection()) {
      final var prepareStatement = connection.prepareStatement(SELECT_ALL_PAYMENT);
      ResultSet rs = prepareStatement.executeQuery();

      while (rs.next()) {
        int paymentid = rs.getInt("paymentid");
        int userid = rs.getInt("userid");
        String paymentmethod = rs.getString("paymentmethod");
        Date date = rs.getDate("date");
        Time paymenttime = rs.getTime("paymenttime");
        String statuspayment = rs.getString("statuspayment");
        String memberName = rs.getString("name");
        String ic = rs.getString("ic");
        byte[] receiptImageByte = rs.getBytes("receipt");
        String base64Image = Base64.getEncoder().encodeToString(receiptImageByte);
        String imageSrc = "data:image/jpeg;base64," + base64Image;
        // System.out.println("paymentstatus : " + paymentid + " : " + statuspayment);
        payment.add(new Payment(paymentid, paymentmethod, date, paymenttime, statuspayment, memberName, ic, imageSrc));
      }
      connection.close();

    } catch (SQLException ex) {
      // return status;
      database.printSQLException(ex);
    }

    return payment;
  }

  public ArrayList<Payment> getAllPaymentById(int usrid) {
    ArrayList<Payment> payment = new ArrayList<>();

    try (Connection connection = dataSource.getConnection()) {
      final var prepareStatement = connection.prepareStatement(SELECT_PAYMENT_ID);
      prepareStatement.setInt(1, usrid);
      ResultSet rs = prepareStatement.executeQuery();

      while (rs.next()) {
        int paymentid = rs.getInt("paymentid");
        int userid = rs.getInt("userid");
        String paymentmethod = rs.getString("paymentmethod");
        Date date = rs.getDate("date");
        Time paymenttime = rs.getTime("paymenttime");
        String statuspayment = rs.getString("statuspayment");
        // String memberName = rs.getString("name");
        // String ic = rs.getString("ic");
        byte[] receiptImageByte = rs.getBytes("receipt");
        String base64Image = Base64.getEncoder().encodeToString(receiptImageByte);
        String imageSrc = "data:image/jpeg;base64," + base64Image;
        // System.out.println("paymentstatus : " + paymentid + " : " + statuspayment);
        payment.add(new Payment(paymentid, paymentmethod, date, paymenttime, statuspayment));
      }
      connection.close();

    } catch (SQLException ex) {
      // return status;
      database.printSQLException(ex);
    }

    return payment;
  }

  public boolean updatePayment(Payment payment){
    boolean status = false;
    try (Connection connection = dataSource.getConnection()) {
      final var prepareStatement = connection.prepareStatement(UPDATE_PAYMENT_STATUS);
      System.out.println("======update payment=====");
      System.out.println("status payment : " + payment.getStatuspayment());
      System.out.println("payment id : " +payment.getPaymentid());
      prepareStatement.setString(1, payment.getStatuspayment());
      prepareStatement.setInt(2, payment.getPaymentid());

      int rowUpdate = prepareStatement.executeUpdate();
      status = rowUpdate > 0;
      
      connection.close();

    } catch (SQLException ex) {
      // return status;
      database.printSQLException(ex);
    }
    return status;
  }
}
