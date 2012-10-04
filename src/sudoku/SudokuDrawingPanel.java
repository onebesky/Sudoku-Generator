package sudoku;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import javax.swing.JPanel;

/**
 * The class is used to render the board
 * @author Ondrej Nebesky
 */
public class SudokuDrawingPanel extends JPanel {

    private SudokuBoard board;
    int dy;
    int dx;
    int sideSize;
    int boxSize;
    // highlighted coordinates
    int hx = -1;
    int hy = -1;
    // selected field
    int sx = -1;
    int sy = -1;
    SudokuField selectedField = null;
    SudokuField previousSelectedField = null;
    int clickedNumber = 0;
    boolean showHints = false;
    // numbers for selected field
    Object[] empty = new Object[0];
    ArrayList emptySelection = new ArrayList();
    Object[] allowedNumbers = empty;
    ArrayList wrongFields = emptySelection;

    public SudokuDrawingPanel() {
        setBackground(Color.white);
        board = new SudokuBoard();
    }

    public void setSudokuBoard(SudokuBoard board) {
        this.board = board;
        this.selectedField = null;
        this.wrongFields = emptySelection;
        this.allowedNumbers = empty;
    }

    @Override
    public void paintComponent(Graphics g) {
        // Paint background
        super.paintComponent(g);

        loadDimensions();

        drawGrid(g);

        drawNumbers(g);

        drawControlBox(g);
    }

    private void loadDimensions() {
        dy = getSize().height;
        dx = getSize().width;
        //sideSize = (dy < dx ? dy : dx);
        if (dy > dx * 23 / 18) {
            sideSize = dx;
        } else {
            sideSize = dy * 18 / 22;
        }
        // height of the box below

        boxSize = (int) (sideSize / 9);
    }

    private void drawGrid(Graphics g) {
        
        // background
        g.setColor(Color.lightGray);
        g.fillRect(0, 0, sideSize / 3, sideSize / 3);
        g.fillRect(sideSize / 3 * 2, 0, sideSize / 3, sideSize / 3);
        g.fillRect(sideSize / 3 * 2, sideSize / 3 * 2, sideSize / 3, sideSize / 3);
        g.fillRect(sideSize / 3, sideSize / 3, sideSize / 3, sideSize / 3);
        g.fillRect(0, sideSize / 3 * 2, sideSize / 3, sideSize / 3);

        // wrong value highlight
        if (showHints) {
            int pos, x, y;
            g.setColor(Color.pink);
            for (int i = 0; i < wrongFields.size(); i++) {
                pos = (Integer) wrongFields.get(i);
                x = pos / 9;
                y = pos % 9;
                g.fillRect(boxSize * x, boxSize * y, boxSize, boxSize);
            }
        }

        // mouse selected
        if (sx > -1) {
            g.setColor(Color.getHSBColor(215, 13, 86));
            g.fillRect(boxSize * sx, boxSize * sy, boxSize, boxSize);
        }

        // mouse highlighted field
        if (hx > -1 && hy < 9) {
            g.setColor(Color.getHSBColor(60, 38, 100));
            g.fillRect(boxSize * hx, boxSize * hy, boxSize, boxSize);
        }

        g.setColor(Color.black);

        // border
        g.fillRect(0, 0, sideSize, 3);
        g.fillRect(0, sideSize / 3, sideSize, 3);
        g.fillRect(0, (sideSize / 3) * 2, sideSize, 3);
        g.fillRect(0, sideSize - 3, sideSize, 3);

        g.fillRect(0, 0, 3, sideSize);
        g.fillRect(sideSize / 3, 0, 3, sideSize);
        g.fillRect((sideSize / 3) * 2, 0, 3, sideSize);
        g.fillRect(sideSize - 3, 0, 3, sideSize);

        // control border
        g.setColor(Color.getHSBColor(60, 38, 100));
        int bottomTop = sideSize + boxSize / 2 - 3;
        int bottom = getSize().height - 10;
        g.fillRect(0, bottomTop, sideSize, bottom - bottomTop);

        g.setColor(Color.black);
        g.fillRect(0, bottomTop, sideSize, 3);
        g.fillRect(0, bottomTop, 3, bottom - bottomTop);
        g.fillRect(sideSize - 3, bottomTop, 3, bottom - bottomTop);
        g.fillRect(0, bottom, sideSize, 3);

        // grid
        for (int w = 0; w < 9; w++) {
            if (w != 3 && w != 6) {
                g.drawLine(w * (boxSize), 0, w * (boxSize), sideSize);
                g.drawLine(0, w * (boxSize), sideSize, w * (boxSize));
            }
        }
    }

