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

public class MusicProvider extends Thread implements Provider<Engine> {

    private Engine engine;
    private Clip clip;
    private FloatControl gainControl;
    private long duration;
    private Timer loopTimer;
    private long timeToPlay;
    private final float maxVolume = 6f;
    private float baseVolume = 5f;
    private final float minVolume = -20f;

    @Override
    public void init(Engine engine) {
        this.loopTimer = engine.getRenderLoopTimer();
        this.engine = engine;
        try {
            String file = "res/soundfile.wav";
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(file));
            AudioFormat format = audioInputStream.getFormat();
            long frames = audioInputStream.getFrameLength();
            this.duration = (long)((frames+0.0) / format.getFrameRate()); //in seconds
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
        if (timeToPlay < loopTimer.getLoopTime()) { //start again after 2 times the duration
            clip.start();
            timeToPlay = loopTimer.getLoopTime() + duration * 1000 * 2; //1000 to convert to miliseconds
        }
    }

    public void cleanup() {
        if (clip != null)
            clip.stop();
        cancelled = true;
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        cancelled = false;
    }

    private boolean on = true;
    public void toggle() {
        if (on) {
            on = false;
            cleanup();
        } else {
            on = true;
            init(engine);
        }
    }

    public void setBaseVolume(float percentage) {
        this.baseVolume = percentage * (maxVolume - minVolume) + minVolume;
        fadeVolumeTo(baseVolume, true);
    }

    //thread parameters
    private float targetVolume;
    private float currentVolume;
    private boolean fading = false;
    private float fadeStep = 0.1f;
    Thread t = new Thread();
    private volatile boolean cancelled = false;

    public void fadeVolumeTo(float value, boolean overridePrevious) {
        currentVolume = gainControl.getValue();
        if (!fading && currentVolume != value && !overridePrevious) {  //prevent running it twice
            targetVolume = value;
            t = new Thread(this);
            t.start();
        } else if (overridePrevious) {
            //cancel t
            cancelled = true;
            //wait for it to finish
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            cancelled = false;
            //start t again
            targetVolume = value;
            t = new Thread(this);
            t.start();
        }
    }

    @Override
    public void run() {
        fading = true;
        if (currentVolume > targetVolume) {
            while (currentVolume > targetVolume) {
                if (cancelled) {
                    cancelled = false;
                    fading = false;
                    return;
                }
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
                if (cancelled) {
                    cancelled = false;
                    fading = false;
                    return;
                }
                currentVolume += fadeStep;
                gainControl.setValue(currentVolume);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        cancelled = false;
        fading = false;
    }

    @Override
    public void draw(Window window, Renderer renderer) {

    }
}
