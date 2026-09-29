package cz.inqool.eas.eil.record.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class RecordTypeDto {
    @NotNull
    private String type;
}
