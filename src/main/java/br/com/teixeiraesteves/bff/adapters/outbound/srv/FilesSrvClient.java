package br.com.teixeiraesteves.bff.adapters.outbound.srv;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import br.com.teixeiraesteves.bff.domain.model.FileData;

import java.util.List;

@Component
public class FilesSrvClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public FilesSrvClient(RestTemplate restTemplate,
                          @Value("${srv.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    private HttpHeaders headers(String authorization) {
        HttpHeaders headers = new HttpHeaders();
        if (authorization != null && !authorization.isBlank()) {
            headers.set(HttpHeaders.AUTHORIZATION, authorization);
        }
        return headers;
    }

    public ResponseEntity<FileSavedResponse> upload(
            MultipartFile file,
            String contentEncoding,
            String hashSha256Hex,
            long originalSizeBytes,
            Long gzipSizeBytes,
            String mimeType,
            String filename,
            String authorization
    ) throws Exception {

        // Prepara recurso do arquivo (mantendo nome)
        String resolvedFilename = (filename != null && !filename.isBlank())
                ? filename
                : (file.getOriginalFilename() != null ? file.getOriginalFilename() : "file.bin");

        ByteArrayResource fileResource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return resolvedFilename;
            }
        };

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", fileResource);
        body.add("contentEncoding", contentEncoding);
        body.add("hashSha256Hex", hashSha256Hex);
        body.add("originalSizeBytes", String.valueOf(originalSizeBytes));

        if (gzipSizeBytes != null) {
            body.add("gzipSizeBytes", String.valueOf(gzipSizeBytes));
        }
        if (mimeType != null && !mimeType.isBlank()) {
            body.add("mimeType", mimeType);
        }
        if (filename != null && !filename.isBlank()) {
            body.add("filename", filename);
        }

        HttpEntity<MultiValueMap<String, Object>> entity =
                new HttpEntity<>(body, headers(authorization));

        return restTemplate.exchange(
                baseUrl + "/api/files",
                HttpMethod.POST,
                entity,
                FileSavedResponse.class
        );
    }

    public ResponseEntity<byte[]> downloadRaw(Long id, String authorization) {
        HttpEntity<Void> entity = new HttpEntity<>(headers(authorization));

        ResponseEntity<byte[]> response = restTemplate.exchange(
                baseUrl + "/api/files/" + id + "/raw",
                HttpMethod.GET,
                entity,
                byte[].class
        );

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.putAll(response.getHeaders());

        return new ResponseEntity<>(response.getBody(), responseHeaders, response.getStatusCode());
    }

    // DTO igual ao do SRV (pode ser record ou class)
    public record FileSavedResponse(Long id, String hashSha256Hex) {}

    public ResponseEntity<FileData> snapshot(Long id, String authorization) {
        HttpEntity<Void> entity = new HttpEntity<>(headers(authorization));
        return restTemplate.exchange(
                baseUrl + "/api/files/" + id,
                HttpMethod.GET,
                entity,
                FileData.class
        );
    }

    public ResponseEntity<byte[]> downloadOriginal(Long id, String authorization) {
        HttpEntity<Void> entity = new HttpEntity<>(headers(authorization));
        try {
            ResponseEntity<byte[]> response = restTemplate.exchange(
                    baseUrl + "/api/files/" + id + "/download",
                    HttpMethod.GET,
                    entity,
                    byte[].class
            );
            HttpHeaders copy = new HttpHeaders();
            copy.putAll(response.getHeaders());
            return new ResponseEntity<>(response.getBody(), copy, response.getStatusCode());
        } catch (org.springframework.web.client.HttpStatusCodeException ex) {
            HttpHeaders hdrs = ex.getResponseHeaders() != null ? ex.getResponseHeaders() : new HttpHeaders();
            byte[] body = ex.getResponseBodyAsByteArray();
            return new ResponseEntity<>(body, hdrs, ex.getStatusCode());
        }
    }

    public ResponseEntity<byte[]> downloadGzip(Long id, String authorization) {
        HttpEntity<Void> entity = new HttpEntity<>(headers(authorization));
        try {
            ResponseEntity<byte[]> response = restTemplate.exchange(
                    baseUrl + "/api/files/" + id + "/download-gzip",
                    HttpMethod.GET,
                    entity,
                    byte[].class
            );
            HttpHeaders copy = new HttpHeaders();
            copy.putAll(response.getHeaders());
            return new ResponseEntity<>(response.getBody(), copy, response.getStatusCode());
        } catch (org.springframework.web.client.HttpStatusCodeException ex) {
            HttpHeaders hdrs = ex.getResponseHeaders() != null ? ex.getResponseHeaders() : new HttpHeaders();
            byte[] body = ex.getResponseBodyAsByteArray();
            return new ResponseEntity<>(body, hdrs, ex.getStatusCode());
        }
    }

    public ResponseEntity<Void> delete(Long id, String authorization) {
        HttpEntity<Void> entity = new HttpEntity<>(headers(authorization));
        return restTemplate.exchange(
                baseUrl + "/api/files/delete/" + id,
                HttpMethod.DELETE,
                entity,
                Void.class
        );
    }

    public ResponseEntity<List<FileData>> batchSnapshotsGet(
            List<Long> ids, boolean includeBase64, String authorization) {

        String idsParam = ids == null || ids.isEmpty()
                ? ""
                : ids.stream().map(String::valueOf).collect(java.util.stream.Collectors.joining(","));
        String url = baseUrl + "/api/files/batch/snapshots?ids=" + idsParam + "&includeBase64=" + includeBase64;

        HttpEntity<Void> entity = new HttpEntity<>(headers(authorization));
        ParameterizedTypeReference<List<FileData>> typeRef = new ParameterizedTypeReference<>() {};
        return restTemplate.exchange(url, HttpMethod.GET, entity, typeRef);
    }

    public ResponseEntity<List<FileData>> batchSnapshotsPost(
            br.com.teixeiraesteves.bff.dto.BatchSnapshotsRequest body,
            String authorization) {

        HttpHeaders headers = headers(authorization);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<br.com.teixeiraesteves.bff.dto.BatchSnapshotsRequest> entity =
                new HttpEntity<>(body, headers);

        ParameterizedTypeReference<List<FileData>> typeRef = new ParameterizedTypeReference<>() {};
        return restTemplate.exchange(
                baseUrl + "/api/files/batch/snapshots",
                HttpMethod.POST,
                entity,
                typeRef
        );
    }

}
