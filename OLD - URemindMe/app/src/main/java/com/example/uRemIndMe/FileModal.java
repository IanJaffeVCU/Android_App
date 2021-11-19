package com.example.uRemIndMe;

import java.util.ArrayList;

public class FileModal {
    public static ArrayList<FileModal> arr_fileModal =new ArrayList<>();
    public String path;
    public Long id;
    public String uploaded;
    public String name;
    public FileModal(){}

    public FileModal(String uploaded, String name, Long id , String path){
        this.id=id ;
        this.path=path;
        this.name = name;
        this.uploaded = uploaded;
    }

    public void add_file(){
        FileModal.arr_fileModal.add(this);
        Global.documentData.savedFileModal.add(this);
    }
    public static boolean checkPresent(String name){
        int flag=0;
        for(FileModal fi:Global.documentData.savedFileModal){
            if(name.equals(Long.toString(fi.id)))flag++;
        }
        if(flag>0)return true;
        return false;

    }
}
