package com.example.finalsummer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout; // Make sure this is imported
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    private EditText editTextUsername;
    private EditText editTextFamilyName, editTextPhone;
    private Button buttonLogin;
    // Renamed to match the new ID in activity_main.xml
    private FrameLayout mainFragmentContainer;

    private Button buttonTakePhoto;
    private ImageView imageViewPhoto;

    // Removed: private BottomNavigationView bottomNav; // This ID no longer exists in activity_main.xml

    private BlankFragment blankFragment; // Declared here for broader scope

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextUsername = findViewById(R.id.editTextName);
        editTextFamilyName = findViewById(R.id.editFamName);
        editTextPhone= findViewById(R.id.editTextPhone);
        buttonLogin = findViewById(R.id.buttonSave);
        // Correctly find the FrameLayout with its new ID
        mainFragmentContainer = findViewById(R.id.main_fragment_container);

        buttonTakePhoto = findViewById(R.id.buttonTakePhoto);
        imageViewPhoto = findViewById(R.id.imageViewPhoto);

        // Remove any findViewById or setup for bottomNav here, as it's no longer in activity_main.xml
        // bottomNav = findViewById(R.id.bottom_navigation); // REMOVE THIS LINE
        // bottomNav.setOnNavigationItemSelectedListener(navListener); // REMOVE THIS LINE
        // bottomNav.setVisibility(View.GONE); // REMOVE THIS LINE

        mainFragmentContainer.setVisibility(View.GONE); // Initially hide fragment container

        buttonLogin.setOnClickListener(v -> performValidation());
        buttonTakePhoto.setOnClickListener(v -> dispatchTakePictureIntent());

        if (savedInstanceState == null) {
            blankFragment = new BlankFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            // Use the new ID when adding the fragment
            fragmentTransaction.add(R.id.main_fragment_container, blankFragment);
            fragmentTransaction.commit();
        } else {
            // Use the new ID when finding the fragment
            blankFragment = (BlankFragment) getSupportFragmentManager().findFragmentById(R.id.main_fragment_container);
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } else {
            Toast.makeText(this, "אין אפליקציית מצלמה זמינה.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            if (imageBitmap != null) {
                imageViewPhoto.setImageBitmap(imageBitmap);
                imageViewPhoto.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(this, "לא הוחזרה תמונה.", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "צילום בוטל.", Toast.LENGTH_SHORT).show();
            imageViewPhoto.setVisibility(View.GONE);
        }
    }

    private void performValidation() {
        String username = editTextUsername.getText().toString().trim();
        String familyName = editTextFamilyName.getText().toString().trim();

        if (username.isEmpty() || familyName.isEmpty()) {
            Toast.makeText(this, "שם פרטי ושם משפחה אינם יכולים להיות ריקים.", Toast.LENGTH_LONG).show();
            mainFragmentContainer.setVisibility(View.GONE); // Use new ID
            showPreValidationViews();

        } else if (username.equalsIgnoreCase("OFIR") && familyName.equalsIgnoreCase("LIRON")) {
            Toast.makeText(this, "ולידציה הצליחה! ברוך הבא " + username + " " + familyName, Toast.LENGTH_SHORT).show();

            // Hide pre-validation views
            editTextUsername.setVisibility(View.GONE);
            editTextFamilyName.setVisibility(View.GONE);
            buttonLogin.setVisibility(View.GONE);
            buttonTakePhoto.setVisibility(View.GONE);
            imageViewPhoto.setVisibility(View.GONE);
            editTextPhone.setVisibility(View.GONE);

            // Show fragment container
            mainFragmentContainer.setVisibility(View.VISIBLE); // Use new ID

            // BlankFragment's onViewCreated will now handle setting its initial internal layout (layout_view1)
            // No explicit call to showSpecificLayout from MainActivity is needed here.

        } else {
            Toast.makeText(this, "שם משתמש או שם משפחה שגויים. נסה שוב.", Toast.LENGTH_LONG).show();
            mainFragmentContainer.setVisibility(View.GONE); // Use new ID
            showPreValidationViews();
        }
    }

    private void showPreValidationViews() {
        editTextUsername.setVisibility(View.VISIBLE);
        editTextFamilyName.setVisibility(View.VISIBLE);
        buttonLogin.setVisibility(View.VISIBLE);
        buttonTakePhoto.setVisibility(View.VISIBLE);
        imageViewPhoto.setVisibility(View.VISIBLE);
    }
}