package com.example.finalsummer;
// 12/07 מסך פתיחה וולידציה ומערך לפרגמנט
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

// Import BlankFragment - make sure this import matches your BlankFragment's package
// import com.example.your_app_name.BlankFragment; // Example: Adjust this if needed

public class MainActivity extends AppCompatActivity {

    private EditText editTextUsername;
    private EditText editTextFamilyName;
    private Button buttonLogin;
    private FrameLayout fragmentContainer;
    private BlankFragment blankFragment; // Assuming BlankFragment is defined elsewhere

    // Camera related views
    private Button buttonTakePhoto;
    private ImageView imageViewPhoto;

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextUsername = findViewById(R.id.editTextName);
        editTextFamilyName = findViewById(R.id.editFamName);
        buttonLogin = findViewById(R.id.buttonSave);
        fragmentContainer = findViewById(R.id.fragment_container);

        // אתחול כפתור הצילום וה-ImageView
        buttonTakePhoto = findViewById(R.id.buttonTakePhoto);
        imageViewPhoto = findViewById(R.id.imageViewPhoto);

        fragmentContainer.setVisibility(View.GONE);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performValidation();
            }
        });

        // Set the OnClickListener for the takePhoto button
        buttonTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent(); // Call the function to launch the camera
            }
        });

        if (savedInstanceState == null) {
            blankFragment = new BlankFragment(); // Make sure BlankFragment is accessible
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.fragment_container, blankFragment);
            fragmentTransaction.commit();
        } else {
            blankFragment = (BlankFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
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
            // Get the thumbnail bitmap from the intent's data
            // Note: This often returns a small thumbnail, not the full-resolution image.
            // If you need the full image, you'll need the previous FileProvider approach.
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            if (imageBitmap != null) {
                imageViewPhoto.setImageBitmap(imageBitmap);
                imageViewPhoto.setVisibility(View.VISIBLE); // Make sure the ImageView is visible after taking a photo
            } else {
                Toast.makeText(this, "לא הוחזרה תמונה.", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "צילום בוטל.", Toast.LENGTH_SHORT).show();
            // If the user cancels, ensure the ImageView is hidden if no photo was taken
            imageViewPhoto.setVisibility(View.GONE);
        }
    }

    private void performValidation() {
        String username = editTextUsername.getText().toString().trim();
        String familyName = editTextFamilyName.getText().toString().trim();

        if (username.isEmpty() || familyName.isEmpty()) {
            Toast.makeText(this, "שם פרטי ושם משפחה אינם יכולים להיות ריקים.", Toast.LENGTH_LONG).show();
            fragmentContainer.setVisibility(View.GONE);
            if (blankFragment != null) {
                blankFragment.hideAll();
            }
            // ודא שהכפתורים והתמונה גלויים אם הולידציה נכשלה בגלל שדות ריקים
            showPreValidationViews();

        } else if (username.equalsIgnoreCase("OFIR") && familyName.equalsIgnoreCase("LIRON")) {
            Toast.makeText(this, "ולידציה הצליחה! ברוך הבא " + username + " " + familyName, Toast.LENGTH_SHORT).show();

            // הפוך את ה-fragmentContainer לגלוי
            fragmentContainer.setVisibility(View.VISIBLE);

            // קרא לפונקציה של ה-Fragment כדי להציג את התוכן הפנימי שלו
            //if (blankFragment != null) {
                //blankFragment.showButtonsAndDefaultView();
            //}

            // 🚨 הסתר את שדות הקלט, כפתור השמירה, כפתור הצילום וה-ImageView
            editTextUsername.setVisibility(View.GONE);
            editTextFamilyName.setVisibility(View.GONE);
            buttonLogin.setVisibility(View.GONE);
            buttonTakePhoto.setVisibility(View.GONE); // הסתר את כפתור הצילום
            imageViewPhoto.setVisibility(View.GONE);  // הסתר את ה-ImageView

        } else {
            Toast.makeText(this, "שם משתמש או שם משפחה שגויים. נסה שוב.", Toast.LENGTH_LONG).show();
            fragmentContainer.setVisibility(View.GONE);
            if (blankFragment != null) {
                blankFragment.hideAll();
            }
            // ודא שהכפתורים והתמונה גלויים אם הולידציה נכשלה בגלל שם משתמש/משפחה שגויים
            showPreValidationViews();
        }
    }

    // פונקציה עזר להצגת הרכיבים שלפני הולידציה
    private void showPreValidationViews() {
        editTextUsername.setVisibility(View.VISIBLE);
        editTextFamilyName.setVisibility(View.VISIBLE);
        buttonLogin.setVisibility(View.VISIBLE);
        buttonTakePhoto.setVisibility(View.VISIBLE);
        imageViewPhoto.setVisibility(View.VISIBLE);
    }
}