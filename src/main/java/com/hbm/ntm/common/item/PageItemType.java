package com.hbm.ntm.common.item;

public enum PageItemType {
    PAGE1("page1", "Page 1"),
    PAGE2("page2", "Page 2"),
    PAGE3("page3", "Page 3"),
    PAGE4("page4", "Page 4"),
    PAGE5("page5", "Page 5"),
    PAGE6("page6", "Page 6"),
    PAGE7("page7", "Page 7"),
    PAGE8("page8", "Page 8");

    private final String translationSuffix;
    private final String displayName;

    PageItemType(final String translationSuffix, final String displayName) {
        this.translationSuffix = translationSuffix;
        this.displayName = displayName;
    }

    public String translationSuffix() {
        return this.translationSuffix;
    }

    public String displayName() {
        return this.displayName;
    }

    public static PageItemType fromName(final String name) {
        for (final PageItemType type : values()) {
            if (type.name().equals(name)) {
                return type;
            }
        }
        return PAGE1;
    }
}
