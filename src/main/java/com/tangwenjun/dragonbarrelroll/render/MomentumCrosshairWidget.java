package com.tangwenjun.dragonbarrelroll.render;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import com.tangwenjun.dragonbarrelroll.ModMath;
import org.joml.Vector2d;
import org.joml.Vector2i;

public class MomentumCrosshairWidget extends RenderHelper {
    public static Vector2i render(GuiGraphicsExtractor context, int scaledWidth, int scaledHeight, Vector2d mouseTurnVec) {
        int centerX = scaledWidth / 2;
        int centerY = scaledHeight / 2 - 1;
        mouseTurnVec.mul(50);
        var lineVec = new Vector2d(mouseTurnVec).add(
                new Vector2d(mouseTurnVec).negate().normalize().mul(Math.min(mouseTurnVec.length(), 10f)));

        if (!lineVec.equals(new Vector2d()) && mouseTurnVec.lengthSquared() > 10f * 10f) {
            ModMath.forBresenhamLine(
                    centerX, centerY,
                    centerX + (int) lineVec.x, centerY + (int) lineVec.y,
                    blankPixel(context)
            );
        }

        // Return crosshair offset for vanilla to apply
        return new Vector2i((int) mouseTurnVec.x, (int) mouseTurnVec.y);
    }
}
