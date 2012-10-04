package sudoku;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * PDF Rendering class is used to save printable boards.
 * 
 * @author Ondrej Nebesky
 */
public class PdfBoard {

    /** Path to the resulting PDF file. */
    public String filename = "board.pdf";

    SudokuLogic logic;

    public PdfBoard(String filePath) {
        filename = filePath;
        logic = new SudokuLogic();
        
    }
    
    public void createDocument(){
        this.createDocument(0);
    }
 
    public void createDocument(int difficulty) {
        try {
            Document document = new Document(PageSize.LETTER, 50, 50, 50, 50);

            PdfWriter.getInstance(document, new FileOutputStream(filename));
            document.open();

            int i;
            PdfPTable mainTable = new PdfPTable(2);
            mainTable.setWidthPercentage(96f);
            mainTable.setSpacingBefore(50f);
            for (i = 0; i < 4; i++) {
                PdfPCell cell = new PdfPCell(createBoard(difficulty));
                cell.setBorder(0);
                cell.setPadding(20f);

                mainTable.addCell(cell);
            }

            document.add(mainTable);
            document.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PdfBoard.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DocumentException ex) {
            Logger.getLogger(PdfBoard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private PdfPTable createBoard(int difficulty) throws DocumentException {
        logic.generateSudoku(difficulty);
        SudokuBoard board = logic.getBoard();
        
        PdfPTable table = new PdfPTable(9); // 3 columns.

        table.setWidthPercentage(42);
        table.setSpacingAfter(10);
        PdfPCell cell;
        boolean paintBackground = true;
        int x, y, number;
        for (int i = 0; i < 81; i++) {
            y = i / 9;
            x = i % 9;
            number = board.getNumber(x, y);
            cell = new PdfPCell(new Phrase(number == 0 ? "" : number + ""));
            cell.setFixedHeight(23f);
            cell.setBorderWidth(0f);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setHorizontalAlignment(Element.ALIGN_MIDDLE);
            cell.setPaddingLeft(8f);
            cell.setPaddingTop(8f);

            // borders
            if (x % 3 == 0) {
                cell.setBorderWidthLeft(2f);
                if (x > 0) {
                    paintBackground = !paintBackground;
                }
            } else {
                cell.setBorderWidthLeft(0.5f);
            }
            if (x == 8) {
                cell.setBorderWidthRight(2f);
            }
            if (y % 3 == 0) {
                cell.setBorderWidthTop(2f);

            } else {
                cell.setBorderWidthTop(0.5f);
            }
            if (y == 8) {
                cell.setBorderWidthBottom(2f);
                cell.setFixedHeight(16f);
            }
            if (i == 27 || i == 54) {
                paintBackground = !paintBackground;
            }

            // background
            if (paintBackground) {
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            }

            table.addCell(cell);

        }
        return table;
    }
}
