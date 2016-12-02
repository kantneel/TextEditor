
/**
 * Created by neelkant on 2/29/16.
 */
public class TestCharList {

    public static void main(String[] args){
        CharList a = new CharList();
        CharList b = new CharList();
        for (int i = 65; i < 75; i++) {
            a.addLast(new CharLink((char) i));
        }
        for (int i = 75; i < 85; i++) {
            b.addLast(new CharLink((char) i));
        }

    }
}
