package cz.inqool.eas.eil.role;

import cz.inqool.eas.common.domain.index.reference.Labeled;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum MarcRole implements Labeled {
    //do not want to filter
    BIBLIOGRAPHIC_ANTECEDENT("ant"),
    AUTHOR("aut"),
    //do not want to filter
    PRESUMED_AUTHOR("dub"),
    PUBLISHER("pbl"),
    //do not want to filter
    CARTOGRAPHER("ctg"),
    //do not want to filter
    ENGRAVER("egr"),
    //do not want to filter
    ETCHER("etr"),
    ILLUSTRATOR("ill"),
    //do not want to filter
    METAL_ENGRAVER("mte"),
    PRINTER("prt"),
    WOODCARVER("wdc"),
    //do not want to filter
    OTHER("oth");

    @Override
    public String getId() {
        return name();
    }

    @Getter
    private final String label;
}
