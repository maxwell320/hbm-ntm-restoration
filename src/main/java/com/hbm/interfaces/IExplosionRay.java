package com.hbm.interfaces;

public interface IExplosionRay {

    void cacheChunksTick(int processTimeMs);

    void destructionTick(int processTimeMs);

    void cancel();

    boolean isComplete();
}
