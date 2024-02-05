package cz.inqool.eas.eil.selection.dto;

import cz.inqool.eas.eil.selection.item.SelectionItemRef;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class SelectionDto {
    private Set<SelectionItemRef> items = new HashSet<>();
}
