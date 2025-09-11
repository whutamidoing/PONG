import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        GameFrame frame = new GameFrame();
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
    }
}       