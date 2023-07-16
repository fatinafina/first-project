package com.heroku.java.MODEL;

import java.sql.Date;
import java.sql.Time;

import org.springframework.web.multipart.MultipartFile;

public class Payment {
  private int paymentid;
  private String paymentmethod;
  private Date date;
  private Time paymenttime;
  private String statuspayment;
  private int userid;

  public MultipartFile receiptimage;
  private String imageSrc;
  private byte[] receiptimagebyte;

  public Payment(int paymentid, String paymentmethod, Date date, Time paymenttime, String statuspayment, int userid,
      MultipartFile receiptimage, String imageSrc, byte[] receiptimagebyte) {
    this.paymentid = paymentid;
    this.paymentmethod = paymentmethod;
    this.date = date;
    this.paymenttime = paymenttime;
    this.statuspayment = statuspayment;
    this.userid = userid;
    this.receiptimage = receiptimage;
    this.imageSrc = imageSrc;
    this.receiptimagebyte = receiptimagebyte;
  }

  public Payment(int paymentid, String paymentmethod, Date date, Time paymenttime, int userid,
      MultipartFile receiptimage, String imageSrc, byte[] receiptimagebyte) {
    this.paymentid = paymentid;
    this.paymentmethod = paymentmethod;
    this.date = date;
    this.paymenttime = paymenttime;
    this.userid = userid;
    this.receiptimage = receiptimage;
    this.imageSrc = imageSrc;
    this.receiptimagebyte = receiptimagebyte;
  }


  public Payment() {
    // default constructor
  }


  public int getPaymentid() {
    return paymentid;
  }


  public void setPaymentid(int paymentid) {
    this.paymentid = paymentid;
  }


  public String getPaymentmethod() {
    return paymentmethod;
  }


  public void setPaymentmethod(String paymentmethod) {
    this.paymentmethod = paymentmethod;
  }


  public Date getDate() {
    return date;
  }


  public void setDate(Date date) {
    this.date = date;
  }


  public Time getPaymenttime() {
    return paymenttime;
  }


  public void setPaymenttime(Time paymenttime) {
    this.paymenttime = paymenttime;
  }


  public String getStatuspayment() {
    return statuspayment;
  }


  public void setStatuspayment(String statuspayment) {
    this.statuspayment = statuspayment;
  }


  public int getUserid() {
    return userid;
  }


  public void setUserid(int userid) {
    this.userid = userid;
  }


  public MultipartFile getReceiptimage() {
    return receiptimage;
  }


  public void setReceiptimage(MultipartFile receiptimage) {
    this.receiptimage = receiptimage;
  }


  public String getImageSrc() {
    return imageSrc;
  }


  public void setImageSrc(String imageSrc) {
    this.imageSrc = imageSrc;
  }


  public byte[] getReceiptimagebyte() {
    return receiptimagebyte;
  }


  public void setReceiptimagebyte(byte[] receiptimagebyte) {
    this.receiptimagebyte = receiptimagebyte;
  }

  
}
