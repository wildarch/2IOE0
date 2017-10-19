package nl.tue.c2IOE0.group5.providers;

import nl.tue.c2IOE0.group5.engine.Engine;
import nl.tue.c2IOE0.group5.engine.Timer;
import nl.tue.c2IOE0.group5.engine.provider.Provider;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.engine.rendering.Window;

import javax.sound.sampled.*;
import java.io.File;

/**
 * @author Tom Peters
 */

public class MusicProvider extends Thread implements Provider {

    private Engine engine;
    private Clip clip;
    private FloatControl gainControl;
    private long duration;
    private Timer loopTimer;
    private long timeToPlay;
    private float baseVolume = 5f;
    private float playVolume = 0f;

    @Override
    public void init(Engine engine) {
        this.loopTimer = engine.getRenderLoopTimer();
        this.engine = engine;
        try {
            String file = "res/soundfile.wav";
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(file));
            AudioFormat format = audioInputStream.getFormat();
            long frames = audioInputStream.getFrameLength();
            this.duration = (long)((frames+0.0) / format.getFrameRate());
            this.clip = AudioSystem.getClip();
            this.clip.open(audioInputStream);
            this.gainControl =
                    (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(baseVolume);
            this.clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update() {
        if (engine.isPaused()) {
            fadeVolumeTo(baseVolume);
        } else {
            fadeVolumeTo(playVolume);
        }
        if (timeToPlay < loopTimer.getLoopTime()) { //start again after 2 times the duration
            clip.start();
            timeToPlay = loopTimer.getLoopTime() + duration * 2;
        }
    }

    public void stopMusic() {
        clip.stop();
    }

    public void setBaseVolume(float volume) {
        this.baseVolume = volume;
    }

    private float targetVolume;
    private float currentVolume;
    private boolean fading = false;
    private float fadeStep = 0.1f;

    public void fadeVolumeTo(float value) {
        currentVolume = gainControl.getValue();
        if (!fading && currentVolume != value) {  //prevent running it twice
            targetVolume = value;
            Thread t = new Thread(this);
            t.start();
        }
    }

    @Override
    public void run() {
        fading = true;

        if (currentVolume > targetVolume) {
            while (currentVolume > targetVolume) {
                currentVolume -= fadeStep;
                gainControl.setValue(currentVolume);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else if (currentVolume < targetVolume) {
            while (currentVolume < targetVolume) {
                currentVolume += fadeStep;
                gainControl.setValue(currentVolume);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        fading = false;
    }

    @Override
    public void draw(Window window, Renderer renderer) {

    }
}
