package com.example.henry.aco_eingangerfassung;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.lang.String;



/**
 * Created by henry on 17.10.16.
 */

public class FileCSV {
    int countAdult, countChild;
    String header ="Postleitzahl;Kinder;Erwachsene;Preis;Datum;Grund\n";
    String plz, sum, timeStamp, cause;

    private Context context;


    String fileName = "Eintrag.csv";


    public FileCSV(Context context){
        this.context = context;
    }

    public void writeFile(){
        timeStamp = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(Calendar.getInstance().getTime());

        String content = plz + ";";
        content += countChild + ";";
        content += countAdult + ";";
        content += sum + ";";
        content += timeStamp + ";";
        content += cause + ";";
        content += "\n";


        try {
            File path = context.getExternalFilesDir(null);

            File file = new File(path, fileName); // external Storage
            FileWriter out = new FileWriter(file, true);
           if(file.length() == 0){
               out.append(header);
           }
            out.append(content);
            out.flush();
            out.close();
        }catch (IOException e){
            Log.e("Exception", "File write failed: " + e.toString());
        }


    }

    public Intent showFile(){
        Intent intent = new Intent();

        File path = context.getExternalFilesDir(null);
        File file = new File(path, fileName);

        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),getMimeType(file.getAbsolutePath()));
        return intent;
    }

    private String getMimeType(String url) {
        String parts[]=url.split("\\.");
        String extension=parts[parts.length-1];
        String type = null;
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public void deleteLastLine(){
        try{
            File path = context.getExternalFilesDir(null);
            File file = new File(path, fileName);
            RandomAccessFile f = new RandomAccessFile(file, "rw");
            byte b;
            long length = f.length() - 1;
            do {
                length -= 1;
                f.seek(length);
                b = f.readByte();
            } while(b != 10);
            f.setLength(length+1);
            f.close();
        }catch (IOException e){
            //Exception
        }

    }




}
