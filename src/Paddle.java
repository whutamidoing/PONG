import java.awt.*;
import java.awt.event.*;

public class Paddle extends Rectangle{

    int id;
    int yVelocity;
    int speed = 10;

    boolean skillReady = true;
    long skillCooldown = 0;
    static final long skillCooldownDuration = 60 * 10; // 10 seconds cooldown

    //Stun Mechanic
    boolean isStunned = false;
    long stunTimer = 0;
    static final long stunDuration = 60 * 1; // 1 second stun duration

    //Curveshot Mechanic
    boolean curveShotActive = false;

    //Powershot Mechanic
    boolean powerShotActive = false;


    Paddle(int x, int y, int PADDLE_WIDTH, int PADDLE_HEIGHT, int id) {
        super(x,y,PADDLE_WIDTH,PADDLE_HEIGHT);
        this.id = id;
    }


    public void keyPressed(KeyEvent e) {
        switch(id) {
            case 1 -> {
                if (e.getKeyCode() == KeyEvent.VK_W) {
                    setYDirection(-speed);
                }
                if (e.getKeyCode() == KeyEvent.VK_S) {
                    setYDirection(speed);
                }
            }
            case 2 -> {
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    setYDirection(-speed);
                }
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    setYDirection(speed);
                }
            }
        }
    }
    public void keyReleased(KeyEvent e) {
        switch(id) {
            case 1 -> {
                if (e.getKeyCode() == KeyEvent.VK_W) {
                    setYDirection(0);
                }
                if (e.getKeyCode() == KeyEvent.VK_S) {
                    setYDirection(0);
                }
            }
            case 2 -> {
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    setYDirection(0);
                }
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    setYDirection(0);
                }
            }
        }
    }
    public void move(){
        if (isStunned) {
            yVelocity = 0; 
            stunTimer++;
            if (stunTimer >= stunDuration) {
                isStunned = false;
                stunTimer = 0;
            }
        }
        if(!skillReady) {
            skillCooldown++;
            if (skillCooldown >= skillCooldownDuration) {
                skillReady = true;
                skillCooldown = 0;
            }
        }

        y += yVelocity;
    }
    public void setYDirection(int yDirection) {
        if(!isStunned) {
            yVelocity = yDirection;
        }
    }
    public void draw(Graphics g) {
        if(isStunned) {
            g.setColor(Color.cyan);
        }
        else if(id ==1) {
            g.setColor(Color.blue);
        }
        else {
            g.setColor(Color.red);
        }
        g.fillRect(x, y, width, height);
    }
}   