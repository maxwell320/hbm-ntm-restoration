package com.hbm.util.i18n;

import java.util.List;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;

public final class I18nUtil {
    private static final ITranslate TRANSLATOR = FMLEnvironment.dist == Dist.CLIENT ? new I18nClient() : new I18nServer();

    private I18nUtil() {
    }

    public static String resolveKey(final String s, final Object... args) {
        return TRANSLATOR.resolveKey(s, args);
    }

    public static String format(final String s, final Object... args) {
        return TRANSLATOR.resolveKey(s, args);
    }

    public static String[] resolveKeyArray(final String s, final Object... args) {
        return TRANSLATOR.resolveKeyArray(s, args);
    }

    public static List<String> autoBreakWithParagraphs(final Object fontRenderer, final String text, final int width) {
        return TRANSLATOR.autoBreakWithParagraphs(fontRenderer, text, width);
    }

    public static List<String> autoBreak(final Object fontRenderer, final String text, final int width) {
        return TRANSLATOR.autoBreak(fontRenderer, text, width);
    }
}
