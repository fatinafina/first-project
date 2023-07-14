package com.heroku.java.MODEL;

public class Packages {
  private int packageid;
  private int itemid;
  private String name;
  private double price;


   public Packages() {
    // default constructor
  }

  
  
  public Packages(String name, double price) {
    this.name = name;
    this.price = price;
  }

  public Packages(int packageid, String name, double price) {
    this.packageid = packageid;
    this.name = name;
    this.price = price;
  }

  public Packages(int packageid, int itemid) {
    this.packageid = packageid;
    this.itemid = itemid;
  }

  public int getPackageid() {
    return packageid;
  }
  public void setPackageid(int packageid) {
    this.packageid = packageid;
  }

  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public double getPrice() {
    return price;
  }
  public void setPrice(double price) {
    this.price = price;
  }



  public int getItemid() {
    return itemid;
  }



  public void setItemid(int itemid) {
    this.itemid = itemid;
  }

  
}
