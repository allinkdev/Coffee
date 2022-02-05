package me.x150.sipprivate.mixin;

import me.x150.sipprivate.feature.module.ModuleRegistry;
import me.x150.sipprivate.feature.module.impl.render.ESP;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelPart.class)
public class ModelPartMixin {
    @Inject(method = "renderCuboids", at = @At("HEAD"))
    void renderCub(MatrixStack.Entry entry, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha, CallbackInfo ci) {
        if (ModuleRegistry.getByClass(ESP.class).recording)
            ModuleRegistry.getByClass(ESP.class).vertexDumps.add(new double[0]);
    }
}
