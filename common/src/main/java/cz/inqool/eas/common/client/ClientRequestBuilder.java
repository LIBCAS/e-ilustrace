package cz.inqool.eas.common.client;

import lombok.Getter;
import org.springframework.http.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static cz.inqool.eas.common.utils.AssertionUtils.notNull;

/**
 * Builder for inter service requests.
 *
 * @see #get()
 * @see #post()
 * @see #put()
 * @see #delete()
 */
@Getter
public abstract class ClientRequestBuilder<RESPONSE, CRB extends ClientRequestBuilder<RESPONSE, CRB>> {

    /**
     * URL path to the endpoint (without server URL)
     */
    protected String urlPath;

    /**
     * HTTP method for this request
     */
    protected HttpMethod method;

    /**
     * HTTP headers configured for this request
     */
    protected HttpHeaders headers;

    /**
     * Type of request response
     */
    protected Class<RESPONSE> responseType = (Class<RESPONSE>) Object.class;

    /**
     * Configured URI variables for this request (used for safe path parametrization)
     */
    protected Map<String, Object> uriVariables = new LinkedHashMap<>();


    public ClientRequestBuilder(HttpMethod method) {
        this.method = method;
    }


    /**
     * Create a GET method request builder.
     * <p>
     * Usage example:
     * <pre>
     *     ClientRequestBuilder&lt;ListFoo, ?&gt; request = ClientRequestBuilder.&lt;ListFoo&gt;get()
     *                 .urlPath("/foo/{id}")
     *                 .setUriVariable("id", identifier)
     *                 .withDefaultHeaders()
     *                 .responseType(ListFoo.class);
     *     ListFoo response = request.execute(restTemplate, serverUrl);
     * </pre>
     *
     * @param <RESPONSE> type of response
     */
    public static <RESPONSE> ClientRequestWithoutPayloadBuilder<RESPONSE> get() {
        return new ClientRequestWithoutPayloadBuilder<>(HttpMethod.GET);
    }

    /**
     * Create a POST method request builder.
     * <p>
     * Usage example:
     * <pre>
     *     Foo foo = new Foo();
     *     ClientRequestBuilder&lt;CreateFooResponse, ?&gt; request = ClientRequestBuilder.&lt;CreateFooResponse&gt;post()
     *                 .urlPath("/foo")
     *                 .withDefaultHeaders()
     *                 .payload(foo)
     *                 .responseType(CreateFooResponse.class);
     *     CreateFooResponse response = request.execute(restTemplate, serverUrl);
     * </pre>
     *
     * @param <RESPONSE> type of response
     */
    public static <RESPONSE> ClientRequestWithPayloadBuilder<RESPONSE> post() {
        return new ClientRequestWithPayloadBuilder<>(HttpMethod.POST);
    }

    /**
     * Create a PUT method request builder.
     * <p>
     * Usage example:
     * <pre>
     *     Foo foo = new Foo();
     *     ClientRequestBuilder&lt;CreateFooResponse, ?&gt; request = ClientRequestBuilder.&lt;CreateFooResponse&gt;put()
     *                 .urlPath("/foo/{id}")
     *                 .setUriVariable("id", identifier)
     *                 .withDefaultHeaders()
     *                 .payload(foo)
     *                 .responseType(CreateFooResponse.class);
     *     CreateFooResponse response = request.execute(restTemplate, serverUrl);
     * </pre>
     *
     * @param <RESPONSE> type of response
     */
    public static <RESPONSE> ClientRequestWithPayloadBuilder<RESPONSE> put() {
        return new ClientRequestWithPayloadBuilder<>(HttpMethod.PUT);
    }

    /**
     * Create a DELETE method request builder.
     * <p>
     * Usage example:
     * <pre>
     *     ClientRequestBuilder&lt;?, ?&gt; request = ClientRequestBuilder.delete()
     *                 .urlPath("/foo/{id}")
     *                 .setUriVariable("id", identifier);
     *     request.execute(restTemplate, serverUrl);
     * </pre>
     *
     * @param <RESPONSE> type of response
     */
    public static <RESPONSE> ClientRequestWithoutPayloadBuilder<RESPONSE> delete() {
        return new ClientRequestWithoutPayloadBuilder<>(HttpMethod.DELETE);
    }


    /**
     * Execute this request using given {@code template} and {@code serverUrl}
     */
    public abstract RESPONSE execute(RestTemplate template, String serverUrl);

    /**
     * Execute this request using given {@code template} and {@code serverUrl}
     */
    public abstract ResponseEntity<RESPONSE> executeEntity(RestTemplate template, String serverUrl);

    protected void validate() {
        notNull(urlPath, () -> new IllegalArgumentException("'urlPath' not set"));
    }

    /**
     * Set URL path to the endpoint (without server URL)
     *
     * @see ClientRequestBuilder#urlPath
     */
    public CRB urlPath(String urlPath) {
        this.urlPath = urlPath;
        //noinspection unchecked
        return (CRB) this;
    }

    /**
     * Configure HTTP headers for this request
     *
     * @see ClientRequestBuilder#headers
     */
    public HttpHeadersBuilder<CRB> headers() {
        //noinspection unchecked
        return new HttpHeadersBuilder<>((CRB) this);
    }

