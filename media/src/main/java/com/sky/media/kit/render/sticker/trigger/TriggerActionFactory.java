package com.sky.media.kit.render.sticker.trigger;

public class TriggerActionFactory {
    public static ITriggerAction createTriggerAction(int i) {
        switch (i) {
            case 1:
                return new ActionHideUntilNotTrigger();
            case 2:
                return new ActionShowUntilNotTrigger();
            case 3:
                return new ActionShowOnceUntilNotTrigger();
            case 4:
                return new ActionShowOnce();
            case 5:
                return new ActionShowLast();
            case 6:
                return new ActionShowAlways();
            case 7:
                return new ActionHideAlways();
            case 8:
                return new ActionShowLastUntilNotTrigger();
            default:
                return null;
        }
    }
}
