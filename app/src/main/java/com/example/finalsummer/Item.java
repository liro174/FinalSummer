package com.example.finalsummer;

public class Item {
    private String itemName;
    private int price;
    private String purchaseDate;

    // קונסטרוקטור ליצירת אובייקט Item חדש
    public Item(String itemName, int price, String purchaseDate) {
        this.itemName = itemName;
        this.price = price;
        this.purchaseDate = purchaseDate;
    }

    // מתודות Getters כדי לקבל את ערכי התכונות
    public String getItemName() {
        return itemName;
    }

    public int getPrice() {
        return price;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    // (אופציונלי) מתודות Setters אם תרצה לשנות את הערכים לאחר יצירת האובייקט
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }
}

