package ee.ria.riha.storage.domain;

import ee.ria.riha.storage.domain.model.MainResource;
import ee.ria.riha.storage.util.Filterable;
import ee.ria.riha.storage.util.Pageable;
import ee.ria.riha.storage.util.PagedResponse;

import java.util.List;

/**
 * @author Valentin Suhnjov
 */
public interface StorageRepository<I, E> {

    List<I> add(E entity);

    E get(I id);

    void update(I id, E entity);

    void remove(I id);

    PagedResponse<E> list(Pageable pageable, Filterable filterable);

}
