package cz.inqool.eas.common.domain.index;

import cz.inqool.eas.common.domain.Domain;
import cz.inqool.eas.common.domain.DomainIndexed;
import cz.inqool.eas.common.domain.index.dto.Result;
import cz.inqool.eas.common.domain.index.dto.aggregation.Aggregation;
import cz.inqool.eas.common.domain.index.dto.params.Params;
import cz.inqool.eas.common.domain.index.dto.sort.FieldSort;
import cz.inqool.eas.common.domain.index.dto.sort.Sort;
import cz.inqool.eas.common.domain.index.field.IndexFieldNode;
import cz.inqool.eas.common.domain.index.field.IndexObjectFields;
import cz.inqool.eas.common.domain.index.field.IndexObjectParser;
import cz.inqool.eas.common.domain.store.DomainObject;
import cz.inqool.eas.common.exception.GeneralException;
import cz.inqool.eas.common.exception.v2.IndexException;
import cz.inqool.eas.common.exception.v2.InvalidArgument;
import cz.inqool.eas.common.projection.Projectable;
import cz.inqool.eas.common.utils.ResourceReader;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseException;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.PutMappingRequest;
import org.elasticsearch.xcontent.XContentType;
import org.elasticsearch.index.query.IdsQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchConverter;
import org.springframework.data.elasticsearch.core.index.MappingBuilder;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static cz.inqool.eas.common.domain.index.QueryUtils.andQuery;
import static cz.inqool.eas.common.domain.index.QueryUtils.orQuery;
import static cz.inqool.eas.common.domain.store.AggregationUtils.processAggregations;
import static cz.inqool.eas.common.exception.v2.ExceptionCode.ARGUMENT_VALUE_IS_NULL;
import static cz.inqool.eas.common.exception.v2.ExceptionCode.INDEX_QUERY_EXECUTION_ERROR;
import static cz.inqool.eas.common.utils.AssertionUtils.*;
import static org.springframework.data.elasticsearch.core.document.Document.parse;

/**
 * Index store for objects extending {@link DomainObject} with standard CRUD operations.
 *
 * @param <ROOT>      Root of the projection type system
 * @param <PROJECTED> Index projection type
 * @param <INDEXED>   Indexed object type
 */
@Slf4j
public class DomainIndex<ROOT extends Projectable<ROOT>, PROJECTED extends Projectable<ROOT>, INDEXED extends DomainIndexed<ROOT, PROJECTED>> {
    protected RestHighLevelClient client;

    protected ElasticsearchConverter converter;

    @Getter
    protected Class<INDEXED> indexedType;

    protected int ELASTIC_SIZE_LIMIT;

    private IndexObjectFields indexObjectFields;

    protected Resource settings;

    public DomainIndex(Class<INDEXED> indexedType) {
        this.indexedType = indexedType;
        this.ELASTIC_SIZE_LIMIT = 10000;
    }

    /**
     * Finds all objects using the public result extractor. Allows to
     * provide custom results extractor to return custom result type and query modifier to change the query.
     * <p>
     *
     * @param queryModifier query modifier
     * @param extractor     function for results extraction
     * @param <X>           type of returned result class
     * @return custom results wrapped by the {@link Result} class
     */
    public <X> X list(Consumer<SearchSourceBuilder> queryModifier, Function<SearchResponse, X> extractor) {
        SearchSourceBuilder builder = SearchSourceBuilder.searchSource();

        queryModifier.accept(builder);

        SearchRequest request = createSearchRequest(builder);
        if (log.isTraceEnabled()) {
            log.trace("ES query:\n{}", request.source().toString());
        }

        SearchResponse response = execute(client -> client.search(request, RequestOptions.DEFAULT));
        if (log.isTraceEnabled()) {
            log.trace("ES response:\n{}", response.toString());
        }

        return extractor.apply(response);
    }

