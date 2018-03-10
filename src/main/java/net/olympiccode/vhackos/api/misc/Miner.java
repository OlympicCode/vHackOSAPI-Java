package net.olympiccode.vhackos.api.misc;

public interface Miner {
    boolean isRunning();
    boolean claim();
    boolean isDone();
    boolean start();
}
