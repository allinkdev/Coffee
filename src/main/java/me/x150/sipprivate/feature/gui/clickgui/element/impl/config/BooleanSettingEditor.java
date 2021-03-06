package me.x150.sipprivate.feature.gui.clickgui.element.impl.config;

import me.x150.sipprivate.feature.config.BooleanSetting;
import me.x150.sipprivate.feature.gui.clickgui.ClickGUI;
import me.x150.sipprivate.feature.gui.clickgui.theme.Theme;
import me.x150.sipprivate.helper.font.FontRenderers;
import me.x150.sipprivate.helper.render.Renderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

public class BooleanSettingEditor extends ConfigBase<BooleanSetting> {
    final double rw = 14;
    final double rh = 5;
    final double rid = 4;
    final double margin = .5;
    double animProgress = 0;

    public BooleanSettingEditor(double x, double y, double width, BooleanSetting configValue) {
        super(x, y, width, FontRenderers.getNormal().getFontHeight() + 2, configValue);
    }

    @Override
    public boolean dragged(double x, double y, double deltaX, double deltaY, int button) {
        return false;
    }

    @Override
    public boolean released() {
        return false;
    }

    @Override
    public boolean clicked(double x, double y, int button) {
        //        System.out.println(x+", "+y+", "+button);
        if (inBounds(x, y) && button == 0) {
            //            System.out.println("clicked");
            configValue.setValue(!configValue.getValue());
            return true;
        }
        return false;
    }

    double getPreferredX() {
        double smoothAnimProgress = easeInOutCubic(animProgress);
        return MathHelper.lerp(smoothAnimProgress, x + margin, x + rw - rid - margin);
        //        return configValue.getValue() ? x + rw - rid - margin : x + margin;
    }

    //    double xSmooth = -1;


    @Override
    public boolean keyPressed(int keycode, int modifiers) {
        return false;
    }

    @Override
    public void render(MatrixStack matrices, double mouseX, double mouseY, double scrollBeingUsed) {
        Theme theme = ClickGUI.theme;
        double smoothAnimProgress = easeInOutCubic(animProgress);
        Renderer.R2D.renderRoundedQuad(matrices, Renderer.Util.lerp(theme.getActive(), theme.getInactive(), smoothAnimProgress), x, y + height / 2d - rh / 2d, x + rw, y + height / 2d + rh / 2d, rh / 2d, 5);
        double rix = getPreferredX();
        //        Renderer.R2D.fill(matrices, theme.getAccent(), rix, y + height / 2d - rh / 2d + margin, rix + rid, y + height / 2d - rh / 2d + margin + rid);
        Renderer.R2D.renderCircle(matrices, theme.getAccent(), rix + rid / 2, y + height / 2d, rid / 2d, 15);
        //        Renderer.R2D.renderCircle(matrices, Theme.ACCENT,);
        FontRenderers.getNormal().drawString(matrices, configValue.getName(), x + rw + 2, y + height / 2d - FontRenderers.getNormal().getMarginHeight() / 2d, 0xFFFFFF);
    }

    double easeInOutCubic(double x) {
        return x < 0.5 ? 4 * x * x * x : 1 - Math.pow(-2 * x + 2, 3) / 2;

    }

    @Override
    public void tickAnim() {
        double a = 0.03;
        if (!configValue.getValue()) {
            a *= -1;
        }
        animProgress += a;
        animProgress = MathHelper.clamp(animProgress, 0, 1);
    }

    @Override
    public boolean charTyped(char c, int mods) {
        return false;
    }
}
