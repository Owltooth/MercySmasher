package MercyGame;
import java.io.File;
import javax.sound.sampled.*;

public class AudioPlayer {

    AudioInputStream audioInputStream;
    Clip clip;

    public AudioPlayer(String audioPath) {
        try {
            audioInputStream = AudioSystem.getAudioInputStream(new File(audioPath).getAbsoluteFile());
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void play() {
        clip.start();
    }
}
