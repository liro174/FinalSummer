package com.example.finalsummer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ItemViewHolder> {

    private List<Item> itemList; // רשימת הפריטים שהאדפטר יציג

    // קונסטרוקטור לקבלת רשימת הפריטים
    public MyAdapter(List<Item> itemList) {
        this.itemList = itemList;
    }

    // ViewHolder מגדיר את הוויג'טים בתוך ה-item_layout.xml
    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewItemName;
        public TextView textViewPrice;
        public TextView textViewPurchaseDate;

        public ItemViewHolder(View itemView) {
            super(itemView);
            textViewItemName = itemView.findViewById(R.id.textViewItemName);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            textViewPurchaseDate = itemView.findViewById(R.id.textViewPurchaseDate);
        }
    }

    // נוצר כשצריך ליצור ViewHolder חדש (לדוגמה, בהתחלה או כשנגללים)
    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // טוען את ה-layout של הפריט הבודד (item_layout.xml)
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_layout, parent, false);
        return new ItemViewHolder(view);
    }

    // נקרא כשצריך "לקשור" נתונים ל-ViewHolder קיים (לדוגמה, גלילה של פריטים)
    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item currentItem = itemList.get(position); // מקבל את הפריט הנוכחי מהרשימה

        // מעדכן את הוויג'טים ב-ViewHolder עם נתוני הפריט
        holder.textViewItemName.setText(currentItem.getItemName());
        holder.textViewPrice.setText("מחיר: " + currentItem.getPrice() + " ₪");
        holder.textViewPurchaseDate.setText("תאריך רכישה: " + currentItem.getPurchaseDate());
    }

    // מחזיר את מספר הפריטים הכולל ברשימה
    @Override
    public int getItemCount() {
        return itemList.size();
    }

    // מתודה ציבורית לעדכון רשימת הפריטים ולרענון ה-RecyclerView
    public void addItem(Item newItem) {
        itemList.add(newItem);
        notifyItemInserted(itemList.size() - 1); // מודיע לאדפטר שנוסף פריט
    }
    public void setItemList(List<Item> newItemList) {
        this.itemList = newItemList;
        // notifyDataSetChanged() is a simple way to refresh the entire list.
        // For better performance with large lists, consider using DiffUtil.
        notifyDataSetChanged();
    }
}
