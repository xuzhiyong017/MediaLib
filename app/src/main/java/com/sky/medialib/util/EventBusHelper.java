package com.sky.medialib.util;

import org.greenrobot.eventbus.EventBus;

public class EventBusHelper {

    private static final EventBus eventBus = EventBus.getDefault();

    public static void post(Object obj) {
        eventBus.post(obj);
    }

    public static void register(Object obj) {
        if (!eventBus.isRegistered(obj)) {
            eventBus.register(obj);
        }
    }

    public static void unregister(Object obj) {
        if (eventBus.isRegistered(obj)) {
            eventBus.unregister(obj);
        }
    }
}
