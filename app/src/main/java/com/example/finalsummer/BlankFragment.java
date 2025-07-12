package com.example.finalsummer; // change to your actual package name

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.widget.ImageButton;
import com.example.finalsummer.R;

public class BlankFragment extends Fragment {

 private FrameLayout layoutContainer;

 @Nullable
 @Override
 public View onCreateView(@NonNull LayoutInflater inflater,
                          @Nullable ViewGroup container,
                          @Nullable Bundle savedInstanceState) {

  View view = inflater.inflate(R.layout.fragment_blank, container, false);

  // Button container and layout area
  layoutContainer = view.findViewById(R.id.layout_container);

  ImageButton button1 = view.findViewById(R.id.button1);
  ImageButton button2 = view.findViewById(R.id.button2);
  ImageButton button3 = view.findViewById(R.id.button3);

  // Set click listeners to swap views
  button1.setOnClickListener(v -> showLayout(R.layout.layout_view1));
  button2.setOnClickListener(v -> showLayout(R.layout.layout_view2));
  button3.setOnClickListener(v -> showLayout(R.layout.layout_view3));

  return view;
 }

 // ✅ Call this to show the buttons and default layout (view1)
 public void showButtonsAndDefaultView() {
  if (getView() == null) return;

  View buttonContainer = getView().findViewById(R.id.button_container);
  if (buttonContainer != null) buttonContainer.setVisibility(View.VISIBLE);

  FrameLayout container = getView().findViewById(R.id.layout_container);
  if (container != null) {
   container.setVisibility(View.VISIBLE);
   container.removeAllViews();
   View layout1 = LayoutInflater.from(getContext()).inflate(R.layout.layout_view1, container, false);
   container.addView(layout1);
  }
 }

 // ✅ Call this to hide all fragment views (used when not authorized)
 public void hideAll() {
  if (getView() == null) return;

  View buttonContainer = getView().findViewById(R.id.button_container);
  if (buttonContainer != null) buttonContainer.setVisibility(View.GONE);

  if (layoutContainer != null) {
   layoutContainer.removeAllViews();
   layoutContainer.setVisibility(View.GONE);
  }
 }

 // ✅ Helper to load any of the 3 layouts
 private void showLayout(int layoutResId) {
  if (layoutContainer == null) return;

  layoutContainer.setVisibility(View.VISIBLE);
  layoutContainer.removeAllViews();
  View layout = LayoutInflater.from(getContext()).inflate(layoutResId, layoutContainer, false);
  layoutContainer.addView(layout);
 }
}
