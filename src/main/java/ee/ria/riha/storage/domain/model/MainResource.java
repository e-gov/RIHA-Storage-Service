package ee.ria.riha.storage.domain.model;

import org.json.JSONObject;
import org.springframework.util.Assert;

/**
 * Holds Information System data as a {@link JSONObject}.
 *
 * @author Valentin Suhnjov
 */
public class MainResource {

    private JSONObject jsonObject = new JSONObject();

    public MainResource() {
        this("{}");
    }

    public MainResource(JSONObject jsonObject) {
        Assert.notNull(jsonObject);
        this.jsonObject = jsonObject;
    }

    public MainResource(String json) {
        this(new JSONObject(json));
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

}
