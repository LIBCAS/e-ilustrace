package cz.inqool.eas.eil.record.illustration.xlsx.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class XlsxIllustrationDto {

    private Set<String> iconclassCodes;

    private String identifier;

    private Set<String> themes;

    private String iconclassState;
}
