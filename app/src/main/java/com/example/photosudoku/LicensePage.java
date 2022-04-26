package com.example.photosudoku;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class LicensePage extends AppCompatActivity {

    TextView apacheLink;
    TextView bsdLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license_page);

        apacheLink = findViewById(R.id.textViewApache);
        apacheLink.setMovementMethod(LinkMovementMethod.getInstance());
        apacheLink.setText(Html.fromHtml("<a href='https://www.apache.org/licenses/LICENSE-2.0.txt'>Apache License 2.0</a>"));

        bsdLink = findViewById(R.id.textViewBSD);
        bsdLink.setMovementMethod(LinkMovementMethod.getInstance());
        bsdLink.setText(Html.fromHtml("<a href='https://opensource.org/licenses/BSD-3-Clause'>BSD 3-Clause License</a>"));
    }
}