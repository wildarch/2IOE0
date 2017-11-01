package nl.tue.c2IOE0.group5.providers;

import nl.tue.c2IOE0.group5.engine.Cleanable;
import nl.tue.c2IOE0.group5.engine.Engine;
import nl.tue.c2IOE0.group5.engine.Timer;
import nl.tue.c2IOE0.group5.engine.provider.Provider;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.engine.rendering.Window;
import nl.tue.c2IOE0.group5.util.Resource;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Tom Peters
 */

public class MusicProvider extends Thread implements Provider<Engine>,Cleanable {

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
        if (!on) return;
        try {
            InputStream fileStream = new BufferedInputStream(Resource.get("/music/SICCMIXX.wav"));
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(fileStream);
            AudioFormat format = audioInputStream.getFormat();
            long frames = audioInputStream.getFrameLength();
            this.duration = (long)((frames+0.0) / format.getFrameRate()); //in seconds
            this.clip = AudioSystem.getClip();
            this.clip.open(audioInputStream);
            this.gainControl =
                    (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(baseVolume);
            this.clip.start();
        } catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
            throw new RuntimeException("Failed to play music: " + e.getMessage());
        }
    }

    @Override
    public void update() {
        if (timeToPlay < loopTimer.getTime()) { //start again after 2 times the duration
            if(clip != null) clip.start();
            timeToPlay = loopTimer.getTime() + duration * 1000 * 2; //1000 to convert to miliseconds
        }
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
    private Thread fadeThread = new Thread();
    private volatile boolean cancelled = false;

    public void fadeVolumeTo(float value, boolean overridePrevious) {
        currentVolume = gainControl.getValue();
        if (!fading && currentVolume != value && !overridePrevious) {  //prevent running it twice
            targetVolume = value;
            fadeThread = new Thread(this);
            fadeThread.start();
        } else if (overridePrevious) {
            //cancel fadeThread
            cancelled = true;
            //wait for it to finish
            waitForMusicThread(fadeThread);
            cancelled = false;
            //start fadeThread again
            targetVolume = value;
            fadeThread = new Thread(this);
            fadeThread.start();
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
                if (currentVolume - fadeStep > -60) {
                    currentVolume -= fadeStep;
                } else {
                    currentVolume = -60;//minimum no clue why
                }
                gainControl.setValue(currentVolume);
                try {
                    fadeThread.sleep(1);
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
                if (currentVolume + fadeStep > 6) {
                    currentVolume += fadeStep;
                } else {
                    currentVolume = 6;//maximum no clue why
                }
                gainControl.setValue(currentVolume);
                try {
                    fadeThread.sleep(1);
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

    public boolean isOn() {
        return on;
    }

    private void waitForMusicThread(Thread t) {
        try {
            t.join();
        } catch(InterruptedException e) {
            System.err.println("Interrupted while waiting for music thread to join");
            e.printStackTrace();
        }
    }

    @Override
    public void cleanup() {
        if (clip != null)
            clip.stop();
        cancelled = true;
        waitForMusicThread(fadeThread);
        cancelled = false;
    }
}