    /**
     * Finds all objects that respect the selected {@link Params} using the public result extractor. Allows to
     * provide custom search hit mapper to return custom result type.
     * <p>
     * Through {@link Params} one could specify filtering, sorting and paging. The returned result also contains the
     * total number of objects passing through the filtering phase.
     *
     * @param params          Parameters to comply with
     * @param searchHitMapper Custom search hit mapper
     * @param <X>             Type of returned result class
     * @return Custom results wrapped by the {@link Result} class
     */
    public <X> Result<X> listByParams(@NotNull Params params, Function<SearchHit, X> searchHitMapper) {
        return listByParams(params, (builder) -> {}, searchHitMapper);
    }

    /**
     * Finds all objects that respect the selected {@link Params} using the public result extractor and other query modifier. Allows to
     * provide custom search hit mapper to return custom result type.
     * <p>
     * Through {@link Params} one could specify filtering, sorting and paging. The returned result also contains the
     * total number of objects passing through the filtering phase.
     *
     * @param params          Parameters to comply with
     * @param queryModifier query modifier
     * @param searchHitMapper Custom search hit mapper
     * @param <X>             Type of returned result class
     * @return Custom results wrapped by the {@link Result} class
     */
    public <X> Result<X> listByParams(@NotNull Params params, Consumer<SearchSourceBuilder> queryModifier, Function<SearchHit, X> searchHitMapper) {
        return list((builder) -> {
            queryParamsModifier(builder, params);
            queryModifier.accept(builder);
            }, response -> constructResult(response, params, searchHitMapper));
    }


    /**
     * Finds all objects that respect the selected {@link Params} using the public result extractor ignoring the
     * paging. Allows to provide custom search hit mapper to return custom result type.
     * <p>
     * Through {@link Params} one could specify filtering, sorting and paging (is ignored). The returned result also
     * contains the total number of objects passing through the filtering phase.
     *
     * @param params          Parameters to comply with
     * @param searchHitMapper Custom search hit mapper
     * @param <X>             Type of returned result class
     * @return List of result objects
     */
    public <X> List<X> listAllByParams(@NotNull Params params, Function<SearchHit, X> searchHitMapper) {
        return listAllByParams(params, (builder) -> {}, searchHitMapper);
    }

    /**
     * Finds all objects that respect the selected {@link Params} using the public result extractor ignoring the
     * paging. Allows to provide custom search hit mapper to return custom result type.
     * <p>
     * Through {@link Params} one could specify filtering, sorting and paging (is ignored). The returned result also
     * contains the total number of objects passing through the filtering phase.
     *
     * @param params          Parameters to comply with
     * @param queryModifier query modifier
     * @param searchHitMapper Custom search hit mapper
     * @param <X>             Type of returned result class
     * @return List of result objects
     */
    public <X> List<X> listAllByParams(@NotNull Params params, Consumer<SearchSourceBuilder> queryModifier, Function<SearchHit, X> searchHitMapper) {
        final int pageSize = 100;

        params.setSize(pageSize);
        params.setOffset(null);

        List<X> result = new ArrayList<>();
        Result<X> partialResult;
        do {
            partialResult = listByParams(params, queryModifier, searchHitMapper);
            result.addAll(partialResult.getItems());

            params.setSearchAfter(partialResult.getSearchAfter());
        } while (partialResult.getItems().size() >= pageSize);

        return result;
    }

    /**
     * Finds all objects that respect the selected {@link Params} using the public result extractor.
     * <p>
     * Through {@link Params} one could specify filtering, sorting and paging. The returned result also contains the
     * total number of objects passing through the filtering phase.
     *
     * @param params Parameters to comply with
     * @return Sorted list of object ids with total number
     */
    public Result<String> listIdsByParams(@NotNull Params params) {
        params.setFields(List.of());
        return listByParams(params, SearchHit::getId);
    }

    /**
     * Finds all objects that respect the selected {@link Params} using the public result extractor ignoring the
     * paging.
     * <p>
     * Through {@link Params} one could specify filtering, sorting and paging (is ignored). The returned result also
     * contains the total number of objects passing through the filtering phase.
     *
     * @param params Parameters to comply with
     * @return List of result ids
     */
    public List<String> listAllIdsByParams(@NotNull Params params) {
        params.setFields(List.of());
        return listAllByParams(params, SearchHit::getId);
    }

