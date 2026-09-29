package cz.inqool.eas.eil.theme.dto;

import cz.inqool.eas.eil.theme.Theme;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ThemeCount {
    private String id;
    private String name;
    private Instant created;
    private Instant updated;
    private Instant deleted;
    private long count;

    public static ThemeCount of(Theme theme, long count) {
        ThemeCount themeCount = new ThemeCount();
        themeCount.id = theme.getId();
        themeCount.name = theme.getName();
        themeCount.created = theme.getCreated();
        themeCount.updated = theme.getUpdated();
        themeCount.deleted = theme.getDeleted();
        themeCount.count = count;
        return themeCount;
    }
}
