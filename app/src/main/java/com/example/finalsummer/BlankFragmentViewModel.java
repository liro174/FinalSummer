package com.example.finalsummer; // <--- ENSURE THIS PACKAGE NAME IS CORRECT

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import java.util.List;

public class BlankFragmentViewModel extends ViewModel {

    private MutableLiveData<List<Item>> itemList;
    private MutableLiveData<List<String>> view2SelectedItems; // <--- THIS LINE IS CRUCIAL

    public LiveData<List<Item>> getItemList() {
        if (itemList == null) {
            itemList = new MutableLiveData<>();
            List<Item> initialList = new ArrayList<>();
            itemList.setValue(initialList);
        }
        return itemList;
    }

    // THIS METHOD MUST BE EXACTLY AS SHOWN BELOW
    public LiveData<List<String>> getView2SelectedItems() {
        if (view2SelectedItems == null) {
            view2SelectedItems = new MutableLiveData<>();
            view2SelectedItems.setValue(new ArrayList<>());
        }
        return view2SelectedItems;
    }

    public void addItem(Item newItem) {
        List<Item> currentList = itemList.getValue();
        if (currentList != null) {
            currentList.add(newItem);
            itemList.setValue(currentList);
        }
    }

    public void removeItem(int position) {
        List<Item> currentList = itemList.getValue();
        if (currentList != null && position >= 0 && position < currentList.size()) {
            currentList.remove(position);
            itemList.setValue(currentList);
        }
    }

    public void addView2Item(String itemName) {
        List<String> currentSelectedItems = view2SelectedItems.getValue();
        if (currentSelectedItems != null) {
            currentSelectedItems.add(itemName);
            view2SelectedItems.setValue(currentSelectedItems);
        }
    }
}