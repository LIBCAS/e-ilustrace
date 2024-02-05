package cz.inqool.eas.eil.theme;

import cz.inqool.eas.common.dated.DatedService;
import cz.inqool.eas.common.dated.index.DatedIndexedObject;
import cz.inqool.eas.common.domain.index.dto.filter.AndFilter;
import cz.inqool.eas.common.domain.index.dto.filter.EqFilter;
import cz.inqool.eas.common.domain.index.dto.filter.NullFilter;
import cz.inqool.eas.common.domain.index.dto.params.Params;
import cz.inqool.eas.eil.record.RecordRepository;
import cz.inqool.eas.eil.security.Permission;
import cz.inqool.eas.eil.security.UserChecker;
import cz.inqool.eas.eil.theme.dto.ThemeCount;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

import static cz.inqool.eas.common.utils.AssertionUtils.coalesce;

@Slf4j
@Service
public class ThemeService extends DatedService<
        Theme,
        ThemeDefault,
        ThemeDefault,
        ThemeCreate,
        ThemeDefault,
        ThemeRepository> {

    private RecordRepository recordRepository;

    public List<ThemeCount> countThemes() {
        List<Theme> themes = repository.listAll().stream().sorted(Comparator.comparing(Theme::getName)).collect(Collectors.toList());
        List<ThemeCount> result = new ArrayList<>();
        themes.forEach(theme -> {
            Params params = new Params();
            params.addFilter(new AndFilter(
                    new NullFilter(DatedIndexedObject.Fields.deleted),
                    new EqFilter("type", "illustration"),
                    new EqFilter("themes.name", theme.getName())
            ));
            result.add(ThemeCount.of(theme, recordRepository.countByParams(params)));
        });
        return result;
    }

    public List<ThemeCount> countThemesByParams(Params params) {
        List<Theme> themes = repository.listAll().stream().sorted(Comparator.comparing(Theme::getName)).collect(Collectors.toList());
        List<ThemeCount> result = new ArrayList<>();
        themes.forEach(theme -> {
            Params modifParams = coalesce(params.copy(), Params::new);
            modifParams.addFilter(
                    new NullFilter(DatedIndexedObject.Fields.deleted),
                    new EqFilter("type", "illustration"),
                    new EqFilter("themes.name", theme.getName()
            ));
            result.add(ThemeCount.of(theme, recordRepository.countByParams(modifParams)));
        });
        return result;
    }

    @Override
    protected void preCreateHook(Theme object) {
        super.preCreateHook(object);
        UserChecker.checkUserHasAnyPermission(Permission.ADMIN);
    }

    @Override
    protected void preUpdateHook(Theme object) {
        super.preUpdateHook(object);
        UserChecker.checkUserHasAnyPermission(Permission.ADMIN);
    }

    @Override
    public void preDeleteHook(@NotNull String id) {
        super.preDeleteHook(id);
        UserChecker.checkUserHasAnyPermission(Permission.ADMIN);
    }

    @Autowired
    public void setRecordRepository(RecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }
}
