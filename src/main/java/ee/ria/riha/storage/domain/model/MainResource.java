package ee.ria.riha.storage.domain.model;

import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Model for Main_resource (Information System) entity.
 *
 * @author Valentin Suhnjov
 */
public class MainResource {

    private Long main_resource_id;

    @JsonRawValue
    private JsonNode json_content;

    public Long getMain_resource_id() {
        return main_resource_id;
    }

    public void setMain_resource_id(Long main_resource_id) {
        this.main_resource_id = main_resource_id;
    }

    public JsonNode getJson_content() {
        return json_content;
    }

    public void setJson_content(JsonNode json_content) {
        this.json_content = json_content;
    }

}
