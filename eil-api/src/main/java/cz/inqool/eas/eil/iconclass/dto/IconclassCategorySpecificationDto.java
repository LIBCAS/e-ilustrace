package cz.inqool.eas.eil.iconclass.dto;

import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
public class IconclassCategorySpecificationDto {

    @NotNull
    private IconclassTxtAttributeDto txt;

    @Getter
    public static class IconclassTxtAttributeDto {
        private String en;
        private String de;
    }
}
