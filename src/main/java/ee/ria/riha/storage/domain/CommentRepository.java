package ee.ria.riha.storage.domain;

import ee.ria.riha.storage.client.StorageClient;
import ee.ria.riha.storage.domain.model.Comment;
import ee.ria.riha.storage.util.Filterable;
import ee.ria.riha.storage.util.Pageable;
import ee.ria.riha.storage.util.PagedResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Valentin Suhnjov
 */
public class CommentRepository implements StorageRepository<Long, Comment> {

    private static final String COMMENT_PATH = "db/comment";

    private final StorageClient storageClient;

    public CommentRepository(StorageClient storageClient) {
        this.storageClient = storageClient;
    }

    @Override
    public List<Long> add(Comment entity) {
        return null;
    }

    @Override
    public Comment get(Long id) {
        return null;
    }

    @Override
    public void update(Long id, Comment entity) {

    }

    @Override
    public void remove(Long id) {

    }

    @Override
    public PagedResponse<Comment> list(Pageable pageable, Filterable filterable) {
        return storageClient.list(COMMENT_PATH, pageable, filterable, CommentList.class);
    }

    private static class CommentList extends ArrayList<Comment> {
    }
}
