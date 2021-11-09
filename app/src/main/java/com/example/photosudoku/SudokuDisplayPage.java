package com.example.photosudoku;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;

public class SudokuDisplayPage extends AppCompatActivity {

    TextView textView;
    TableLayout table;
    ConstraintLayout mainLayout;

    public static final String SUDOKU_KEY = "sudoku";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sudoku_display_page);

        //textView = (TextView)findViewById(R.id.sudokuTextView);
        table = (TableLayout)findViewById(R.id.sudokuTable);
        mainLayout = (ConstraintLayout)findViewById(R.id.sudokuDisplayLayout);

        Intent intent = getIntent();

//        Bitmap bmp = (Bitmap)intent.getParcelableExtra(CameraPage.BitmapKey);
//        imageView.setImageBitmap(bmp);

        int[][] sudoku = (int[][])intent.getSerializableExtra(SUDOKU_KEY);
        createSudokuUI(sudoku);



        //String str = arrayToString(sudoku);
        //textView.setText(str);
    }

    private String arrayToString(int[][] array){
        StringBuilder sb = new StringBuilder();
        for (int[] row : array){
            for (int col : row){
                if (col == 0){
                    sb.append("_");
                }
                else{
                    sb.append(col);
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private void createSudokuUI(int[][] sudoku){
        TableRow.LayoutParams rowParams = new TableRow.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT,0.11f);
        for (int row = 0; row < 9; row++){
            TableRow tableRow = new TableRow(this);
            table.addView(tableRow);
            for (int col = 0; col < 9; col++){
                EditText cell = new EditText(this);
                cell.setEms(1);
                cell.setInputType(InputType.TYPE_CLASS_NUMBER);
                cell.setLayoutParams(rowParams);
                cell.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                cell.setMaxEms(1);

                cell.setBackground(getCellBorder(row,col));

                tableRow.addView(cell);

                if (sudoku[row][col] != 0){
                    cell.setText(String.valueOf(sudoku[row][col]));
                }
            }
        }
    }

    private Drawable getCellBorder(int row, int col){
        int rowRem = row % 3;
        int colRem = col % 3;

        if (rowRem == 0){
            if (colRem == 0) return ContextCompat.getDrawable(this,R.drawable.border_top_left);
            else if (colRem == 1) return ContextCompat.getDrawable(this,R.drawable.border_top);
            else if (colRem == 2) return ContextCompat.getDrawable(this,R.drawable.border_top_right);
        }
        else if (rowRem == 1){
            if (colRem == 0)  return ContextCompat.getDrawable(this,R.drawable.border_left);
            else if (colRem == 1)  return ContextCompat.getDrawable(this,R.drawable.border_middle);
            else if (colRem == 2)  return ContextCompat.getDrawable(this,R.drawable.border_right);
        }
        else if (rowRem == 2){
            if (colRem == 0) return ContextCompat.getDrawable(this,R.drawable.border_bottom_left);
            else if (colRem == 1) return ContextCompat.getDrawable(this,R.drawable.border_bottom);
            else if (colRem == 2) return ContextCompat.getDrawable(this,R.drawable.border_bottom_right);
        }

        return ContextCompat.getDrawable(this,R.drawable.thin_cell_border);
    }
}