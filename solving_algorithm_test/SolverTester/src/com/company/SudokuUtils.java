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

    public static int[][] EXAMPLE3 = {{0,0,0,4,0,0,0,0,5},
                                    {4,6,0,0,2,0,0,0,0},
                                    {3,0,5,8,0,0,0,0,9},
                                    {0,0,9,0,0,5,0,0,8},
                                    {6,0,0,0,0,0,0,0,2},
                                    {1,0,0,2,0,0,7,0,0},
                                    {2,0,0,0,0,8,5,0,3},
                                    {0,0,0,0,7,0,0,8,1},
                                    {9,0,0,0,0,2,0,0,0}};

    public static int[][] EXAMPLE4 = {{0,0,0,0,0,0,0,1,2},
                                    {5,0,0,6,0,0,0,0,0},
                                    {2,0,0,0,0,0,0,3,0},
                                    {0,0,0,5,0,0,8,0,0},
                                    {0,0,1,0,0,0,0,0,0},
                                    {0,4,0,0,0,0,0,0,0},
                                    {0,0,0,4,0,1,7,0,0},
                                    {8,6,0,0,0,0,5,0,0},
                                    {0,0,0,0,3,0,0,0,0}};

    public static int[][] EXAMPLE5 = {{0,0,7,1,5,0,0,0,6},
                                    {0,1,0,4,0,0,7,9,5},
                                    {0,8,6,0,7,0,0,0,2},
                                    {0,4,0,0,0,0,0,0,7},
                                    {9,0,1,0,4,0,0,3,0},
                                    {0,0,3,0,0,0,9,0,0},
                                    {1,0,0,2,3,7,8,0,9},
                                    {7,5,8,0,1,0,0,0,0},
                                    {0,0,9,5,0,4,6,0,0}};

    public static int[][] EXAMPLE6 = {{0,0,0,6,0,3,0,0,0},
                                    {0,3,0,0,1,0,0,5,0},
                                    {0,0,9,0,0,0,2,0,0},
                                    {7,0,0,1,0,6,0,0,9},
                                    {0,2,0,0,0,0,0,8,0},
                                    {1,0,0,4,0,9,0,0,3},
                                    {0,0,8,0,0,0,1,0,0},
                                    {0,5,0,0,9,0,0,7,0},
                                    {0,0,0,7,0,4,0,0,0}};

    public static int[][] EXAMPLE7 = {{0,0,8,0,1,0,0,0,9},
                                    {6,0,1,0,9,0,3,2,0},
                                    {0,4,0,0,3,7,0,0,5},
                                    {0,3,5,0,0,8,2,0,0},
                                    {0,0,2,6,5,0,8,0,0},
                                    {0,0,4,0,0,1,7,5,0},
                                    {5,0,0,3,4,0,0,8,0},
                                    {0,9,7,0,8,0,5,0,6},
                                    {1,0,0,0,6,0,9,0,0}};

    public static int[][] EXAMPLE8 = {{0,0,0,7,0,0,0,0,0},
                                    {1,0,0,0,0,0,0,0,0},
                                    {0,0,0,4,3,0,2,0,0},
                                    {0,0,0,0,0,0,0,0,6},
                                    {0,0,0,5,0,9,0,0,0},
                                    {0,0,0,0,0,0,4,1,8},
                                    {0,0,0,0,8,1,0,0,0},
                                    {0,0,2,0,0,0,0,5,0},
                                    {0,4,0,0,0,0,3,0,0}};

    public static int[][] EXAMPLE9 = {{0,3,0,0,0,5,0,0,0},
                                    {5,0,0,8,0,0,2,0,0},
                                    {0,0,6,0,2,1,0,7,0},
                                    {8,7,0,0,0,4,6,0,0},
                                    {0,6,0,0,0,0,0,3,0},
                                    {0,0,4,6,0,0,0,1,8},
                                    {0,4,0,1,9,0,7,0,0},
                                    {0,0,2,0,0,8,0,0,6},
                                    {0,0,0,4,0,0,0,2,0}};

    public static int[][] EXAMPLE10 = {{0,8,2,5,4,3,6,1,9},
                                    {0,0,0,0,9,0,0,3,0},
                                    {0,0,1,7,0,8,4,0,0},
                                    {0,0,0,0,0,2,0,0,0},
                                    {0,7,0,3,0,4,0,0,1},
                                    {0,0,0,0,0,0,3,6,7},
                                    {0,4,0,0,5,0,8,0,3},
                                    {1,0,0,0,0,0,0,7,0},
                                    {0,0,0,0,3,9,0,0,0}};

    //naked pair (3,9) in the first row - OK
    public static int[][] EXAMPLE111 = {{1,0,0,0,0,2,0,0,8},
                                        {0,0,0,0,0,0,0,0,0},
                                        {0,6,2,0,0,0,4,0,0},
                                        {5,0,0,7,9,0,0,6,0},
                                        {0,8,0,5,0,0,9,7,0},
                                        {0,0,0,0,0,0,0,0,0},
                                        {0,0,0,4,0,0,3,0,0},
                                        {0,0,0,0,0,0,0,5,0},
                                        {0,0,0,6,0,0,0,0,0}};

    //naked pair (3,9) in the first row plus cell of (6,7) in the first row - OK
    public static int[][] EXAMPLE112 = {{1,0,0,0,0,2,0,0,8},
                                        {0,0,0,0,0,0,0,0,5},
                                        {0,6,2,0,0,0,4,0,0},
                                        {5,0,0,7,9,0,0,6,0},
                                        {0,8,0,5,0,0,9,7,0},
                                        {0,0,0,0,0,0,0,0,0},
                                        {0,0,0,4,0,0,3,0,0},
                                        {0,0,0,0,0,0,0,5,0},
                                        {0,0,0,6,0,0,0,0,0}};

    //naked pairs (3,9) & (6,7) in the first row
    public static int[][] EXAMPLE113 = {{1,0,0,0,0,2,0,0,8},
                                        {0,0,0,0,0,0,0,0,5},
                                        {0,6,2,0,5,0,4,0,0},
                                        {5,0,0,7,9,0,0,6,0},
                                        {0,8,0,0,4,0,9,7,0},
                                        {0,0,0,0,0,0,0,0,0},
                                        {0,0,0,4,0,0,3,0,0},
                                        {0,0,0,0,3,0,0,5,0},
                                        {0,0,0,6,0,0,0,0,0}};

    //a single cell of two candidates (3,9) in the first row - OK
    public static int[][] EXAMPLE114 = {{1,0,0,0,0,2,0,0,8},
                                        {0,0,0,0,0,0,0,0,5},
                                        {0,6,2,0,0,0,4,0,0},
                                        {5,0,0,7,9,0,0,6,0},
                                        {0,8,0,0,0,0,0,7,0},
                                        {0,0,0,0,0,0,0,0,0},
                                        {0,0,0,4,0,0,3,0,0},
                                        {0,0,0,0,0,0,0,5,0},
                                        {0,0,0,6,0,0,0,0,0}};

    //cells of (3,9) & (6,7) in the first row - OK
    public static int[][] EXAMPLE115 = {{1,0,0,0,0,2,0,0,8},
                                        {0,0,0,0,0,0,0,0,5},
                                        {0,6,2,0,0,0,4,0,0},
                                        {5,0,0,7,9,0,0,6,0},
                                        {0,8,0,0,0,0,9,7,0},
                                        {0,0,0,0,0,0,0,0,0},
                                        {0,0,0,4,0,0,3,0,0},
                                        {0,0,0,0,0,0,0,5,0},
                                        {0,0,0,6,0,0,0,0,0}};

    //pair of (3,9) & a single cell of (3,4) in the first row - OK
    public static int[][] EXAMPLE116 = {{1,0,0,0,0,2,0,0,8},
                                        {0,0,0,0,7,6,0,0,5},
                                        {0,6,2,0,0,0,4,0,0},
                                        {5,0,0,7,9,0,0,6,0},
                                        {0,8,0,5,0,0,0,7,0},
                                        {0,0,0,0,0,0,0,0,0},
                                        {0,0,0,4,0,0,3,0,0},
                                        {0,0,0,0,0,0,0,5,0},
                                        {0,0,0,6,5,0,0,0,0}};
}
