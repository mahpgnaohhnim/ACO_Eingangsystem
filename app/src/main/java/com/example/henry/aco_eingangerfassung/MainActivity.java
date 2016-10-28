package com.example.henry.aco_eingangerfassung;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Button addChildBtn, addAdultBtn, removeChildBtn, removeAdultBtn, showListBtn, submitBtn, resetBtn, deleteLineBtn, scanQRBtn;
    TextView countChild, countAdult, childSum, adultSum, totalCount, totalSum;
    int currentAdultCount, currentChildCount;
    Spinner dropDown;
    EditText postalCode;
    FileCSV csvFilehandler = new FileCSV(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermission();

        countChild = (TextView) findViewById(R.id.countChild);
        countAdult = (TextView) findViewById(R.id.countAdult);
        childSum = (TextView) findViewById(R.id.childPrice);
        adultSum = (TextView) findViewById(R.id.adultPrice);
        totalCount = (TextView) findViewById(R.id.totalCount);
        totalSum = (TextView) findViewById(R.id.totalSum);

        postalCode = (EditText) findViewById(R.id.postalCode);

        addChildBtn = (Button) findViewById(R.id.addChild);
        addAdultBtn = (Button) findViewById(R.id.addAdult);
        removeChildBtn = (Button) findViewById(R.id.removeChild);
        removeAdultBtn = (Button) findViewById(R.id.removeAdult);
        //scanQRBtn = (Button) findViewById(R.id.scanBtn);
        showListBtn = (Button) findViewById(R.id.showListBtn);
        submitBtn = (Button) findViewById(R.id.submitBtn);
        resetBtn = (Button) findViewById(R.id.resetBtn);
        deleteLineBtn = (Button) findViewById(R.id.deleteLineBtn);

        dropDown = (Spinner) findViewById(R.id.dropdown);

        //dropDown.setOnItemSelectedListener(this);

        currentAdultCount = getCurrentNumByTextview(countAdult);
        currentChildCount = getCurrentNumByTextview(countChild);

        addChildBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                addChild();
                updateCounts();
            }
        });

        removeChildBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                removeChild();
                updateCounts();
            }
        });

        addAdultBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                addAdult();
                updateCounts();
            }
        });

        removeAdultBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                removeAdult();
                updateCounts();
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                submit();
            }
        });

        resetBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                resetStats();
            }
        });

        showListBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                showCSVList();
            }
        });

        deleteLineBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                deleteLine();
            }
        });

        /*scanQRBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
               callQRScanner();
            }
        });*/
    }

    private void submit(){
        String price = totalSum.getText().toString();
        String cause = dropDown.getSelectedItem().toString();
        String count = totalCount.getText().toString();
        if(count != "0"){

            csvFilehandler.countAdult = currentAdultCount;
            csvFilehandler.plz = postalCode.getText().toString();
            csvFilehandler.countChild = currentChildCount;
            csvFilehandler.sum = price;
            csvFilehandler.cause = cause;

            csvFilehandler.writeFile();

            resetStats();
        }
    }

    private void addChild(){
        currentChildCount++;
        countChild.setText(Integer.toString(currentChildCount));
    }

    private void removeChild(){
        currentChildCount--;
        if(currentChildCount < 0){
            currentChildCount = 0;
        }
        countChild.setText(Integer.toString(currentChildCount));
    }

    private void addAdult(){
        currentAdultCount++;
        countAdult.setText(Integer.toString(currentAdultCount));
    }

    private void removeAdult(){
        currentAdultCount--;
        if(currentAdultCount < 0){
            currentAdultCount = 0;
        }
        countAdult.setText(Integer.toString(currentAdultCount));
    }

    private void updateCounts(){
        int priceChild = 2;
        int priceAdult = 5;
        int totalChildPrice = currentChildCount*priceChild;
        int totalAdultPrice = currentAdultCount*priceAdult;
        int totalPrice = totalAdultPrice+totalChildPrice;
        int counts = currentChildCount + currentAdultCount;

        totalSum.setText(totalPrice + "€");
        childSum.setText(totalChildPrice + "€");
        adultSum.setText(totalAdultPrice + "€");
        totalCount.setText(Integer.toString(counts));
    }

    private int getCurrentNumByTextview(TextView target){
        int num = Integer.parseInt(target.getText().toString());
        return num;
    }

    private void resetStats(){
        currentAdultCount = 0;
        currentChildCount = 0;
        countChild.setText("0");
        countAdult.setText("0");
        postalCode.setText("");
        updateCounts();
    }

    private void checkPermission(){
        if(Build.VERSION.SDK_INT >= 23){
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                System.out.println("yeah");
            } else{
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
            if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                System.out.println("yeah");
            } else{
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
            }
        }else{
            Log.v("Permission", "Permission Granted");
        }
    }

    private void showCSVList(){
        Intent csvViever = csvFilehandler.showFile();
        startActivity(csvViever);
    }

    private void deleteLine(){
        AlertDialog.Builder alertDelete = new AlertDialog.Builder(this);
        alertDelete.setMessage(R.string.alertDeleteLineMessage)
                .setPositiveButton(R.string.alertDeleteLinePositive, new DialogInterface.OnClickListener(){

                    public void onClick(DialogInterface dialog, int id){
                        csvFilehandler.deleteLastLine();
                    }

                })
                .setNegativeButton(R.string.alertDeleteLineNegative, new  DialogInterface.OnClickListener(){
                   public void onClick(DialogInterface dialog, int id){
                       dialog.cancel();
                   }
                });

        AlertDialog alertDialog = alertDelete.create();
        alertDialog.show();

    }
/*
    private void callQRScanner(){
        Intent intentQRScanner = new Intent(this, ZBarScannerActivity.class);
        startActivity(intentQRScanner);
    }*/

}
