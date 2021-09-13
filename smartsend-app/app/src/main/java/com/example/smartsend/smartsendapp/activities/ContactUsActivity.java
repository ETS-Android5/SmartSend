package com.example.smartsend.smartsendapp.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartsend.smartsendapp.R;

import java.util.Calendar;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class ContactUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setImage(R.drawable.logo_title)
                .setDescription("Distribution System Made Easy")
                .addItem(new Element().setTitle("Version 1.0"))
                .addGroup("CONNECT WITH US!")
                .addGitHub("https://github.com/erezhasson/SmartSend", "GitHub")
                .addEmail("il.smartsend@gmail.com")
                .addWebsite("Your website/")
                .addYoutube("UCbekhhidkzkGryM7mi5Ys_w")
                .addPlayStore("com.example.smartsendapp.smartsend")
                .addItem(createCopyright())
                .create();
        setContentView(aboutPage);
    }

    private Element createCopyright()
    {
        Element copyright = new Element();
        @SuppressLint("DefaultLocale") final String copyrightString = String.format("All rights reserved SmartSend ©%d", Calendar.getInstance().get(Calendar.YEAR));
        copyright.setTitle(copyrightString);
        copyright.setGravity(Gravity.CENTER);
        copyright.setOnClickListener(v -> Toast.makeText(ContactUsActivity.this, copyrightString, Toast.LENGTH_SHORT).show());

        return copyright;
    }
}