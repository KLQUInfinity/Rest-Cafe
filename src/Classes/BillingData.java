/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Classes;

public class BillingData {

    private String productName;
    private String productCount;
    private String productPrice;
    private String productNotes;
    private String productTotal;

    public BillingData() {
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setProductCount(String productCount) {
        this.productCount = productCount;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public void setProductNotes(String productNotes) {
        this.productNotes = productNotes;
    }

    public void setProductTotal(String productTotal) {
        this.productTotal = productTotal;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductCount() {
        return productCount;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public String getProductNotes() {
        return productNotes;
    }

    public String getProductTotal() {
        return productTotal;
    }

}
