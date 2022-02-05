package me.x150.sipprivate.feature.module.impl.render;

import me.x150.sipprivate.feature.module.Module;
import me.x150.sipprivate.feature.module.ModuleType;
import me.x150.sipprivate.helper.ByteCounter;
import me.x150.sipprivate.helper.event.EventType;
import me.x150.sipprivate.helper.event.Events;
import me.x150.sipprivate.helper.event.events.LoreQueryEvent;
import net.minecraft.client.util.math.MatrixStack;

import java.text.StringCharacterIterator;

public class ItemByteSize extends Module {
    public ItemByteSize() {
        super("ItemByteSize", "Shows the size of an item in bytes on the tooltip", ModuleType.RENDER);
        Events.registerEventHandler(EventType.LORE_QUERY, event -> {
            if (!this.isEnabled()) {
                return;
            }
            LoreQueryEvent e = (LoreQueryEvent) event;
            ByteCounter inst = ByteCounter.instance();
            inst.reset();
            boolean error = false;
            try {
                e.getSource().getOrCreateNbt().write(inst);
            } catch (Exception ignored) {
                error = true;
            }
            long count = inst.getSize();
            String fmt;
            if (error) {
                fmt = "§cError";
            } else {
                fmt = humanReadableByteCountBin(count);
            }
            e.addClientLore("Size: " + fmt);
        });
    }

    public static String humanReadableByteCountBin(long bytes) {
        long absB = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);
        if (absB < 1024) {
            return bytes + " B";
        }
        long value = absB;
        StringCharacterIterator ci = new StringCharacterIterator("KMGTPE");
        for (int i = 40; i >= 0 && absB > 0xfffccccccccccccL >> i; i -= 10) {
            value >>= 10;
            ci.next();
        }
        value *= Long.signum(bytes);
        return String.format("%.1f %ciB", value / 1024.0, ci.current());
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

