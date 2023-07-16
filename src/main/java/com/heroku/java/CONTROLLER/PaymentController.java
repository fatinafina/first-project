package com.heroku.java.CONTROLLER;

import java.io.IOException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.heroku.java.MODEL.Packages;
import com.heroku.java.MODEL.Payment;
import com.heroku.java.SERVICES.PackageServices;
import com.heroku.java.SERVICES.PaymentServices;

import jakarta.servlet.http.HttpSession;

@Controller
public class PaymentController {

  private final DataSource dataSource;
  // private AccountServices accountServices;
  private PackageServices packageServices;
  private PaymentServices paymentServices;

  @Autowired
  public PaymentController(DataSource dataSource, PackageServices packageServices, PaymentServices paymentServices) {
    this.dataSource = dataSource;
    // this.accountServices = accountServices;
    this.packageServices = packageServices;
    this.paymentServices = paymentServices;
  }

  @GetMapping("/make-payment-m")
  String makePayment(HttpSession session, Model model) {
    Packages pg = new Packages();
    if (session.getAttribute("packageid") != null) {
      int pid = (int) session.getAttribute("packageid");

      pg.setPackageid(pid);
      pg = packageServices.getPackageById(pg);
      System.out.println(pg.getName());

      model.addAttribute("package", pg);
      return "make-payment-m";
    } else {
      return "redirect:/";
    }

  }

  @PostMapping("/addPayment")
  public String addPayment(@ModelAttribute("payment") Payment payment) {

    MultipartFile receiptimage = payment.getReceiptimage();
    try {

      payment.setReceiptimagebyte(receiptimage.getBytes());
      boolean status = paymentServices.inserPayment(payment);

      System.out.println("insert payment : " + status);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return "redirect:/payment-history-m";
  }
}
