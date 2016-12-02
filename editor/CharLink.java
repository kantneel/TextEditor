

import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Created by neelkant on 2/29/16.
 */
public class CharLink {
    private static final Font NORMAL = new Font("Verdana", 12);
    private Text ch;
    public CharLink next;
    public CharLink prev;
    private double chWidth;

    public CharLink(char character, CharLink n, Font f) {
        ch = new Text(String.valueOf(character));
        next = n;
        ch.setFont(f);
        chWidth = ch.getLayoutBounds().getWidth();
    }

    public CharLink(char character, CharLink n) {
        this(character, n, NORMAL);
    }

    public CharLink(char character) {
        this(character, null, NORMAL);
    }

    public CharLink(char character, Font f) {
        this(character, null, f);
    }

    public Text item() {
        return ch;
    }

    public CharLink next() {
        if (next == null) {
            return this;
        }
        return next;
    }

    public double width(){
        return chWidth;
    }

    public void addLink(CharLink c) {
        next = c;
        c.prev = this;
    }

}
