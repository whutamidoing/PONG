import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class GamePanel extends JPanel implements Runnable {

    static final int GAME_WIDTH = 1440;
    static final int GAME_HEIGHT = (int)(GAME_WIDTH * (0.5555));
    static final Dimension SCREEN_SIZE = new Dimension(GAME_WIDTH, GAME_HEIGHT);
    static final int BALL_DIAMETER = 25;
    static final int PADDLE_WIDTH = 25;
    static final int PADDLE_HEIGHT = (int)(GAME_HEIGHT * 0.20);
    Thread gameThread;
    Random random;
    Paddle paddle1;
    Paddle paddle2;
    Ball ball;
    Score score;
    ProgressBar progressBar1;
    ProgressBar progressBar2;

    GamePanel() {
        random = new Random();
        newPaddles();
        newBall();
        newProgressBar();
        score = new Score(GAME_WIDTH, GAME_HEIGHT);
        this.setFocusable(true);
        this.addKeyListener(new AL());
        this.setPreferredSize(SCREEN_SIZE);
        this.requestFocusInWindow();
        this.setBackground(Color.black);

        gameThread = new Thread(this);
        gameThread.start();
    }
    
    public void newBall() {
        ball = new Ball((GAME_WIDTH/2)-(BALL_DIAMETER/2), random.nextInt(GAME_HEIGHT-BALL_DIAMETER), BALL_DIAMETER, BALL_DIAMETER);
    }

    public void newPaddles() {
        paddle1 = new Paddle(0+50, (GAME_HEIGHT/2)-(PADDLE_HEIGHT/2), PADDLE_WIDTH, PADDLE_HEIGHT, 1);
        paddle2 = new Paddle(GAME_WIDTH-PADDLE_WIDTH-50, (GAME_HEIGHT/2)-(PADDLE_HEIGHT/2), PADDLE_WIDTH, PADDLE_HEIGHT, 2);
    }

    public void newProgressBar() {
        progressBar1 = new ProgressBar(100, 70, 150, 10);
        progressBar2 = new ProgressBar(GAME_WIDTH-250,70, 150,10);
    }
    
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if(paddle1.skillReady) {
            progressBar1.progress = progressBar1.width;
        }else {
            progressBar1.progress = (int)((paddle1.skillCooldown / (double)Paddle.SKILL_COOLDOWN_DURATION) * progressBar1.width) ;
        }
        if(paddle2.skillReady) {
            progressBar2.progress = progressBar2.width;
        } else {
            progressBar2.progress = (int)((paddle2.skillCooldown / (double)Paddle.SKILL_COOLDOWN_DURATION) * progressBar2.width);            
        }

        paddle1.draw(g);
        paddle2.draw(g);
        ball.draw(g);
        score.draw(g);
        progressBar1.draw(g);
        progressBar2.draw(g);
    }

    public void move() {
        paddle1.move();
        paddle2.move();
        ball.move();
    }

    public void checkCollision() {
        if(ball.y <= 0) {
            ball.setYDirection(-ball.yVelocity);
        }
        if(ball.y >= GAME_HEIGHT - BALL_DIAMETER) {
            ball.setYDirection(-ball.yVelocity);
        }
        if(ball.intersects(paddle1)) {
            ball.setXDirection(-ball.xVelocity);
            if(ball.xVelocity > 0) {
                ball.xVelocity++;
            } else {
                ball.xVelocity--;
            }
            ball.setXDirection(ball.xVelocity);
            ball.setYDirection(ball.yVelocity);

            ball.isCurveApplied = false;
            ball.isBoostedApplied = false;

            if(paddle1.curveShotActive) {
                ball.isCurveApplied = true;
                paddle1.curveShotActive = false;
                SoundPlayer.playSound("res/Zombie.wav", false);
            }
            else if(paddle1.powerShotActive) {
                ball.isBoostedApplied = true;
                paddle1.powerShotActive = false;
                SoundPlayer.playSound("res/impact.wav", false);
            }
            else {
                SoundPlayer.playSound("res/harpfsh.wav", false);
            }
        }
        if(ball.intersects(paddle2)) {
            ball.setXDirection(-ball.xVelocity);
            if(ball.xVelocity >0) {
                ball.xVelocity++;
            } else {
                ball.xVelocity--;
            }
            ball.setXDirection(ball.xVelocity);
            ball.setYDirection(ball.yVelocity);

            ball.isCurveApplied = false;
            ball.isBoostedApplied = false;

            if(paddle2.curveShotActive) {
                ball.isCurveApplied = true;
                paddle2.curveShotActive = false;
                SoundPlayer.playSound("res/Zombie.wav", false);
            }
            else if(paddle2.powerShotActive) {
                ball.isBoostedApplied = true;
                paddle2.powerShotActive = false;
                SoundPlayer.playSound("res/impact.wav", false);
            }
            else {
                SoundPlayer.playSound("res/harplongf.wav", false);
            }
        }

        if(paddle1.y <= 0) {
            paddle1.y = 0;
        }
        if(paddle1.y + paddle1.height >= GAME_HEIGHT) {
            paddle1.y = GAME_HEIGHT - paddle1.height;
        }
        if(paddle2.y <= 0) {
            paddle2.y = 0;
        }
        if(paddle2.y + paddle2.height >= GAME_HEIGHT) {
            paddle2.y = GAME_HEIGHT - paddle2.height;
        }

        if(ball.x <= 0) {
            score.player2Score++;
            newPaddles();
            newBall();
            SoundPlayer.playSound("res/Blue-Lock.wav", false);
            System.err.println("Player 2: " + score.player2Score);
        }
        if(ball.x >= GAME_WIDTH - BALL_DIAMETER) {
            score.player1Score++;
            newPaddles();
            newBall();
            SoundPlayer.playSound("res/Blue-Lock.wav", false);
            System.err.println("Player 1: " + score.player1Score);
        }
    }

    //Gameloop
    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        while(true) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            if (delta >= 1) {
                move();
                checkCollision();
                repaint();
                delta--;
            }
        }
    }

    public class AL extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            paddle1.keyPressed(e);
            paddle2.keyPressed(e);

            if (e.getKeyCode() == KeyEvent.VK_E) { 
                // Player1 stuns Player2
                if (!paddle2.isStunned && paddle1.skillReady) {
                    paddle2.isStunned = true;
                    paddle2.stunTimer = 0;
                    paddle1.skillReady = false;
                    paddle1.skillCooldown = 0;
                    SoundPlayer.playSound("res/You-Crying.wav", false);
                    SoundPlayer.playSound("res/metavision.wav", false);
                    System.out.println("Player 1 stunned Player 2");
                }
            }
            if (e.getKeyCode() == KeyEvent.VK_L) { 
                // Player2 stuns Player1
                if (!paddle1.isStunned && paddle2.skillReady) {
                    paddle1.isStunned = true;
                    paddle1.stunTimer = 0;
                    paddle2.skillReady = false;
                    paddle2.skillCooldown = 0;
                    SoundPlayer.playSound("res/You-Crying.wav", false);
                    SoundPlayer.playSound("res/metavision.wav", false);
                    System.out.println("Player 2 stunned Player 1");
                }
            }
            if(e.getKeyCode() == KeyEvent.VK_R) {
                // Curveshot for Player1
                if (paddle1.skillReady) {
                    paddle1.curveShotActive = true;
                    paddle1.skillReady = false;
                    paddle1.skillCooldown = 0;
                    SoundPlayer.playSound("res/metavision.wav", false);
                    System.out.println("Player 1 used Curveshot");
                }
            }
            if(e.getKeyCode() == KeyEvent.VK_K) {
                // Curveshot for Player2
                if (paddle2.skillReady) {
                    paddle2.curveShotActive = true;
                    paddle2.skillReady = false;
                    paddle2.skillCooldown = 0;
                    SoundPlayer.playSound("res/metavision.wav", false);
                    System.out.println("Player 2 used Curveshot");
                }
            }
            if(e.getKeyCode() == KeyEvent.VK_F){
                // Poweshot for Player1
                if(paddle1.skillReady){
                    paddle1.powerShotActive = true;
                    paddle1.skillReady = false;
                    paddle1.skillCooldown = 0;
                    SoundPlayer.playSound("res/metavision.wav", false);
                    System.out.println("Player 1 used Powershot");
                }
            }
            if(e.getKeyCode() == KeyEvent.VK_M){
                // Poweshot for Player2
                if(paddle2.skillReady){
                    paddle2.powerShotActive = true;
                    paddle2.skillReady = false;
                    paddle2.skillCooldown = 0;
                    SoundPlayer.playSound("res/metavision.wav", false);
                    System.out.println("Player 2 used Powershot");
                }
            }
        }
        @Override
        public void keyReleased(KeyEvent e) {
            paddle1.keyReleased(e);
            paddle2.keyReleased(e);
        }
    }
}
