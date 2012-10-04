/*
 * SudokuView.java
 */
package sudoku;

import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.Task;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import java.io.IOException;
import javax.swing.ButtonGroup;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;

/**
 * The application's main frame.
 */
public final class SudokuView extends FrameView {

    private SudokuBoard board;
    private SudokuLogic logic;
    
    // pdf file
    private String pdfPath = "sudoku.pdf";
    public File file;
    
    public static JFrame self;

    //private boolean showHints = false;
    public SudokuView(SingleFrameApplication app) {
        super(app);
        self = this.getFrame();

        initComponents();

        // select only one item from menu
        ButtonGroup group = new ButtonGroup();
        group.add(randomDifficultyMenuItem);
        group.add(easyDifficultyMenuItem);
        group.add(mediumDifficultyMenuItem);
        group.add(hardDifficultyMenuItem);

        drawingPanel.setFocusable(true);

        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                } else if ("message".equals(propertyName)) {
                    String text = (String) (evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer) (evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }
        });

        // test
        board = new SudokuBoard();


        logic = new SudokuLogic();
        logic.setBoard(board);
        this.getFrame().setTitle("Sudoku Generator");

        //SudokuTests test = new SudokuTests(new SudokuBoard());
        //test.testHiddenPairs();
        //test.logAverageDifficulty();

        ((SudokuDrawingPanel) drawingPanel).setSudokuBoard(board);

        // render one game
        generateBoard().run();
    }

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = SudokuApp.getApplication().getMainFrame();
            aboutBox = new SudokuAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        SudokuApp.getApplication().show(aboutBox);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        drawingPanel = new SudokuDrawingPanel();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        newGameMenuItem = new javax.swing.JMenuItem();
        pdfMenuItem = new javax.swing.JMenuItem();
        difficultyMenu = new javax.swing.JMenu();
        randomDifficultyMenuItem = new javax.swing.JRadioButtonMenuItem();
        easyDifficultyMenuItem = new javax.swing.JRadioButtonMenuItem();
        mediumDifficultyMenuItem = new javax.swing.JRadioButtonMenuItem();
        hardDifficultyMenuItem = new javax.swing.JRadioButtonMenuItem();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        showHintsMenuItem = new javax.swing.JCheckBoxMenuItem();
        showSolutionMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();

        mainPanel.setName("mainPanel"); // NOI18N

        drawingPanel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        drawingPanel.setName("drawingPanel"); // NOI18N
        drawingPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                drawingPanelMouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                drawingPanelMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                drawingPanelMousePressed(evt);
            }
        });
        drawingPanel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                drawingPanelMouseMoved(evt);
            }
        });
        drawingPanel.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                drawingPanelKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout drawingPanelLayout = new javax.swing.GroupLayout(drawingPanel);
        drawingPanel.setLayout(drawingPanelLayout);
        drawingPanelLayout.setHorizontalGroup(
            drawingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 580, Short.MAX_VALUE)
        );
        drawingPanelLayout.setVerticalGroup(
            drawingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 417, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(drawingPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(drawingPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        menuBar.setName("menuBar"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(sudoku.SudokuApp.class).getContext().getResourceMap(SudokuView.class);
        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(sudoku.SudokuApp.class).getContext().getActionMap(SudokuView.class, this);
        newGameMenuItem.setAction(actionMap.get("generateBoard")); // NOI18N
        newGameMenuItem.setText(resourceMap.getString("newGameMenuItem.text")); // NOI18N
        newGameMenuItem.setToolTipText(resourceMap.getString("newGameMenuItem.toolTipText")); // NOI18N
        newGameMenuItem.setName("newGameMenuItem"); // NOI18N
        fileMenu.add(newGameMenuItem);

        pdfMenuItem.setAction(actionMap.get("createPdfBoard")); // NOI18N
        pdfMenuItem.setText(resourceMap.getString("pdfMenuItem.text")); // NOI18N
        pdfMenuItem.setName("pdfMenuItem"); // NOI18N
        fileMenu.add(pdfMenuItem);

        difficultyMenu.setText(resourceMap.getString("difficultyMenu.text")); // NOI18N
        difficultyMenu.setName("difficultyMenu"); // NOI18N

        randomDifficultyMenuItem.setSelected(true);
        randomDifficultyMenuItem.setText(resourceMap.getString("randomDifficultyMenuItem.text")); // NOI18N
        randomDifficultyMenuItem.setName("randomDifficultyMenuItem"); // NOI18N
        difficultyMenu.add(randomDifficultyMenuItem);

        easyDifficultyMenuItem.setSelected(true);
        easyDifficultyMenuItem.setText(resourceMap.getString("easyDifficultyMenuItem.text")); // NOI18N
        easyDifficultyMenuItem.setName("easyDifficultyMenuItem"); // NOI18N
        difficultyMenu.add(easyDifficultyMenuItem);

        mediumDifficultyMenuItem.setSelected(true);
        mediumDifficultyMenuItem.setText(resourceMap.getString("mediumDifficultyMenuItem.text")); // NOI18N
        mediumDifficultyMenuItem.setName("mediumDifficultyMenuItem"); // NOI18N
        difficultyMenu.add(mediumDifficultyMenuItem);

        hardDifficultyMenuItem.setSelected(true);
        hardDifficultyMenuItem.setText(resourceMap.getString("hardDifficultyMenuItem.text")); // NOI18N
        hardDifficultyMenuItem.setName("hardDifficultyMenuItem"); // NOI18N
        difficultyMenu.add(hardDifficultyMenuItem);

        fileMenu.add(difficultyMenu);

        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        showHintsMenuItem.setAction(actionMap.get("showHintsSwitch")); // NOI18N
        showHintsMenuItem.setText(resourceMap.getString("showHintsMenuItem.text")); // NOI18N
        showHintsMenuItem.setName("showHintsMenuItem"); // NOI18N
        helpMenu.add(showHintsMenuItem);

        showSolutionMenuItem.setAction(actionMap.get("solveTheBoard")); // NOI18N
        showSolutionMenuItem.setText(resourceMap.getString("showSolutionMenuItem.text")); // NOI18N
        showSolutionMenuItem.setName("showSolutionMenuItem"); // NOI18N
        helpMenu.add(showSolutionMenuItem);

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        statusPanel.setName("statusPanel"); // NOI18N

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusPanelSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 580, Short.MAX_VALUE)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statusMessageLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 410, Short.MAX_VALUE)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusAnimationLabel)
                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addComponent(statusPanelSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(statusMessageLabel)
                    .addComponent(statusAnimationLabel)
                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3))
        );

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
    }// </editor-fold>//GEN-END:initComponents

    private void drawingPanelMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_drawingPanelMouseMoved
        // detect a field on drawing panel
        if (((SudokuDrawingPanel) drawingPanel).highlight(evt.getX(), evt.getY())) {
            drawingPanel.validate();
            drawingPanel.repaint();
        }
    }//GEN-LAST:event_drawingPanelMouseMoved

    private void drawingPanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_drawingPanelMouseClicked
    }//GEN-LAST:event_drawingPanelMouseClicked

    private void drawingPanelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_drawingPanelMouseExited
        // reset the highlight
        if (((SudokuDrawingPanel) drawingPanel).highlight(64000, -1)) {
            drawingPanel.validate();
            drawingPanel.repaint();
        }
    }//GEN-LAST:event_drawingPanelMouseExited

    private void drawingPanelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_drawingPanelMousePressed
        if (((SudokuDrawingPanel) drawingPanel).select(evt.getX(), evt.getY())) {
            // update possibly wrong numbers, display hints
            if (((SudokuDrawingPanel) drawingPanel).showHints) {
                ((SudokuDrawingPanel) drawingPanel).wrongFields = logic.getWrongGuess();
                System.out.println("hints updated");
            }
            drawingPanel.validate();
            drawingPanel.repaint();
        }
    }//GEN-LAST:event_drawingPanelMousePressed

    private void drawingPanelKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_drawingPanelKeyPressed
        // TODO add your handling code here:
        int i = Character.getNumericValue(evt.getKeyChar());
        ((SudokuDrawingPanel) drawingPanel).setSelectedValue(i);
        drawingPanel.validate();
        drawingPanel.repaint();
    }//GEN-LAST:event_drawingPanelKeyPressed

    @Action
    public Task generateBoard() {
        return new GenerateBoardTask(getApplication());
    }

    private class GenerateBoardTask extends org.jdesktop.application.Task<Object, Void> {

        GenerateBoardTask(org.jdesktop.application.Application app) {
            // Runs on the EDT.  Copy GUI state that
            // doInBackground() depends on from parameters
            // to GenerateBoardTask fields, here.
            super(app);
        }

        @Override
        protected Object doInBackground() {
            statusMessageLabel.setText("Generating the board ...");
            int difficulty = 0;
            if (easyDifficultyMenuItem.isSelected()) {
                difficulty = 1;
            }
            if (mediumDifficultyMenuItem.isSelected()) {
                difficulty = 2;
            }
            if (hardDifficultyMenuItem.isSelected()) {
                difficulty = 3;
            }
            logic.generateSudoku(difficulty);
            statusMessageLabel.setText("Difficulty of the puzzle is " + logic.getDifficulty() + ". Generating the puzzle took " + logic.getDurationInMilis() + " ms.");
            return null;  // return your result
        }

        @Override
        protected void succeeded(Object result) {
            updateGUI();
        }
    }

    @Action
    public void solveTheBoard() {
        board = logic.getBoard();
        //board = 
        //if (!board.isEmptyBoard()){
        ((SudokuDrawingPanel) drawingPanel).resetHints();
        //logic.solveTheBoard(board);
        logic.solveByReplace();
        //}
        updateGUI();
    }

    public void updateGUI() {
        drawingPanel.validate();
        drawingPanel.repaint();
    }

    @Action
    public void showHintsSwitch() {
        ((SudokuDrawingPanel) drawingPanel).showHints = showHintsMenuItem.isSelected();
        // update possibly wrong numbers, display hints
        if (((SudokuDrawingPanel) drawingPanel).showHints) {
            ((SudokuDrawingPanel) drawingPanel).wrongFields = logic.getWrongGuess();
            //System.out.println("hints updated");
        }
        updateGUI();
    }

    @Action
    public void createEmptyBoard() {
        board = logic.resetBoard();
        ((SudokuDrawingPanel) drawingPanel).setSudokuBoard(board);
        updateGUI();
    }

    @Action
    public Task createPdfBoard() {

        JFileChooser fc = new JFileChooser();
        FileFilter pdfFilter = new FileFilter() {

            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".pdf");
            }

            public String getDescription() {
                return ".pdf";
            }
        };
        fc.setFileFilter(pdfFilter);
        fc.setMultiSelectionEnabled(false);
        fc.addChoosableFileFilter(pdfFilter);
        fc.setSelectedFile(new File(this.pdfPath));
        
        int returnVal = fc.showSaveDialog(self);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = fc.getSelectedFile();

            if (!file.getName().endsWith(".pdf")) {
                pdfPath = file.getAbsolutePath() + ".pdf";
                file = new File(pdfPath);
                System.out.println(pdfPath);
            }
            pdfPath = file.getAbsolutePath();
        }

        return new CreatePdfBoardTask(getApplication());
    }

    private class CreatePdfBoardTask extends org.jdesktop.application.Task<Object, Void> {

        CreatePdfBoardTask(org.jdesktop.application.Application app) {
            // Runs on the EDT.  Copy GUI state that
            // doInBackground() depends on from parameters
            // to CreatePdfBoardTask fields, here.
            super(app);
        }

        @Override
        protected Object doInBackground() {
            int difficulty = 0;
            if (easyDifficultyMenuItem.isSelected()) {
                difficulty = 1;
            }
            if (mediumDifficultyMenuItem.isSelected()) {
                difficulty = 2;
            }
            if (hardDifficultyMenuItem.isSelected()) {
                difficulty = 3;
            }
            PdfBoard pdfBoard = new PdfBoard(pdfPath);
            pdfBoard.createDocument(difficulty);
            return null;  // return your result
        }

        @Override
        protected void succeeded(Object result) {
            try {

                if (System.getProperty("os.name").equalsIgnoreCase("Unix") || System.getProperty("os.name").equalsIgnoreCase("Linux")) {
                    Runtime.getRuntime().exec(new String[]{"open", file.getAbsolutePath()});
                } else {
                    Runtime.getRuntime().exec(new String[]{"rundll32", "url.dll,FileProtocolHandler", file.getAbsolutePath()});
                }
            } catch (IOException ex) {
            }
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu difficultyMenu;
    private javax.swing.JPanel drawingPanel;
    private javax.swing.JRadioButtonMenuItem easyDifficultyMenuItem;
    private javax.swing.JRadioButtonMenuItem hardDifficultyMenuItem;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JRadioButtonMenuItem mediumDifficultyMenuItem;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem newGameMenuItem;
    private javax.swing.JMenuItem pdfMenuItem;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JRadioButtonMenuItem randomDifficultyMenuItem;
    private javax.swing.JCheckBoxMenuItem showHintsMenuItem;
    private javax.swing.JMenuItem showSolutionMenuItem;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    // End of variables declaration//GEN-END:variables
    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;
    private JDialog aboutBox;
}
