
package sudoku;

import java.util.ArrayList;
import java.util.Random;

/**
 * Contains a couple of methods used to solve the board.
 * @author Ondrej Nebesky
 */
public class SudokuLogic {

    /** board with missing numbers */
    private SudokuBoard board;
    
    /** full board as it was computed at the first step */
    private SudokuBoard fullBoard;

    private Random random;
    // temporary counter for measuring the performance
    private int tmpCounter = 0;
    private int genCounter = 0;
    int difficulty = 0;
    int targetDifficulty = 0;
    String messages = "";
    long startTime, endTime = 0;

    public SudokuLogic() {
        board = new SudokuBoard();
        random = new Random();
    }

    public SudokuBoard resetBoard() {
        board = new SudokuBoard();
        board.markGeneratedFields();
        fullBoard = null;
        this.messages = "";
        return board;
    }

    /**
     * Generate a board with given difficulty
     * @param difficulty 0 - random, 1 - easy, 2 - medium, 3 - difficult
     */
    public void generateSudoku(int difficulty) {
        this.difficulty = 0;
        this.targetDifficulty = difficulty;
        if (difficulty == 0) this.targetDifficulty = 4;
        
        startTime = System.currentTimeMillis();
        while (this.difficulty == 0) {
            System.out.println("Generating new sudoku board.");
            board.clear();
            this.messages = "";
            generateFullBoard(0);
            fullBoard = board.clone();
            removeNumbersFromTheBoard(board);
            if (difficulty == 1) {
                // easy
                if (this.difficulty < 200) {
                    break;
                }
            } else if (difficulty == 2) {
                if (this.difficulty >= 200 && this.difficulty < 400) {
                    break;
                }
            } else if (difficulty == 3) {
                if (this.difficulty >= 400) {
                    break;
                }
            } else {
                break;
            }
            this.difficulty = 0;
        }
        endTime = System.currentTimeMillis();
        System.out.println(this.messages);
    }

    /**
     * Get a list of wrong guesses by user comparing partial and full board
     * @return
     */
    public ArrayList getWrongGuess(){
        ArrayList wrongFields = new ArrayList();
        int i, x, y;
        SudokuField userField;
        for (i = 0; i < 81; i ++){
            x = i / 9;
            y = i % 9;
            userField = board.getValue(x, y);
            if (!userField.generated && userField.value > 0){
                if (fullBoard.getNumber(x, y) != userField.value){
                    wrongFields.add(i);
                }
            }
        }
        return wrongFields;
        // process the output
        /*int[] output = new int[wrongFields.size()];
        for (i = 0; i < wrongFields.size(); i++) {
            output[i] = ((SudokuField)wrongFields.get(i)).value;
        }
        return output;*/
    }
    
    public long getDurationInMilis() {
        return (this.endTime - this.startTime);
    }

    /**
     * Generate full board using brute force and recursion
     * @param pos
     * @return
     */
    public boolean generateFullBoard(int pos) {

        tmpCounter++;

        int x = pos / 9;
        int y = pos % 9;

        // processing last number ... yay
        if (pos == 81) {
            return true;
        }

        // index in possible values array
        int candidate;

        ArrayList possibleVals = board.getAllowedNumbers(x, y);
        if (possibleVals.isEmpty()) {
            return false;
        }

        // go through all possible values and keep other for case that one further
        // iteration won't fit
        while (!possibleVals.isEmpty()) {
            candidate = randNum(possibleVals.size());
            board.setNumber(x, y, ((Integer) possibleVals.get(candidate)));
            if (board.isValid(x, y)) {

                if (generateFullBoard(pos + 1)) {
                    return true;
                }
            }
            // this one didn't work (didn't produce the right board), so we
            // are going to discard it
            possibleVals.remove(candidate);
        }
        // no candidate found, return one step back
        board.setNumber(x, y, 0);
        return false;
    }

    public SudokuBoard getBoard() {
        return board;
    }

    /**
     * Replace values from the previously generated full board in the current board
     * @return
     */
    public SudokuBoard solveByReplace() {
        if (fullBoard == null){
            // solve the board
            solveTheBoard(board);
        }else{
            // just copy the values
            int i = 0, x, y, computedField;
            SudokuField userField;
            for (i = 0; i < 81; i ++){
                x = i / 9;
                y = i % 9;
                userField = board.getValue(x, y);
                computedField = fullBoard.getNumber(x, y);
                if (!userField.generated){
                    if (computedField != userField.value){
                        board.setNumber(x, y, computedField);
                    }
                }
            }
        }
        return board;
    }

    private int randNum(int max) {
        return random.nextInt(max);
    }

    public void setBoard(SudokuBoard board) {
        this.board = board;
    }

    public int getDifficulty() {
        return this.difficulty;
    }

    /**
     * Remove numbers from the full board
     * @param board
     */
    public void removeNumbersFromTheBoard(SudokuBoard board) {
        int remove, removed;
        int x, y;
        String oldMessages = messages;

        // create permutation
        ArrayList<Integer> permutation = createShuffledArrayList();

        SudokuBoard newBoard;
        while (permutation.size() > 0) {

            // keep history of solving techniques
            oldMessages = messages;
            this.messages = "";
            genCounter++;

            // remove the number
            // check also if it is empty or not
            remove = permutation.get(0);
            permutation.remove(0);

            x = remove / 9;
            y = remove % 9;
            removed = board.getNumber(x, y);

            board.setNumber(x, y, 0);
            board.updateAllowedNumbersEverywhere(x, y);

            // try to solve it
            newBoard = board.clone();

            solveTheBoard(newBoard);

            // we removed too much
            if (!newBoard.isFullBoard()) {
                this.messages = oldMessages;
                board.setNumber(x, y, removed);
                board.updateAllowedNumbersEverywhere(x, y);
            }
        }
        board.markGeneratedFields();
    }

