package com.example.finalsummer;

import android.app.DatePickerDialog;
import android.graphics.Canvas; // For drawing swipe background
import android.graphics.Color; // For swipe background color
import android.graphics.Paint; // For drawing text
import android.graphics.drawable.ColorDrawable; // For swipe background
import android.graphics.drawable.Drawable; // For icons
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView; // New import for TextView in layout_view2
import android.widget.Toast;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat; // For getting drawable
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper; // NEW import for swipe functionality
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class BlankFragment extends Fragment {

 private FrameLayout layoutContainer;
 private RecyclerView recyclerView;
 private MyAdapter myAdapter;
 private FloatingActionButton fabAddItem;
 private EditText etPurchaseDate;
 private int selectedYear, selectedMonth, selectedDay;
 private ImageButton button1, button2, button3;

 private BlankFragmentViewModel viewModel;

 // Declare TextView for View 2 items
 private TextView textViewSelectedItems; // NEW

 @Override
 public void onCreate(@Nullable Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  viewModel = new ViewModelProvider(this).get(BlankFragmentViewModel.class);
 }

 @Nullable
 @Override
 public View onCreateView(@NonNull LayoutInflater inflater,
                          @Nullable ViewGroup container,
                          @Nullable Bundle savedInstanceState) {
  View view = inflater.inflate(R.layout.fragment_blank, container, false);

  layoutContainer = view.findViewById(R.id.layout_container);
  button1 = view.findViewById(R.id.button1);
  button2 = view.findViewById(R.id.button2);
  button3 = view.findViewById(R.id.button3);

  button1.setOnClickListener(v -> showSpecificLayout(R.layout.layout_view1));
  button2.setOnClickListener(v -> showSpecificLayout(R.layout.layout_view2));
  button3.setOnClickListener(v -> showSpecificLayout(R.layout.layout_view3));

  return view;
 }

 @Override
 public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
  super.onViewCreated(view, savedInstanceState);

  showSpecificLayout(R.layout.layout_view1); // Load default view on creation

  // Observe the main item list from the ViewModel
  viewModel.getView2SelectedItems().observe(getViewLifecycleOwner(), items -> {
   if (textViewSelectedItems != null) {
    updateView2ItemsDisplay(items);

   }
  });

  // NEW: Observe the selected items for View 2 from the ViewModel
  viewModel.getView2SelectedItems().observe(getViewLifecycleOwner(), items -> {
   if (textViewSelectedItems != null) { // Check if textViewSelectedItems is initialized
    updateView2ItemsDisplay(items);
   }
  });
 }

 private void showSpecificLayout(int layoutResId) {
  if (layoutContainer == null) return;

  layoutContainer.setVisibility(View.VISIBLE);
  layoutContainer.removeAllViews(); // Clear previous views from the container

  View loadedLayout = LayoutInflater.from(getContext()).inflate(layoutResId, layoutContainer, false);
  layoutContainer.addView(loadedLayout);

  if (layoutResId == R.layout.layout_view1) {
   recyclerView = loadedLayout.findViewById(R.id.recyclerViewShoppingList);
   fabAddItem = loadedLayout.findViewById(R.id.fabAddItem);

   if (myAdapter == null) {
    myAdapter = new MyAdapter(viewModel.getItemList().getValue());
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    recyclerView.setAdapter(myAdapter);
   } else {
    myAdapter.setItemList(viewModel.getItemList().getValue());
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    recyclerView.setAdapter(myAdapter);
    // No need for notifyDataSetChanged() here, as LiveData observer handles it.
   }

   if (fabAddItem != null) {
    fabAddItem.setOnClickListener(v -> showAddItemDialog());
   }

   // NEW: Attach ItemTouchHelper for swipe functionality
   attachItemTouchHelper(recyclerView);

  } else if (layoutResId == R.layout.layout_view2) {
   // NEW: Initialize textViewSelectedItems for layout_view2
   textViewSelectedItems = loadedLayout.findViewById(R.id.textViewSelectedItems);
   // Update display immediately with current ViewModel data
   updateView2ItemsDisplay(viewModel.getView2SelectedItems().getValue());
  }
  // else if (layoutResId == R.layout.layout_view3) { ... }
 }

 // NEW: Helper method to update the TextView in layout_view2 with bullet points
 private void updateView2ItemsDisplay(List<String> items) {
  if (textViewSelectedItems == null) return; // Ensure view is initialized

  if (items != null && !items.isEmpty()) {
   StringBuilder builder = new StringBuilder();
   for (String item : items) {
    builder.append("• ").append(item).append("\n");
   }
   textViewSelectedItems.setText(builder.toString());
  } else {
   textViewSelectedItems.setText("רשימה ריקה.");
  }
 }


 private void attachItemTouchHelper(RecyclerView recyclerView) {
  new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

   // We don't need drag & drop, so isLongPressDragEnabled is false
   @Override
   public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
    return false;
   }

   // This is where swipe logic goes
   @Override
   public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
    int position = viewHolder.getAdapterPosition();
    Item swipedItem = viewModel.getItemList().getValue().get(position);

    if (direction == ItemTouchHelper.RIGHT) {
     // Swipe right: Delete item
     viewModel.removeItem(position);
     Toast.makeText(getContext(), "הפריט \"" + swipedItem.getItemName() + "\" נמחק", Toast.LENGTH_SHORT).show();
    } else if (direction == ItemTouchHelper.LEFT) {
     // Swipe left: Add item name to View 2 list
     viewModel.addView2Item(swipedItem.getItemName());
     // IMPORTANT: We DO NOT remove the item from the main list here,
     // unless that's the desired behavior.
     // If you want to remove it: viewModel.removeItem(position);
     // For now, let's keep it in the main list and just add to view2.
     Toast.makeText(getContext(), "הפריט \"" + swipedItem.getItemName() + "\" נוסף לתצוגה 2", Toast.LENGTH_SHORT).show();

     // Re-add the item to its original position because we didn't remove it from the main list.
     // This is crucial for "swipe to add to another list" where the original item should remain.
     // If you uncommented viewModel.removeItem(position) above, remove this line.
     myAdapter.notifyItemChanged(position); // Notify adapter that the item is back
    }
   }

   // NEW: Custom drawing for swipe background and icons
   @Override
   public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
    View itemView = viewHolder.itemView;
    ColorDrawable background;
    Drawable icon;
    int iconMargin;
    int iconTop;
    int iconBottom;

    if (dX > 0) { // Swiping right (delete)
     background = new ColorDrawable(Color.RED);
     icon = ContextCompat.getDrawable(getContext(), android.R.drawable.ic_menu_delete); // Or your own delete icon
     iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
     iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
     iconBottom = iconTop + icon.getIntrinsicHeight();
     int iconLeft = itemView.getLeft() + iconMargin;
     int iconRight = iconLeft + icon.getIntrinsicWidth();
     icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
     background.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + ((int) dX), itemView.getBottom());
    } else if (dX < 0) { // Swiping left (add to view 2)
     background = new ColorDrawable(Color.BLUE); // Or another color
     icon = ContextCompat.getDrawable(getContext(), android.R.drawable.ic_input_add); // Or your own add icon
     iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
     iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
     iconBottom = iconTop + icon.getIntrinsicHeight();
     int iconRight = itemView.getRight() - iconMargin;
     int iconLeft = iconRight - icon.getIntrinsicWidth();
     icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
     background.setBounds(itemView.getRight() + ((int) dX), itemView.getTop(), itemView.getRight(), itemView.getBottom());
    } else { // Not swiping
     background = new ColorDrawable(Color.TRANSPARENT);
     background.setBounds(0, 0, 0, 0); // No background
     icon = null;
    }

    background.draw(c);
    if (icon != null) {
     icon.draw(c);
    }

    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
   }
  }).attachToRecyclerView(recyclerView);
 }


 private void showAddItemDialog() {
  AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
  builder.setTitle("הוסף פריט חדש");

  View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_item, null);
  builder.setView(dialogView);

  EditText etItemName = dialogView.findViewById(R.id.editTextItemName);
  EditText etPrice = dialogView.findViewById(R.id.editTextPrice);
  etPurchaseDate = dialogView.findViewById(R.id.editTextPurchaseDate);

  final Calendar c = Calendar.getInstance();
  selectedYear = c.get(Calendar.YEAR);
  selectedMonth = c.get(Calendar.MONTH);
  selectedDay = c.get(Calendar.DAY_OF_MONTH);
  updateDateEditText(selectedYear, selectedMonth, selectedDay);

  etPurchaseDate.setOnClickListener(v -> {
   DatePickerDialog datePickerDialog = new DatePickerDialog(
           getContext(),
           (view, year, month, dayOfMonth) -> {
            selectedYear = year;
            selectedMonth = month;
            selectedDay = dayOfMonth;
            updateDateEditText(year, month, dayOfMonth);
           },
           selectedYear, selectedMonth, selectedDay);
   datePickerDialog.show();
  });

  builder.setPositiveButton("הוסף", (dialog, which) -> {
   String name = etItemName.getText().toString();
   String priceStr = etPrice.getText().toString();
   String purchaseDate = etPurchaseDate.getText().toString();

   if (name.isEmpty() || priceStr.isEmpty() || purchaseDate.isEmpty()) {
    Toast.makeText(getContext(), "שם, מחיר ותאריך אינם יכולים להיות ריקים.", Toast.LENGTH_LONG).show();
    return;
   }

   try {
    int price = Integer.parseInt(priceStr);
    Item newItem = new Item(name, price, purchaseDate);

    viewModel.addItem(newItem);
    Toast.makeText(getContext(), "פריט נוסף: " + name, Toast.LENGTH_SHORT).show();

   } catch (NumberFormatException e) {
    Toast.makeText(getContext(), "מחיר חייב להיות מספר תקין", Toast.LENGTH_SHORT).show();
   }
  });

  builder.setNegativeButton("ביטול", (dialog, which) -> dialog.cancel());

  builder.show();
 }

 private void updateDateEditText(int year, int month, int dayOfMonth) {
  String formattedDate = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month + 1, year);
  etPurchaseDate.setText(formattedDate);
 }

 public void hideAll() {
  if (getView() == null) return;
  View buttonContainer = getView().findViewById(R.id.button_container);
  if (buttonContainer != null) buttonContainer.setVisibility(View.GONE);
  if (layoutContainer != null) {
   layoutContainer.removeAllViews();
   layoutContainer.setVisibility(View.GONE);
  }
 }
}