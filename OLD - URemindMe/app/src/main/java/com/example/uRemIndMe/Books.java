package com.example.uRemIndMe;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;

import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static android.os.Environment.getExternalStorageState;

public class Books extends AppCompatActivity {

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

        @NonNull
        @Override
        public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v =getLayoutInflater().inflate(R.layout.books_recycler,parent,false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, final int position) {
            final FileModal data = Global.documentData.savedFileModal.get(position);
            holder.name.setText(data.name);
            holder.uploaded.setText(data.uploaded);

            holder.download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    StorageReference ref = mStorageRef.child(Global.documentData.userInfo.uid+"/Books/"+data.id+".pdf");
                    getFileUrl(ref);

                }
            });


            QRGEncoder qrgEncoder = new QRGEncoder(Global.documentData.userInfo.uid+"/Books/"+data.id, null, QRGContents.Type.TEXT, 380);
            qrgEncoder.setColorBlack(Color.BLACK);
            qrgEncoder.setColorWhite(Color.WHITE);

            final Bitmap bitmap = qrgEncoder.getBitmap();
            // Setting Bitmap to ImageView

            holder.qrimage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Books.this);
                    ImageView imageView = new ImageView(Books.this);
                    ViewGroup.LayoutParams params  = new ViewGroup.LayoutParams(400,400);
                    imageView.setLayoutParams(params);
                    imageView.setImageBitmap(bitmap);
                    builder.setView(imageView);
                    builder.show();
                }
            });

        }

        @Override
        public int getItemCount() {
            return Global.documentData.savedFileModal.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            ImageView preview;
            ImageButton qrimage;
            Button download;
            TextView name, uploaded;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                qrimage = itemView.findViewById(R.id.books_recycler_imagebutton);
                download = itemView.findViewById(R.id.books_recycler_download);
                name = itemView.findViewById(R.id.books_recycler_name);
                uploaded = itemView.findViewById(R.id.books_recycler_uploaded);
            }
        }
    }


    StorageReference mStorageRef;
    DatabaseReference databaseReference;
    RecyclerView rv;

    static RecyclerViewAdapter adapter;

    Integer STORAGE_WRITE_REQUEST_CODE = 100;
    Integer STORAGE_READ_REQUET_CODE = 100;
    FloatingActionButton upload;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books);
        Toolbar toolbar = findViewById(R.id.books_toolbar);
        setSupportActionBar(toolbar);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(Books.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_WRITE_REQUEST_CODE);
        }
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(Books.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_READ_REQUET_CODE);
        }

        mStorageRef = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference(Global.documentData.userInfo.uid);
        //Toast.makeText(this, Global.documentData.userinfo.uid, Toast.LENGTH_SHORT).show();

        //Toolbar


        //Refrencing objects
        rv = findViewById(R.id.books_rv);

        upload = findViewById(R.id.books_add);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogAddBook dialog = new DialogAddBook(Books.this);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                dialog.show(ft,"hello");
            }
        });

        if(Global.documentData.savedFileModal == null){
            Global.documentData.savedFileModal = new ArrayList<>();
        }

        rv.setLayoutManager(new LinearLayoutManager(Books.this));
        adapter = new RecyclerViewAdapter();
        rv.setAdapter(adapter);

        //Refrencing the object
        if(Global.documentData.savedFileModal == null){
            Global.documentData.savedFileModal = new ArrayList<>();
        }

    }





    public void uploadPDFFile(Uri data) {
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();
        String location_file=data.getPath();


         final FileModal upload_fileModal =new FileModal(Global.documentData.userInfo.name,DialogAddBook.nameString,System.currentTimeMillis(),data.getPath());
        //String filext=location_file.substring(location_file.lastIndexOf("."));
        //Toast.makeText(this, ""+data, Toast.LENGTH_LONG).show();
        //System.out.println(data);
        StorageReference reference=mStorageRef.child(Global.documentData.userInfo.uid+"/Books/"+ upload_fileModal.id+".pdf");
        reference.putFile(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uri=taskSnapshot.getStorage().getDownloadUrl();
                        while(!uri.isComplete());
                        Uri url=uri.getResult();
                        uploadPDF uploadPDF=new uploadPDF(url.toString());
                        databaseReference.child(databaseReference.push().getKey()).setValue(uploadPDF);
                        Toast.makeText(Books.this, "FileModal Uploaded", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();

                        Global.documentData.savedFileModal.add(upload_fileModal);
                        Global.userRef.update("savedFileModal",Global.documentData.savedFileModal);
                        adapter.notifyDataSetChanged();


                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

                double progress=(100.0*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                progressDialog.setMessage("Uploaded: "+(int)progress+"%");
            }
        });

    }
    private void viewAllFiles(Long id){
        StorageReference listRef = mStorageRef.child(Global.documentData.userInfo.uid+"/Books/");

        listRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference item : listResult.getItems()) {
                            int flag=0;
                            //System.out.println(FileModal.arr_fileModal.toString());
                            for(int i = 0; i< FileModal.arr_fileModal.size(); i++){
                                if(item.getName().substring(0,item.getName().lastIndexOf('.')-1).equals(((Long) FileModal.arr_fileModal.get(i).id).toString())){
                                    flag++;
                                }
                            }
                            if(flag==0){
                                getFileUrl(item);
                                FileModal down_fileModal =new FileModal();
                                down_fileModal.id=Long.valueOf(item.getName().substring(0,item.getName().lastIndexOf('.')-1));
                                System.out.println(down_fileModal.id);
                                down_fileModal.path=DIRECTORY_DOWNLOADS;
                                down_fileModal.uploaded = Global.documentData.userInfo.name;
                                down_fileModal.name = "om";
                                Global.documentData.savedFileModal.add(down_fileModal);
                                Global.userRef.update("savedFileModal",Global.documentData.savedFileModal);
                            }

                            //Opening FileModal
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Uh-oh, an error occurred!
                    }
                });
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
        int flag = 0 ,idx=0;
        for(int i = 0; i< Global.documentData.savedFileModal.size(); i++){
            if(ref.getName().substring(0,ref.getName().lastIndexOf('.')).equals(((Long)Global.documentData.savedFileModal.get(i).id).toString())){
                flag++;idx=i;
            }
        }
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
            Toast.makeText(this, "FileModal Already Exists", Toast.LENGTH_SHORT).show();
            String s="/storage/emulated/0/Android/data/com.example.collegemate/files/Download";
            /*File file = new File(s+"/"+Global.documentData.savedFileModal.get(idx).id+".pdf");
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);*/
            File file =new File(s+"/"+Global.documentData.savedFileModal.get(idx).id+".pdf");
            System.out.println(file.exists()+"~~~~~~~~~"+file.getAbsolutePath());
            loadDocInReader(s+"/"+Global.documentData.savedFileModal.get(idx).id+".pdf");

            return;
        }
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String url =uri.toString();
                downloadFile(Books.this,name.substring(0,name.lastIndexOf('.')),name.substring(name.lastIndexOf('.')),DIRECTORY_DOWNLOADS,url);
            }
        });
    }


    private void loadDocInReader(String doc)
             {

         {
            Intent intent = new Intent();

            intent.setPackage("cn.wps.moffice_eng");
            //intent.setPackage("com.adobe.reader");
             intent.setDataAndType(Uri.parse(doc), "application/pdf");

            startActivity(intent);

        }
    }


    private View.OnClickListener downloadEvent = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
