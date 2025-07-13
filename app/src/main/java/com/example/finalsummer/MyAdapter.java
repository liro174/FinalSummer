package com.example.finalsummer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button; // Don't forget to import Button if you add them later

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList; // Import ArrayList for safe initialization
import java.util.List;
import java.util.Locale; // Import Locale for String.format

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ItemViewHolder> {

    // --- REVISION 1: Initialize itemList directly to an empty ArrayList.
    // This ensures it is NEVER null from the moment the adapter is created.
    private List<Item> itemList = new ArrayList<>();

    // --- NEW: Interface for item actions (to support delete/move buttons later) ---
    // If you don't plan to have delete/move buttons, you can remove this interface
    // and the corresponding listener field and related code in onBindViewHolder.
    public interface OnItemActionListener {
        void onItemDelete(int position);
        void onItemMoveToView2(String itemName);
    }

    private OnItemActionListener listener;
    // -----------------------------------------------------------------------------

    // --- REVISION 2: Constructor now takes the initial list AND an optional listener ---
    // This constructor allows you to initialize the adapter with data and set up callbacks.
    public MyAdapter(List<Item> initialItemList, OnItemActionListener listener) {
        // Safely add items from the initial list.
        // If initialItemList is null, itemList remains an empty ArrayList, which is safe.
        if (initialItemList != null) {
            this.itemList.addAll(initialItemList);
        }
        this.listener = listener; // Set the action listener
    }

    // --- Constructor Overload for backward compatibility (if you don't need a listener yet) ---
    // If you are currently creating MyAdapter without a listener, use this constructor.
    // However, for delete/move functionality, the above constructor is needed.
    public MyAdapter(List<Item> initialItemList) {
        this(initialItemList, null); // Calls the main constructor with a null listener
    }
    // Add this new method to expose the item list
    public List<Item> getItemList() {
        return itemList;
    }
    // ------------------------------------------------------------------------------------------

    // ViewHolder מגדיר את הוויג'טים בתוך ה-item_layout.xml
    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewItemName;
        public TextView textViewPrice;
        public TextView textViewPurchaseDate;
        public Button buttonDelete;     // Add these if you have them in item_layout.xml
        public Button buttonMoveToView2; // Add these if you have them in item_layout.xml

        public ItemViewHolder(View itemView) {
            super(itemView);
            textViewItemName = itemView.findViewById(R.id.textViewItemName);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            // Assuming item_layout.xml has textViewPurchaseDate with this ID
            textViewPurchaseDate = itemView.findViewById(R.id.textViewPurchaseDate);

            // Add these if you have buttons in item_layout.xml
            // buttonDelete = itemView.findViewById(R.id.buttonDelete);
            // buttonMoveToView2 = itemView.findViewById(R.id.buttonMoveToView2);
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
        // Because itemList is guaranteed not to be null, this is safe.
        Item currentItem = itemList.get(position);

        holder.textViewItemName.setText(currentItem.getItemName());
        // Use String.format with Locale for consistent number formatting.
        holder.textViewPrice.setText(String.format(Locale.getDefault(), "מחיר: %d ₪", currentItem.getPrice()));
        holder.textViewPurchaseDate.setText("תאריך רכישה: " + currentItem.getPurchaseDate());

        // --- NEW: Set click listeners for buttons in each row (if you have them) ---
        // Uncomment and adapt if your item_layout.xml has these buttons.
        /*
        if (holder.buttonDelete != null) {
            holder.buttonDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemDelete(holder.getAdapterPosition());
                }
            });
        }

        if (holder.buttonMoveToView2 != null) {
            holder.buttonMoveToView2.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemMoveToView2(currentItem.getItemName());
                }
            });
        }
        */
        // --------------------------------------------------------------------------
    }

    // מחזיר את מספר הפריטים הכולל ברשימה
    @Override
    public int getItemCount() {
        // REVISION 3: Now itemList is guaranteed non-null, so .size() is always safe.
        return itemList.size();
    }

    // מתודה ציבורית לעדכון רשימת הפריטים ולרענון ה-RecyclerView
    public void addItem(Item newItem) {
        // Since itemList is guaranteed non-null, .add() is safe.
        itemList.add(newItem);
        notifyItemInserted(itemList.size() - 1); // מודיע לאדפטר שנוסף פריט
    }

    public void setItemList(List<Item> newItemList) {
        this.itemList.clear(); // Clear existing items safely.
        // REVISION 4: Only add items if the new list is not null.
        if (newItemList != null) {
            this.itemList.addAll(newItemList);
        }
        notifyDataSetChanged();
    }

    // --- NEW: Helper method for SwipeToDeleteCallback (if you use it) ---
    // If you don't plan to use swipe-to-delete, you can remove this.
    public Item getItemAt(int position) {
        // This is safe because itemList is never null and position is checked by caller (ItemTouchHelper).
        return itemList.get(position);
    }
    // --------------------------------------------------------------------
}