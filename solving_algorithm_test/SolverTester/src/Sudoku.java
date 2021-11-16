
public class Sudoku {
	
	private int[][] grid;
    private int[][] solution;
    private Cell[][] sudoku;

    public Sudoku(int[][] sudokuGrid){
        grid = sudokuGrid;
        this.sudoku = buildSudoku(sudokuGrid);
    }

    private Cell[][] buildSudoku(int[][] grid){
        Cell[][] matrix = new Cell[grid.length][grid.length];
        for(int x = 0; x < grid.length; x++){
            for(int y = 0; y < grid.length; y++){
                matrix[x][y] = new Cell(grid[x][y],x,y);
            }
        }
        return matrix;
    }
}
