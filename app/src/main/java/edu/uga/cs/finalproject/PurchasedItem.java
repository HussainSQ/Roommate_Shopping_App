package edu.uga.cs.finalproject;

public class PurchasedItem {
    private String cost;
    private String itemID;
    private String groceryList;
    private String purchaserID;

    public PurchasedItem(){
    }

    public PurchasedItem(String cost, String itemID, String groceryList, String purchaserID){
        this.cost = cost;
        this.itemID = itemID;
        this.groceryList = groceryList;
        this.purchaserID = purchaserID;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public String getGroceryList() {
        return groceryList;
    }

    public void setGroceryList(String groceryList) {
        this.groceryList = groceryList;
    }

    public String getPurchaserID() {
        return purchaserID;
    }

    public void setPurchaserID(String purchaserID) {
        this.purchaserID = purchaserID;
    }
}
