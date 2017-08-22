package ee.ria.riha.storage.domain.model;

/**
 * Holds Information System data as json string.
 *
 * @author Valentin Suhnjov
 */
public class MainResource {

    private String json_context;

    public MainResource() {
    }

    public MainResource(String json_context) {
        this.json_context = json_context;
    }

    public String getJson_context() {
        return json_context;
    }

    public void setJson_context(String json_context) {
        this.json_context = json_context;
    }

}
