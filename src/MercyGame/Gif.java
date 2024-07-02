package MercyGame;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class Gif {
    private List<Icon> frames = new ArrayList<Icon>();

    public List<Icon> getFrames() {
        List<Icon> _frames = new ArrayList<>(frames);
        return _frames;
    }

    public Gif(String gifFolder, String fileExtension) {
        int numOfFrames = new File(gifFolder).list().length;
        for (int i = 0; i < numOfFrames; i++) {
           Icon ico = new ImageIcon(gifFolder + "/Frame" + String.valueOf(i) + "." + fileExtension);
           frames.add(ico);
        }
    }
}

class GifThread extends Thread {
    private List<Icon> frames;
    private JLabel label;
    private int delay;
    private int executionTime;

    public int getExecutionTime() {
        return executionTime;
    }

    public GifThread(Gif gifToPlay, JLabel label, int delay) {
        this.frames = gifToPlay.getFrames();
        this.label = label;
        this.delay = Math.abs(delay);
        this.executionTime = this.delay * frames.size();
        if (delay < 0) {
            Collections.reverse(this.frames);
        }
    }

    public void run() {
        for (int i = 0; i < frames.size(); i++) {
            try {
                Thread.sleep(delay);
                label.setIcon(frames.get(i));
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.out.println("You should kill yourself now!");
            }
        }
    }
}