    /**
     * Create permutation of the list swapping single indexes
     * @param list
     */
    public ArrayList createShuffledArrayList() {
        ArrayList<Integer> list = new ArrayList();
        int a, b, c;
        Random rand = new Random();
        for (int i = 0; i < 81; i++) {
            list.add(i);
        }
        for (int i = 0; i < 81; i++) {
            a = rand.nextInt(81);
            b = rand.nextInt(81);
            c = list.get(a);
            list.set(a, list.get(b));
            list.set(b, c);
        }
        return list;
    }

    /**
     * Return difficulty of the puzzle
     * right now, it returns one if there is at least one solution
     * and zero for no solutions
     * @param board
     * @return
     */
    public void solveTheBoard(SudokuBoard board) {
        //if (tmpCounter > 72){
        boolean solved = false;
        boolean change = true;

        this.difficulty = 0;

        int filledFields = board.getNumberOfIndicies();
        int filledAfterSolving = 0;

        boolean changed = true;

        // difficulty grows only if there is a change
        while (changed == true && !solved) {
            // easy level

            change = solveSingeCandidate(board);
            if (change) {
                this.messages += "Solving using single candidate (" + this.difficulty + ") \n";
            }

            change = solveSinglePosition(board);
            if (change) {
                this.messages += "Solving using single position (" + this.difficulty + ") \n";
            }

            if (this.targetDifficulty > 1) {
                change = solveCandidateLines(board);
                if (change) {
                    this.messages += "Solving using candidate lines (" + this.difficulty + ") \n";
                }

                if (this.targetDifficulty > 2) {
                    change = solveNakedPairs(board);
                    if (change) {
                        this.messages += "Solving using naked pairs (" + this.difficulty + ") \n";
                    }

                    change = solveHiddenPairs(board);
                    if (change) {
                        this.messages += "Solving using hidden pairs (" + this.difficulty + ") \n";
                    }
                }
            }

            solved = board.isFullBoard();

            filledAfterSolving = board.getNumberOfIndicies();
            if (filledFields == filledAfterSolving || solved) {
                changed = false;
            } else {
                filledFields = filledAfterSolving;
            }
        }

        this.messages += "Difficulty of the puzzle: " + this.difficulty + "\n";

    }

    public boolean fillMissingNumbers() {
        return solveSingeCandidate(board);
        //return solveSinglePosition(board);
    }

    /**
     * Fills the field if there is only one candidate
     */
    public boolean solveSingeCandidate(SudokuBoard board) {
        int origDifficulty = this.difficulty;
        boolean change = false;
        int current = 0;
        int x, y;


        while (true) {

            x = current / 9;
            y = current % 9;
            if (board.checkCandidates(x, y)) {
                this.difficulty++;
                change = true;
            }
            current++;
            if (current == 81) {
                if (!change) {
                    break;
                }
                change = false;
                current = 0;
            }
        }
        if (origDifficulty == this.difficulty) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Find if there is only position for the candidate
     * @param board
     * @return true if board is full
     */
    public boolean solveSinglePosition(SudokuBoard board) {
        int origDifficulty = this.difficulty;
        boolean change = false;
        int current = 0;
        int x, y;
        while (true) {

            x = current / 9;
            y = current % 9;
            if (board.findSinglePosition(x, y)) {
                change = true;
                this.difficulty++;
            }
            current++;
            if (current == 81) {
                if (!change) {
                    break;
                }
                change = false;
                current = 0;
            }
        }
        if (origDifficulty == this.difficulty) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * http://www.palmsudoku.com/pages/techniques-3.php
     * medium difficulty
     * @param board
     * @return
     */
    public boolean solveCandidateLines(SudokuBoard board) {
        int origDifficulty = this.difficulty;
        boolean change = false;

        // detect squares
        for (int i = 0; i < 9; i += 3) {
            for (int j = 0; j < 9; j += 3) {
                change = board.solveCandidateLinesInSquare(i, j);
                // solving for square 9x9
                if (change) {
                    this.difficulty += 9;
                }
            }
        }
        if (origDifficulty == this.difficulty) {
            return false;
        } else {
            return true;
        }
    }

    public boolean solveNakedPairs(SudokuBoard board) {
        int origDifficulty = this.difficulty;
        // detect squares
        for (int i = 0; i < 9; i++) {
            if (board.solveNakedPairsRow(i)) {
                this.difficulty += 9;
            }
            if (board.solveNakedPairsCol(i)) {
                this.difficulty += 9;
            }
        }
        if (origDifficulty == this.difficulty) {
            return false;
        } else {
            return true;
        }
    }

    public boolean solveHiddenPairs(SudokuBoard board) {
        int origDifficulty = this.difficulty;
        ArrayList<SudokuField> fieldsRow;
        ArrayList<SudokuField> fieldsCol;

        // rows
        for (int i = 0; i < 9; i++) {
            fieldsRow = new ArrayList();
            fieldsCol = new ArrayList();
            for (int j = 0; j < 9; j++) {
                // get fiels in row and col
                if (board.getNumber(i, j) == 0) {
                    fieldsCol.add(board.getValue(i, j));
                }
                if (board.getNumber(j, i) == 0) {
                    fieldsRow.add(board.getValue(j, i));
                }
            }
            if (board.findHiddenPairs(fieldsRow) || board.findHiddenPairs(fieldsCol)) {
                this.difficulty += 12;
            }

        }

        if (origDifficulty == this.difficulty) {
            return false;
        } else {
            return true;
        }
    }
}
