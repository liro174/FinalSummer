package com.example.finalsummer;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Item {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String itemName;
    private int price;
    private String purchaseDate; // <--- CORRECTED TYPO HERE: 'purchaseDate'

    // Constructor
    public Item(String itemName, int price, String date) {
        this.itemName = itemName;
        this.price = price;
        this.purchaseDate = date; // <--- ASSIGN TO CORRECTED FIELD NAME HERE
    }
    public Item() {

    }

    // --- Getters and Setters for all fields ---
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    // These getters/setters are already correctly spelled for 'purchaseDate'
    public String getPurchaseDate() {
        return purchaseDate; // <--- RETURN CORRECTED FIELD NAME
    }

    public void setPurchaseDate(String date) {
        this.purchaseDate = date; // <--- SET CORRECTED FIELD NAME
    }
}