package com.hbm.ntm.common.material;

import java.util.List;

public final class HbmMaterials {
    public static final HbmMaterialDefinition CARBON = HbmMaterialDefinition.of("carbon", "Carbon", HbmMaterialShape.WIRE);
    public static final HbmMaterialDefinition IRON = HbmMaterialDefinition.of("iron", "Iron", HbmMaterialShape.FRAGMENT, HbmMaterialShape.DUST, HbmMaterialShape.PLATE, HbmMaterialShape.PIPE, HbmMaterialShape.CAST_PLATE, HbmMaterialShape.WELDED_PLATE)
        .withItemId(HbmMaterialShape.DUST, "powder_iron");
    public static final HbmMaterialDefinition GOLD = HbmMaterialDefinition.of("gold", "Gold", HbmMaterialShape.FRAGMENT, HbmMaterialShape.DUST, HbmMaterialShape.PLATE, HbmMaterialShape.WIRE, HbmMaterialShape.DENSE_WIRE, HbmMaterialShape.CAST_PLATE)
        .withItemId(HbmMaterialShape.DUST, "powder_gold");
    public static final HbmMaterialDefinition URANIUM = HbmMaterialDefinition.of("uranium", "Uranium", HbmMaterialShape.FRAGMENT, HbmMaterialShape.INGOT, HbmMaterialShape.NUGGET, HbmMaterialShape.BILLET, HbmMaterialShape.DUST)
        .withItemId(HbmMaterialShape.DUST, "powder_uranium");
    public static final HbmMaterialDefinition U233 = HbmMaterialDefinition.of("u233", "U-233", HbmMaterialShape.INGOT, HbmMaterialShape.NUGGET, HbmMaterialShape.BILLET);
    public static final HbmMaterialDefinition U235 = HbmMaterialDefinition.of("u235", "U-235", HbmMaterialShape.INGOT, HbmMaterialShape.NUGGET, HbmMaterialShape.BILLET);
    public static final HbmMaterialDefinition U238 = HbmMaterialDefinition.of("u238", "U-238", HbmMaterialShape.FRAGMENT, HbmMaterialShape.INGOT, HbmMaterialShape.NUGGET, HbmMaterialShape.BILLET);
    public static final HbmMaterialDefinition TH232 = HbmMaterialDefinition.of("th232", "Thorium", HbmMaterialShape.FRAGMENT, HbmMaterialShape.INGOT, HbmMaterialShape.NUGGET, HbmMaterialShape.BILLET, HbmMaterialShape.DUST)
        .withItemId(HbmMaterialShape.DUST, "powder_thorium");
    public static final HbmMaterialDefinition PLUTONIUM = HbmMaterialDefinition.of("plutonium", "Plutonium", HbmMaterialShape.INGOT, HbmMaterialShape.NUGGET, HbmMaterialShape.BILLET, HbmMaterialShape.DUST)
        .withItemId(HbmMaterialShape.DUST, "powder_plutonium");
    public static final HbmMaterialDefinition PU238 = HbmMaterialDefinition.of("pu238", "Pu-238", HbmMaterialShape.INGOT, HbmMaterialShape.NUGGET, HbmMaterialShape.BILLET);
    public static final HbmMaterialDefinition PU239 = HbmMaterialDefinition.of("pu239", "Pu-239", HbmMaterialShape.INGOT, HbmMaterialShape.NUGGET, HbmMaterialShape.BILLET);
    public static final HbmMaterialDefinition PU240 = HbmMaterialDefinition.of("pu240", "Pu-240", HbmMaterialShape.INGOT, HbmMaterialShape.NUGGET, HbmMaterialShape.BILLET);
    public static final HbmMaterialDefinition NEPTUNIUM = HbmMaterialDefinition.of("neptunium", "Neptunium", HbmMaterialShape.INGOT, HbmMaterialShape.NUGGET, HbmMaterialShape.BILLET, HbmMaterialShape.DUST)
        .withItemId(HbmMaterialShape.DUST, "powder_neptunium");
    public static final HbmMaterialDefinition POLONIUM = HbmMaterialDefinition.of("polonium", "Polonium", HbmMaterialShape.FRAGMENT, HbmMaterialShape.INGOT, HbmMaterialShape.NUGGET, HbmMaterialShape.BILLET, HbmMaterialShape.DUST)
        .withItemId(HbmMaterialShape.DUST, "powder_polonium");
    public static final HbmMaterialDefinition TECHNETIUM = HbmMaterialDefinition.of("technetium", "Technetium", HbmMaterialShape.FRAGMENT, HbmMaterialShape.INGOT, HbmMaterialShape.NUGGET, HbmMaterialShape.BILLET);
    public static final HbmMaterialDefinition RA226 = HbmMaterialDefinition.of("ra226", "Ra-226", HbmMaterialShape.FRAGMENT, HbmMaterialShape.INGOT, HbmMaterialShape.NUGGET, HbmMaterialShape.BILLET, HbmMaterialShape.DUST)
        .withItemId(HbmMaterialShape.DUST, "powder_ra226");
    public static final HbmMaterialDefinition ACTINIUM = HbmMaterialDefinition.of("actinium", "Actinium", HbmMaterialShape.INGOT, HbmMaterialShape.NUGGET, HbmMaterialShape.BILLET, HbmMaterialShape.DUST)
        .withItemId(HbmMaterialShape.DUST, "powder_actinium");
    public static final HbmMaterialDefinition CO60 = HbmMaterialDefinition.of("co60", "Co-60", HbmMaterialShape.INGOT, HbmMaterialShape.NUGGET, HbmMaterialShape.BILLET, HbmMaterialShape.DUST)
        .withItemId(HbmMaterialShape.DUST, "powder_co60");
    public static final HbmMaterialDefinition AU198 = HbmMaterialDefinition.of("au198", "Au-198", HbmMaterialShape.INGOT, HbmMaterialShape.NUGGET, HbmMaterialShape.BILLET, HbmMaterialShape.DUST)
        .withItemId(HbmMaterialShape.DUST, "powder_au198");
    public static final HbmMaterialDefinition PB209 = HbmMaterialDefinition.of("pb209", "Pb-209", HbmMaterialShape.INGOT, HbmMaterialShape.NUGGET, HbmMaterialShape.BILLET);
    public static final HbmMaterialDefinition TITANIUM = HbmMaterialDefinition.of("titanium", "Titanium", HbmMaterialShape.FRAGMENT, HbmMaterialShape.INGOT, HbmMaterialShape.DUST, HbmMaterialShape.PLATE, HbmMaterialShape.DENSE_WIRE, HbmMaterialShape.CAST_PLATE, HbmMaterialShape.WELDED_PLATE, HbmMaterialShape.SHELL)
        .withItemId(HbmMaterialShape.DUST, "powder_titanium");
    public static final HbmMaterialDefinition COPPER = HbmMaterialDefinition.of("copper", "Copper", HbmMaterialShape.FRAGMENT, HbmMaterialShape.INGOT, HbmMaterialShape.DUST, HbmMaterialShape.WIRE, HbmMaterialShape.PLATE, HbmMaterialShape.DENSE_WIRE, HbmMaterialShape.CAST_PLATE, HbmMaterialShape.WELDED_PLATE, HbmMaterialShape.SHELL, HbmMaterialShape.PIPE)
        .withItemId(HbmMaterialShape.DUST, "powder_copper");
    public static final HbmMaterialDefinition RED_COPPER = HbmMaterialDefinition.of("red_copper", "Red Copper", HbmMaterialShape.INGOT, HbmMaterialShape.DUST, HbmMaterialShape.WIRE, HbmMaterialShape.DENSE_WIRE)
        .withItemId(HbmMaterialShape.DUST, "powder_red_copper");
    public static final HbmMaterialDefinition ADVANCED_ALLOY = HbmMaterialDefinition.of("advanced_alloy", "Advanced Alloy", HbmMaterialShape.INGOT, HbmMaterialShape.DUST, HbmMaterialShape.WIRE, HbmMaterialShape.DENSE_WIRE, HbmMaterialShape.PLATE, HbmMaterialShape.CAST_PLATE)
        .withItemId(HbmMaterialShape.DUST, "powder_advanced_alloy");
    public static final HbmMaterialDefinition TUNGSTEN = HbmMaterialDefinition.of("tungsten", "Tungsten", HbmMaterialShape.FRAGMENT, HbmMaterialShape.INGOT, HbmMaterialShape.DUST, HbmMaterialShape.WIRE, HbmMaterialShape.BOLT, HbmMaterialShape.DENSE_WIRE, HbmMaterialShape.CAST_PLATE, HbmMaterialShape.WELDED_PLATE)
        .withItemId(HbmMaterialShape.DUST, "powder_tungsten");
    public static final HbmMaterialDefinition ALUMINIUM = HbmMaterialDefinition.of("aluminium", "Aluminium", HbmMaterialShape.FRAGMENT, HbmMaterialShape.INGOT, HbmMaterialShape.DUST, HbmMaterialShape.WIRE, HbmMaterialShape.PLATE, HbmMaterialShape.CAST_PLATE, HbmMaterialShape.WELDED_PLATE, HbmMaterialShape.SHELL, HbmMaterialShape.PIPE)
        .withItemId(HbmMaterialShape.DUST, "powder_aluminium");
    public static final HbmMaterialDefinition STEEL = HbmMaterialDefinition.of("steel", "Steel", HbmMaterialShape.INGOT, HbmMaterialShape.DUST, HbmMaterialShape.WIRE, HbmMaterialShape.BOLT, HbmMaterialShape.PLATE, HbmMaterialShape.CAST_PLATE, HbmMaterialShape.WELDED_PLATE, HbmMaterialShape.SHELL, HbmMaterialShape.PIPE, HbmMaterialShape.LIGHT_BARREL, HbmMaterialShape.HEAVY_BARREL, HbmMaterialShape.LIGHT_RECEIVER, HbmMaterialShape.GRIP)
        .withItemId(HbmMaterialShape.DUST, "powder_steel");
    public static final HbmMaterialDefinition TCALLOY = HbmMaterialDefinition.of("tcalloy", "TC Alloy", HbmMaterialShape.INGOT, HbmMaterialShape.DUST, HbmMaterialShape.CAST_PLATE, HbmMaterialShape.WELDED_PLATE, HbmMaterialShape.LIGHT_BARREL, HbmMaterialShape.HEAVY_BARREL, HbmMaterialShape.LIGHT_RECEIVER, HbmMaterialShape.HEAVY_RECEIVER)
        .withItemId(HbmMaterialShape.DUST, "powder_tcalloy");
    public static final HbmMaterialDefinition CDALLOY = HbmMaterialDefinition.of("cdalloy", "CD Alloy", HbmMaterialShape.INGOT, HbmMaterialShape.CAST_PLATE, HbmMaterialShape.WELDED_PLATE, HbmMaterialShape.LIGHT_BARREL, HbmMaterialShape.HEAVY_BARREL, HbmMaterialShape.LIGHT_RECEIVER, HbmMaterialShape.HEAVY_RECEIVER);
    public static final HbmMaterialDefinition SCHRABIDIUM = HbmMaterialDefinition.of("schrabidium", "Schrabidium", HbmMaterialShape.INGOT, HbmMaterialShape.NUGGET, HbmMaterialShape.WIRE, HbmMaterialShape.BILLET, HbmMaterialShape.DUST, HbmMaterialShape.DENSE_WIRE, HbmMaterialShape.PLATE, HbmMaterialShape.CAST_PLATE)
        .withItemId(HbmMaterialShape.DUST, "powder_schrabidium");
    public static final HbmMaterialDefinition SOLINIUM = HbmMaterialDefinition.of("solinium", "Solinium", HbmMaterialShape.INGOT, HbmMaterialShape.NUGGET, HbmMaterialShape.BILLET);
    public static final HbmMaterialDefinition SCHRABIDATE = HbmMaterialDefinition.of("schrabidate", "Schrabidate", HbmMaterialShape.INGOT, HbmMaterialShape.DUST, HbmMaterialShape.DENSE_WIRE, HbmMaterialShape.CAST_PLATE)
        .withItemId(HbmMaterialShape.DUST, "powder_schrabidate");
    public static final HbmMaterialDefinition GH336 = HbmMaterialDefinition.of("gh336", "GH-336", HbmMaterialShape.INGOT, HbmMaterialShape.NUGGET, HbmMaterialShape.BILLET);
    public static final HbmMaterialDefinition LEAD = HbmMaterialDefinition.of("lead", "Lead", HbmMaterialShape.INGOT, HbmMaterialShape.NUGGET, HbmMaterialShape.DUST, HbmMaterialShape.PLATE)
        .withItemId(HbmMaterialShape.DUST, "powder_lead");
    public static final HbmMaterialDefinition BISMUTH = HbmMaterialDefinition.of("bismuth", "Bismuth", HbmMaterialShape.INGOT, HbmMaterialShape.NUGGET, HbmMaterialShape.BILLET, HbmMaterialShape.DUST)
        .withItemId(HbmMaterialShape.DUST, "powder_bismuth");
    public static final HbmMaterialDefinition ARSENIC = HbmMaterialDefinition.of("arsenic", "Arsenic", HbmMaterialShape.INGOT, HbmMaterialShape.NUGGET);
    public static final HbmMaterialDefinition TANTALIUM = HbmMaterialDefinition.of("tantalium", "Tantalium", HbmMaterialShape.FRAGMENT, HbmMaterialShape.INGOT, HbmMaterialShape.NUGGET, HbmMaterialShape.DUST)
        .withItemId(HbmMaterialShape.DUST, "powder_tantalium");
    public static final HbmMaterialDefinition NIOBIUM = HbmMaterialDefinition.of("niobium", "Niobium", HbmMaterialShape.FRAGMENT, HbmMaterialShape.INGOT, HbmMaterialShape.NUGGET, HbmMaterialShape.DUST, HbmMaterialShape.DENSE_WIRE)
        .withItemId(HbmMaterialShape.DUST, "powder_niobium");
    public static final HbmMaterialDefinition BERYLLIUM = HbmMaterialDefinition.of("beryllium", "Beryllium", HbmMaterialShape.FRAGMENT, HbmMaterialShape.INGOT, HbmMaterialShape.NUGGET, HbmMaterialShape.BILLET, HbmMaterialShape.DUST)
        .withItemId(HbmMaterialShape.DUST, "powder_beryllium");
    public static final HbmMaterialDefinition COBALT = HbmMaterialDefinition.of("cobalt", "Cobalt", HbmMaterialShape.FRAGMENT, HbmMaterialShape.INGOT, HbmMaterialShape.NUGGET, HbmMaterialShape.BILLET, HbmMaterialShape.DUST)
        .withItemId(HbmMaterialShape.DUST, "powder_cobalt");
    public static final HbmMaterialDefinition SILICON = HbmMaterialDefinition.of("silicon", "Silicon", HbmMaterialShape.INGOT, HbmMaterialShape.NUGGET, HbmMaterialShape.BILLET);
    public static final HbmMaterialDefinition DURA_STEEL = HbmMaterialDefinition.of("dura_steel", "Dura Steel", HbmMaterialShape.INGOT, HbmMaterialShape.DUST, HbmMaterialShape.BOLT, HbmMaterialShape.PLATE, HbmMaterialShape.CAST_PLATE, HbmMaterialShape.PIPE, HbmMaterialShape.LIGHT_BARREL, HbmMaterialShape.HEAVY_BARREL, HbmMaterialShape.LIGHT_RECEIVER, HbmMaterialShape.HEAVY_RECEIVER, HbmMaterialShape.GRIP)
        .withItemId(HbmMaterialShape.DUST, "powder_dura_steel");
    public static final HbmMaterialDefinition DESH = HbmMaterialDefinition.of("desh", "Desh", HbmMaterialShape.INGOT, HbmMaterialShape.NUGGET, HbmMaterialShape.DUST, HbmMaterialShape.CAST_PLATE, HbmMaterialShape.LIGHT_BARREL, HbmMaterialShape.HEAVY_BARREL, HbmMaterialShape.LIGHT_RECEIVER, HbmMaterialShape.STOCK, HbmMaterialShape.GRIP)
        .withItemId(HbmMaterialShape.DUST, "powder_desh");
    public static final HbmMaterialDefinition EUPHEMIUM = HbmMaterialDefinition.of("euphemium", "Euphemium", HbmMaterialShape.INGOT, HbmMaterialShape.NUGGET, HbmMaterialShape.DUST)
        .withItemId(HbmMaterialShape.DUST, "powder_euphemium");
    public static final HbmMaterialDefinition DINEUTRONIUM = HbmMaterialDefinition.of("dineutronium", "Dineutronium", HbmMaterialShape.INGOT, HbmMaterialShape.NUGGET, HbmMaterialShape.DUST)
        .withItemId(HbmMaterialShape.DUST, "powder_dineutronium");

    private static final List<HbmMaterialDefinition> ORDERED = List.of(
        CARBON,
        IRON,
        GOLD,
        URANIUM,
        U233,
        U235,
        U238,
        TH232,
        PLUTONIUM,
        PU238,
        PU239,
        PU240,
        NEPTUNIUM,
        POLONIUM,
        TECHNETIUM,
        RA226,
        ACTINIUM,
        CO60,
        AU198,
        PB209,
        TITANIUM,
        COPPER,
        RED_COPPER,
        ADVANCED_ALLOY,
        TUNGSTEN,
        ALUMINIUM,
        STEEL,
        TCALLOY,
        CDALLOY,
        SCHRABIDIUM,
        SOLINIUM,
        SCHRABIDATE,
        GH336,
        LEAD,
        BISMUTH,
        ARSENIC,
        TANTALIUM,
        NIOBIUM,
        BERYLLIUM,
        COBALT,
        SILICON,
        DURA_STEEL,
        DESH,
        EUPHEMIUM,
        DINEUTRONIUM
    );

    private HbmMaterials() {
    }

    public static List<HbmMaterialDefinition> ordered() {
        return ORDERED;
    }
}
