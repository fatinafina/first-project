package com.heroku.java.CONTROLLER;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import com.heroku.java.MODEL.Packages;
import com.heroku.java.MODEL.Users;
import com.heroku.java.SERVICES.AccountServices;
import com.heroku.java.SERVICES.PackageServices;

import jakarta.servlet.http.HttpSession;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class PackageController {

  private final DataSource dataSource;
  // private AccountServices accountServices;
  private PackageServices packageServices;

  @Autowired
  public PackageController(DataSource dataSource, PackageServices packageServices) {
    this.dataSource = dataSource;
    // this.accountServices = accountServices;
    this.packageServices = packageServices;
  }

  // public void init() {
  // accountServices = new AccountServices(dataSource);
  // }

  // @GetMapping("/profile-s")
  // public String showStaffProfile(Model model, Users users, HttpSession session)
  // {
  // if (accountServices.checkSession(session)) {
  // users.setUserid((int) session.getAttribute("userid"));
  // model.addAttribute("account", accountServices.getUserInformation(users));

  // return "profile-s";
  // } else {
  // return "redirect:/";
  // }
  // }

  @PostMapping("/create-package")
  public String createPackage(@ModelAttribute("Package") Packages packages,
      @RequestParam("itemCheckbox") List<String> checkboxValue) {

    System.out.println("value = " + checkboxValue);
    int pack_id = packageServices.insertPackage(packages);

    for (String value : checkboxValue) {
      packageServices.insertPackageDetails(pack_id, value);
      System.out.println(value);
    }
    return "redirect:/package-s";
  }

  @GetMapping("/create-package-s")
  String createPackage() {
    return "create-package-s";
  }

  @GetMapping("/package-s")
  String showPackage(Model model) {
    List<Packages> packages = packageServices.getAllPackages();
    List<Packages> itemPackages = packageServices.getAllItem();

    model.addAttribute("packages", packages);
    model.addAttribute("items", itemPackages);
    return "package-s";
  }

  @GetMapping("/update-package-s")
  String getPackageDetails(Model model, @RequestParam(name = "pid") int packageId) {
    Packages packages = new Packages();
    packages.setPackageid(packageId);

    packages = packageServices.getPackageById(packages);
    List<Integer> itemIds = packageServices.getItemIdsByPackageId(packages.getPackageid());

    model.addAttribute("items", itemIds);
    System.out.println(itemIds);
    model.addAttribute("packages", packages);
    System.out.println(packages.getPackageid());
    return "update-package-s";
  }

  @PostMapping("/update-package-s")
  String updatePackage(Model model, @ModelAttribute("updatePackage") Packages packages,
      @RequestParam(name = "pid") int packageId,
      @RequestParam(name = "itemCheckbox", required = false) List<String> selectedItems) {

    packages.setPackageid(packageId);

    boolean statusDelete = false;

    if (selectedItems == null || selectedItems.isEmpty()) {
      // No checkboxes selected, return an error message or perform desired action
      model.addAttribute("error", "Please select at least one item.");
      return "redirect:/update-package-s?pid=" + packageId + "&error"; // Replace with your error page template or error
                                                                       // handling logic
    }

    statusDelete = packageServices.deleteAllItemByPackageId(packageId);
    boolean statusUpdate = packageServices.updatePackages(packages);
    if (statusDelete) {
      // packageServices.insertPackageDetails(packages);
      for (String value : selectedItems) {
        packageServices.insertPackageDetails(packageId, value);
      }
    }

    return "redirect:/package-s";
  }

  @PostMapping("/delete-package")
  String deletePackage(Model model, @ModelAttribute("package") Packages packages) {

    System.out.println("package id : " + packages.getPackageid());
    boolean status = packageServices.deletePackages(packages);
    System.out.println("status delete package : " + status);
    return "redirect:/package-s";
  }

}
