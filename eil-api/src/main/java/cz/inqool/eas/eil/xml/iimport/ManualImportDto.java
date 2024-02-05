package cz.inqool.eas.eil.xml.iimport;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ManualImportDto {
    private String outputDir;
    private String reg;
    int start;
    int end;
}
