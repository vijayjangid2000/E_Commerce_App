package com.e.e_commerce_app.SimBuy;

import android.text.Spannable;

import java.util.Objects;

public class ProductSimCardModel {

    private String id, description, mobile_number;
    private String product_name, customer_priceStr, retail_priceStr, dealer_commissionStr, statusStr;
    private double customer_price, retail_price, dealer_commission;
    private boolean isInCart, status;
    int priority = 1; // set this according to list size

    // For highLighting Search Results
    private boolean isSearchResult;
    private Spannable spannable;

    public ProductSimCardModel(String productId, String productName,
                               double customerPrice, double retailPrice, double dealerCommission,
                               String status, String description) {

        this.id = productId;
        this.product_name = productName;
        mobile_number = productName.replaceAll("\\s", "");

        this.customer_price = customerPrice;
        this.retail_price = retailPrice;
        this.dealer_commission = dealerCommission;

        customer_priceStr = "₹ " + customerPrice;
        retail_priceStr = "Retail Price: " + "₹ " + retailPrice;
        dealer_commissionStr = "Dealer Commission: " + "₹ " + dealer_commission;

        this.statusStr = status;
        this.status = statusStr.equalsIgnoreCase("active");

        this.description = description;
        isInCart = false;
        isSearchResult = false;
    }


    // GETTER AND SETTER

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }


    public String getCustomer_priceStr() {
        return customer_priceStr;
    }

    public void setCustomer_priceStr(String customer_priceStr) {
        this.customer_priceStr = customer_priceStr;
    }
    public String getRetail_priceStr() {
        return retail_priceStr;
    }

    public void setRetail_priceStr(String retail_priceStr) {
        this.retail_priceStr = retail_priceStr;
    }

    public double getCustomer_price() {
        return customer_price;
    }

    public void setCustomer_price(double customer_price) {
        this.customer_price = customer_price;
    }

    public double getRetail_price() {
        return retail_price;
    }

    public void setRetail_price(double retail_price) {
        this.retail_price = retail_price;
    }

    public String getMobile_number() {
        return mobile_number;
    }

    public void setMobile_number(String mobile_number) {
        this.mobile_number = mobile_number;
    }

    public boolean isInCart() {
        return isInCart;
    }

    public void setInCart(boolean inCart) {
        isInCart = inCart;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDealer_commissionStr() {
        return dealer_commissionStr;
    }

    public void setDealer_commissionStr(String dealer_commissionStr) {
        this.dealer_commissionStr = dealer_commissionStr;
    }

    public double getDealer_commission() {
        return dealer_commission;
    }

    public void setDealer_commission(double dealer_commission) {
        this.dealer_commission = dealer_commission;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean isSearchResult() {
        return isSearchResult;
    }

    public void setSearchResult(boolean searchResult) {
        isSearchResult = searchResult;
    }

    public Spannable getSpannable() {
        return spannable;
    }

    public void setSpannable(Spannable spannable) {
        this.spannable = spannable;
    }

    public String getStatusStr() {
        return statusStr;
    }

    public void setStatusStr(String statusStr) {
        this.statusStr = statusStr;
    }

// For deleting object from arrayList

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductSimCardModel)) return false;
        ProductSimCardModel that = (ProductSimCardModel) o;
        return Double.compare(that.getCustomer_price(), getCustomer_price()) == 0 &&
                Double.compare(that.getRetail_price(), getRetail_price()) == 0 &&
                priority == that.priority &&
                Objects.equals(getId(), that.getId()) &&
                Objects.equals(getProduct_name(), that.getProduct_name()) &&
                Objects.equals(getCustomer_priceStr(), that.getCustomer_priceStr()) &&
                Objects.equals(getRetail_priceStr(), that.getRetail_priceStr()) &&
                Objects.equals(mobile_number, that.mobile_number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getProduct_name(), getCustomer_priceStr(), getRetail_priceStr(), getCustomer_price(), getRetail_price(), mobile_number, priority);
    }

}
