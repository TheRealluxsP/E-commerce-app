package com.example.clothingstore.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import com.example.clothingstore.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;


public class AddArticleActivity extends AppCompatActivity {

    private EditText etArticleName, etArticlePrice, etArticleDescription;
    private Spinner spinnerCategory;
    private ImageView imageViewUpload;
    private Uri selectedImageUri;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_CAMERA_REQUEST = 2;
    private Uri photoUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_article);

        etArticleName = findViewById(R.id.etArticleName);
        etArticlePrice = findViewById(R.id.etArticlePrice);
        etArticleDescription = findViewById(R.id.etArticleDescription);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        imageViewUpload = findViewById(R.id.imageViewUpload);
        Button btnUploadImage = findViewById(R.id.btnUploadImage);
        Button btnSubmitArticle = findViewById(R.id.btnSubmitArticle);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.article_categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        btnUploadImage.setOnClickListener(v -> openImageChooser());

        btnSubmitArticle.setOnClickListener(v -> submitArticle());

    }

    private void openImageChooser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image");
        builder.setItems(new CharSequence[]{"Choose from Gallery", "Take Photo"}, (dialog, which) -> {
            if (which == 0) {
                // Choose from Gallery
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            } else if (which == 1) {
                // Take Photo
                requestCameraPermission();
            }
        });
        builder.show();
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            // Create a file to save the image
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to create image file", Toast.LENGTH_SHORT).show();
                return;
            }

            // Continue only if the file was successfully created
            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(this, "com.example.clothingstore.fileprovider", photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(cameraIntent, PICK_CAMERA_REQUEST);
            }
        }
    }

    // Create an image file for the camera
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".png", storageDir);
    }

    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // If permission is already granted, open the camera
            openCamera();
        } else {
            // Request the camera permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PICK_CAMERA_REQUEST);
        }
    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
                // Gallery result
                selectedImageUri = data.getData();
                imageViewUpload.setImageURI(selectedImageUri);
            } else if (requestCode == PICK_CAMERA_REQUEST) {
                // Camera result
                selectedImageUri = photoUri;
                imageViewUpload.setImageURI(selectedImageUri);
            }
        }
    }


    private void submitArticle(){
        String name = etArticleName.getText().toString();
        String priceStr = etArticlePrice.getText().toString();
        String description = etArticleDescription.getText().toString();
        String category = spinnerCategory.getSelectedItem().toString();

        if (name.isEmpty() || priceStr.isEmpty() || description.isEmpty() || selectedImageUri == null) {
            Toast.makeText(this, "Please fill in all fields and select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Adding article... Please do not close this page", Toast.LENGTH_SHORT).show();

        double price = Double.parseDouble(priceStr);

        uploadImageToFirebase(selectedImageUri, name, price, description, category);
    }

    private void uploadImageToFirebase(Uri imageUri, String name, double price, String description, String category) {
        // Get a reference to Firebase Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // Create a unique filename for the image
        String fileName = "articles/" + UUID.randomUUID().toString() + ".png";  // Using UUID to ensure unique filenames

        // Get a reference to the location where the image will be stored
        StorageReference imageRef = storageRef.child(fileName);

        // Upload the image
        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Get the download URL of the uploaded image
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                String imageUrl = uri.toString();  // This is the URL of the uploaded image

                                // Now that we have the image URL, send the data back to MainActivity
                                Intent resultIntent = new Intent();
                                resultIntent.putExtra("articleName", name);
                                resultIntent.putExtra("articlePrice", price);
                                resultIntent.putExtra("articleDescription", description);
                                resultIntent.putExtra("articleCategory", category);
                                resultIntent.putExtra("articleImageUrl", imageUrl);  // Pass the URL to MainActivity
                                setResult(RESULT_OK, resultIntent);
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                // Handle failure in retrieving download URL
                                Toast.makeText(AddArticleActivity.this, "Failed to get image URL", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    // Handle failure in uploading the image
                    Toast.makeText(AddArticleActivity.this, "Image upload failed", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PICK_CAMERA_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, open the camera
                openCamera();
            } else {
                // Permission denied, show a message to the user
                Toast.makeText(this, "Camera permission is required to take photos", Toast.LENGTH_SHORT).show();
            }
        }
    }

}