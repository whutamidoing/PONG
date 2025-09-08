import java.awt.*;

public class ProgressBar extends Rectangle {
    int progress;
    
    ProgressBar(int x, int y, int width, int height) {
        super(x, y, width, height);
    }
    public void draw(Graphics g) {
        g.setColor(Color.gray);
        g.fillRect(x, y, width, height);

        g.setColor(Color.green);
        g.fillRect(x, y, progress, height);
    }
}
