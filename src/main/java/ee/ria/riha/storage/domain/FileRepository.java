package ee.ria.riha.storage.domain;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import static java.net.HttpURLConnection.HTTP_OK;
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

    public ResponseEntity download(UUID uuid) throws IOException {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(fileServiceUrl)
                .path("/").path(uuid.toString());

        HttpURLConnection conn = (HttpURLConnection) new URL(uriBuilder.toUriString()).openConnection();

        ResponseEntity.BodyBuilder builder = ResponseEntity.status(conn.getResponseCode());
        if (conn.getResponseCode() != HTTP_OK) {
            return builder.build();
        }

        builder.header(HttpHeaders.CONTENT_LENGTH, conn.getHeaderField(HttpHeaders.CONTENT_LENGTH));
        builder.header(HttpHeaders.CONTENT_TYPE, conn.getHeaderField(HttpHeaders.CONTENT_TYPE));
        builder.header(HttpHeaders.CONTENT_DISPOSITION, conn.getHeaderField(HttpHeaders.CONTENT_DISPOSITION));

        return builder.body(new InputStreamResource(conn.getInputStream()));
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
