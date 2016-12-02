
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ScrollBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.*;
import java.util.LinkedList;


public class Editor extends Application {
    private int WINDOW_WIDTH = 500;
    private int WINDOW_HEIGHT = 500;
    private static final Font NORMAL = new Font("Verdana", 12);
    private static int currentLine = 0;
    public static Cursor cursor = new Cursor();
    static TArray allText = new TArray(NORMAL);
    public static Text displayText = new Text(250, 250, "");
    private static String filename;
    private LinkedList<TArray> undos = new LinkedList<>();
    private LinkedList<int[]> cursors = new LinkedList<>();
    private LinkedList<TArray> redos = new LinkedList<>();
    private LinkedList<int[]> cursorsTwo = new LinkedList<>();

    private class KeyEventHandler implements EventHandler<KeyEvent>{

        int textCenterX;
        int textCenterY;

        /** The Text to display on the screen. */

        public KeyEventHandler(final Group root, int windowWidth, int windowHeight) {
            textCenterX = windowWidth / 2;
            textCenterY = windowHeight / 2;

            // Always set the text origin to be VPos.TOP! Setting the origin to be VPos.TOP means
            // that when the text is assigned a y-position, that position corresponds to the
            // highest position across all letters (for example, the top of a letter like "I", as
            // opposed to the top of a letter like "e"), which makes calculating positions much
            // simpler!
            displayText.setTextOrigin(VPos.TOP);
            displayText.setFont(NORMAL);
            displayText.setWrappingWidth(windowWidth);

            // All new Nodes need to be added to the root in order to be displayed.
        }

        @Override
        public void handle(KeyEvent keyEvent) {
            if (keyEvent.getEventType() == KeyEvent.KEY_TYPED && !keyEvent.isShortcutDown()) {
                // Use the KEY_TYPED event rather than KEY_PRESSED for letter keys, because with
                // the KEY_TYPED event, javafx handles the "Shift" key and associated
                // capitalization.
                String characterTyped = keyEvent.getCharacter();
                if (characterTyped.length() > 0 && characterTyped.charAt(0) != 8) {
                    char actualTyped = characterTyped.charAt(0);
                    pushUndoStack(true);

                    allText.addTo(currentLine, (int) cursor.getX(), actualTyped);
                    displayText.setText(allText.makeString());

                    CharLink temp = new CharLink(actualTyped, allText.font);
                    cursor.setX(cursor.getX() + temp.width());
                    keyEvent.consume();
                    centerText();
                }
            } else if (keyEvent.getEventType() == KeyEvent.KEY_PRESSED) {
                // Arrow keys should be processed using the KEY_PRESSED event, because KEY_PRESSED
                // events have a code that we can check (KEY_TYPED events don't have an associated
                // KeyCode).
                KeyCode code = keyEvent.getCode();
                if (code == KeyCode.UP && currentLine != 0) {
                    moveCursorUp();
                }
                else if (code == KeyCode.DOWN && currentLine != allText.getMaxLine()) {
                    moveCursorDown();
                }
                else if (code == KeyCode.LEFT && cursor.getX() > 5) {
                    moveCursorLeft();
                }
                else if (code == KeyCode.RIGHT && cursor.getX() - 5 < allText.getList(currentLine).getTotWidth()) {
                    moveCursorRight();
                }
                else if (code == KeyCode.BACK_SPACE) {
                    pushUndoStack(true);
                    allText.removeFrom(currentLine, (int) cursor.getX(), cursor);
                    displayText.setText(allText.makeString());
                }
                else if (code == KeyCode.ENTER) {
                    pushUndoStack(true);
                    allText.setMaxLine(allText.getMaxLine() + 1);
                    currentLine++;
                    cursor.setY(allText.cursorAssignPos(5, (int) cursor.getY() + allText.height())[1]);
                    cursor.setX(5);
                }
                else if (keyEvent.isShortcutDown()) {
                    if (keyEvent.getCode() == KeyCode.S) {
                        saveFile();
                    }
                    else if (keyEvent.getCode() == KeyCode.P) {
                        System.out.println(cursor.getX() + " " + cursor.getY());
                    }
                    else if (keyEvent.getCode() == KeyCode.EQUALS) {
                        pushUndoStack(true);
                        Font f = new Font(displayText.getFont().getName(), displayText.getFont().getSize() + 4);
                        displayText.setFont(f);
                        cursor.setHeight((int) f.getSize() * 4 / 3);
                        allText = allText.render(f);
                        displayText.setText(allText.makeString());
                    }
                    else if (keyEvent.getCode() == KeyCode.MINUS) {
                        pushUndoStack(true);
                        Font f = new Font(displayText.getFont().getName(), Math.max(1, displayText.getFont().getSize() - 4));
                        displayText.setFont(f);
                        cursor.setHeight((int) f.getSize() * 4 / 3);
                        allText = allText.render(f);
                        cursor.setX(allText.cursorAssignPos((int)allText.getList(currentLine).getTotWidth(), (int)(currentLine * f.getSize() * 4/3))[0]);
                        displayText.setText(allText.makeString());
                    }
                    else if (keyEvent.getCode() == KeyCode.Z && undos.size() != 0) {
                        pushRedoStack(allText.render(allText.font));
                        allText = undos.removeFirst();
                        displayText.setText(allText.makeString());
                        int[] pos = cursors.removeFirst();
                        cursor.setX(pos[0]);
                        cursor.setY(pos[1]);
                    }
                    else if(keyEvent.getCode() == KeyCode.Y && redos.size() != 0) {
                        pushUndoStack(false);
                        allText = redos.removeFirst();
                        displayText.setText(allText.makeString());
                        int[] pos = cursorsTwo.removeFirst();
                        cursor.setX(pos[0]);
                        cursor.setY(pos[1]);
                    }
                }
                keyEvent.consume();
                centerText();
            }
        }

