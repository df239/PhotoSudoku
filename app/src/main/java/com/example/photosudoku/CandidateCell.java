package com.example.photosudoku;

import android.content.Context;
import android.widget.LinearLayout;

public class CandidateCell extends LinearLayout {
    private Context context;

    public CandidateCell(Context context){
        super(context);
        this.context = context;
    }
}
