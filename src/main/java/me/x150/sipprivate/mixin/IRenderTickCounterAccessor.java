package me.x150.sipprivate.mixin;

import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderTickCounter.class)
public interface IRenderTickCounterAccessor {

    @Mutable
    @Accessor("tickTime")
    void setTickTime(float v);
}
