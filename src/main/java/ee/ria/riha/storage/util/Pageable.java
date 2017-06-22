package ee.ria.riha.storage.util;

/**
 * @author Valentin Suhnjov
 */
public interface Pageable {

    int getPageNumber();

    int getPageSize();

    int getOffset();

}
