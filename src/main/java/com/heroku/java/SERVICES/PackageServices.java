package com.heroku.java.SERVICES;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.heroku.java.HELPER.SQLEx;
import com.heroku.java.MODEL.Users;
import com.heroku.java.MODEL.Packages;

import jakarta.servlet.http.HttpSession;

@Service
public class PackageServices {

  private final DataSource dataSource;
  private final SQLEx database;
  private final HttpSession session;

  @Autowired
  private BCryptPasswordEncoder passwordEncoder;

  @Autowired
  public PackageServices(DataSource dataSource, SQLEx database, HttpSession session) {
    this.dataSource = dataSource;
    this.database = database;
    this.session = session;
  }

  private final String INSERT_PACKAGEDETAILS = "INSERT INTO packagedetails (packageid,itemid) VALUES (?,?)";
  private final String INSERT_PACKAGE = "INSERT INTO packages (name,price) VALUES (?,?) RETURNING packageid;";
  private final String UPDATE_PACKAGE_MEMBER = "UPDATE member SET packageid=? WHERE userid=? ;";
  private final String SELECT_ALL_PACKAGES = "SELECT * FROM packages ORDER BY packageid;";
  private final String SELECT_PACKAGE_ITEM = "SELECT * FROM packagedetails ORDER BY itemid;";
  private final String SELECT_PACKAGES_ID = "SELECT * FROM packages WHERE packageid = ?;";
  private final String DELETE_ITEM_PACKAGEID = "DELETE FROM packagedetails WHERE packageid=?";
  private final String DELETE_PACKAGE_ID = "DELETE FROM packages WHERE packageid=?;";
  private final String UPDATE_PACKAGE_ID = "UPDATE packages SET name=?, price=? WHERE packageid=?;";

  public int insertPackage(Packages packages) {
    int id = 0;

    try (Connection connection = dataSource.getConnection()) {
      PreparedStatement statement = connection.prepareStatement(INSERT_PACKAGE);
      statement.setString(1, packages.getName());
      statement.setDouble(2, packages.getPrice());
      ResultSet parentResultSet = statement.executeQuery();
      System.out.println("Package inserted...");
      // System.out.println("Insert package...");

      if (parentResultSet.next()) {
        int parentId = parentResultSet.getInt("packageid");
        id = parentId;
        System.out.println("id : " + id);
      }

    } catch (SQLException ex) {
      // return status;
      database.printSQLException(ex);
    }
    return id;
  }

  public void insertPackageDetails(int package_id, String data) {
    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(INSERT_PACKAGEDETAILS)) {
      statement.setInt(1, package_id);
      statement.setInt(2, Integer.parseInt(data));
      System.out.println("id : " + package_id);
      System.out.println("data : " + Integer.parseInt(data));
      statement.executeUpdate();
    } catch (SQLException ex) {
      // return status;
      database.printSQLException(ex);
    }
  }

  public String insertPackageMember(int userid, int packageid) {
    String url = "";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(UPDATE_PACKAGE_MEMBER)) {
      statement.setInt(1, packageid);
      statement.setInt(2, userid);

      int rowUpdate = statement.executeUpdate();
      boolean status = rowUpdate > 0;
      if(status){
        session.setAttribute("packageid", packageid);
        url = "redirect:/home-m?join=success";
        
      }else{
         url = "redirect:/home-m?join=failed";
      }
    } catch (SQLException ex) {
      // return status;
      database.printSQLException(ex);
    }
    return url;
  }

  public ArrayList<Packages> getAllPackages() {
    ArrayList<Packages> packages = new ArrayList<>();

    try (Connection connection = dataSource.getConnection()) {
      final var prepareStatement = connection.prepareStatement(SELECT_ALL_PACKAGES);
      ResultSet rs = prepareStatement.executeQuery();

      while (rs.next()) {
        int packageid = rs.getInt("packageid");
        String name = rs.getString("name");
        double price = rs.getDouble("price");

        packages.add(new Packages(packageid, name, price));
      }
      connection.close();

    } catch (SQLException ex) {
      // return status;
      database.printSQLException(ex);
    }

    return packages;
  }

  public ArrayList<Packages> getAllItem() {
    ArrayList<Packages> packages = new ArrayList<>();

    try (Connection connection = dataSource.getConnection()) {
      final var prepareStatement = connection.prepareStatement(SELECT_PACKAGE_ITEM);
      ResultSet rs = prepareStatement.executeQuery();

      while (rs.next()) {
        int packageid = rs.getInt("packageid");
        int itemid = rs.getInt("itemid");

        packages.add(new Packages(packageid, itemid));
      }
      connection.close();

    } catch (SQLException ex) {
      // return status;
      database.printSQLException(ex);
    }

    return packages;
  }

  public Packages getPackageById(Packages packages) {
    Packages pg = new Packages();

    try (Connection connection = dataSource.getConnection()) {
      final var prepareStatement = connection.prepareStatement(SELECT_PACKAGES_ID);
      prepareStatement.setInt(1, packages.getPackageid());
      System.out.println(packages.getPackageid());
      ResultSet rs = prepareStatement.executeQuery();

      while (rs.next()) {
        int packageid = rs.getInt("packageid");
        String name = rs.getString("name");
        double price = rs.getDouble("price");

        pg = new Packages(packageid, name, price);
      }
      connection.close();

    } catch (SQLException ex) {
      // return status;
      database.printSQLException(ex);
    }

    return pg;
  }

  public List<Integer> getItemIdsByPackageId(int packageId) {

    String sql = "SELECT itemid FROM packagedetails WHERE packageid = ?";

    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setInt(1, packageId);

      try (ResultSet resultSet = statement.executeQuery()) {
        List<Integer> itemIds = new ArrayList<>();
        while (resultSet.next()) {
          int itemId = resultSet.getInt("itemid");
          itemIds.add(itemId);
        }
        return itemIds;
      }
    } catch (SQLException e) {
      // Handle any exceptions
      return Collections.emptyList();
    }
  }

  public boolean deleteAllItemByPackageId(int packageId) {
    boolean status = false;

    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(DELETE_ITEM_PACKAGEID)) {
      statement.setInt(1, packageId);

      int rowsAffected = statement.executeUpdate();
      status = rowsAffected > 0;

    } catch (SQLException ex) {
      // return status;
      database.printSQLException(ex);
    }

    return status;
  }

  public boolean updatePackages(Packages packages) {
    boolean status = false;

    try (Connection connection = dataSource.getConnection()) {

      final var prepareStatement = connection.prepareStatement(UPDATE_PACKAGE_ID);

      prepareStatement.setString(1, packages.getName());
      prepareStatement.setDouble(2, packages.getPrice());
      prepareStatement.setInt(3, packages.getPackageid());
      prepareStatement.executeUpdate();
      status = true;

      connection.close();
    } catch (SQLException ex) {
      // return status;
      database.printSQLException(ex);
    }

    return status;
  }

  public boolean deletePackages(Packages packages) {
    boolean status = false;

    try (Connection connection = dataSource.getConnection()) {

      final var prepareStatement = connection.prepareStatement(DELETE_PACKAGE_ID);
      prepareStatement.setInt(1, packages.getPackageid());

      prepareStatement.executeQuery();
      status = true;
      connection.close();

    } catch (SQLException ex) {
      // return status;
      database.printSQLException(ex);
      status = false;
    }

    return status;
  }

}
