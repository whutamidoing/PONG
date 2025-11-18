import java.awt.*;
import java.util.*;

public class Ball extends Rectangle {

    Random random;
    int xVelocity;
    int yVelocity;
    int speed = 2;

    boolean isCurveApplied = false;
    boolean isBoostedApplied = false;

    long curveTimer = 0;
    static final long curveDuration = 60 * 2; //120 frames
    int adjustedY = yVelocity;

    Ball(int x, int y, int width, int height) {
        super(x, y, width, height);
        random = new Random();

        int randomXDirection = random.nextInt(2);
        if(randomXDirection == 0) randomXDirection--;
        setXDirection(randomXDirection*speed);

        int yDirection = random.nextInt(2);
        if(yDirection == 0) yDirection--;
        setYDirection(yDirection*speed);
    }

    public void setXDirection(int randomXDirection) {
        xVelocity = randomXDirection;
    }
    public void setYDirection(int randomYDirection) {
        yVelocity = randomYDirection;
    }
    
    public void move() {
        // base velocities
        int dx = xVelocity;
        int dy = yVelocity;

        if (isCurveApplied) {
            double archHeight = 10;   // maximum vertical displacement
            double archLength = 100.0; // horizontal distance (in frames or pixels) of one full arch

            double frequency = (2 * Math.PI) / archLength;

            int curveOffset = (int)(Math.sin(curveTimer * frequency) * archHeight);
            dy += curveOffset;

            curveTimer++;
            if (curveTimer >= curveDuration) {
                isCurveApplied = false;
                curveTimer = 0;
            }
        }

        if(isBoostedApplied) {
            dx *= 2;
        }

        int newX = x + dx;
        int newY = y + dy;

        int minY = 0;
        int maxY = GamePanel.GAME_HEIGHT - this.height;

        while (newY < minY || newY > maxY) {
            if (newY < minY) {
                newY = minY + (minY - newY);
                yVelocity = Math.abs(yVelocity);
            } else if (newY > maxY) {
                newY = maxY - (newY - maxY);
                yVelocity = -Math.abs(yVelocity);
            }
        }

        x = newX;
        y = newY;
    }

    public void draw(Graphics g) {
        g.setColor(Color.white);
        g.fillOval(x, y, width, height);
    }
}