    /**
     * Set default HTTP headers for this request
     *
     * @see ClientRequestBuilder#headers
     */
    public CRB withDefaultHeaders() {
        //noinspection unchecked
        return new HttpHeadersBuilder<>((CRB) this)
                .setContentType(MediaType.APPLICATION_JSON)
                .set();
    }

    /**
     * Set URI variable for this request (used for safe path parametrization)
     *
     * @see ClientRequestBuilder#uriVariables
     */
    public CRB setUriVariable(String name, Object value) {
        this.uriVariables.put(name, value);
        //noinspection unchecked
        return (CRB) this;
    }

    /**
     * Set type of request response
     *
     * @see ClientRequestBuilder#responseType
     */
    public CRB responseType(Class<RESPONSE> responseType) {
        this.responseType = responseType;
        //noinspection unchecked
        return (CRB) this;
    }

    CRB setHeaders(HttpHeaders headers) {
        this.headers = headers;
        //noinspection unchecked
        return (CRB) this;
    }


    public static class HttpHeadersBuilder<REQUEST_BUILDER extends ClientRequestBuilder<?, REQUEST_BUILDER>> {

        private final HttpHeaders headers = new HttpHeaders();
        private final REQUEST_BUILDER requestBuilder;


        public HttpHeadersBuilder(REQUEST_BUILDER requestBuilder) {
            this.requestBuilder = requestBuilder;
        }


        /**
         * Set content-type header on this request
         *
         * @see HttpHeaders#setContentType(MediaType)
         */
        public HttpHeadersBuilder<REQUEST_BUILDER> setContentType(MediaType contentType) {
            headers.setContentType(contentType);
            return this;
        }

        /**
         * Set accept header on this request
         *
         * @see HttpHeaders#setAccept(List)
         */
        public HttpHeadersBuilder<REQUEST_BUILDER> setAccept(List<MediaType> accept) {
            headers.setAccept(accept);
            return this;
        }

        /**
         * Add HTTP header to this request
         */
        public HttpHeadersBuilder<REQUEST_BUILDER> add(String headerName, String headerValue) {
            headers.add(headerName, headerValue);
            return this;
        }

        /**
         * Modifies already set HTTP headers
         */
        public HttpHeadersBuilder<REQUEST_BUILDER> modify(Consumer<HttpHeaders> modifier) {
            modifier.accept(headers);
            return this;
        }

        /**
         * Return call for this HTTP headers builder, returning back to the client request builder
         */
        public REQUEST_BUILDER set() {
            return requestBuilder.setHeaders(headers);
        }
    }


    public static class ClientRequestWithoutPayloadBuilder<RESPONSE> extends ClientRequestBuilder<RESPONSE, ClientRequestWithoutPayloadBuilder<RESPONSE>> {

        public ClientRequestWithoutPayloadBuilder(HttpMethod method) {
            super(method);
        }


        @Override
        public RESPONSE execute(RestTemplate template, String serverUrl) {
            validate();

            String url = serverUrl + urlPath;

            switch (method) {
                case GET:
                    return template.getForObject(url, responseType, uriVariables);
                case DELETE:
                    template.delete(url, uriVariables);
                    return null;
                default:
                    throw new UnsupportedOperationException();
            }
        }

        @Override
        public ResponseEntity<RESPONSE> executeEntity(RestTemplate template, String serverUrl) {
            validate();

            String url = serverUrl + urlPath;

            switch (method) {
                case GET:
                    return template.exchange(url, HttpMethod.GET, null, responseType, uriVariables);
                case DELETE:
                    return template.exchange(url, HttpMethod.DELETE, null, responseType, uriVariables);
                default:
                    throw new UnsupportedOperationException();
            }
        }
    }


    public static class ClientRequestWithPayloadBuilder<RESPONSE> extends ClientRequestBuilder<RESPONSE, ClientRequestWithPayloadBuilder<RESPONSE>> {

        /**
         * Request payload
         */
        @Getter
        private Object payload;


        public ClientRequestWithPayloadBuilder(HttpMethod method) {
            super(method);
        }


        /**
         * Set request payload
         */
        public ClientRequestWithPayloadBuilder<RESPONSE> payload(Object payload) {
            this.payload = payload;
            return this;
        }

        @Override
        protected void validate() {
            super.validate();
        }

        @Override
        public RESPONSE execute(RestTemplate template, String serverUrl) throws RestClientException {
            return executeEntity(template, serverUrl).getBody();
        }

        @Override
        public ResponseEntity<RESPONSE> executeEntity(RestTemplate template, String serverUrl) throws RestClientException {
            validate();

            String url = serverUrl + urlPath;

            HttpEntity<?> request = new HttpEntity<>(payload, headers);

            switch (method) {
                case POST:
                    return template.exchange(url, HttpMethod.POST, request, responseType, uriVariables);
                case PUT:
                    return template.exchange(url, HttpMethod.PUT, request, responseType, uriVariables);
                default:
                    throw new UnsupportedOperationException();
            }
        }
    }
}
