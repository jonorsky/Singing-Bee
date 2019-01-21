package com.frostflux.www.minimalquiz;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class First_Page extends AppCompatActivity {

    Button mStartButton;

    // POPUP
    Dialog epicDialog;
    Button positivePopupBtn, btnAccept;
    TextView titleTv, messageTv;
    ImageView closePopupPositiveImg;
    // POPUP

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Animate Start
        ImageView bgone = (ImageView) findViewById(R.id.bgone);
        bgone.animate().scaleX((float) 1.7).scaleY((float) 1.7).setDuration(5000).start();

        // Animate End

        //POPUP
        epicDialog = new Dialog(this);

        positivePopupBtn = (Button) findViewById(R.id.startButton1);
        positivePopupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowPositivePopup();
            }
        });
        //POPUP



        mStartButton = (Button) findViewById(R.id.startButton);
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(First_Page.this, God_File.class));
            }
        });
    }


    // Function Starts
    public void ShowPositivePopup(){
        epicDialog.setContentView(R.layout.epic_popout_positive);
        closePopupPositiveImg = (ImageView) epicDialog.findViewById(R.id.closePopupPositiveImg);
        btnAccept =     (Button)    epicDialog.findViewById(R.id.btnAccept);
        titleTv =       (TextView)  epicDialog.findViewById(R.id.titleTv);
        messageTv =     (TextView)  epicDialog.findViewById(R.id.message);

        closePopupPositiveImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                epicDialog.dismiss();
            }
        });

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                epicDialog.dismiss();
            }
        });

        epicDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        epicDialog.show();
    }

}
