package ee.ria.riha.storage.client;

import ee.ria.riha.storage.util.Filterable;
import ee.ria.riha.storage.util.Pageable;
import ee.ria.riha.storage.util.PagedResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

import static ee.ria.riha.storage.client.OperationType.COUNT;
import static ee.ria.riha.storage.client.OperationType.GET;

/**
 * Makes requests to RIHA-Storage for data.
 *
 * @author Valentin Suhnjov
 */
public class StorageClient {

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
     * @param responseType the type of the return value
     * @return list of JSON formatted resource descriptions
     */
    public <T> T find(String path, Class<T> responseType) {
        return find(path, null, null, responseType);
    }

    /**
     * Convenient overload of {@link #find(String, Pageable, Class)}
     *
     * @param path         data resource path
     * @param pageable     pagination
     * @param responseType the type of the return value
     * @return list of JSON formatted resource descriptions
     */
    public <T> T find(String path, Pageable pageable, Class<T> responseType) {
        return find(path, pageable, null, responseType);
    }

    /**
     * Convenient overload of {@link #find(String, Pageable, Class)}
     *
     * @param path         data resource path
     * @param filterable   filtering, sorting and fields
     * @param responseType the type of the return value
     * @return list of JSON formatted resource descriptions
     */
    public <T> T find(String path, Filterable filterable, Class<T> responseType) {
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
     * @param responseType the type of the return value
     * @return retrieved resource
     */
    public <T> T find(String path, Pageable pageable, Filterable filterable, Class<T> responseType) {
        Assert.hasText(path, "path must be specified");

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

        return restTemplate.getForObject(uriBuilder.build(false).toUriString(), responseType);
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
        Assert.hasText(path, "path must be specified");

        UriComponentsBuilder uriBuilder = createRequestForPathAndOperation(path, COUNT);
        if (filterable != null && filterable.getFilter() != null) {
            uriBuilder.queryParam("filter", filterable.getFilter());
        }

        String responseString = restTemplate.getForObject(uriBuilder.build(false).toUriString(), String.class);
        JSONObject jsonObject = new JSONObject(responseString);

        return jsonObject.getLong("ok");
    }

    /**
     * Stores json data in the storage.
     *
     * @param path data resource path
     * @param data JSON data
     * @return ids of created entities
     */
    public List<Long> create(String path, String data) {
        return create(path, new JSONObject(data));
    }

    /**
     * Stores json entity in the storage.
     *
     * @param path   data resource path
     * @param entity JSON data
     * @return ids of created entities
     */
    public List<Long> create(String path, Object entity) {
        return create(path, new JSONObject(entity));
    }

    private List<Long> create(String path, JSONObject jsonObject) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(baseUrl);

        JSONObject request = new JSONObject();
        request.put("op", "post");
        request.put("path", path);
        request.put("data", jsonObject);

        ResponseEntity<List> responseEntity = restTemplate.postForEntity(uriBuilder.toUriString(),
                                                                         request.toString(),
                                                                         List.class);

        List<Long> createdEntityIds = new ArrayList<>();
        responseEntity.getBody().forEach(id -> createdEntityIds.add(((Integer) id).longValue()));

        return createdEntityIds;
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
                                     Class<? extends List<T>> responseType) {
        Assert.hasText(path, "path must be specified");

        PagedResponse<T> response = new PagedResponse<>(pageable);

        long totalElements = count(path, filterable);
        response.setTotalElements(totalElements);

        if (totalElements > 0) {
            response.setContent(find(path, pageable, filterable, responseType));
        }

        return response;
    }

    /**
     * Retrieve paged records in form of JSON array. Concrete response type is not required as response may be
     * represented by JSON with arbitrary structure.
     *
     * @param path       data resource path
     * @param pageable   paging information
     * @param filterable filtering information
     * @return paged response
     */
    public PagedResponse<String> list(String path, Pageable pageable, Filterable filterable) {
        Assert.hasText(path, "path must be specified");

        PagedResponse<String> response = new PagedResponse<>(pageable);

        long totalElements = count(path, filterable);
        response.setTotalElements(totalElements);

        if (totalElements > 0) {
            String json = find(path, pageable, filterable, String.class);
            JSONArray jsonArray = new JSONArray(json);
            jsonArray.forEach(item -> response.getContent().add(item.toString()));
        }

        return response;
    }

}
