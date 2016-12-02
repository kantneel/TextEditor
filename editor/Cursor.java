

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * Created by neelkant on 3/1/16.
 */
public class Cursor {

    private static final Color B = Color.BLACK;
    private static int width = 1;
    private static int height = 16;
    private Rectangle rec;

    public Cursor(){
        rec = new Rectangle(width, height);
        setX(5);
    }

    private class RectangleBlinkEventHandler implements EventHandler<ActionEvent> {
        private int currentColorIndex = 0;
        private Color[] boxColors =
                {Color.BLACK, Color.WHITE};

        RectangleBlinkEventHandler() {
            // Set the color to be the first color in the list.
            changeColor();
        }

        private void changeColor() {
            rec.setFill(boxColors[currentColorIndex]);
            currentColorIndex = 1 - currentColorIndex;
        }

        @Override
        public void handle(ActionEvent event) {
            changeColor();
        }
    }

    public void makeRectangleColorChange() {
        // Create a Timeline that will call the "handle" function of RectangleBlinkEventHandler
        // every 1 second.
        final Timeline timeline = new Timeline();
        // The rectangle should continue blinking forever.
        timeline.setCycleCount(Timeline.INDEFINITE);
        RectangleBlinkEventHandler cursorChange = new RectangleBlinkEventHandler();
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(1), cursorChange);
        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
    }

    public double getX(){
        return rec.getX();
    }

    public double getY(){
        return rec.getY();
    }

    public void setX(double d){
        rec.setX(d);
    }

    public void setY(double d){
        rec.setY(d);
    }

    public int getHeight() {
       return height;
    }

    public int getWidth() {
        return width;
    }

    public void setHeight(int i) {
       rec.setHeight(i);
    }

    public Rectangle rec(){
        return rec;
    }
}
