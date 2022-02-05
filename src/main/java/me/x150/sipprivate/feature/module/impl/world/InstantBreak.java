/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.x150.sipprivate.feature.module.impl.world;

import me.x150.sipprivate.feature.config.EnumSetting;
import me.x150.sipprivate.feature.module.Module;
import me.x150.sipprivate.feature.module.ModuleType;
import me.x150.sipprivate.helper.event.EventType;
import me.x150.sipprivate.helper.event.Events;
import me.x150.sipprivate.helper.event.events.PacketEvent;
import me.x150.sipprivate.helper.render.Renderer;
import me.x150.sipprivate.helper.util.Utils;
import me.x150.sipprivate.mixin.IClientPlayerInteractionManagerAccessor;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InstantBreak extends Module {

    final List<Vec3d> positions = new ArrayList<>();
    final List<PlayerActionC2SPacket> whitelist = new ArrayList<>();
    final EnumSetting<Priority> prio = this.config.create(new EnumSetting.Builder<>(Priority.Speed)
            .name("Priority")
            .description("What to do with the blocks being broken")
            .get());

    public InstantBreak() {
        super("InstantBreak", "Breaks a block a lot faster", ModuleType.WORLD);
        Events.registerEventHandler(EventType.PACKET_SEND, event -> {
            if (!this.isEnabled()) {
                return;
            }
            PacketEvent pe = (PacketEvent) event;
            if (pe.getPacket() instanceof PlayerActionC2SPacket packet) {
                if (!whitelist.contains(packet)) {
                    if (packet.getAction() == PlayerActionC2SPacket.Action.START_DESTROY_BLOCK && prio.getValue() == Priority.Order) {
                        event.setCancelled(true);
                    }
                } else {
                    whitelist.remove(packet);
                }
            }
        });
    }

    @Override
    public void tick() {
        if (Objects.requireNonNull(client.interactionManager).isBreakingBlock()) {
            BlockPos last = ((IClientPlayerInteractionManagerAccessor) client.interactionManager).getCurrentBreakingPos();
            if (prio.getValue() == Priority.Order) {
                Vec3d p = new Vec3d(last.getX(), last.getY(), last.getZ());
                if (!positions.contains(p)) {
                    positions.add(p);
                }
            } else {
                Objects.requireNonNull(client.getNetworkHandler()).sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, last, Direction.DOWN));
                positions.clear();
            }
        }
        Vec3d p = client.gameRenderer.getCamera().getPos();
        if (positions.size() == 0) {
            return;
        }
        Vec3d latest = positions.get(0);
        if (latest.add(0.5, 0.5, 0.5).distanceTo(p) >= client.interactionManager.getReachDistance()) {
            positions.remove(0);
            return;
        }
        BlockPos bp = new BlockPos(latest);
        if (Objects.requireNonNull(client.world).getBlockState(bp).isAir()) {
            positions.remove(0);
            return;
        }
        PlayerActionC2SPacket pstart = new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, bp, Direction.DOWN);
        whitelist.add(pstart);
        Objects.requireNonNull(client.getNetworkHandler()).sendPacket(pstart);
        client.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, bp, Direction.DOWN));
    }

    @Override
    public void enable() {

    }

    @Override
    public void disable() {
        positions.clear();
    }

    @Override
    public String getContext() {
        return null;
    }

    @Override
    public void onWorldRender(MatrixStack matrices) {
        for (Vec3d position : new ArrayList<>(positions)) {
            Renderer.R3D.renderOutline(position, new Vec3d(1, 1, 1), Utils.getCurrentRGB(), matrices);
            //Renderer.renderFilled(position,new Vec3d(1,1,1),new Color(0,0,0,150),matrices);
        }
    }

    @Override
    public void onHudRender() {

    }

    //    final MultiValue                  prio      = (MultiValue) this.config.create("Priority", "Speed", "Order", "Speed").description("What to prioritize when breaking blocks");
    public enum Priority {
        Speed, Order
    }
}