    /**
     * Return count of all objects that respect the selected {@link Params}
     *
     * @param params Parameters to comply with
     * @return count
     */
    public long countByParams(@NotNull Params params) {
        QueryBuilder queryBuilder = toQueryBuilder(params);
        if (queryBuilder == null) {
            queryBuilder = orQuery(List.of());
        }
        return count(queryBuilder);
    }

    /**
     * Return count of all objects that satisfies given {@code queryBuilder}
     *
     * @param queryBuilder queryBuilder to comply with
     * @return count
     */
    private long count(QueryBuilder queryBuilder) {
        CountRequest request = createCountRequest(queryBuilder);
        if (log.isTraceEnabled()) {
            log.trace("ES query:\n{}", request.query().toString());
        }

        CountResponse response = execute(client -> client.count(request, RequestOptions.DEFAULT));
        if (log.isTraceEnabled()) {
            log.trace("ES response:\n{}", response.toString());
        }

        return response.getCount();
    }

    /**
     * Index object implementing {@link Domain} into elasticsearch
     *
     * @param obj Object to be indexed
     */
    public void index(@NotNull INDEXED obj) {
        IndexRequest request = createIndexRequest(obj);
        execute(client -> client.index(request, RequestOptions.DEFAULT));
        refresh();
    }

    /**
     * Index collection of objects implementing {@link Domain} into elasticsearch
     *
     * @param objects Collection of objects to be indexed
     */
    public void index(@NotNull Collection<? extends INDEXED> objects) {
        if (objects.isEmpty()) {
            return;
        }

        BulkRequest request = createBulkIndexRequest(objects);
        execute(client -> client.bulk(request, RequestOptions.DEFAULT));
        refresh();
    }

    /**
     * Removes object from index.
     *
     * @param obj Object to remove
     */
    public void delete(@NotNull INDEXED obj) {
        DeleteRequest request = createDeleteRequest(obj);

        execute(client -> client.delete(request, RequestOptions.DEFAULT));
        refresh();
    }

    /**
     * Removes collection of objects from index.
     *
     * @param objects Collection of objects
     */
    public void delete(@NotNull Collection<? extends INDEXED> objects) {
        if (objects.isEmpty()) {
            return;
        }

        DeleteByQueryRequest request = createBulkDeleteRequest(objects);

        execute(client -> client.deleteByQuery(request, RequestOptions.DEFAULT));
        refresh();
    }

    /**
     * Execute a callback with the {@link RestHighLevelClient}
     *
     * @param callback The callback to execute, must not be {@literal null}
     * @param <T>      The type returned from the callback
     * @return the callback result
     */
    public <T> T execute(@NotNull ClientCallback<T> callback) {
        notNull(callback, () -> new InvalidArgument(ARGUMENT_VALUE_IS_NULL, "callback must not be null")
                .debugInfo(info -> info.name("callback")));

        try {
            return callback.doWithClient(client);
        } catch (ElasticsearchException e) {
            IndexException.Details.DetailsBuilder debugInfoBuilder = IndexException.Details.builder();
            if (e.getSuppressed().length > 0) {
                List<String> messages = Arrays.stream(e.getSuppressed())
                        .peek(throwable -> {
                            if (throwable instanceof ResponseException) {
                                Response response = ((ResponseException) throwable).getResponse();
                                debugInfoBuilder
                                        .property("request", response.getRequestLine().toString())
                                        .property("status", response.getStatusLine().toString());
                            }
                        })
                        .map(Throwable::getMessage)
                        .collect(Collectors.toList());
                debugInfoBuilder.property("messages", messages);
            }
            throw new IndexException(INDEX_QUERY_EXECUTION_ERROR, e.getMessage(), e).debugInfo(debugInfoBuilder.build());
        } catch (IOException | RuntimeException e) {
            throw new IndexException(INDEX_QUERY_EXECUTION_ERROR, e.getMessage(), e);
        }
    }

    /**
     * Flushes the index.
     */
    public void refresh() {
        RefreshRequest request = new RefreshRequest(getIndexName());
        execute(client -> client.indices().refresh(request, RequestOptions.DEFAULT));
    }

