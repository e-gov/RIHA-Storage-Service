package ee.ria.riha.storage.domain;

import ee.ria.riha.storage.client.StorageClient;
import ee.ria.riha.storage.domain.model.MainResource;
import ee.ria.riha.storage.util.Filterable;
import ee.ria.riha.storage.util.PageRequest;
import ee.ria.riha.storage.util.Pageable;
import ee.ria.riha.storage.util.PagedResponse;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Valentin Suhnjov
 */
public class MainResourceRepository implements StorageRepository<Long, MainResource> {

    private static final String MAIN_RESOURCE_PATH = "db/main_resource";
    private static final String NOT_IMPLEMENTED = "Not implemented";

    private final StorageClient storageClient;

    public MainResourceRepository(StorageClient storageClient) {
        this.storageClient = storageClient;
    }

    @Override
    public List<Long> add(MainResource infoSystem) {
        return storageClient.create(MAIN_RESOURCE_PATH, infoSystem.getJsonObject().toString());
    }

    @Override
    public MainResource get(Long id) {
        String infoSystem = storageClient.get(MAIN_RESOURCE_PATH, id, String.class);
        return infoSystem != null && ! infoSystem.isEmpty() ? new MainResource(infoSystem) : null;
    }

    @Override
    public void update(Long id, MainResource infoSystem) {
        throw new RuntimeException(NOT_IMPLEMENTED);
    }

    @Override
    public void remove(Long id) {
        throw new RuntimeException(NOT_IMPLEMENTED);
    }

    @Override
    public PagedResponse<MainResource> list(Pageable pageable, Filterable filterable) {
        PagedResponse<String> response = storageClient.list(MAIN_RESOURCE_PATH, pageable, filterable);

        return new PagedResponse<>(new PageRequest(response.getPage(), response.getSize()),
                                   response.getTotalElements(),
                                   response.getContent().stream()
                                           .map(MainResource::new)
                                           .collect(Collectors.toList()));
    }

}
