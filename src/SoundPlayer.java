import java.io.File;
import java.io.IOException;
import javax.sound.sampled.*;

public class SoundPlayer {
    public static void playSound(String filePath, boolean shouldLoop) {
        try {
            File soundFile = new File(filePath);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);

            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float min = gainControl.getMinimum();
            float max = gainControl.getMaximum();
            
             if (shouldLoop) {     
                float dB = (max - min) * 0.75f + min;
                gainControl.setValue(dB);
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            } else {
                float dB = (max - min) * 0.9f + min;
                gainControl.setValue(dB);
                clip.start();
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Error playing sound: " + e.getMessage());
        }
    }
}
