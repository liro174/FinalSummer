package com.example.finalsummer;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton; // Import ImageButton
import android.widget.LinearLayout;
import android.widget.FrameLayout; // Import FrameLayout
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Calendar;


public class BlankFragment extends Fragment implements MyAdapter.OnItemActionListener {

 private BlankFragmentViewModel viewModel;
 private MyAdapter adapter;

 // Navigation buttons (from fragment_blank.xml)
 private ImageButton button1;
 private ImageButton button2;
 private ImageButton button3;

 // The inner FrameLayout container within fragment_blank.xml
 private FrameLayout innerLayoutContainer; // Matches ID: @id/inner_fragment_layout_container

 // UI elements for layout_view3 (for displaying max/low items)
 private Button buttonShowMax;
 private Button buttonShowLow;
 private TextView textViewMaxItemDisplay;
 private TextView textViewLowItemDisplay;

 // UI elements for detailed item view within layout_view3
 private LinearLayout detailedItemViewContainer;
 private TextView detailedItemName;
 private TextView detailedItemPrice;
 private TextView detailedItemDate;

 // Data for layout_view3
 private Item currentMostExpensiveItem;
 private Item currentCheapestItem;


 public BlankFragment() {
  // Required empty public constructor
 }

 @Override
 public View onCreateView(LayoutInflater inflater, ViewGroup container,
                          Bundle savedInstanceState) {
  // Inflate the root layout for the fragment itself (fragment_blank.xml)
  // This layout contains the navigation buttons and the inner FrameLayout
  return inflater.inflate(R.layout.fragment_blank, container, false);
 }

 @Override
 public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
  super.onViewCreated(view, savedInstanceState);

  // Initialize ViewModel
  viewModel = new ViewModelProvider(this).get(BlankFragmentViewModel.class);

  // --- Find UI elements from fragment_blank.xml ---
  button1 = view.findViewById(R.id.button1);
  button2 = view.findViewById(R.id.button2);
  button3 = view.findViewById(R.id.button3);
  innerLayoutContainer = view.findViewById(R.id.inner_fragment_layout_container);

  // --- Set OnClickListeners for navigation buttons ---
  if (button1 != null) {
   button1.setOnClickListener(v -> showSpecificLayout(R.layout.layout_view1));
  } else {
   Log.e("BlankFragment", "button1 (R.id.button1) not found in fragment_blank.xml!");
  }
  if (button2 != null) {
   button2.setOnClickListener(v -> showSpecificLayout(R.layout.layout_view2));
  } else {
   Log.e("BlankFragment", "button2 (R.id.button2) not found in fragment_blank.xml!");
  }
  if (button3 != null) {
   button3.setOnClickListener(v -> showSpecificLayout(R.layout.layout_view3));
  } else {
   Log.e("BlankFragment", "button3 (R.id.button3) not found in fragment_blank.xml!");
  }

  // Initialize adapter (can be done once, then reused)
  if (adapter == null) {
   adapter = new MyAdapter(new ArrayList<>(), this);
  }

  // --- Observe LiveData from ViewModel ---
  viewModel.getItemList().observe(getViewLifecycleOwner(), items -> {
   if (adapter != null) {
    adapter.setItemList(items);
    Log.d("BlankFragment", "Items updated: " + items.size());
   } else {
    Log.w("BlankFragment", "Adapter is null in getItemList observer. This might happen before RecyclerView is set up.");
   }
  });

  viewModel.getMostExpensiveItem().observe(getViewLifecycleOwner(), item -> {
   currentMostExpensiveItem = item;
   // Update display only if layout_view3's TextView is currently visible/active
   if (textViewMaxItemDisplay != null) {
    textViewMaxItemDisplay.setText(item == null ? "יקר ביותר: אין" : String.format(Locale.getDefault(),
            "פריט יקר ביותר: %s (מחיר: %d ש\"ח)",
            item.getItemName(), item.getPrice()));
   }
  });

  viewModel.getCheapestItem().observe(getViewLifecycleOwner(), item -> {
   currentCheapestItem = item;
   // Update display only if layout_view3's TextView is currently visible/active
   if (textViewLowItemDisplay != null) {
    textViewLowItemDisplay.setText(item == null ? "זול ביותר: אין" : String.format(Locale.getDefault(),
            "פריט זול ביותר: %s (מחיר: %d ש\"ח)",
            item.getItemName(), item.getPrice()));
   }
  });

