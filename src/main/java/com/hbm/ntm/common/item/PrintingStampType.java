package com.hbm.ntm.common.item;

public enum PrintingStampType {
    PRINTING1("printing1", "Printing Press Stamp (Part 1)", PageItemType.PAGE1),
    PRINTING2("printing2", "Printing Press Stamp (Part 2)", PageItemType.PAGE2),
    PRINTING3("printing3", "Printing Press Stamp (Part 3)", PageItemType.PAGE3),
    PRINTING4("printing4", "Printing Press Stamp (Part 4)", PageItemType.PAGE4),
    PRINTING5("printing5", "Printing Press Stamp (Part 5)", PageItemType.PAGE5),
    PRINTING6("printing6", "Printing Press Stamp (Part 6)", PageItemType.PAGE6),
    PRINTING7("printing7", "Printing Press Stamp (Part 7)", PageItemType.PAGE7),
    PRINTING8("printing8", "Printing Press Stamp (Part 8)", PageItemType.PAGE8);

    private final String translationSuffix;
    private final String displayName;
    private final PageItemType pageType;

    PrintingStampType(final String translationSuffix, final String displayName, final PageItemType pageType) {
        this.translationSuffix = translationSuffix;
        this.displayName = displayName;
        this.pageType = pageType;
    }

    public String translationSuffix() {
        return this.translationSuffix;
    }

    public String displayName() {
        return this.displayName;
    }

    public PageItemType pageType() {
        return this.pageType;
    }

    public static PrintingStampType fromName(final String name) {
        for (final PrintingStampType type : values()) {
            if (type.name().equals(name)) {
                return type;
            }
        }
        return PRINTING1;
    }
}
