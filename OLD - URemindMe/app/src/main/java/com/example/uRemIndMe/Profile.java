package com.example.uRemIndMe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Profile extends AppCompatActivity {

    TextView name,gender,branch,year,batch;
    Button editprofile;
    ImageView photo;
    ImageButton editprofilephoto;
    ProgressBar pb;

    List<String> branches = new ArrayList<>();
    List<String> batches = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Setting up the toolbar
        Toolbar toolbar = findViewById(R.id.profile_toolbar);
        setSupportActionBar(toolbar);

        //Refrencing
        name = findViewById(R.id.profile_name);
        gender = findViewById(R.id.profile_gender);
        branch = findViewById(R.id.profile_branch);
        year = findViewById(R.id.profile_year);
        batch = findViewById(R.id.profile_batch);
        editprofile = findViewById(R.id.profile_editprofile);
        editprofilephoto = findViewById(R.id.profile_edit_profileimage);
        photo = findViewById(R.id.profile_image);
        pb = findViewById(R.id.profile_pb);


        editprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("query",0);
                SignUpInfoDialog dialog = new SignUpInfoDialog(Profile.this);
                FragmentTransaction ft= getSupportFragmentManager().beginTransaction();
                dialog.setArguments(bundle);
                dialog.show(ft,"hello");
            }
        });

        editprofilephoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED){
                    ActivityCompat.requestPermissions(Profile.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_WRITE_REQUEST_CODE);
                }
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED){
                    ActivityCompat.requestPermissions(Profile.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_READ_REQUET_CODE);
                }

                Intent pictureIntent = new Intent(Intent.ACTION_GET_CONTENT);
                pictureIntent.setType("image/*");  // 1
                pictureIntent.addCategory(Intent.CATEGORY_OPENABLE);  // 2
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    String[] mimeTypes = new String[]{"image/jpeg", "image/png"};  // 3
                    pictureIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                }
                startActivityForResult(Intent.createChooser(pictureIntent,"Select Picture"), PICK_IMAGE);  // 4
            }
        });

        loadImage();


        showData();
    }


    private void loadImage(){
        StorageReference ref = FirebaseStorage.getInstance().getReference();
        pb.setVisibility(View.VISIBLE);
        try {
            final File localFile = File.createTempFile("images", "jpg");
            ref.child("profile/"+Global.documentData.userInfo.uid+"/profile.jpeg").getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    pb.setVisibility(View.GONE);
                    Bitmap image = BitmapFactory.decodeFile(localFile.getPath());
                    photo.setImageBitmap(image);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Profile.this, "User Profile Doesnot Exists", Toast.LENGTH_SHORT).show();
                    pb.setVisibility(View.GONE);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void openCropActivity(Uri sourceUri, Uri destinationUri) {
        UCrop.of(sourceUri, destinationUri)
                .withMaxResultSize(300, 300)
                .withAspectRatio(5f, 5f)
                .start(this);
    }

    Integer PICK_IMAGE = 100, STORAGE_WRITE_REQUEST_CODE= 100, STORAGE_READ_REQUET_CODE = 100;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE){

            Uri sourceUri = data.getData(); // 1
            File file = null; // 2

            try {

                file = getImageFile();
                Uri destinationUri = Uri.fromFile(file);  // 3
                openCropActivity(sourceUri, destinationUri);  // 4

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        else if(requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK){
            final Uri imageURI = UCrop.getOutput(data);

            File file = new File(imageURI.getPath());

            try {
                InputStream stream =new FileInputStream(file);
                photo.setImageBitmap(BitmapFactory.decodeStream(stream));

                pb.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(),"Uploading Photo....",Toast.LENGTH_SHORT).show();

                StorageReference storage = FirebaseStorage.getInstance().getReference();

                storage.child("profile/"+Global.documentData.userInfo.uid+"/profile.jpeg").putFile(imageURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        pb.setVisibility(View.GONE);
                        Global.profileimage = BitmapFactory.decodeFile(imageURI.getPath());
                        Toast.makeText(getApplicationContext(),"Profile Photo Successfully Uploaded",Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pb.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(),"Upload Unsuccessful",Toast.LENGTH_SHORT).show();

                    }
                });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

    String currentPhotoPath = "";

    private java.io.File getImageFile() throws IOException {
        String imageFileName = "JPEG_" + System.currentTimeMillis() + "_";
        java.io.File storageDir = new java.io.File(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DCIM
                ), "Camera"
        );
        java.io.File file = File.createTempFile(
                imageFileName, ".jpg", storageDir
        );
        currentPhotoPath = "file:" + file.getAbsolutePath();
        return file;
    }



    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    public void showData(){
        Global.ModalClasses.UserInfoModal data = Global.documentData.userInfo;
        name.setText(data.name);
        if(data.gender == 1){
            gender.setText("Male");
        }else{
            gender.setText("Female");
        }
        branch.setText(Global.branches.get(data.branch));
        batch.setText(Global.batches.get(data.batch));
        year.setText(getResources().getStringArray(R.array.year_info)[Global.documentData.userInfo.year]);

    }
}
