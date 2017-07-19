package ee.ria.riha.storage.domain;

import ee.ria.riha.storage.util.Filterable;
import ee.ria.riha.storage.util.Pageable;
import ee.ria.riha.storage.util.PagedResponse;

import java.util.List;

/**
 * @author Valentin Suhnjov
 */
public interface StorageRepository<ID, T> {

    List<ID> add(T entity);

    T get(ID id);

    void update(ID id, T entity);

    void remove(ID id);

    PagedResponse<T> list(Pageable pageable, Filterable filterable);

}
