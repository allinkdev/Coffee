/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.x150.sipprivate.feature.module.impl.movement;

import me.x150.sipprivate.CoffeeClientMain;
import me.x150.sipprivate.feature.config.DoubleSetting;
import me.x150.sipprivate.feature.module.Module;
import me.x150.sipprivate.feature.module.ModuleType;
import net.minecraft.client.util.math.MatrixStack;

import java.util.Objects;

public class Step extends Module {

    //    final SliderValue height = (SliderValue) this.config.create("Step height", 3, 1, 50, 0).description("How high to step");
    final DoubleSetting height = this.config.create(new DoubleSetting.Builder(3)
            .name("Height")
            .description("How high to step")
            .min(1)
            .max(50)
            .precision(0)
            .get());

    public Step() {
        super("Step", "Allows you to step up full blocks", ModuleType.MOVEMENT);
    }

    @Override
    public void tick() {
        if (CoffeeClientMain.client.player == null || CoffeeClientMain.client.getNetworkHandler() == null) {
            return;
        }
        CoffeeClientMain.client.player.stepHeight = (float) (height.getValue() + 0);
    }

    @Override
    public void enable() {

    }

    @Override
    public void disable() {
        if (CoffeeClientMain.client.player == null || CoffeeClientMain.client.getNetworkHandler() == null) {
            return;
        }
        Objects.requireNonNull(client.player).stepHeight = 0.6f;
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

