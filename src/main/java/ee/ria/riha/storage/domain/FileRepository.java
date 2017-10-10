package ee.ria.riha.storage.domain;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.InputStream;
import java.util.UUID;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;

/**
 * Stand alone repository for file resource upload and download.
 */
public class FileRepository {

    private static final String FILE_PATH = "/file";

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String fileServiceUrl;

    public FileRepository(RestTemplate restTemplate, String baseUrl) {
        Assert.notNull(restTemplate, "restTemplate must be provided");
        Assert.notNull(baseUrl, "baseUrl must be provided");
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
        this.fileServiceUrl = UriComponentsBuilder.fromHttpUrl(baseUrl).path(FILE_PATH).toUriString();
    }

    public UUID upload(InputStream inputStream, String fileName, String contentType) {
        Assert.notNull(inputStream, "uploaded file input stream must be defined");
        Assert.hasText(fileName, "uploaded file name must be defined");
        Assert.hasText(contentType, "uploaded file content type must be defined");

        HttpEntity<InputStreamResource> filePart = createFilePart(inputStream, fileName, contentType);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>(1);
        parts.add("file", filePart);

        String response = restTemplate.postForObject(fileServiceUrl, new HttpEntity<>(parts, headers), String.class);

        return UUID.fromString(response);
    }

    private HttpEntity<InputStreamResource> createFilePart(InputStream inputStream, String fileName, String contentType) {
        InputStreamResource part = new MultipartInputStreamFileResource(inputStream, fileName);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, contentType);

        return new HttpEntity<>(part, headers);
    }

    private static class MultipartInputStreamFileResource extends InputStreamResource {
        private final String filename;

        MultipartInputStreamFileResource(InputStream inputStream, String filename) {
            super(inputStream);
            this.filename = filename;
        }

        @Override
        public String getFilename() {
            return this.filename;
        }

        @Override
        public long contentLength() {
            return -1;
        }

    }

}
