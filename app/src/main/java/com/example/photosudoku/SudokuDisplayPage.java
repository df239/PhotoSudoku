package com.example.photosudoku;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sudoku_display_page);

        //textView = (TextView)findViewById(R.id.sudokuTextView);
        table = (TableLayout)findViewById(R.id.sudokuTable);
        mainLayout = (ConstraintLayout)findViewById(R.id.cameraPageLayout);

        Intent intent = getIntent();

//        Bitmap bmp = (Bitmap)intent.getParcelableExtra(CameraPage.BitmapKey);
//        imageView.setImageBitmap(bmp);

        int[][] sudoku = (int[][])intent.getSerializableExtra(CameraPage.SudokuKey);
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
        Drawable thinBg = ContextCompat.getDrawable(this,R.drawable.thin_cell_border);
        for (int row = 0; row < 9; row++){
            TableRow tableRow = (TableRow)table.getChildAt(row);
            for (int col = 0; col < 9; col++){

                EditText cell = (EditText)tableRow.getChildAt(col);
                cell.setBackground(thinBg);
//                cell.setEms(1);
//                cell.setInputType(InputType.TYPE_CLASS_NUMBER);
//                cell.setLayoutParams(new TableLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT,0.11f));
//                cell.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
//                cell.setMaxEms(1);

                if (sudoku[row][col] != 0){
                    cell.setText(String.valueOf(sudoku[row][col]));
                }
            }
        }
        table.setBackground(ContextCompat.getDrawable(this,R.drawable.thick_cell_border));
    }
}