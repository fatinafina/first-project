package com.heroku.java.SERVICES;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.heroku.java.HELPER.SQLEx;
import com.heroku.java.MODEL.Users;

import jakarta.servlet.http.HttpSession;

@Service
public class AccountServices {

  private final DataSource dataSource;
  private final SQLEx database;
  private final HttpSession session;

  @Autowired
  private BCryptPasswordEncoder passwordEncoder;

  @Autowired
  public AccountServices(DataSource dataSource, SQLEx database, HttpSession session) {
    this.dataSource = dataSource;
    this.database = database;
    this.session = session;
  }

  private final String LOGIN_SQL = "SELECT u.userid,name, email, password, CASE WHEN s.userid is null THEN 0  WHEN s.userid IS NOT NULL THEN s.userid END AS role, m.packageid from khairatuser u left outer join staff s on (u.userid = s.userid) left outer join member m on (u.userid = m.userid) where email=?;";
  private final String SELECT_EMAIL = "SELECT email FROM khairatuser WHERE email=?;";
  private final String INSERT_USER_STAFF = "INSERT INTO khairatuser (name, ic, email,password) VALUES (?,?,?,?) RETURNING userid AS userid;";
  private final String INSERT_STAFF = "INSERT INTO staff (userid, managerid) VALUES (?,?);";
  private final String SELECT_ALL_STAFF = "SELECT khairatuser.userid, name, ic, email, password FROM khairatuser JOIN staff ON (khairatuser.userid = staff.userid) ORDER BY userid;";
  private final String SELECT_ALL_MEMBER = "SELECT khairatuser.userid, name, ic, email, password FROM khairatuser JOIN member ON (khairatuser.userid = member.userid) ORDER BY khairatuser.userid;";
  private final String SELECT_USER_ID = "SELECT * FROM khairatuser WHERE userid=?;";
  private final String UPDATE_STAFF_ID = "UPDATE khairatuser SET name=?, ic=?, email=?, password=? WHERE userid=?;";
  private final String UPDATE_STAFF_ID_noPass = "UPDATE khairatuser SET name=?, ic=?, email=? WHERE userid=?;";
  private final String DELETE_USER_ID = "DELETE FROM khairatuser WHERE userid=?";

  public boolean checkSession(HttpSession session) {
    boolean status = false;
    if (session.getAttribute("userid") != null) {
      status = true;
    } else {
      System.out.println("Session expired or invalid...");
      status = false;
    }
    return status;
  }

  public boolean checkEmail(String email) {
    boolean status = false; // Default status value
    try (Connection connection = dataSource.getConnection()) {
      // PrepareStatement
      var prepareStatement = connection.prepareStatement(SELECT_EMAIL);

      // set Parameter value
      prepareStatement.setString(1, email);
      final var resultSet = prepareStatement.executeQuery(SELECT_EMAIL);

      status = resultSet.next(); // Set value status if found the email then return true;

      connection.close();
    } catch (Throwable t) {
      System.out.println("message : " + t.getMessage());
      status = false;
    }
    return status;
  }

  public String loginAccount(Users users) {

    System.out.println("user.getmail" + users.getEmail());

    try (Connection connection = dataSource.getConnection()) {
      final var prepareStatement = connection.prepareStatement(LOGIN_SQL);
      prepareStatement.setString(1, users.getEmail());
      ResultSet rs = prepareStatement.executeQuery();
      String returnAcc = "";

      while (rs.next()) {
        int userid = rs.getInt("userid");
        String email = rs.getString("email");
        String name = rs.getString("name");
        String password = rs.getString("password");
        int role = rs.getInt("role");
        int packageid = rs.getInt("packageid");
        System.out.println("package id : " + packageid);
        System.out.println(userid + " " + email + " " + password + " " + role);

        if (email.equals(users.getEmail()) && passwordEncoder.matches(users.getPassword(), password)) {
          session.setAttribute("userid", userid);
          session.setAttribute("name", name);
          session.setAttribute("email", email);

          if(packageid != 0){
            session.setAttribute("packageid", packageid);
          }else{
            session.setAttribute("packageid", 0);
          }

          if (role == 0) {
            returnAcc = "member";
          } else {
            returnAcc = "staff";
          }
        } else {
          returnAcc = "none";
        }

      }
      return returnAcc;

    } catch (SQLException ex) {
      // System.out.println("message" + t.getMessage());
      database.printSQLException(ex);
      return "error";
    }
  }

  public boolean createAccountMember(Users users) {
    boolean status = false;
    try (Connection connection = dataSource.getConnection()) {
      var prepareStatement = connection
          .prepareStatement("INSERT INTO khairatuser (name, ic, email, password) VALUES (?,?,?,?) RETURNING userid");

      prepareStatement.setString(1, users.getName());
      prepareStatement.setString(2, users.getIc());
      prepareStatement.setString(3, users.getEmail());
      prepareStatement.setString(4, passwordEncoder.encode(users.getPassword()));

      ResultSet parentResultSet = prepareStatement.executeQuery();
      if (parentResultSet.next()) {
        long parentId = parentResultSet.getLong("userid");

        String childInsertSql = "INSERT INTO member (userid) VALUES (?)";
        PreparedStatement childInsertStatement = connection.prepareStatement(childInsertSql);
        childInsertStatement.setLong(1, parentId);
        childInsertStatement.executeUpdate();

        childInsertStatement.close();
      }

      parentResultSet.close();

      connection.close();

      status = true;

    } catch (Throwable t) {
      System.out.println("message" + t.getMessage());
      status = false;
    }
    return status;
  }

