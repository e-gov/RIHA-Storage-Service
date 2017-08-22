package ee.ria.riha.storage.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ValueNode;
import com.fasterxml.jackson.databind.util.RawValue;
import ee.ria.riha.storage.client.StorageClient;
import ee.ria.riha.storage.domain.model.MainResource;
import ee.ria.riha.storage.util.Filterable;
import ee.ria.riha.storage.util.PageRequest;
import ee.ria.riha.storage.util.Pageable;
import ee.ria.riha.storage.util.PagedResponse;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Makes calls against RIHA-Storage and performs translation between main_resource resources and MainResource entities
 *
 * @author Valentin Suhnjov
 */
public class MainResourceRepository implements StorageRepository<Long, MainResource> {

    private static final String MAIN_RESOURCE_PATH = "db/main_resource";
    private static final String MAIN_RESOURCE_VIEW_PATH = "db/main_resource_view";
    private static final String NOT_IMPLEMENTED = "Not implemented";

    private final StorageClient storageClient;

    public MainResourceRepository(StorageClient storageClient) {
        this.storageClient = storageClient;
    }

    @Override
    public PagedResponse<MainResource> list(Pageable pageable, Filterable filterable) {
        PagedResponse<JsonNode> response = storageClient.list(MAIN_RESOURCE_VIEW_PATH, pageable, filterable,
                                                              JsonNode.class);

        return new PagedResponse<>(new PageRequest(response.getPage(), response.getSize()),
                                   response.getTotalElements(),
                                   response.getContent().stream()
                                           .map(json -> new MainResource(json.toString()))
                                           .collect(Collectors.toList()));
    }

    @Override
    public MainResource get(Long id) {
        JsonNode mainResource = storageClient.get(MAIN_RESOURCE_PATH, id, JsonNode.class);
        return mainResource != null ? new MainResource(mainResource.toString()) : null;
    }

    @Override
    public List<MainResource> find(Filterable filterable) {
        return storageClient.find(MAIN_RESOURCE_VIEW_PATH, filterable, JsonNode.class).stream()
                .map(json -> new MainResource(json.toString()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Long> add(MainResource mainResource) {
        ValueNode jsonNode = JsonNodeFactory.instance.rawValueNode(new RawValue(mainResource.getJson_context()));
        return storageClient.create(MAIN_RESOURCE_PATH, jsonNode);
    }

    @Override
    public void update(Long id, MainResource mainResource) {
        throw new RuntimeException(NOT_IMPLEMENTED);
    }

    @Override
    public void remove(Long id) {
        throw new RuntimeException(NOT_IMPLEMENTED);
    }

}
