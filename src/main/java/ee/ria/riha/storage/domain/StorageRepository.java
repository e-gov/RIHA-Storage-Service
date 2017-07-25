package ee.ria.riha.storage.domain;

import ee.ria.riha.storage.util.Filterable;
import ee.ria.riha.storage.util.Pageable;
import ee.ria.riha.storage.util.PagedResponse;

import java.util.List;

/**
 * @author Valentin Suhnjov
 */
public interface StorageRepository<K, T> {

    List<K> add(T entity);

    T get(K id);

    void update(K id, T entity);

    void remove(K id);

    PagedResponse<T> list(Pageable pageable, Filterable filterable);

}
