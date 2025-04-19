package com.example.clothingstore.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import android.os.Environment;
import android.provider.MediaStore;
import android.widget.*;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.bumptech.glide.Glide;
import com.example.clothingstore.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class UpdateArticleActivity extends AppCompatActivity {

    private EditText etUpdateArticleName, etUpdateArticleDescription, etUpdateArticlePrice;
    private Spinner spinnerCategory;
    private ImageView imageViewUpdate;
    private Uri selectedImageUri;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_CAMERA_REQUEST = 2;
    private Uri photoUri;
    private String name, description, imageUrl, category;
    private double price;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_article);

        etUpdateArticleName = findViewById(R.id.etUpdateArticleName);
        etUpdateArticleDescription = findViewById(R.id.etUpdateArticleDescription);
        etUpdateArticlePrice = findViewById(R.id.etUpdateArticlePrice);
        spinnerCategory = findViewById(R.id.spinnerCategoryUpdate);
        imageViewUpdate = findViewById(R.id.imageViewUpdate);
        Button btnUpdateImage = findViewById(R.id.btnUpdateImage);
        Button btnUpdateArticle = findViewById(R.id.btnUpdateArticle);

        name = getIntent().getStringExtra("productName");
        description = getIntent().getStringExtra("productDescription");
        price = getIntent().getDoubleExtra("productPrice", 0);
        imageUrl = getIntent().getStringExtra("productImage");
        category = getIntent().getStringExtra("productCategory");
        Glide.with(this)
                .load(imageUrl)
                .into(imageViewUpdate);


        etUpdateArticleName.setText(name);
        etUpdateArticleDescription.setText(description);
        etUpdateArticlePrice.setText(String.valueOf(price));


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.article_categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        // Set the selected category in the spinner
        int categoryPosition = adapter.getPosition(category);  // Get the position of the category in the array
        spinnerCategory.setSelection(categoryPosition);  // Set the spinner to the corresponding position

        btnUpdateImage.setOnClickListener(v -> openImageChooser());

        btnUpdateArticle.setOnClickListener(v -> updateArticle());
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
                openCamera();
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

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".png", storageDir);
    }

    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PICK_CAMERA_REQUEST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            imageViewUpdate.setImageURI(selectedImageUri);
        }else if (requestCode == PICK_CAMERA_REQUEST){
            selectedImageUri = photoUri;
            imageViewUpdate.setImageURI(selectedImageUri);
        }
    }

    private void updateArticle() {
        name = etUpdateArticleName.getText().toString();
        description = etUpdateArticleDescription.getText().toString();
        String priceStr = etUpdateArticlePrice.getText().toString();
        String category = spinnerCategory.getSelectedItem().toString();

        if (name.equals("") || priceStr.equals("") || description.equals("")) {
            Toast.makeText(this, "Please fill in all fields and select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.parseDouble(priceStr);

        if (selectedImageUri == null) {
            finishWithResult(name,price,description,category,imageUrl);
            return;
        }

        uploadImageToFirebase(selectedImageUri, name, price, description, category);
    }

    private void finishWithResult(String name, double price, String description, String category, String imageUrl) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("articleName", name);
        resultIntent.putExtra("articlePrice", price);
        resultIntent.putExtra("articleDescription", description);
        resultIntent.putExtra("articleCategory", category);
        resultIntent.putExtra("articleImageUrl", imageUrl);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void uploadImageToFirebase(Uri imageUri, String name, double price, String description, String category) {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        String fileName = "articles/" + UUID.randomUUID().toString() + ".png";

        StorageReference imageRef = storageRef.child(fileName);

        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Get the download URL of the uploaded image
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                String imageUrl = uri.toString();  // This is the URL of the uploaded image

                                // Now that we have the image URL, send the data back to MainActivity
                                finishWithResult(name, price, description, category, imageUrl);
                            })
                            .addOnFailureListener(e -> {
                                // Handle failure in retrieving download URL
                                Toast.makeText(UpdateArticleActivity.this, "Failed to get image URL", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    // Handle failure in uploading the image
                    Toast.makeText(UpdateArticleActivity.this, "Image upload failed", Toast.LENGTH_SHORT).show();
                });
    }
}