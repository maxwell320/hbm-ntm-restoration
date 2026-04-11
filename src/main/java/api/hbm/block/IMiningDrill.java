package api.hbm.block;

@Deprecated
public interface IMiningDrill {

    DrillType getDrillTier();

    int getDrillRating();

    enum DrillType {
        PRIMITIVE,
        INDUSTRIAL,
        HITECH
    }
}
