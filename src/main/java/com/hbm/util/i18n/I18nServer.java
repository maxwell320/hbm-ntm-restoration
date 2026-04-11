package com.hbm.util.i18n;

import java.util.ArrayList;
import java.util.List;

public class I18nServer implements ITranslate {
    public static final String SARCASTIC_MESSAGE = "I18N CALL SERVERSIDE - GREAT JOB";

    @Override
    public String resolveKey(final String s, final Object... args) {
        return SARCASTIC_MESSAGE;
    }

    @Override
    public String[] resolveKeyArray(final String s, final Object... args) {
        return new String[] {SARCASTIC_MESSAGE};
    }

    @Override
    public List<String> autoBreakWithParagraphs(final Object fontRenderer, final String text, final int width) {
        final List<String> list = new ArrayList<>();
        list.add(SARCASTIC_MESSAGE);
        return list;
    }

    @Override
    public List<String> autoBreak(final Object fontRenderer, final String text, final int width) {
        final List<String> list = new ArrayList<>();
        list.add(SARCASTIC_MESSAGE);
        return list;
    }
}
