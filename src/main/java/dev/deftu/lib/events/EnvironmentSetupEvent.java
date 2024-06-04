package dev.deftu.lib.events;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

@FunctionalInterface
public interface EnvironmentSetupEvent {
    Event<EnvironmentSetupEvent> EVENT = EventFactory.createArrayBacked(EnvironmentSetupEvent.class, (listeners) -> (type) -> {
        for (EnvironmentSetupEvent listener : listeners) {
            listener.onEnvironmentSetup(type);
        }
    });

    void onEnvironmentSetup(EnvType type);
}
