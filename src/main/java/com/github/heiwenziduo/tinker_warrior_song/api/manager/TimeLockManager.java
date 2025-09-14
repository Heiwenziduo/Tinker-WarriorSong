package com.github.heiwenziduo.tinker_warrior_song.api.manager;

public class TimeLockManager {
    private int timeLock = 0;

    public int getTimeLock() {
        return timeLock;
    }

    public void setTimeLock(int t) {
        if (t < 0) t = 0;
        timeLock = t;
    }

    public void timeLockDecrement() {
        if (timeLock > 0) timeLock--;
    }

    public boolean isTimeLocked() {
        return timeLock > 0;
    }
}
