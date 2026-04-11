package com.hbm.util.i18n;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class I18nClient implements ITranslate {

    @Override
    public String resolveKey(final String s, final Object... args) {
        return Component.translatable(s, args).getString();
    }

    @Override
    public String[] resolveKeyArray(final String s, final Object... args) {
        return this.resolveKey(s, args).split("\\$");
    }

    @Override
    public List<String> autoBreakWithParagraphs(final Object fontRenderer, final String text, final int width) {
        final String[] paragraphs = text.split("\\$");
        final List<String> lines = new ArrayList<>();
        for (final String paragraph : paragraphs) {
            lines.addAll(this.autoBreak(fontRenderer, paragraph, width));
        }
        return lines;
    }

    @Override
    public List<String> autoBreak(final Object fontRenderer, final String text, final int width) {
        final Font font = (Font) fontRenderer;
        final List<String> lines = new ArrayList<>();
        final String[] words = text.split(" ");
        if (words.length == 0) {
            return lines;
        }
        lines.add(words[0]);
        int indent = font.width(words[0]);
        for (int w = 1; w < words.length; w++) {
            indent += font.width(" " + words[w]);
            if (indent <= width) {
                final int lastIndex = lines.size() - 1;
                lines.set(lastIndex, lines.get(lastIndex) + " " + words[w]);
            } else {
                lines.add(words[w]);
                indent = font.width(words[w]);
            }
        }
        return lines;
    }
}
