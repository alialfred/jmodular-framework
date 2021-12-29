package com.alisoftclub.frameworks.modular;

import com.alisoftclub.frameworks.modular.annotations.TimerEvent;
import com.alisoftclub.frameworks.modular.plugin.Plugin;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class TimerEventRunner
        implements Runnable {

    private static final Logger logger = Logger.getLogger(TimerEventRunner.class.getCanonicalName());
    private final TimerEvent timerEvent;
    private final Plugin plugin;
    private final Method method;
    private Thread thread;

    public TimerEventRunner(Plugin plugin, Method method, TimerEvent timerEvent) {
        this.plugin = plugin;
        this.method = method;
        this.timerEvent = timerEvent;
    }

    public Thread getThread() {
        return this.thread;
    }

    public void start() {
        this.thread = new Thread(this);
        this.thread.setPriority(this.timerEvent.priority());
        this.thread.setDaemon(this.timerEvent.daemon());
        this.thread.start();
    }

    public void stop() {
        this.thread.interrupt();
        this.thread = null;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {

            try {
                this.method.invoke(this.plugin, new Object[]{this});

                TimeUnit.SECONDS.sleep(this.timerEvent.timeout());
            } catch (IllegalAccessException | IllegalArgumentException | java.lang.reflect.InvocationTargetException | InterruptedException ex) {
                logger.severe(ex.getMessage());
            }
        }
    }
}


/* Location:              E:\java\reverse-engineered\reconciler\lib\modular-framework-1.0.0.1.jar!\com\alisoftclub\amf\TimerEventRunner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
