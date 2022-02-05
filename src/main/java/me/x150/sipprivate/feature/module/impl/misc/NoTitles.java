/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.x150.sipprivate.feature.module.impl.misc;

import me.x150.sipprivate.CoffeeClientMain;
import me.x150.sipprivate.feature.gui.notifications.Notification;
import me.x150.sipprivate.feature.module.Module;
import me.x150.sipprivate.feature.module.ModuleType;
import me.x150.sipprivate.helper.event.EventType;
import me.x150.sipprivate.helper.event.Events;
import me.x150.sipprivate.helper.event.events.PacketEvent;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleFadeS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;

public class NoTitles extends Module {
    long blocked = 0L;
    Notification lastShown = null;

    public NoTitles() {
        super("NoTitles", "Completely removes any titles from rendering", ModuleType.MISC);
        Events.registerEventHandler(EventType.PACKET_RECEIVE, event -> {
            if (!this.isEnabled()) {
                return;
            }
            PacketEvent pe = (PacketEvent) event;
            if (pe.getPacket() instanceof TitleS2CPacket) {
                blocked++;
                // create new notif if old one expired
                if (lastShown.creationDate + lastShown.duration < System.currentTimeMillis()) {
                    lastShown = Notification.create(6000, "NoTitles", false, Notification.Type.SUCCESS, "Blocked " + blocked + " titles");
                }
                // else just set the current notif to our shit
                else {
                    lastShown.contents[0] = "Blocked " + blocked + " titles";
                }
                event.setCancelled(true);
            } else if (pe.getPacket() instanceof SubtitleS2CPacket || pe.getPacket() instanceof TitleFadeS2CPacket) {
                event.setCancelled(true);
                CoffeeClientMain.client.inGameHud.setDefaultTitleFade();
            }
        });
    }

    @Override
    public void tick() {
    }

    @Override
    public void enable() {

    }

    @Override
    public void disable() {

    }

    @Override
    public String getContext() {
        return null;
    }

    @Override
    public void onWorldRender(MatrixStack matrices) {

    }

    @Override
    public void onHudRender() {

    }
}

