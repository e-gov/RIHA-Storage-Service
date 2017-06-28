package ee.ria.riha.storage.client;

/**
 * @author Valentin Suhnjov
 */
public enum OperationType {
    GET("get"),
    COUNT("count");

    private final String value;

    OperationType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
