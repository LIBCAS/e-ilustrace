package cz.inqool.eas.common.domain.index;

import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;

/**
 * Callback for wrapping elasticsearch calls.
 * @param <T>
 */
public interface ClientCallback<T> {
    T doWithClient(RestHighLevelClient client) throws IOException;
}
