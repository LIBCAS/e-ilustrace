package cz.inqool.eas.common.history;

import cz.inqool.eas.common.authored.AuthoredService;
import cz.inqool.eas.common.domain.index.dto.Result;
import cz.inqool.eas.common.domain.index.dto.filter.EqFilter;
import cz.inqool.eas.common.domain.index.dto.params.Params;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import javax.transaction.Transactional;

import java.util.List;

import static cz.inqool.eas.common.utils.AssertionUtils.coalesce;

@Slf4j
public class HistoryService extends AuthoredService<
        History,
        HistoryDetail,
        HistoryList,
        HistoryCreate,
        HistoryUpdate,
        HistoryRepository
        > {
    /**
     * Retrieves list view of objects that respect the selected {@link Params} and was created for specified entity.
     *
     * @param params Parameters to comply with
     * @return Sorted list of objects with total number
     */
    @Transactional
    public Result<HistoryList> listForEntity(String entityId, @Nullable Params params) {
        params = coalesce(params, Params::new);
        params.addFilter(new EqFilter("entityId", entityId));
        return super.list(params);
    }

    /**
     * Retrieves all entries for specified entity.
     */
    @Transactional
    public List<HistoryList> fullForEntity(String entityId) {
        return repository.listFull(entityId);
    }
}
