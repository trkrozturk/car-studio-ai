package org.car.studio.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

/**
 * Service responsible for encoding image files to Base64 data URIs
 */
@ApplicationScoped
public class ImageEncodingService {

    private static final String DEFAULT_CONTENT_TYPE = "image/png";

    /**
     * Converts a file upload to a Base64-encoded data URI
     *
     * @param fileUpload the file to encode
     * @return Base64 data URI string
     * @throws IOException if file cannot be read
     */
    public String encodeToDataUri(FileUpload fileUpload) throws IOException {
        byte[] fileContent = Files.readAllBytes(fileUpload.uploadedFile());
        String base64 = Base64.getEncoder().encodeToString(fileContent);
        String contentType = determineContentType(fileUpload);

        return "data:" + contentType + ";base64," + base64;
    }

    private String determineContentType(FileUpload fileUpload) {
        String contentType = fileUpload.contentType();
        return (contentType == null || contentType.isEmpty()) ? DEFAULT_CONTENT_TYPE : contentType;
    }
}