        private void moveCursorUp() {
            currentLine = currentLine - 1;
            cursor.setX(5 + (int) Math.round(allText.getList(currentLine).intCharAt((int) cursor.getX())));
            cursor.setY(14.5 * currentLine);
        }

        private void moveCursorDown() {
            currentLine = Math.min(allText.getMaxLine(), currentLine + 1);
            cursor.setX(5 + (int) Math.round(allText.getList(currentLine).intCharAt((int) cursor.getX())));
            cursor.setY(14.5 * currentLine);
        }

        private void moveCursorLeft() {
            cursor.setX(cursor.getX() -
                    (int) Math.round(allText.getList(currentLine).realCharAt((int) Math.round(cursor.getX())).width()));
        }

        private void moveCursorRight() {
            cursor.setX(cursor.getX() +
                    (int) Math.round(allText.getList(currentLine).realCharAt((int) Math.round(cursor.getX())).next.width()));
        }

        private void centerText() {

            double textTop = 0;
            double textLeft = 5;

            displayText.setX(textLeft);
            displayText.setY(textTop);
            displayText.toFront();
        }
    }

    private void pushUndoStack(boolean b) {
        if (b) {
            redos = new LinkedList<>();
        }
        if (undos.size() == 100) {
            undos.removeLast();
        }
        undos.addFirst(allText.render(allText.font));
        int[] pos = new int[2];
        pos[0] = (int) cursor.getX();
        pos[1] = (int) cursor.getY();
        cursors.addFirst(pos);
    }

    private void pushRedoStack(TArray t) {
        redos.addFirst(t);
        int[] pos = new int[2];
        pos[0] = (int) cursor.getX();
        pos[1] = (int) cursor.getY();
        cursorsTwo.addFirst(pos);
    }



    private class MouseClickEventHandler implements EventHandler<MouseEvent> {
        /** A Text object that will be used to print the current mouse position. */

        MouseClickEventHandler(Group root) {
            root.getChildren().add(cursor.rec());
        }


        @Override
        public void handle(MouseEvent mouseEvent) {
            int mousePressedX = (int) mouseEvent.getX();
            int mousePressedY = (int) mouseEvent.getY();

            int[] coors = allText.cursorAssignPos(mousePressedX, mousePressedY);
            cursor.setX(coors[0] + 5);
            cursor.setY(coors[1]);
            currentLine = (int) Math.round(coors[1] / 14.5);
        }
    }


    @Override
    public void start(Stage primaryStage) {
        // Create a Node that will be the parent of all things displayed on the screen.
        Group root = new Group();
        // The Scene represents the window: its height and width will be the height and width
        // of the window displayed.
        int windowWidth = 500;
        int windowHeight = 500;
        Scene scene = new Scene(root, windowWidth, windowHeight, Color.WHITE);

        displayText.setX(5);
        displayText.setY(0);
        displayText.toFront();
        // To get information about what keys the user is pressing, create an EventHandler.
        // EventHandler subclasses must override the "handle" function, which will be called
        // by javafx.

        /** KeyEventHandler and MouseClickEventHandler **/

        EventHandler<KeyEvent> keyEventHandler =
                new KeyEventHandler(root, windowWidth, windowHeight);
        scene.setOnKeyTyped(keyEventHandler);
        scene.setOnKeyPressed(keyEventHandler);

        EventHandler<MouseEvent> mouseClickEventHandler =
                new MouseClickEventHandler(root);
        scene.setOnMouseClicked(mouseClickEventHandler);

        primaryStage.setTitle("OP Editor");

        /** Setting up basic scrollBar - no real functionality yet **/

        ScrollBar scrollBar = new ScrollBar();
        scrollBar.setOrientation(Orientation.VERTICAL);
        scrollBar.setPrefHeight(WINDOW_HEIGHT);
        root.getChildren().add(scrollBar);
        root.getChildren().add(displayText);



        double usableScreenWidth = WINDOW_WIDTH - scrollBar.getLayoutBounds().getWidth();
        scrollBar.setLayoutX(usableScreenWidth);

        /** Setting up the cursor, will test **/
        cursor.makeRectangleColorChange();


        // This is boilerplate, necessary to setup the window where things are displayed.
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void saveFile() {
        try {
            File f = new File(filename);
            FileWriter fw = new FileWriter(f.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(displayText.getText());
            bw.close();
        }
        catch (IOException e) {
            System.out.println("Unable to open file " + filename);
        }
    }
    public static void openFile(String s) {
        BufferedReader br;
        try {
            filename = s;
            File f = new File(s);
            if (!f.exists()) {
                f.createNewFile();
            }
            br = new BufferedReader(new FileReader(s));
            int intRead = -1;
            while ((intRead = br.read()) != -1) {
                // The integer read can be cast to a char, because we're assuming ASCII.
                char charRead = (char) intRead;
                allText.populate(charRead);
            }
            br.close();

        }
        catch (IOException e) {
            System.out.println("Unable to open file " + s);
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Error: Need a file.");
        }
        else {
            openFile(args[0]);
            launch(args);
        }
    }

    public static void setCurrentLine(int i) {
        currentLine = i;
    }

    public static int getCurrentLine() {
        return currentLine;
    }
}