package com.search.manager.utility;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandExecutor extends TimerTask {

    private static final Logger log = LoggerFactory.getLogger(CommandExecutor.class);

    private AtomicBoolean running = new AtomicBoolean(false);
    private AtomicBoolean stopped = new AtomicBoolean(false);
    private BlockingQueue<Command> commands;
    private Timer timer = new Timer();

    private int maxCount = 1000;
    private int interval = 1000;

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public void init() {
        log.info("Starting command executor.");

        commands = new ArrayBlockingQueue<Command>(maxCount);
        timer.schedule(this, 500, interval);
    }

    public void destroy() {
        log.info("Stopping command executor.");
        timer.cancel();
        stopped.set(true);
        log.info("Command executor stopped.");
    }

    public boolean addCommand(Command command) {
        if (commands.offer(command) && !stopped.get()) {
            log.info("New command added to queue. " + command);
            return true;
        } else {
            log.info("Unable to append command to queue.");
        }

        return false;
    }

    public void run() {
        log.trace("Running command executor.");

        if (!running.get()) {
            running.set(true);

            try {
                Command command = commands.poll();

                while (command != null && !stopped.get()) {
                    log.info("Running command " + command);
                    command.execute();
                    command = commands.poll();
                }
            } finally {
                log.trace("Command executor stopped.");
                running.set(false);
            }
        }
    }
}
