package sudoku;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class store data structure for the board.
 * @author One
 */
public class SudokuBoard {

    private boolean debug = true;
    private SudokuField[][] board;

    public SudokuBoard() {

        // initialize the board, fill zero inside
        board = new SudokuField[9][9];

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                board[i][j] = new SudokuField(0, i + j * 9);
            }
        }
    }

    /**
     * Reset all the fields to zero
     */
    public void clear() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                board[i][j].clear();
            }
        }
    }

    private void debug(String s) {
        if (debug) {
            System.out.println(s);
        }
    }

    private void debugArrayList(ArrayList list) {
        if (debug) {
            String output = "";
            for (int i = 0; i < list.size(); i++) {
                output += list.get(i) + ", ";
            }
            System.out.println(output);
        }
    }

    public SudokuField[][] getBoard() {
        return board;
    }

    public void setBoard(SudokuField[][] board) {
        this.board = board;
    }

    public void setNumber(int x, int y, int number) {
        if (number >= 0 && number <= 9) {
            board[x][y].setValue(number);
        }
    }

    public int getNumber(int x, int y) {
        return board[x][y].getValue();
    }

    /**
     * Returns SudokuField at given coordinates
     * @param x
     * @param y
     * @return
     */
    public SudokuField getValue(int x, int y) {
        return board[x][y];
    }

    /**
     * Get allowed numbers for a single cell
     * @param position modulo 81
     * @return ArrayList
     */
    public ArrayList getAllowedNumbers(int position){
        int x = position / 9;
        int y = position % 9;
        return getAllowedNumbers(x, y);
    }

    /**
     * Allowed numbers for a single cell
     * @param x
     * @param y
     * @return
     */
    public ArrayList getAllowedNumbers(int x, int y) {
        ArrayList possibleVars = new ArrayList();
        ArrayList foundVars = new ArrayList();

        // return empty list if there is number already
        if (board[x][y].getValue() > 0) {
            return foundVars;
        }

        // add possible numbers
        for (int k = 1; k <= 9; k++) {
            possibleVars.add(k);
        }

        // check row and col
        for (int i = 0; i < 9; i++) {
            if (board[x][i].isInArayList(possibleVars) && !board[x][i].isEmpty()) {
                foundVars.add(board[x][i].getValue());
                //System.out.println("Found " + board[x][i] + " in a row");
            }
            if (board[i][y].isInArayList(possibleVars) && !board[i][y].isEmpty()) {
                foundVars.add(board[i][y].getValue());
                //System.out.println("Found " + board[i][y] + " in a column");
            }

        }

        // check the square
        for (int i = x - x % 3; i < x - x % 3 + 3; i++) {
            for (int j = y - y % 3; j < y - y % 3 + 3; j++) {
                if (board[i][j].isInArayList(possibleVars) && (x != i || y != j) && !board[i][j].isEmpty()) {
                    foundVars.add(board[i][j].getValue());
                }
            }
        }
        possibleVars.removeAll(foundVars);
        return possibleVars;
    }

    /**
     * Update all the values in row, column and square
     * This method will remove not valid candidates
     * TODO: Has some issues
     * @param x
     * @param y
     */
    public void updateAllowedNumbersEverywhere(int x, int y) {
        ArrayList candidates;
        for (int i = 0; i < 9; i++) {
            candidates = getAllowedNumbers(x, i);
            board[x][i].setCandidates(candidates);
            candidates = getAllowedNumbers(i, y);
            board[i][y].setCandidates(candidates);
        }

        // check square
        for (int i = x - x % 3; i < x - x % 3 + 3; i++) {
            for (int j = y - y % 3; j < y - y % 3 + 3; j++) {
                candidates = getAllowedNumbers(i, j);
                board[i][j].setCandidates(candidates);
            }
        }
    }

    /**
     * Remove the candidate from given square, row and column after it is added
     * to the current field at x,y
     * @param x
     * @param y
     * @param candidate 
     */
    public void removeCandidateEverywhere(int x, int y, int candidate) {
        // check row and col
        for (int i = 0; i < 9; i++) {
            board[x][i].removeCandidate(candidate);
            board[i][y].removeCandidate(candidate);
        }

        // check square
        for (int i = x - x % 3; i < x - x % 3 + 3; i++) {
            for (int j = y - y % 3; j < y - y % 3 + 3; j++) {
                board[i][j].removeCandidate(candidate);
            }
        }
    }

    /**
     * Is the value placed at this position corresponding the sudoku rules?
     * @param x
     * @param y
     * @return 
     */
    public boolean isValid(int x, int y) {
        int val = board[x][y].getValue();
        for (int i = 0; i < 9; i++) {
            if (board[x][i].getValue() == val && i != y) {
                return false;
            }
            if (board[i][y].getValue() == val && i != x) {
                return false;
            }
        }

        for (int i = x - x % 3; i < x - x % 3 + 3; i++) {
            for (int j = y - y % 3; j < y - y % 3 + 3; j++) {
                if (board[i][j].getValue() == val && (i != x || j != y)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Returns number of filled fields
     * @return
     */
    public int getNumberOfIndicies() {
        int indicies = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (!board[i][j].isEmpty()) {
                    indicies++;
                }
            }
        }
        return indicies;
    }

    /**
     * Create a deep copy of the whole board and all its fields
     * @return 
     */
    @Override
    public SudokuBoard clone() {
        SudokuBoard newBoard = new SudokuBoard();
        SudokuField[][] arrayBoard = new SudokuField[9][9];
        try {
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    arrayBoard[i][j] = (SudokuField) board[i][j].clone();
                }
            }
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(SudokuBoard.class.getName()).log(Level.SEVERE, null, ex);
        }
        newBoard.setBoard((SudokuField[][]) arrayBoard);
        return newBoard;
    }

    /**
     * Generate list of possibilities for each field
     */
    public void generateAllPossibleCandidates() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                board[i][j].setCandidates(getAllowedNumbers(i, j));
            }
        }
    }

    /**
     * This methods adds possible candidates to particular field
     * @param i
     * @param j
     */
    public void updateCandidates(int i, int j) {

        board[i][j].setCandidates(getAllowedNumbers(i, j));
        // should update candidates also for column, row and square
        System.out.println("Updating possibilites for " + i + " " + j + " which is " + board[i][j].getSizeOfCandidates());
    }

    /**
     * If there is only one candidate on this position, remove it
     * and update other possible solutions.
     * This method only removes possible candidates.
     * @param i
     * @param j
     * @return false - no change
     */
    public boolean checkCandidates(int i, int j) {

        if (board[i][j].getSizeOfCandidates() == 1) {
            board[i][j].setValue(board[i][j].getCandidate(0));

            board[i][j].removeCandidate(board[i][j].getValue());
            removeCandidateEverywhere(i, j, board[i][j].getValue());

            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        String output = "";
        String candidates = "";
        for (int i = 0; i < 9; i++) {
            candidates = "   ";
            for (int j = 0; j < 9; j++) {
                if (board[j][i].isEmpty()) {
                    output += " -";
                } else {
                    output += " " + board[j][i].toString();
                }
                candidates += " " + board[j][i].getSizeOfCandidates();
            }
            output += candidates;
            output += System.getProperty("line.separator");
        }
        return output;
    }

    public boolean isFullBoard() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board[j][i].isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isEmptyBoard() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (!board[j][i].isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Basic solving technique looking if there is only one option how to place
     * a number
     * @param x
     * @param y
     * @return
     */
    public boolean findSinglePosition(int x, int y) {

        ArrayList<Integer> candidates = board[x][y].getCandidates();
        ArrayList<Integer> foundInRows = new ArrayList();
        ArrayList<Integer> found = new ArrayList();
        for (int candidate : candidates) {

            // check rows
            for (int step = 0; step < 9; step += 3) {
                if (x == step + 0) {
                    if (isValueInRow(candidate, step + 1) && isValueInRow(candidate, step + 2)) {
                        foundInRows.add(candidate);
                    }
                }
                if (x == step + 1) {
                    if (isValueInRow(candidate, step + 0) && isValueInRow(candidate, step + 2)) {
                        foundInRows.add(candidate);
                    }
                }
                if (x == step + 2) {
                    if (isValueInRow(candidate, step + 0) && isValueInRow(candidate, step + 1)) {
                        foundInRows.add(candidate);
                    }
                }
            }
        }
        // check columns
        for (int candidate : foundInRows) {
            for (int step = 0; step < 9; step += 3) {
                if (x == step + 0) {
                    if (isValueInCol(candidate, step + 1) && isValueInCol(candidate, step + 2)) {
                        found.add(candidate);
                    }
                }
                if (x == step + 1) {
                    if (isValueInCol(candidate, step + 0) && isValueInCol(candidate, step + 2)) {
                        found.add(candidate);
                    }
                }
                if (x == step + 2) {
                    if (isValueInCol(candidate, step + 0) && isValueInCol(candidate, step + 1)) {
                        found.add(candidate);
                    }
                }
            }
        }

        if (found.size() > 0) {
            for (Integer val : found) {
                board[x][y].setValue(val);
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * return true if the value is found in a given row
     * @param value
     * @param row starting from 0
     * @return
     */
    public boolean isValueInRow(int value, int row) {
        for (int i = 0; i < 9; i++) {
            if (board[i][row].getValue() == value) {
                return true;
            }
        }
        return false;
    }

    /**
     * return true if value is found in a given column
     * @param value
     * @param row starting from 0
     * @return
     */
    public boolean isValueInCol(int value, int col) {
        for (int i = 0; i < 9; i++) {
            if (board[col][i].getValue() == value) {
                return true;
            }
        }
        return false;
    }

    /**
     * Execute candidate lines on 3x3 square
     * find two in line and delete colliding candidates
     * @param x left top corner
     * @param y left top corner
     * @return
     */
    public boolean solveCandidateLinesInSquare(int x, int y) {
        //debug("--- Solving using candidate lines on " + x + " " + y);

        // put squares into arrays (numbers from zero to eight in 9x9 block)

        // array where candidate can't be found
        int[][] b = {
            {4, 5, 7, 8},
            {3, 5, 6, 8},
            {3, 4, 6, 7},
            {1, 2, 7, 8},
            {0, 2, 6, 8},
            {0, 1, 6, 7},
            {1, 2, 4, 5},
            {0, 2, 3, 5},
            {0, 1, 3, 4}
        };

        // good array
        // first row than col
        int[][] g = {
            {1, 2, 3, 6},
            {0, 2, 4, 7},
            {0, 1, 5, 8},
            {4, 5, 0, 6},
            {3, 5, 1, 7},
            {3, 4, 2, 8},
            {7, 8, 0, 3},
            {6, 8, 1, 4},
            {6, 7, 2, 5}
        };

        boolean foundInBad = false;
        boolean foundInGood = false;
        boolean foundInRow = false;

        // there was at least one change
        boolean found = false;

        // position where it was found
        int foundx, foundy;

        ArrayList<Integer> candidates;

        SudokuField field1, field2, field3, field4;

        int lx, ly;

        // first one
        for (int field = 0; field < 9; field++) {
            lx = field % 3;
            ly = field / 3;
            candidates = board[x + lx][y + ly].getCandidates();
            //debug("Checking field " + (x + lx) + " " + (y + ly));
            for (int candidate : candidates) {
                //debug("Searching for candidate " + candidate);

                foundInBad = false;
                // all bad zones
                //for (int i = 0; i < b.length; i++) {
                // all squares in zone
                for (int j = 0; j < 4; j++) {
                    // search in the bad zone
                    //debug("Searching in bad zone " + (x + b[field][j] % 3) + " " + (y + b[field][j] / 3));
                    if (board[x + b[field][j] % 3][y + b[field][j] / 3].containsCandidate(candidate)) {
                        //debug(" - field already contains candidate " + candidate);
                        //debugArrayList(board[x + b[field][j] % 3][y + b[field][j] / 3].getCandidates());
                        foundInBad = true;
                    }
                }
                //}
                //debug("Found in bad area: " + foundInBad);
                if (!foundInBad) {
                    foundInGood = false;
                    foundx = -1;
                    foundy = -1;

                    // search good zones
                    // for (int i = 0; i < 4; i++) {
                    // all squares in zone
                    // has to be found only in col or only in row
                    field1 = board[x + g[field][0] % 3][y + g[field][0] / 3];
                    field2 = board[x + g[field][1] % 3][y + g[field][1] / 3];
                    field3 = board[x + g[field][2] % 3][y + g[field][2] / 3];
                    field4 = board[x + g[field][3] % 3][y + g[field][3] / 3];

                    // row
                    if ((field1.containsCandidate(candidate) || field2.containsCandidate(candidate)) && (!field3.containsCandidate(candidate) || !field4.containsCandidate(candidate))) {
                        foundInGood = true;
                        foundInRow = true;
                        foundx = x + g[field][0] % 3;
                        foundy = y + g[field][0] / 3;
                        //debug("Found positive in row: " + foundx + " " + foundy);
                    }

                    // col
                    if ((!field1.containsCandidate(candidate) || !field2.containsCandidate(candidate)) && (field3.containsCandidate(candidate) || field4.containsCandidate(candidate))) {
                        foundInGood = true;
                        foundInRow = false;
                        foundx = x + g[field][3] % 3;
                        foundy = y + g[field][3] / 3;
                        //debug("Found positive in col: " + foundx + " " + foundy);
                    }

                    // }
                    // remove candidate from other fields
                    if (foundInGood) {
                        if (foundInRow) {
                            for (int i = 0; i < 9; i++) {
                                if (i < x || i > x + 2) {
                                    //debug("Removing candidate " + candidate + " from " + i + " " + foundy);
                                    board[i][foundy].removeCandidate(candidate);


                                }
                            }
                        } else {
                            // column
                            for (int i = 0; i < 9; i++) {
                                if (i < y || i > y + 2) {
                                    //debug("Removing candidate " + candidate + " from " + foundx + " " + i);
                                    board[foundx][i].removeCandidate(candidate);

                                }
                            }
                        }
                        found = true;
                    }
                }


            }

        }
        return found;
    }

    public boolean solveNakedPairsRow(int row) {
        ArrayList<Integer> pairs = new ArrayList();

        int a1, b1, a2, b2;
 
        // find pairs
        for (int i = 0; i < 9; i++) {
            if (board[i][row].getSizeOfCandidates() == 2) {
                pairs.add(i);
            }
        }

        // find matching pairs
        for (int i = 0; i < pairs.size() - 1; i++) {

            a1 = board[pairs.get(i)][row].getCandidate(0);
            a2 = board[pairs.get(i)][row].getCandidate(1);

            for (int j = i + 1; j < pairs.size(); j++) {

                b1 = board[pairs.get(j)][row].getCandidate(0);
                b2 = board[pairs.get(j)][row].getCandidate(1);
                if ((a1 == b1 && a2 == b2) || (a1 == b2 && a2 == b1)) {
                    // found naked pair
                    for (int k = 0; k < 9; k++) {
                        if (k != pairs.get(i) && k != pairs.get(j)) {
                            board[k][row].removeCandidate(a1);
                            board[k][row].removeCandidate(a2);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean solveNakedPairsCol(int col) {
        ArrayList<Integer> pairs = new ArrayList();
        int a1, b1, a2, b2;

        // find pairs
        for (int i = 0; i < 9; i++) {
            if (board[col][i].getSizeOfCandidates() == 2) {
                pairs.add(i);
            }
        }

        // find matching pairs
        for (int i = 0; i < pairs.size() - 1; i++) {
            a1 = board[col][pairs.get(i)].getCandidate(0);
            a2 = board[col][pairs.get(i)].getCandidate(1);
            for (int j = i + 1; j < pairs.size(); j++) {

                b1 = board[col][pairs.get(j)].getCandidate(0);
                b2 = board[col][pairs.get(j)].getCandidate(1);
                if ((a1 == b1 && a2 == b2) || (a1 == b2 && a2 == b1)) {
                    // found naked pair
                    for (int k = 0; k < 9; k++) {
                        if (k != pairs.get(i) && k != pairs.get(j)) {
                            board[col][k].removeCandidate(a1);
                            board[col][k].removeCandidate(a2);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean findHiddenPairs(ArrayList<SudokuField> fields) {
        ArrayList<SudokuField>[] found;
        SudokuField field;
        SudokuField nakedPair = null;

        
        // create
        for (int i = 0; i < fields.size(); i++) {
            field = fields.get(i);
      
            found = new ArrayList[field.getSizeOfCandidates()];
            for (int c = 0; c < field.getSizeOfCandidates(); c++) {
                found[c] = new ArrayList();
                for (int j = 0; j < fields.size(); j++) {
                    if (j != i && fields.get(j).containsCandidate(field.getCandidates().get(c))) {
                        found[c].add(fields.get(j));
                    }
                }
            }

            // check array for hidden pairs
            nakedPair = null;
            for (int j = 0; j < found.length; j++) {
                if (found[j].size() == 1) {
                    if (nakedPair != null) {

                        if (nakedPair.getPosition() == found[j].get(0).getPosition()) {

                            // remove additional value(s)
                            SudokuField a, b;
                            a = nakedPair;
                            b = field;
                            if (a.getSizeOfCandidates() < b.getSizeOfCandidates()) {
                                b = nakedPair;
                                a = field;
                            }
                            for (int k = 0; k < a.getSizeOfCandidates(); k++) {
                                if (!b.containsCandidate(a.getCandidate(k))) {
                                    //debug("Removing candidate " + a.getCandidate(k));
                                    a.removeCandidate(a.getCandidate(k));
                                }
                            }
                            return true;
                        }
                    } else {
                        
                        nakedPair = found[j].get(0);
                    }
                }
            }
        }
        return false;
    }

    /** 
     * once it is produced, we want to set empty fields to be filled by users 
     */
    public void markGeneratedFields(){
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if ((board[i][j]).getValue() == 0){
                    (board[i][j]).generated = false;
                }else{
                    (board[i][j]).generated = true;
                }
            }
        }
    }
}
