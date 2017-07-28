package ee.ria.riha.storage.domain;

import ee.ria.riha.storage.client.StorageClient;
import ee.ria.riha.storage.domain.model.Comment;
import ee.ria.riha.storage.util.Filterable;
import ee.ria.riha.storage.util.Pageable;
import ee.ria.riha.storage.util.PagedResponse;

import java.util.List;

/**
 * Makes calls against RIHA-Storage and performs translation between comment resources and Comment entities
 *
 * @author Valentin Suhnjov
 */
public class CommentRepository implements StorageRepository<Long, Comment> {

    private static final String COMMENT_PATH = "db/comment";
    private static final String NOT_IMPLEMENTED = "Not implemented";

    private final StorageClient storageClient;

    public CommentRepository(StorageClient storageClient) {
        this.storageClient = storageClient;
    }

    @Override
    public PagedResponse<Comment> list(Pageable pageable, Filterable filterable) {
        return storageClient.list(COMMENT_PATH, pageable, filterable, Comment.class);
    }

    @Override
    public Comment get(Long id) {
        return storageClient.get(COMMENT_PATH, id, Comment.class);
    }

    @Override
    public List<Comment> find(Filterable filterable) {
        return storageClient.find(COMMENT_PATH, filterable, Comment.class);
    }

    @Override
    public List<Long> add(Comment entity) {
        return storageClient.create(COMMENT_PATH, entity);
    }

    @Override
    public void update(Long id, Comment entity) {
        throw new RuntimeException(NOT_IMPLEMENTED);
    }

    @Override
    public void remove(Long id) {
        throw new RuntimeException(NOT_IMPLEMENTED);
    }

}
