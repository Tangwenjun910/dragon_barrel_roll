package com.tangwenjun.dragonbarrelroll.render;

import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;

import java.util.function.BiConsumer;

public class RenderHelper {
    public static BiConsumer<Integer, Integer> blankPixel(PoseStack matrices) {
        return (x, y) -> {
            int r = 255, g = 255, b = 255, a = 255;
            var matrix = matrices.last().pose();
            var builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

            builder.addVertex(matrix, (float) x, (float) y + 1, 0.0F).setColor(r, g, b, a);
            builder.addVertex(matrix, (float) x + 1, (float) y + 1, 0.0F).setColor(r, g, b, a);
            builder.addVertex(matrix, (float) x + 1, (float) y, 0.0F).setColor(r, g, b, a);
            builder.addVertex(matrix, (float) x, (float) y, 0.0F).setColor(r, g, b, a);
            BufferUploader.drawWithShader(builder.buildOrThrow());
        };
    }
}