  // --- Initial layout display ---
  // Automatically show layout_view1 when the fragment is created.
  // This ensures the RecyclerView and FAB are visible immediately after validation.
  showSpecificLayout(R.layout.layout_view1);
 }


 /**
  * Inflates and displays the specified layout resource inside the innerLayoutContainer.
  * Handles specific view initialization for each layout.
  * @param layoutResId The R.layout ID of the layout to display.
  */
 public void showSpecificLayout(int layoutResId) {
  // Ensure the inner container exists before attempting to add views
  if (innerLayoutContainer == null) {
   Log.e("BlankFragment", "Error: innerLayoutContainer (R.id.inner_fragment_layout_container) is null.");
   Toast.makeText(getContext(), "Internal error: Layout container not found.", Toast.LENGTH_LONG).show();
   return;
  }

  // Clear any previously loaded views from the container
  innerLayoutContainer.removeAllViews();

  LayoutInflater inflater = LayoutInflater.from(getContext());
  View loadedLayout = null; // Will hold the inflated view for the current layoutResId

  if (layoutResId == R.layout.layout_view1) {
   // Inflate layout_view1 which contains RecyclerView and FAB
   loadedLayout = inflater.inflate(R.layout.layout_view1, innerLayoutContainer, false);

   // --- Find and set up RecyclerView (ID: @id/recyclerView) ---
   RecyclerView recyclerView = loadedLayout.findViewById(R.id.recyclerView);
   if (recyclerView != null) {
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    if (adapter == null) { // Fallback, should ideally be initialized in onViewCreated
     adapter = new MyAdapter(new ArrayList<>(), this);
    }
    recyclerView.setAdapter(adapter);

    // Setup SwipeToDeleteCallback
    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(adapter));
    itemTouchHelper.attachToRecyclerView(recyclerView);

    // --- Find and set up FloatingActionButton (ID: @id/fab) ---
    FloatingActionButton fab = loadedLayout.findViewById(R.id.fab);
    if(fab != null) {
     fab.setOnClickListener(v -> showAddItemDialog());
    } else {
     Log.e("BlankFragment", "FloatingActionButton (R.id.fab) NOT FOUND in layout_view1.xml!");
    }
   } else {
    Log.e("BlankFragment", "RecyclerView (R.id.recyclerView) NOT FOUND in layout_view1.xml!");
    Toast.makeText(getContext(), "Error: RecyclerView not found for View 1.", Toast.LENGTH_LONG).show();
   }

  } else if (layoutResId == R.layout.layout_view2) {
   // Inflate layout_view2
   loadedLayout = inflater.inflate(R.layout.layout_view2, innerLayoutContainer, false);

   // --- Find TextView for selected items (ID: @id/textViewSelectedItems) ---
   TextView textViewSelectedItems = loadedLayout.findViewById(R.id.textViewSelectedItems);
   if (textViewSelectedItems != null) {
    viewModel.getView2SelectedItems().observe(getViewLifecycleOwner(), items -> {
     StringBuilder sb = new StringBuilder();
     if (items != null && !items.isEmpty()) {
      for (String item : items) {
       sb.append("• ").append(item).append("\n");
      }
     } else {
      sb.append("רשימה ריקה."); // Matches the default text in your XML
     }
     textViewSelectedItems.setText(sb.toString());
    });
   } else {
    Log.e("BlankFragment", "TextView (R.id.textViewSelectedItems) NOT FOUND in layout_view2.xml!");
   }


  } else if (layoutResId == R.layout.layout_view3) {
   // Inflate layout_view3
   loadedLayout = inflater.inflate(R.layout.layout_view3, innerLayoutContainer, false);

   // --- Find UI elements for layout_view3 ---
   buttonShowMax = loadedLayout.findViewById(R.id.buttonShowMax);
   buttonShowLow = loadedLayout.findViewById(R.id.buttonShowLow);
   textViewMaxItemDisplay = loadedLayout.findViewById(R.id.textViewMaxItemDisplay);
   textViewLowItemDisplay = loadedLayout.findViewById(R.id.textViewLowItemDisplay);

   detailedItemViewContainer = loadedLayout.findViewById(R.id.detailedItemViewContainer);
   detailedItemName = loadedLayout.findViewById(R.id.detailedItemName);
   detailedItemPrice = loadedLayout.findViewById(R.id.detailedItemPrice);
   detailedItemDate = loadedLayout.findViewById(R.id.detailedItemDate);

   // --- Set initial text for max/low items on view load ---
   if (textViewMaxItemDisplay != null) {
    if (currentMostExpensiveItem != null) {
     textViewMaxItemDisplay.setText(String.format(Locale.getDefault(),
             "פריט יקר ביותר: %s (מחיר: %d ש\"ח)",
             currentMostExpensiveItem.getItemName(), currentMostExpensiveItem.getPrice()));
    } else {
     textViewMaxItemDisplay.setText("יקר ביותר: אין");
    }
   }
   if (textViewLowItemDisplay != null) {
    if (currentCheapestItem != null) {
     textViewLowItemDisplay.setText(String.format(Locale.getDefault(),
             "פריט זול ביותר: %s (מחיר: %d ש\"ח)",
             currentCheapestItem.getItemName(), currentCheapestItem.getPrice()));
    } else {
     textViewLowItemDisplay.setText("זול ביותר: אין");
    }
   }

   // --- Set OnClickListeners for buttons in layout_view3 ---
   if (buttonShowMax != null) {
    buttonShowMax.setOnClickListener(v -> {
     if (currentMostExpensiveItem != null) {
      textViewMaxItemDisplay.setText(String.format(Locale.getDefault(),
              "פריט יקר ביותר: %s (מחיר: %d ש\"ח)",
              currentMostExpensiveItem.getItemName(), currentMostExpensiveItem.getPrice()));
      displayDetailedItem(currentMostExpensiveItem);
     } else {
      textViewMaxItemDisplay.setText("פריט יקר ביותר: אין פריטים");
      hideDetailedItemView();
     }
     textViewLowItemDisplay.setText("זול ביותר: "); // Clear other display
    });
   } else {
    Log.e("BlankFragment", "buttonShowMax (R.id.buttonShowMax) not found in layout_view3.xml!");
   }

   if (buttonShowLow != null) {
    buttonShowLow.setOnClickListener(v -> {
     if (currentCheapestItem != null) {
      textViewLowItemDisplay.setText(String.format(Locale.getDefault(),
              "פריט זול ביותר: %s (מחיר: %d ש\"ח)",
              currentCheapestItem.getItemName(), currentCheapestItem.getPrice()));
      displayDetailedItem(currentCheapestItem);
     } else {
      textViewLowItemDisplay.setText("זול ביותר: אין פריטים");
      hideDetailedItemView();
     }
     textViewMaxItemDisplay.setText("יקר ביותר: "); // Clear other display
    });
   } else {
    Log.e("BlankFragment", "buttonShowLow (R.id.buttonShowLow) not found in layout_view3.xml!");
   }

  }

  // Add the inflated layout to the inner container
  if (loadedLayout != null) {
   innerLayoutContainer.addView(loadedLayout);
  } else {
   Log.e("BlankFragment", "Error: loadedLayout is null for layoutResId: " + layoutResId);
  }
 }

 // --- Helper methods for layout_view3's detailed item display ---
 private void displayDetailedItem(Item item) {
  if (item != null && detailedItemViewContainer != null &&
          detailedItemName != null && detailedItemPrice != null && detailedItemDate != null) {
   detailedItemName.setText(item.getItemName());
   detailedItemPrice.setText(String.format(Locale.getDefault(), "מחיר: %d ש\"ח", item.getPrice()));
   detailedItemDate.setText("תאריך רכישה: " + item.getPurchaseDate());
   detailedItemViewContainer.setVisibility(View.VISIBLE);
  } else if (detailedItemViewContainer != null) {
   hideDetailedItemView();
  }
 }

 private void hideDetailedItemView() {
  if (detailedItemViewContainer != null) {
   detailedItemViewContainer.setVisibility(View.GONE);
  }
 }

 // --- Dialog for adding new items ---
 private void showAddItemDialog() {
  if (getContext() == null) {
   Log.e("BlankFragment", "Context is null when trying to show AddItemDialog.");
   return;
  }

  AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
  builder.setTitle("הוסף פריט חדש");

  LinearLayout layout = new LinearLayout(getContext());
  layout.setOrientation(LinearLayout.VERTICAL);
  layout.setPadding(50, 20, 50, 20);

  final EditText inputName = new EditText(getContext());
  inputName.setHint("שם פריט");
  layout.addView(inputName);

  final EditText inputPrice = new EditText(getContext());
  inputPrice.setHint("מחיר");
  inputPrice.setInputType(InputType.TYPE_CLASS_NUMBER);
  layout.addView(inputPrice);

  // --- Date Picker Integration ---
  final EditText inputDate = new EditText(getContext());
  inputDate.setHint("תאריך (YYYY-MM-DD)");
  // Make EditText non-editable but clickable to open DatePickerDialog
  inputDate.setFocusable(false);
  inputDate.setClickable(true);
  inputDate.setInputType(InputType.TYPE_NULL); // Prevent soft keyboard from showing
  layout.addView(inputDate);

  // Get current date for default DatePicker selection
  final Calendar calendar = Calendar.getInstance();
  int year = calendar.get(Calendar.YEAR);
  int month = calendar.get(Calendar.MONTH);
  int day = calendar.get(Calendar.DAY_OF_MONTH);

  // Format current date and set it as default text
  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
  inputDate.setText(sdf.format(new Date()));

  // Set OnClickListener to show DatePickerDialog
  inputDate.setOnClickListener(v -> {
   DatePickerDialog datePickerDialog = new DatePickerDialog(
           getContext(),
           (view, selectedYear, selectedMonth, selectedDay) -> {
            // When a date is set, update the EditText
            calendar.set(selectedYear, selectedMonth, selectedDay);
            inputDate.setText(sdf.format(calendar.getTime()));
           },
           year, month, day // Initial date for the picker
   );
   datePickerDialog.show();
  });
  // --- END OF Date Picker Integration ---

  builder.setView(layout);

  builder.setPositiveButton("הוסף", (dialog, which) -> {
   String itemName = inputName.getText().toString().trim();
   String priceStr = inputPrice.getText().toString().trim();
   String purchaseDate = inputDate.getText().toString().trim(); // This will now come from the DatePicker

   if (itemName.isEmpty() || priceStr.isEmpty() || purchaseDate.isEmpty()) {
    Toast.makeText(getContext(), "אנא מלא את כל השדות", Toast.LENGTH_SHORT).show();
    return;
   }

   try {
    int price = Integer.parseInt(priceStr);
    Item newItem = new Item(itemName, price, purchaseDate);
    viewModel.addItem(newItem);
    Toast.makeText(getContext(), "פריט נוסף בהצלחה!", Toast.LENGTH_SHORT).show();
   } catch (NumberFormatException e) {
    Toast.makeText(getContext(), "מחיר לא חוקי", Toast.LENGTH_SHORT).show();
   }
  });
  builder.setNegativeButton("בטל", (dialog, which) -> dialog.cancel());

  builder.show();
 }

 // --- MyAdapter.OnItemActionListener implementations ---
 @Override
 public void onItemDelete(int position) {
  viewModel.removeItem(position);
  Toast.makeText(getContext(), "פריט נמחק", Toast.LENGTH_SHORT).show();
 }

 @Override
 public void onItemMoveToView2(String itemName) {
  viewModel.addView2Item(itemName);
  Toast.makeText(getContext(), itemName + " הועבר ל-View 2", Toast.LENGTH_SHORT).show();
 }

 // --- Swipe-to-delete functionality for RecyclerView ---
 public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {
  private MyAdapter mAdapter;

  public SwipeToDeleteCallback(MyAdapter adapter) {
   super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
   mAdapter = adapter;
  }

  @Override
  public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
   return false;
  }

  @Override
  public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
   int position = viewHolder.getAdapterPosition(); // Get the position of the swiped item
   Item swipedItem = mAdapter.getItemList().get(position); // Get the actual Item object

   if (direction == ItemTouchHelper.RIGHT) {
    // If swiped RIGHT, move the item to View 2
    onItemMoveToView2(swipedItem.getItemName());
    // After moving, remove it from the current list (Layout 1)
    onItemDelete(position);
   } else if (direction == ItemTouchHelper.LEFT) {
    // If swiped LEFT, delete the item from Layout 1
    onItemDelete(position);
   }
  }
 }
 }
