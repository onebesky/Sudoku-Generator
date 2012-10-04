
package sudoku;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;

/**
 * Simple tests used to simulate different situations to solve the board
 * @author Ondrej Nebesky
 */
public class SudokuTests {

    private SudokuBoard start;
    private SudokuBoard board;
    private SudokuBoard end;
    private SudokuLogic logic;
    // index of field for which we want to watch the candidate changes
    private ArrayList<Integer> watchCandidates = new ArrayList();

    public SudokuTests(SudokuBoard board) {
        logic = new SudokuLogic();
        this.start = board;
        board = start.clone();
        end = new SudokuBoard();
        logic.setBoard(board);
    }

    public boolean testCandidateLines() {
        int x, y;
        watchCandidates.clear();
        watchCandidates.add(80);
        watchCandidates.add(79);
        watchCandidates.add(78);
        watchCandidates.add(70);
        watchCandidates.add(61);
        watchCandidates.add(25);
        watchCandidates.add(52);
        board = new SudokuBoard();

        prepareBoard(board,
                "001957063"
                + "000806070"
                + "769130805"
                + "007261350"
                + "312495786"
                + "056378000"
                + "108609507"
                + "090710608"
                + "674583000");

        for (int i = 0; i < 81; i++) {
            x = i / 9;
            y = i % 9;
            board.updateAllowedNumbersEverywhere(x, y);
        }

        System.out.println(board.toString());
        for (int i = 0; i < watchCandidates.size(); i++) {
            debugSquare(watchCandidates.get(i));
        }
        board.solveCandidateLinesInSquare(6, 6);
        for (int i = 0; i < watchCandidates.size(); i++) {
            debugSquare(watchCandidates.get(i));
        }
        return false;
    }

    public void prepareBoard(SudokuBoard board, String numbers) {
        int x, y;
        for (int i = 0; i < numbers.length(); i++) {
            x = i % 9;
            y = i / 9;
            board.setNumber(x, y, Integer.parseInt(numbers.substring(i, i + 1)));
        }
    }

    private void debugSquare(int i) {
        debugSquare(i % 9, i / 9);
    }

    private void debugSquare(int x, int y) {
        System.out.println("-------------");
        System.out.println("Field: " + x + " " + y);
        int val = board.getValue(x, y).getValue();
        if (val == 0) {
            ArrayList<Integer> candidates = board.getValue(x, y).getCandidates();
            String candidatesString = "";
            for (int i = 0; i < candidates.size(); i++) {
                candidatesString += candidates.get(i) + ", ";
            }
            System.out.println("Candidates: " + candidatesString);
        } else {
            System.out.println("Value: " + val);
        }
    }

    public void testNakedPairs() {
        int x, y;
        board = new SudokuBoard();
        watchCandidates.add(79);
        watchCandidates.add(78);
        watchCandidates.add(75);
        watchCandidates.add(73);
        watchCandidates.add(72);

        prepareBoard(board,
                "400270600"
                + "798156234"
                + "020840007"
                + "237468951"
                + "849531726"
                + "561792843"
                + "082015479"
                + "070024300"
                + "004087002");
        for (int i = 0; i < 81; i++) {
            x = i / 9;
            y = i % 9;
            board.updateAllowedNumbersEverywhere(x, y);
        }

        System.out.println(board.toString());
        for (int i = 0; i < watchCandidates.size(); i++) {
            debugSquare(watchCandidates.get(i));
        }
        board.solveNakedPairsRow(8);
        for (int i = 0; i < watchCandidates.size(); i++) {
            debugSquare(watchCandidates.get(i));
        }
    }

    public void testHiddenPairs() {
        int x, y;
        board = new SudokuBoard();
        watchCandidates.add(20);
        watchCandidates.add(21);
        watchCandidates.add(23);
        watchCandidates.add(25);
        watchCandidates.add(26);

        prepareBoard(board,
                "801006094"
                + "300009080"
                + "970080500"
                + "547062030"
                + "632000050"
                + "198375246"
                + "083620915"
                + "065198000"
                + "219500008");
        for (int i = 0; i < 81; i++) {
            x = i / 9;
            y = i % 9;
            board.updateAllowedNumbersEverywhere(x, y);
        }

        System.out.println(board.toString());
        for (int i = 0; i < watchCandidates.size(); i++) {
            debugSquare(watchCandidates.get(i));
        }

        logic.solveHiddenPairs(board);
        for (int i = 0; i < watchCandidates.size(); i++) {
            debugSquare(watchCandidates.get(i));
        }
    }

    public void logAverageDifficulty() {
        
        try {
            FileWriter fstream = new FileWriter("performanceLog.txt", true);
            BufferedWriter out = new BufferedWriter(fstream);
            for (int i = 0; i < 1000; i++){
                logic.generateSudoku(0);
                // write to file

                out.write(logic.getDifficulty() + ";" + logic.getDurationInMilis());
                out.newLine();
            }
            //Close the output stream
            out.close();
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }
}
