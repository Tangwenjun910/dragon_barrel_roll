package com.tangwenjun.dragonbarrelroll.render;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import com.tangwenjun.dragonbarrelroll.ModMath;
import com.tangwenjun.dragonbarrelroll.math.MagicNumbers;
import org.joml.Vector2d;

public class HorizonLineWidget extends RenderHelper {
    public static void render(GuiGraphicsExtractor context, int scaledWidth, int scaledHeight, double roll, double pitch) {
        int centerX = scaledWidth / 2 - 1;
        int centerY = scaledHeight / 2 - 1;
        roll *= -MagicNumbers.TORAD;

        var v = new Vector2d(Math.cos(roll), Math.sin(roll));
        var offset = new Vector2d(v).perpendicular().mul(pitch * scaledHeight * 0.007);

        centerX += Math.round(offset.x);
        centerY += Math.round(offset.y);

        for (int i = 0; i < 2; i++) {
            v.negate();

            var start = v.mul(10.0, new Vector2d());
            var end = v.mul(50.0, new Vector2d());

            ModMath.forBresenhamLine(
                    centerX + (int) start.x, centerY + (int) start.y,
                    centerX + (int) end.x, centerY + (int) end.y,
                    blankPixel(context)
            );
        }
    }
}
