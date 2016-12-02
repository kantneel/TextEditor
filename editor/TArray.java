import javafx.scene.text.Font;

/**
 * Created by neelkant on 3/1/16.
 */
public class TArray {
    /** A TArray (TextArray) is an array that contains many CharLists.
     *  Each CharList will occupy a different line, and using the totWidth of each CharList,
     *  we can figure out when to move onto the next line (next slot in the TArray).
     *  The TArray is also useful since the height attribute enables us to accurately determine
     *  which line the mouse click occurred on, and so a location in the CharList can be determined as well.
     */
    private int height;
    public CharList[] lines;
    private int maxLine;
    public Font font;

    public TArray(Font f) {
        lines = new CharList[1000];
        lines[0] = new CharList();
        height = (int) f.getSize() * 5/4;
        maxLine = 0;
        font = f;
    }

    public CharList getList(int index) {
        return lines[index];
    }

    public int height() {
        return height;
    }

    public int getMaxLine() {
        return maxLine;
    }

    public void setMaxLine(int i) {
        maxLine = i;
        lines[i] = new CharList();
    }

    public String makeString(int start, int end) {
        String result = "";
        for (int i = start; i <= end; i++) {
            if (lines[i] != null) {
                result += lines[i].getString();
            }
        }
        return result;
    }

    public String makeString() {
        return makeString(0, maxLine);
    }

    public void addTo(int index, int xPos, char c) {
        if (lines[index] == null) {
            lines[index] = new CharList();
        }
        lines[index].addAt(new CharLink(c), xPos);

    }

    public void populate(char c) {
        CharLink cs = new CharLink(c);
        if (c == '\n' || c == '\r') {
            lines[maxLine].addLast(new CharLink('\n'));
            maxLine++;
            lines[maxLine] = new CharList();
        }
        else if (lines[maxLine].getTotWidth() + cs.width() < lines[maxLine].getWidthLim()) {
            lines[maxLine].addLast(cs);
        }
        else {
            maxLine++;
            lines[maxLine] = new CharList();
            lines[maxLine].addLast(cs);
        }
    }

    public TArray render(Font font) {
        String currentString = makeString();
        TArray repo = new TArray(font);
        while (currentString.length() > 0) {
            char c = currentString.charAt(0);
            CharLink cs = new CharLink(c, font);
            if (c == '\n' || c == '\r') {
                repo.lines[repo.maxLine].addLast(new CharLink('\n'));
                repo.maxLine++;
                repo.lines[repo.maxLine] = new CharList();
            } else if (repo.lines[repo.maxLine].getTotWidth() + cs.width() < repo.lines[repo.maxLine].getWidthLim()) {
                repo.lines[repo.maxLine].addLast(cs);
            } else {
                repo.maxLine++;
                repo.lines[repo.maxLine] = new CharList();
                repo.lines[repo.maxLine].addLast(cs);

            }
            if (currentString.length() == 1) {
                currentString = "";
            } else {
                currentString = currentString.substring(1);
            }
        }
        return repo;
    }

    public void removeFrom(int index, int xPos, Cursor c) {
        if (lines[index].size() == 1) {
            lines[index] = null;
            maxLine--;

            Editor.setCurrentLine(Math.max(0, index - 1));
            assignPointer(Editor.getCurrentLine(), (int) lines[Editor.getCurrentLine()].getTotWidth() + 5);
            int[] newPos = cursorAssignPos((int) lines[Editor.getCurrentLine()].getTotWidth() + 5, Editor.getCurrentLine() * height);
            Editor.cursor.setX(newPos[0]);
            Editor.cursor.setY(newPos[1]);
        }
        else {
            assignPointer(index, xPos);
            lines[index].pointerRemove(c);

        }
    }

    public void assignPointer(int line, int xPos) {
        lines[line].assignPointer(xPos);
    }

    public void changeHeight(int i) {
        height = i;
    }

    public int[] cursorAssignPos(int x, int y) {
        int line = y / height;
        if (y < 0) {
            return new int[]{0, 0};
        }
        if (line > maxLine) {
            line = maxLine;
        }

        assignPointer(line, x);
        return new int[]{lines[line].intCharAt(x), line * height};
    }

}
