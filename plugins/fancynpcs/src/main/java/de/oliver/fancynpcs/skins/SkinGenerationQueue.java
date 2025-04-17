package de.oliver.fancynpcs.skins;

import java.util.concurrent.ScheduledFuture;

public interface SkinGenerationQueue<R> {

    void run();

    void add(R request);

    ScheduledFuture<?> getScheduler();
}
