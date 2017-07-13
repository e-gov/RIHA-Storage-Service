package ee.ria.riha.storage.domain.model;

import java.util.UUID;

/**
 * Holds comment entity.
 *
 * @author Valentin Suhnjov
 */
public class Comment {
    private Long comment_id;
    private Long comment_parent_id;
    private UUID infosystem_uuid;

    public Long getComment_id() {
        return comment_id;
    }

    public void setComment_id(Long comment_id) {
        this.comment_id = comment_id;
    }

    public Long getComment_parent_id() {
        return comment_parent_id;
    }

    public void setComment_parent_id(Long comment_parent_id) {
        this.comment_parent_id = comment_parent_id;
    }

    public UUID getInfosystem_uuid() {
        return infosystem_uuid;
    }

    public void setInfosystem_uuid(UUID infosystem_uuid) {
        this.infosystem_uuid = infosystem_uuid;
    }
}
