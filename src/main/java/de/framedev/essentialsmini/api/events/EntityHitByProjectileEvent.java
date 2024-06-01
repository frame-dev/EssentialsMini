package de.framedev.essentialsmini.api.events;

import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class EntityHitByProjectileEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Entity hitEntity;
    private final Entity shooter;

    public EntityHitByProjectileEvent(Entity hitEntity, Entity shooter) {
        this.hitEntity = hitEntity;
        this.shooter = shooter;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}
