/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.x150.sipprivate.feature.module.impl.movement;

import me.x150.sipprivate.CoffeeClientMain;
import me.x150.sipprivate.feature.config.DoubleSetting;
import me.x150.sipprivate.feature.module.Module;
import me.x150.sipprivate.feature.module.ModuleType;
import me.x150.sipprivate.helper.util.Utils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.Objects;

public class AutoElytra extends Module {

//    final SliderValue fallDist = (SliderValue) this.config.create("Fall distance", 3, 2, 10, 1).description("How far to fall for the elytra to equip");

    final DoubleSetting fallDist = this.config.create(new DoubleSetting.Builder(3)
            .name("Fall distance")
            .description("How long to fall for the elytra to equip")
            .min(2)
            .max(10)
            .precision(1)
            .get());

    public AutoElytra() {
        super("AutoElytra", "Automatically equips an elytra from your inventory if you fell long enough", ModuleType.MOVEMENT);
    }

    boolean equippedElytra() {
        return Objects.requireNonNull(CoffeeClientMain.client.player).getInventory().armor.get(2).getItem() == Items.ELYTRA;
    }

    @Override
    public void tick() {
        if (Objects.requireNonNull(CoffeeClientMain.client.player).fallDistance > fallDist.getValue()) {
            if (!equippedElytra()) { // do we not have an elytra equipped?
                for (int i = 0; i < (9 * 4 + 1); i++) { // gotta equip
                    ItemStack stack = CoffeeClientMain.client.player.getInventory().getStack(i); // is it an elytra?
                    if (stack.getItem() == Items.ELYTRA) {
                        Utils.Inventory.moveStackToOther(Utils.Inventory.slotIndexToId(i), 6); // equip
                        break; // we found the item, cancel the loop
                    }
                }
            }
        }
    }

    @Override
    public void enable() {

    }

    @Override
    public void disable() {

    }

    @Override
    public String getContext() {
        float fd = Objects.requireNonNull(CoffeeClientMain.client.player).fallDistance;
        if (fd > fallDist.getMin()) {
            return Utils.Math.roundToDecimal(fd, 1) + " | " + fallDist.getValue();
        }
        return null;
    }

    @Override
    public void onWorldRender(MatrixStack matrices) {

    }

    @Override
    public void onHudRender() {

    }
}

