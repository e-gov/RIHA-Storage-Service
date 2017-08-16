package ee.ria.riha.storage.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ee.ria.riha.storage.util.Filterable;
import ee.ria.riha.storage.util.Pageable;
import ee.ria.riha.storage.util.PagedResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import static ee.ria.riha.storage.client.OperationType.*;

/**
 * Makes requests to RIHA-Storage for data.
 *
 * @author Valentin Suhnjov
 */
public class StorageClient {

    public static final String MESSAGE_PATH_MUST_BE_SPECIFIED = "path must be specified";
    private RestTemplate restTemplate;
    private String baseUrl;

    public StorageClient(RestTemplate restTemplate, String baseUrl) {
        Assert.notNull(restTemplate, "restTemplate must be provided");
        Assert.notNull(baseUrl, "baseUrl must be provided");
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    /**
     * Convenient overload of {@link #find(String, Pageable, Class)}
     *
     * @param path         data resource path
     * @param responseType the type of the returned resource
     * @return list of found resources
     */
    public <T> List<T> find(String path, Class<T> responseType) {
        return find(path, null, null, responseType);
    }

    /**
     * Convenient overload of {@link #find(String, Pageable, Class)}
     *
     * @param path         data resource path
     * @param pageable     pagination
     * @param responseType the type of the returned resource
     * @return list of found resources
     */
    public <T> List<T> find(String path, Pageable pageable, Class<T> responseType) {
        return find(path, pageable, null, responseType);
    }

    /**
     * Convenient overload of {@link #find(String, Pageable, Class)}
     *
     * @param path         data resource path
     * @param filterable   filtering, sorting and fields
     * @param responseType the type of the returned resource
     * @return list of found resources
     */
    public <T> List<T> find(String path, Filterable filterable, Class<T> responseType) {
        return find(path, null, filterable, responseType);
    }

    /**
     * <p>Retrieves data from storage. Capable of paging, filtering and sorting. Returned fields may be restricted to
     * defined set.</p> <p>Filter format definition:
     * <pre>
     * filter               = filter-definition *[ "," filter-definition ]
     * filter-definition    = property-name "," operation "," property-value
     * property-name        = string            ; property to search for
     * operation            = "="
     *                      | ">"
     *                      | "<"
     *                      | ">="
     *                      | "<="
     *                      | "!="
     *                      | "<>"
     *                      | "like"
     *                      | "ilike"
     *                      | "?&"
     *                      | "null_or_>"
     *                      | "null_or_<="
     *                      | "isnull"
     *                      | "isnotnull"
     * property-value       = string            ; filter value
     * Example: name,like,malis,owner,=,70001484
     * </pre>
     * </p> <p>Sort format definition:
     * <pre>
     * sort                 = sort-prefix property-name
     * sort-prefix          = ""        ; sort ascending
     *                      | "-"       ; sort descending
     * property-name        = string    ; property to sort by
     *
     * Example: -name   ; order by name DESC
     * </pre>
     * </p> <p>Fields format definition:
     * <pre>
     * fields               = property-name *[ "," property-name ]
     * property-name        = string    ; property to return
     * </pre>
     * </p>
     *
     * @param path         data resource path
     * @param pageable     pagination
     * @param filterable   filtering, sorting and fields
     * @param responseType the type of the returned resource
     * @return list of found resources
     */
    public <T> List<T> find(String path, Pageable pageable, Filterable filterable, final Class<T> responseType) {
        Assert.hasText(path, MESSAGE_PATH_MUST_BE_SPECIFIED);

        UriComponentsBuilder uriBuilder = createRequestForPathAndOperation(path, GET);

        if (pageable != null) {
            uriBuilder.queryParam("limit", pageable.getPageSize());
            uriBuilder.queryParam("offset", pageable.getOffset());
        }

        if (filterable != null) {
            if (filterable.getFilter() != null) {
                uriBuilder.queryParam("filter", filterable.getFilter());
            }
            if (filterable.getSort() != null) {
                uriBuilder.queryParam("sort", filterable.getSort());
            }
            if (filterable.getFields() != null) {
                uriBuilder.queryParam("fields", filterable.getFields());
            }
        }

        ParameterizedTypeReference<List<T>> listResponseType = new ParameterizedTypeReference<List<T>>() {
            @Override
            public Type getType() {
                return new ParameterizedListTypeReference((ParameterizedType) super.getType(),
                                                          new Type[]{responseType});
            }
        };

        ResponseEntity<List<T>> responseEntity = restTemplate.exchange(uriBuilder.build(false).toUriString(),
                                                                       HttpMethod.GET, null,
                                                                       listResponseType);

        return responseEntity.getBody();
    }

    private UriComponentsBuilder createRequestForPathAndOperation(String path, OperationType operation) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("path", path)
                .queryParam("op", operation.getValue());
    }

