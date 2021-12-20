package com.example.photosudoku;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CandidateCell extends LinearLayout {
    private Context context;
    private int value = 0;
    private List<Integer> candidates;
    private boolean editMode;

    TextView topLeft;
    TextView top;
    TextView topRight;
    TextView left;
    TextView middle;
    TextView right;
    TextView bottomLeft;
    TextView bottom;
    TextView bottomRight;
    EditText valText;

    public CandidateCell(Context context, boolean editMode){
        super(context);
        this.context = context;
        this.candidates = new ArrayList<>();
        this.editMode = editMode;
        init();
    }

    private void init(){
        View view = inflate(context, R.layout.candidate_cell, this);
        topLeft = view.findViewById(R.id.candidate_top_left);
        top = view.findViewById(R.id.candidate_top);
        topRight = view.findViewById(R.id.candidate_top_right);
        left = view.findViewById(R.id.candidate_left);
        middle = view.findViewById(R.id.candidate_middle);
        right = view.findViewById(R.id.candidate_right);
        bottomLeft = view.findViewById(R.id.candidate_bottom_left);
        bottom = view.findViewById(R.id.candidate_bottom);
        bottomRight = view.findViewById(R.id.candidate_bottom_right);

        valText = view.findViewById(R.id.cellValueEditText);
        int padding_dp = 7;
        float scale = view.getResources().getDisplayMetrics().density;
        int padding_px = (int) (padding_dp * scale + 0.5f);
        valText.setPadding(0,padding_px,0,padding_px);
        if(this.editMode){
            valText.setTypeface(null, Typeface.BOLD);
        }
    }

    public void setValue(int value){
        this.value = value;
    }

    public int getValue(){
        return this.value;
    }

    public void addCandidates(Collection<Integer> candidates){
        this.candidates.addAll(candidates);
    }

    public List<Integer> getCandidates(){
        return this.candidates;
    }

    public void displayCell(){
        if(this.value != 0){
            this.valText.setText(String.valueOf(this.value));
        }
        else{
            for(int c : this.candidates){
                switch(c){
                    case 1:{
                        topLeft.setText("1");
                        break;
                    }
                    case 2:{
                        top.setText("2");
                        break;
                    }
                    case 3:{
                        topRight.setText("3");
                        break;
                    }
                    case 4:{
                        left.setText("4");
                        break;
                    }
                    case 5:{
                        middle.setText("5");
                        break;
                    }
                    case 6:{
                        right.setText("6");
                        break;
                    }
                    case 7:{
                        bottomLeft.setText("7");
                        break;
                    }
                    case 8:{
                        bottom.setText("8");
                        break;
                    }
                    case 9:{
                        bottomRight.setText("9");
                        break;
                    }
                }
            }
        }
    }
}
