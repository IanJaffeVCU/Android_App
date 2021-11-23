package com.example.uRemIndMe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.Result;

import java.util.ArrayList;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class Scanner extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;
    Integer MY_CAMERA_REQUEST_CODE = 100;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(Scanner.this, new String[] {Manifest.permission.CAMERA},MY_CAMERA_REQUEST_CODE);
        }

        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);                // Set the scanner view as the content view
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }



    @Override
    public void handleResult(Result rawResult) {

        String s=rawResult.getText().toString();
        if(Global.documentData.savedFileModal==null){
            Global.documentData.savedFileModal=new ArrayList<>();
        }
        if(FileModal.checkPresent(s.substring(s.lastIndexOf('/')+1))==false){
            StorageReference mStorageRef= FirebaseStorage.getInstance().getReference();
            mStorageRef=mStorageRef.child(s+".pdf");
            getFileUrl(mStorageRef);
        }else{
            Toast.makeText(this, "FileModal Already Exists", Toast.LENGTH_LONG).show();
        }
        finish();
        Toast.makeText(this, "File Has been Downloaded", Toast.LENGTH_LONG).show();

    }
    private void downloadFile(Context context, String fileName, String fileExtension, String destinationDirectory, String url) {

        DownloadManager downloadmanager = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDirectory, fileName + fileExtension);

        downloadmanager.enqueue(request);

    }
    private void getFileUrl(StorageReference ref){
        final String name=ref.getName();
        int flag = 0 ;
        if(flag==0){
            //getFileUrl(ref);
            FileModal down_fileModal =new FileModal();
            down_fileModal.id=Long.valueOf(ref.getName().substring(0,ref.getName().lastIndexOf('.')));
            System.out.println(down_fileModal.id);
            down_fileModal.path=DIRECTORY_DOWNLOADS;
            down_fileModal.uploaded = Global.documentData.userInfo.name;
            down_fileModal.name = "om";
            Global.documentData.savedFileModal.add(down_fileModal);
            Global.userRef.update("savedFileModal",Global.documentData.savedFileModal);
        }else{
            Toast.makeText(this, "FileModal Already Exists", Toast.LENGTH_LONG).show();
            return;
        }
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String url =uri.toString();
                downloadFile(Scanner.this,name.substring(0,name.lastIndexOf('.')),name.substring(name.lastIndexOf('.')),DIRECTORY_DOWNLOADS,url);
            }
        });
    }
}