    /**
     * Initializes index storage.
     */
    public void initIndex() {
        CreateIndexRequest initRequest = createInitIndexRequest();
        execute(client -> client.indices().create(initRequest, RequestOptions.DEFAULT).isAcknowledged());

        PutMappingRequest request = createPutMappingRequest();
        execute(client -> client.indices().putMapping(request, RequestOptions.DEFAULT).isAcknowledged());
    }

    /**
     * Drops index storage.
     */
    public void dropIndex() {
        DeleteIndexRequest deleteRequest = createDeleteIndexRequest();
        execute(client -> client.indices().delete(deleteRequest, RequestOptions.DEFAULT).isAcknowledged());
    }

    /**
     * Checks if index storage is initialized.
     */
    public boolean isIndexInitialized() {
        GetIndexRequest request = new GetIndexRequest(getIndexName());
        return execute(client -> client.indices().exists(request, RequestOptions.DEFAULT));
    }

    private CreateIndexRequest createInitIndexRequest() {
        String settings = ResourceReader.asString(this.settings);
        return new CreateIndexRequest(getIndexName()).settings(settings, XContentType.JSON);
    }

    private DeleteIndexRequest createDeleteIndexRequest() {
        return new DeleteIndexRequest(getIndexName());
    }

    private SearchRequest createSearchRequest(SearchSourceBuilder builder) {

        return new SearchRequest()
                .indices(getIndexName())
                .source(builder);
    }

    private CountRequest createCountRequest(QueryBuilder builder) {
        return new CountRequest()
                .indices(getIndexName())
                .query(builder);
    }

    protected IndexRequest createIndexRequest(INDEXED obj) {
        org.springframework.data.elasticsearch.core.document.Document document = converter.mapObject(obj);

        customIndexHook(document, obj);

        return new IndexRequest().
                index(getIndexName()).
                id(obj.getId()).
                source(document);
    }

    protected BulkRequest createBulkIndexRequest(Collection<? extends INDEXED> objects) {
        BulkRequest request = new BulkRequest();
        objects.stream()
                .map(this::createIndexRequest)
                .forEach(request::add);

        return request;
    }

    private DeleteRequest createDeleteRequest(INDEXED obj) {
        return new DeleteRequest().
                index(getIndexName()).
                id(obj.getId());
    }

    private DeleteByQueryRequest createBulkDeleteRequest(Collection<? extends INDEXED> objects) {
        List<String> ids = objects.
                stream().
                map(DomainIndexed::getId).
                collect(Collectors.toList());

        IdsQueryBuilder query = QueryBuilders.
                idsQuery().
                addIds(ids.toArray(String[]::new));

        return new DeleteByQueryRequest(getIndexName())
                .setQuery(query);
    }

    protected PutMappingRequest createPutMappingRequest() {
        String mapping = new MappingBuilder(converter).
                buildPropertyMapping(indexedType);

        org.springframework.data.elasticsearch.core.document.Document document = parse(mapping);

        customMappingHook(document);

        return new PutMappingRequest(getIndexName()).
                source(document);
    }

    /**
     * Analyze indexed object class.
     */
    public void initializeIndexedFields() {
        IndexObjectFields fields = new IndexObjectFields();

        IndexObjectParser.parse(indexedType, fields);

        customFieldsHook(fields);

        if (this instanceof DynamicIndex) {
            Map<String, IndexFieldNode> dynamicFields = ((DynamicIndex) this).getDynamicFields();
            fields.putAll(dynamicFields);
        }

        this.setIndexObjectFields(fields);
    }

    /**
     * Gets ElasticSearch index name
     *
     * @return name of ElasticSearch index
     */
    protected String getIndexName() {
        Document document = indexedType.getAnnotation(Document.class);

        if (document != null) {
            return document.indexName();
        } else {
            throw new GeneralException("Missing Elasticsearch @Document.indexName for " + indexedType.getSimpleName());
        }
    }

