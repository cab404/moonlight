package com.cab404.moonlight.framework;

/**
 * @author cab404
 */
public abstract class ModuleImpl<T> implements Module<T> {
    private boolean finished = false;

    public void finish() {
        finished = true;
    }

    @Override public boolean haveYouFinished() {
        return finished;
    }
}
