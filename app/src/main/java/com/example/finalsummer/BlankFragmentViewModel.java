package com.example.finalsummer;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.util.ArrayList;
import java.util.List;

public class BlankFragmentViewModel extends AndroidViewModel {

    private ItemDao itemDao;
    private LiveData<List<Item>> allItems;
    private MutableLiveData<List<String>> view2SelectedItems;

    private LiveData<Integer> totalPrice;
    private LiveData<Integer> itemCount;

    // --- NEW: LiveData fields for most expensive and cheapest items ---
    private LiveData<Item> mostExpensiveItem;
    private LiveData<Item> cheapestItem;
    // -----------------------------------------------------------------

    public BlankFragmentViewModel(Application application) {
        super(application);
        AppDatabase db = AppDatabase.getDatabase(application);
        itemDao = db.itemDao();
        allItems = itemDao.getAllItems();

        if (view2SelectedItems == null) {
            view2SelectedItems = new MutableLiveData<>();
            view2SelectedItems.setValue(new ArrayList<>());
        }

        totalPrice = itemDao.getTotalPrice();
        itemCount = itemDao.getItemCount();

        // --- NEW: Initialize LiveData fields from ItemDao ---
        mostExpensiveItem = itemDao.getMostExpensiveItem();
        cheapestItem = itemDao.getCheapestItem();
        // ---------------------------------------------------
    }

    public LiveData<List<Item>> getItemList() {
        return allItems;
    }

    public void addItem(Item newItem) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            itemDao.insert(newItem);
        });
    }

    public void removeItem(int position) {
        List<Item> currentList = allItems.getValue();
        if (currentList != null && position >= 0 && position < currentList.size()) {
            Item itemToDelete = currentList.get(position);
            AppDatabase.databaseWriteExecutor.execute(() -> {
                itemDao.delete(itemToDelete);
            });
        }
    }

    public LiveData<List<String>> getView2SelectedItems() {
        return view2SelectedItems;
    }

    public void addView2Item(String itemName) {
        List<String> currentSelectedItems = view2SelectedItems.getValue();
        if (currentSelectedItems != null) {
            currentSelectedItems.add(itemName);
            view2SelectedItems.setValue(currentSelectedItems);
        }
    }

    public LiveData<Integer> getTotalPrice() {
        return totalPrice;
    }

    public LiveData<Integer> getItemCount() {
        return itemCount;
    }

    // --- NEW: Getters for most expensive and cheapest items ---
    public LiveData<Item> getMostExpensiveItem() {
        return mostExpensiveItem;
    }

    public LiveData<Item> getCheapestItem() {
        return cheapestItem;
    }
    // ----------------------------------------------------------
}