    /**
     * Draw a box with numbers user can click to
     * @param g 
     */
    private void drawControlBox(Graphics g) {
        int y = boxSize * 11 - boxSize / 2;
        int x = (int) (boxSize * 0.29);
        // highlight for moving mouse and selected value
        int highlight = -1, selectedHighlight = -1;
        if (hx > -1 && hy > 9) {
            // select number field
            highlight = hx;
        }
        if (selectedField != null) {
            if (previousSelectedField != selectedField) {
                if (!selectedField.generated && selectedField.value > 0) {
                    int tmpVal = selectedField.value;
                    selectedField.value = 0;
                    allowedNumbers = board.getAllowedNumbers(sx, sy).toArray();
                    System.out.println("Get candidates for " + tmpVal + "(" + selectedField.position + " at " + highlight);
                    selectedField.value = tmpVal;
                    previousSelectedField = selectedField;
                }
            }
            if (selectedField.value > 0) {
                selectedHighlight = selectedField.value - 1;
            }
        }
        for (int i = 0; i < 9; i++) {
            if (sx >= 0 && isCandidate(i + 1)) {
                if (i == highlight || i == selectedHighlight) {
                    g.setColor(Color.red);
                } else {
                    g.setColor(Color.blue);
                }
            } else {
                g.setColor(Color.gray);
            }
            g.drawString((i + 1) + "", boxSize * i + x, y);
        }
    }

    /**
     * Is given number possible candidate for the selected field?
     * @param number
     * @return 
     */
    private boolean isCandidate(int number) {
        int i;
        for (i = 0; i < allowedNumbers.length; i++) {
            if ((Integer) allowedNumbers[i] == number) {
                return true;
            }
        }
        return false;
    }

    /**
     * Draw a single number to the board
     * @param generated was it generated by computer?
     */
    private void drawNumber(Graphics g, int x, int y, int number, boolean generated) {
        if (generated) {
            g.setColor(Color.black);
        } else {
            g.setColor(Color.blue);
        }
        g.drawString(number + "", boxSize * x + (int) (boxSize * 0.29), boxSize * (y + 1) - (int) (boxSize * 0.22));
    }

    private void drawNumbers(Graphics g) {
        SudokuField field;
        int number;
        g.setFont(new Font("Arial", 0, (int) (boxSize * 0.8)));
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                field = board.getValue(i, j);
                number = field.value;
                if (number > 0 && number < 10) {
                    drawNumber(g, i, j, number, field.generated);
                }
            }
        }
    }

    /**
     * Highlight a field
     * @param x
     * @param y
     */
    public boolean highlight(int x, int y) {
        int newHx = -1, newHy = -1;
        if (x < sideSize && y < sideSize + boxSize * 3) {
            newHx = x / boxSize;
            newHy = y / boxSize;
        } else {
            newHx = -1;
            newHx = -1;

        }
        if (newHx != hx || newHy != hy) {
            hx = newHx;
            hy = newHy;
            return true;
        }
        return false;
    }

    /**
     * Select a field on the board
     * @param x
     * @param y
     */
    public boolean select(int x, int y) {
        if (x < sideSize && y < sideSize) {
            // a field was selected
            sx = x / boxSize;
            sy = y / boxSize;
            selectedField = board.getValue(sx, sy);
            if (selectedField.generated) {
                sx = -1;
                sy = -1;
                selectedField = null;
                allowedNumbers = empty;
            } else {
                allowedNumbers = board.getAllowedNumbers(sx, sy).toArray();
            }
        } else if (x < sideSize && hy > 9) {
            // clicked control number
            clickedNumber = hx + 1;
            previousSelectedField = null;
            if (selectedField != null) {
                if (!selectedField.generated) {
                    if (clickedNumber == selectedField.value) {
                        selectedField.value = 0;
                    } else {
                        selectedField.value = clickedNumber;
                    }
                }
            }
        } else {
            sx = -1;
            sy = -1;
            selectedField = null;
        }

        return true;
    }

    /**
     * When the key is pressed
     * @param val number from 0 to 9
     */
    public void setSelectedValue(int val) {
        if (selectedField != null) {
            clickedNumber = val;
            if (!selectedField.generated) {
                if (clickedNumber == selectedField.value) {
                    selectedField.value = 0;
                } else {
                    selectedField.value = clickedNumber;
                }
            }
        }
    }

    public void resetHints() {
        wrongFields = emptySelection;
    }
}
