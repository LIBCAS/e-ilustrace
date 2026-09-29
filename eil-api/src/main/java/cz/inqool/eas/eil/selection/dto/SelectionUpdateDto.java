package cz.inqool.eas.eil.selection.dto;

import cz.inqool.eas.eil.record.book.BookRef;
import cz.inqool.eas.eil.record.illustration.IllustrationRef;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class SelectionUpdateDto {
    private Set<IllustrationRef> illustrations = new HashSet<>();
    private Set<BookRef> books = new HashSet<>();
}
