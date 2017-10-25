package nl.tue.c2IOE0.group5.providers;

import nl.tue.c2IOE0.group5.util.AnimatedObjectRecord;
import nl.tue.c2IOE0.group5.engine.Engine;
import nl.tue.c2IOE0.group5.engine.Timer;
import nl.tue.c2IOE0.group5.engine.objects.Animatable;
import nl.tue.c2IOE0.group5.engine.provider.Provider;
import nl.tue.c2IOE0.group5.engine.rendering.Renderer;
import nl.tue.c2IOE0.group5.engine.rendering.Window;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Geert van Ieperen
 * a provider that lets all Animatables make their animations and movements based on frames
 * created on 13-10-2017.
 */
public class AnimationProvider implements Provider<Engine> {
    private Set<AnimatedObjectRecord> targets;
    private Timer timer;
    private Engine engine;

    @Override
    public void init(Engine engine) {
        this.engine = engine;
        targets = new HashSet<>();
        timer = engine.getRenderLoopTimer();
    }

    public void add(Animatable newTarget){
        AnimatedObjectRecord newUnit = new AnimatedObjectRecord(newTarget);
        targets.add(newUnit);
    }

    @Override
    public void draw(Window window, Renderer renderer) {
        targets.forEach(t -> t.updateAnimation(timer.getElapsedTime()));
    }

    @Override
    public void update() {
        if (engine != null && engine.isPaused()) return;
        targets.removeIf(AnimatedObjectRecord::mustBeRemoved);
    }
}