    /**
     * Performs count operation on storage with defined filter.
     *
     * @param path       data resource path
     * @param filterable filter definition
     * @return number of records
     * @see #find(String, Pageable, Filterable, Class)
     */
    public long count(String path, Filterable filterable) {
        Assert.hasText(path, MESSAGE_PATH_MUST_BE_SPECIFIED);

        UriComponentsBuilder uriBuilder = createRequestForPathAndOperation(path, COUNT);
        if (filterable != null && filterable.getFilter() != null) {
            uriBuilder.queryParam("filter", filterable.getFilter());
        }

        JsonNode response = restTemplate.getForObject(uriBuilder.build(false).toUriString(), JsonNode.class);

        return response.get("ok").asLong();
    }

    /**
     * Stores json entity in the storage.
     *
     * @param path   data resource path
     * @param entity entity model
     * @return ids of created entities
     */
    public List<Long> create(String path, Object entity) {
        return postRequest(path, entity, POST, new ParameterizedTypeReference<List<Long>>() {
        });
    }

    private <T> T postRequest(String path, Object entity, OperationType operationType,
                              ParameterizedTypeReference<T> responseType) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(baseUrl);

        ObjectNode request = JsonNodeFactory.instance.objectNode();
        request.put("op", operationType.getValue());
        request.put("path", path);
        request.putPOJO("data", entity);

        ResponseEntity<T> responseEntity = restTemplate.exchange(uriBuilder.toUriString(),
                                                                 HttpMethod.POST,
                                                                 new HttpEntity<Object>(request),
                                                                 responseType
        );

        return responseEntity.getBody();
    }

    /**
     * Retrieves single record with given id. Creates request in form
     * <pre>
     * request-parameters = "path=" resource-path "/" record-id "&op=get"
     * </pre>
     *
     * @param path         data resource path
     * @param id           an id of a record
     * @param responseType the type of the return value
     * @return single record with given id
     */
    public <T> T get(String path, Long id, Class<T> responseType) {
        UriComponentsBuilder uriBuilder = createRequestForPathAndOperation(path + "/" + id.toString(), GET);
        return restTemplate.getForObject(uriBuilder.toUriString(), responseType);
    }

    /**
     * Retrieve paged records and converts them to specified type.
     *
     * @param path         data resource path
     * @param pageable     paging information
     * @param filterable   filtering information
     * @param responseType concrete class for result binding
     * @return paged response
     */
    public <T> PagedResponse<T> list(String path, Pageable pageable, Filterable filterable,
                                     Class<T> responseType) {
        Assert.hasText(path, MESSAGE_PATH_MUST_BE_SPECIFIED);

        PagedResponse<T> response = new PagedResponse<>(pageable);

        long totalElements = count(path, filterable);
        response.setTotalElements(totalElements);

        if (totalElements > 0) {
            response.setContent(find(path, pageable, filterable, responseType));
        }

        return response;
    }

    /**
     * Updates existing entity in the storage.
     *
     * @param path   data resource path
     * @param id     an id of a record
     * @param entity entity model  @return ids of updated entitites
     * @return number of updated records
     */
    public Long update(String path, Long id, Object entity) {
        JsonNode response = postRequest(path + "/" + id.toString(), entity, PUT,
                                        new ParameterizedTypeReference<JsonNode>() {
                                        });

        return response.get("ok").asLong();
    }

    public class ParameterizedListTypeReference implements ParameterizedType {
        private ParameterizedType delegate;
        private Type[] actualTypeArguments;

        ParameterizedListTypeReference(ParameterizedType delegate, Type[] actualTypeArguments) {
            this.delegate = delegate;
            this.actualTypeArguments = actualTypeArguments;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return actualTypeArguments;
        }

        @Override
        public Type getRawType() {
            return delegate.getRawType();
        }

        @Override
        public Type getOwnerType() {
            return delegate.getOwnerType();
        }

    }

}
