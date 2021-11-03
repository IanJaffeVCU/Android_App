package com.example.uRemIndMe;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.android.material.textfield.TextInputEditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import static android.app.Activity.RESULT_OK;

public class DialogAddBook extends DialogFragment {

    TextInputEditText name;
    ImageButton upload;
    ImageView preview;
    Books b ;

    static String nameString;

    DialogAddBook(Books b){
        this.b = b;
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.dialog_add_book,container,false);

        //Setting up the toolbar

        Toolbar toolbar = rootview.findViewById(R.id.dialog_add_book_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });




        //Refrencin objects

        name = rootview.findViewById(R.id.dialog_add_book_name);
        upload = rootview.findViewById(R.id.dialog_add_book_upload);
        preview = rootview.findViewById(R.id.dialog_add_books_image_preview);


        upload.setOnClickListener(uploadCallback);

        return rootview;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1 && resultCode==RESULT_OK
                && data!=null && data.getData()!=null){
            b.uploadPDFFile(data.getData());
            getDialog().dismiss();
        }
    }





    @Override
    public void onStart() {
        super.onStart();

        int height = ViewGroup.LayoutParams.MATCH_PARENT;
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setLayout(height,width);
    }

    private View.OnClickListener uploadCallback = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            nameString = name.getText().toString();
            Intent intent=new Intent();
            intent.setType("application/pdf");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,"Select PDF FILE"),1);


        }
    };


}