  public boolean createAccountStaff(Users users) {
    boolean status = false;

    try (Connection connection = dataSource.getConnection()) {
      // if true then can't register cause email already exist
      if (checkEmail(users.getEmail()) != true) {

        final var prepareStatement = connection.prepareStatement(INSERT_USER_STAFF);
        // debug
        System.out.println("Services name : " + users.getName());
        // initiliaze all parameter (Store into database)
        prepareStatement.setString(1, users.getName());
        prepareStatement.setString(2, users.getIc());
        prepareStatement.setString(3, users.getEmail());
        prepareStatement.setString(4, passwordEncoder.encode(users.getPassword()));
        prepareStatement.execute();
        // get and set Return userid from sql
        ResultSet user_id = prepareStatement.getResultSet();
        int userid = 0;
        if (user_id.next()) {
          userid = user_id.getInt(1);
        }
        System.out.println("userid = " + userid);

        final var prepareStatementStaff = connection
            .prepareStatement(INSERT_STAFF);
        // Set table child userid for staff
        prepareStatementStaff.setInt(1, userid);
        prepareStatementStaff.setInt(2, users.getUserid());
        // prepareStatementStaff.setInt(2, null);
        prepareStatementStaff.executeUpdate();
        status = true;
        connection.close();
      } else {
        status = false;
        System.out.println(">>> Email already exist");
      }
      return status;
    } catch (SQLException ex) {
      // System.out.println("message" + t.getMessage());
      status = false;
      database.printSQLException(ex);
    }
    return status;
  }

  public ArrayList<Users> getAllStaff() {
    ArrayList<Users> users = new ArrayList<>();

    try (Connection connection = dataSource.getConnection()) {
      final var prepareStatement = connection.prepareStatement(SELECT_ALL_STAFF);
      ResultSet rs = prepareStatement.executeQuery();

      while (rs.next()) {
        int userid = rs.getInt("userid");
        String name = rs.getString("name");
        String ic = rs.getString("ic");
        String email = rs.getString("email");

        users.add(new Users(userid, name, ic, email));
      }
      connection.close();

    } catch (SQLException ex) {
      // return status;
      database.printSQLException(ex);
    }

    return users;
  }

  public ArrayList<Users> getAllMember() {
    ArrayList<Users> users = new ArrayList<>();

    try (Connection connection = dataSource.getConnection()) {
      final var prepareStatement = connection.prepareStatement(SELECT_ALL_MEMBER);
      ResultSet rs = prepareStatement.executeQuery();

      while (rs.next()) {
        int userid = rs.getInt("userid");
        String name = rs.getString("name");
        String ic = rs.getString("ic");
        String email = rs.getString("email");

        users.add(new Users(userid, name, ic, email));
      }
      connection.close();

    } catch (SQLException ex) {
      // return status;
      database.printSQLException(ex);
    }

    return users;
  }

  public Users getUserInformation(Users users) {
    Users usr = null;

    try (Connection connection = dataSource.getConnection()) {

      final var prepareStatement = connection.prepareStatement(SELECT_USER_ID);
      prepareStatement.setInt(1, users.getUserid());

      ResultSet rs = prepareStatement.executeQuery();

      while (rs.next()) {
        int userid = rs.getInt("userid");
        String name = rs.getString("name");
        String ic = rs.getString("ic");
        String email = rs.getString("email");
        String password = "password"; // dummy password data for security

        usr = new Users(userid, name, ic, email, password);
      }
      connection.close();
    } catch (SQLException ex) {
      // return status;
      database.printSQLException(ex);
    }

    return usr;
  }

  public boolean updateUsers(Users users) {
    boolean status = false;

    try (Connection connection = dataSource.getConnection()) {

      final var prepareStatement = connection.prepareStatement(UPDATE_STAFF_ID);
      final var prepareStatement2 = connection.prepareStatement(UPDATE_STAFF_ID_noPass);

      if (users.getPassword().equals("password")) {

        prepareStatement2.setString(1, users.getName());
        prepareStatement2.setString(2, users.getIc());
        prepareStatement2.setString(3, users.getEmail());
        prepareStatement2.setInt(4, users.getUserid());
        prepareStatement2.executeUpdate();
        System.out.println("Update with without password done...");
        status = true;
      } else {
        prepareStatement.setString(1, users.getName());
        prepareStatement.setString(2, users.getIc());
        prepareStatement.setString(3, users.getEmail());
        prepareStatement.setString(4, passwordEncoder.encode(users.getPassword()));
        prepareStatement.setInt(5, users.getUserid());
        prepareStatement.executeUpdate();
        System.out.println("Update with password done.. ");
        status = true;
      }
      connection.close();
    } catch (SQLException ex) {
      // return status;
      database.printSQLException(ex);
    }

    return status;
  }

  public boolean deleteUsers(Users users) {
    boolean status = false;

    try (Connection connection = dataSource.getConnection()) {

      if (users.getUserid() != 1) {
        final var prepareStatement = connection.prepareStatement(DELETE_USER_ID);
        prepareStatement.setInt(1, users.getUserid());

        prepareStatement.executeQuery();
        status = true;
        connection.close();
      }

    } catch (SQLException ex) {
      // return status;
      database.printSQLException(ex);
      status = true;
    }

    return status;
  }
}
