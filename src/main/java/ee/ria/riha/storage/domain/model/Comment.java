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
    private String title;
    private String comment;
    private String author_name;
    private String author_personal_code;
    private String organization_name;
    private String organization_code;
    private String status;
    private String type;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getAuthor_name() {
        return author_name;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }

    public String getAuthor_personal_code() {
        return author_personal_code;
    }

    public void setAuthor_personal_code(String author_personal_code) {
        this.author_personal_code = author_personal_code;
    }

    public String getOrganization_name() {
        return organization_name;
    }

    public void setOrganization_name(String organization_name) {
        this.organization_name = organization_name;
    }

    public String getOrganization_code() {
        return organization_code;
    }

    public void setOrganization_code(String organization_code) {
        this.organization_code = organization_code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