    /**
     * Modify query by supplied Params object.
     *
     * @param builder Query
     * @param params  Params
     */
    private void queryParamsModifier(SearchSourceBuilder builder, Params params) {
        if (params.getSize() != null && params.getSize() != -1) {
            gte(params.getSize(), 0, () -> new IllegalArgumentException("size"));
        }

        int size = ((params.getSize() != null) && (params.getSize() != -1)) ? params.getSize() : ELASTIC_SIZE_LIMIT;
        builder.size(size);
        builder.trackTotalHits(true);

        ifPresent(toQueryBuilder(params), builder::query);

        List<Sort<?>> sorts = new ArrayList<>();
        ifPresent(params.getSort(), sorts::addAll);
        sorts.add(new FieldSort("id", SortOrder.ASC));

        sorts.stream()
                .map(sort -> params.getFlipDirection() ? sort.withReversedOrder() : sort)
                .map(sort -> sort.toSortBuilder(this.getIndexObjectFields()))
                .forEach(builder::sort);

        if (params.getSearchAfter() != null) {
            builder.searchAfter(params.getSearchAfter());
        } else if (params.getOffset() != null) {
            builder.from(params.getOffset());
        }

        if (params.getFields() != null) {
            builder.storedFields(params.getFields());
        }

        if (params.getAggregations() != null) {
            for (Aggregation aggregation : params.getAggregations()) {
                builder.aggregation(aggregation.toAggregationBuilder(this.getIndexObjectFields()));
            }
        }
    }

    /**
     * Create a QueryBuilder from filters on given params
     *
     * @param params Params
     * @return query builder or {@code null} if no filter is present
     */
    private QueryBuilder toQueryBuilder(Params params) {
        if (params.getFilters() != null && !params.getFilters().isEmpty()) {
            List<QueryBuilder> filterList = params.getFilters().stream()
                    .map(filter -> filter.toQueryBuilder(this.getIndexObjectFields()))
                    .collect(Collectors.toList());

            return (filterList.size() > 1) ? andQuery(filterList) : filterList.get(0);
        } else {
            return null;
        }
    }

    /**
     * Creates a result object from given search {@code response} using items returned by {@code searchHitMapper}
     *
     * @param response        ElasticSearch response
     * @param searchHitMapper Function that converts search hit to a mapped object
     * @param params          Parameters used during searching
     * @param <X>             Type of the result objects
     * @return Sorted list of objects with total number
     */
    private <X> Result<X> constructResult(SearchResponse response, Params params, Function<SearchHit, X> searchHitMapper) {
        SearchHits searchHits = response.getHits();

        List<X> items = StreamSupport.stream(searchHits.spliterator(), true)
                .map(searchHitMapper)
                .collect(Collectors.toList());

        Result<X> result = new Result<>();
        result.setItems(items);
        result.setCount(searchHits.getTotalHits().value);
        if (searchHits.getHits().length > 0) {
            SearchHit lastHit = searchHits.getAt(searchHits.getHits().length - 1);
            if (lastHit.getSortValues().length > 0) {
                result.setSearchAfter(lastHit.getSortValues());
            }
        } else { // use last searchAfter if results are empty
            result.setSearchAfter(params.getSearchAfter());
        }

        result.setAggregations(processAggregations(response.getAggregations()));

        return result;
    }

    /**
     * Returns prepared object fields.
     */
    public IndexObjectFields getIndexObjectFields() {
        return indexObjectFields;
    }

    /**
     * Sets prepared object fields.
     */
    protected void setIndexObjectFields(IndexObjectFields fields) {
        this.indexObjectFields = fields;
    }

    /**
     * Allows to add/modify custom mapping for entity.
     */
    protected void customMappingHook(org.springframework.data.elasticsearch.core.document.Document document) {
    }

    /**
     * Allows to add/modify custom fields for entity.
     */
    protected void customFieldsHook(IndexObjectFields fields) {
    }

    /**
     * Allows to add/modify custom index request for entity.
     */
    protected void customIndexHook(org.springframework.data.elasticsearch.core.document.Document document, INDEXED obj) {
    }

    @Autowired
    public void setClient(RestHighLevelClient client) {
        this.client = client;
    }

    @Autowired
    public void setConverter(ElasticsearchConverter converter) {
        this.converter = converter;
    }

    @Autowired
    public void setSettings(@Value("classpath:es_settings.json") Resource settings) {
        this.settings = settings;
    }
}
