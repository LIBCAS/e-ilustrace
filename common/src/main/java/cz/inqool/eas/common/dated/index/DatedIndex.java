package cz.inqool.eas.common.dated.index;

import cz.inqool.eas.common.dated.DatedIndexed;
import cz.inqool.eas.common.dated.store.DatedObject;
import cz.inqool.eas.common.domain.DomainIndexed;
import cz.inqool.eas.common.domain.index.DomainIndex;
import cz.inqool.eas.common.projection.Projectable;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchDateConverter;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

import static cz.inqool.eas.common.domain.index.QueryUtils.andQuery;
import static java.util.Arrays.asList;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.existsQuery;

/**
 * Index store for objects extending {@link DatedObject} with standard CRUD operations.
 * <p>
 * Objects are not removed on delete, rather a delete flag is set.
 *
 * @param <ROOT>      Root of the projection type system
 * @param <PROJECTED> Index projection type
 * @param <INDEXED>   Indexed object type
 */
@Slf4j
@Setter
public class DatedIndex<ROOT extends Projectable<ROOT>, PROJECTED extends Projectable<ROOT>, INDEXED extends DatedIndexed<ROOT, PROJECTED>> extends DomainIndex<ROOT, PROJECTED, INDEXED> {

    protected ElasticsearchDateConverter dateConverter;

    public DatedIndex(Class<INDEXED> indexedType) {
        super(indexedType);

        this.dateConverter = ElasticsearchDateConverter.of("yyyy-MM-dd'T'HH:mm:ss.SSS");
    }

    /**
     * {@inheritDoc}
     * <p>
     * Only objects without deleted flag is returned.
     */
    @Override
    public <X> X list(Consumer<SearchSourceBuilder> queryModifier, Function<SearchResponse, X> extractor) {
        return super.list(queryModifier.andThen(builder -> {
            BoolQueryBuilder deletedQuery = boolQuery().mustNot(existsQuery("deleted"));

            builder.query(andQuery(asList(builder.query(), deletedQuery)));
        }), extractor);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Object is not removed on delete, rather a delete flag is set.
     */
    @Override
    public void delete(@NotNull INDEXED obj) {
        UpdateRequest request = createDeleteRequest(obj);
        execute(client -> client.update(request, RequestOptions.DEFAULT));
        refresh();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Objects are not removed on delete, rather a delete flag is set.
     */
    @Override
    public void delete(@NotNull Collection<? extends INDEXED> objects) {
        if (objects.isEmpty()) {
            return;
        }

        BulkRequest request = createBulkDeleteRequest(objects);
        execute(client -> client.bulk(request, RequestOptions.DEFAULT));
        refresh();
    }

    /**
     * Removes object from index.
     *
     * @see DomainIndex#delete(DomainIndexed)
     */
    public void deletePermanently(@NotNull INDEXED obj) {
        super.delete(obj);
    }

    /**
     * Removes collection of objects from index.
     *
     * @see DomainIndex#delete(Collection)
     */
    public void deletePermanently(@NotNull Collection<? extends INDEXED> objects) {
        super.delete(objects);
    }

    /**
     * Creates index request with deleted flag for specified object.
     *
     * @param obj Specified object
     * @return Index request
     */
    protected UpdateRequest createDeleteRequest(INDEXED obj) {
        String deleted = obj.getDeleted() != null ? dateConverter.format(obj.getDeleted()) : null;

        return new UpdateRequest()
                .index(getIndexName())
                .id(obj.getId())
                .doc("deleted", deleted);
    }

    private BulkRequest createBulkDeleteRequest(Collection<? extends INDEXED> objects) {
        BulkRequest request = new BulkRequest();

        objects.stream()
                .map(this::createDeleteRequest)
                .forEach(request::add);

        return request;
    }
}
