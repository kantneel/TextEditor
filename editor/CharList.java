

import java.util.LinkedList;


/**
 * Created by neelkant on 2/28/16.
 */
public class CharList extends LinkedList {

    public CharLink sentinel;
    private CharLink pointer;
    private int size;
    private double totWidth;
    private static int widthLim = 500;

    public CharList() {
        sentinel = new CharLink('#');
        size = 0;
        totWidth = 0;
        pointer = sentinel;
    }

    public void addLast(CharLink c) {
        if (size == 0) {
            sentinel.addLink(c);
            c.addLink(sentinel);
        }
        else {
            sentinel.prev.addLink(c);
            c.addLink(sentinel);
        }
        size += 1;
        totWidth += c.width();
    }


    public int getWidthLim() {
        return widthLim;
    }

    @Override
    public int size() {
        return size;
    }

    public void addAt(CharLink c, int pixel) {
        CharLink loc = realCharAt(pixel);
        if (loc.next() == null) {
            loc.addLink(c);
        }
        else {
            CharLink temp = loc.next();
            loc.addLink(c);
            c.addLink(temp);
        }
        size += 1;
        totWidth += c.width();
    }

    public void pointerRemove(Cursor c) {
        if (pointer != null || pointer != sentinel) {
            totWidth -= pointer.width();
            c.setX(c.getX() - (int) Math.round(pointer.width()));
            pointer.prev.addLink(pointer.next);
            if (pointer.next == sentinel) {
                pointer.next = null;
            }
            size--;
        }
    }

    public void assignPointer(int pixel) {
        if (pixel < 0) {
            throw new RuntimeException("Invalid Pixel for given CharList");
        }
        if (size == 0) {
            pointer = sentinel;
        }
        else if (pixel > totWidth) {
            pointer = sentinel.prev;
        }
        else {
            int widthLeft = pixel;
            CharLink first = sentinel.next;
            while ((first.next != null && first.next != sentinel) && first.width() + first.next.width() < widthLeft) {
                widthLeft -= (int) Math.round(first.width());
                first = first.next();
            }
            pointer = first;
        }
    }

    @Override
    public CharLink get(int index) {
        if ((index >= size() && size != 0) || index < -1) {
            throw new RuntimeException("Invalid index for given CharList");
        }
        if (size == 0) {
            return sentinel;
        }
        CharLink first = sentinel.next();
        for (int i = 0; i < index; i++) {
            first = first.next();
        }
        return first;
    }

    public String getString() {
        String result = "";
        for (int i = 0; i < size(); i++) {
            result += get(i).item().getText();
        }
        return result;
    }

    public double getTotWidth() {
        return totWidth;
    }

    public int intCharAt(double pixel) {
        if (pixel < 0) {
            throw new RuntimeException("Invalid Pixel for given CharList");
        }
        else if (pixel > totWidth) {
            return (int) Math.round(totWidth);
        }
        int widthLeft = (int) pixel;
        CharLink first = sentinel.next();
        while (first.width() < widthLeft) {
            widthLeft -= (int) Math.round(first.width());
            first = first.next();
        }
        return (int) Math.round(pixel - widthLeft);
    }

    public CharLink realCharAt(int pixel) {
        if (pixel < 0) {
            return get(0);
        }
        if (size == 0) {
            return sentinel;
        }
        else if (pixel > totWidth) {
            return get(size - 1);
        }
        int widthLeft = (int) pixel;
        CharLink first = sentinel.next();
        while (first.width() < widthLeft) {
            widthLeft -= (int) Math.round(first.width());
            first = first.next();
        }
        return first;
    }




}
