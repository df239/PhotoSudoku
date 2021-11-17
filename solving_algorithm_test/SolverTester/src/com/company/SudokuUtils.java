package com.company;

public class SudokuUtils {

    public static int getBox(int row, int col){
        if(row/3 == 0){
            if(col/3 == 0) return 0;
            else if(col/3 == 1) return 1;
            else return 2;
        }
        else if(row/3 == 1){
            if(col/3 == 0) return 3;
            else if(col/3 == 1) return 4;
            else return 5;
        }
        else{
            if(col/3 == 0) return 6;
            else if(col/3 == 1) return 7;
            else return 8;
        }
    }

    public static int[][] EXAMPLE1 = {{3,0,0,0,0,8,2,4,0},
                                    {0,0,0,6,3,0,0,5,9},
                                    {2,0,6,0,0,4,0,7,0},
                                    {0,0,1,0,0,0,0,2,7},
                                    {5,0,0,0,0,0,0,0,1},
                                    {6,9,0,0,0,0,5,0,0},
                                    {0,1,0,2,0,0,3,0,5},
                                    {8,6,0,0,4,3,0,0,0},
                                    {0,2,3,7,0,0,0,0,4}};

    public static int[][] EXAMPLE2 = {{0,0,8,0,1,0,0,0,9},
                                    {6,0,1,0,9,0,3,2,0},
                                    {0,4,0,0,3,7,0,0,5},
                                    {0,3,5,0,0,8,2,0,0},
                                    {0,0,2,6,5,0,8,0,0},
                                    {0,0,4,0,0,1,7,5,0},
                                    {5,0,0,3,4,0,0,8,0},
                                    {0,9,7,0,8,0,5,0,6},
                                    {1,0,0,0,6,0,9,0,0}};
}
