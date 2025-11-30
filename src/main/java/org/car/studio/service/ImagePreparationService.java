package org.car.studio.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.car.studio.model.PlateConfiguration;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service responsible for preparing image data for AI processing
 */
@ApplicationScoped
public class ImagePreparationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImagePreparationService.class);

    @Inject
    ImageEncodingService imageEncodingService;

    /**
     * Prepares a list of image data URIs from uploaded files
     *
     * @param images uploaded image files
     * @param plateConfig plate configuration
     * @return list of Base64 data URIs
     * @throws IOException if file reading fails
     */
    public List<String> prepareImageUrls(List<FileUpload> images, PlateConfiguration plateConfig) throws IOException {
        if (images == null || images.isEmpty()) {
            LOGGER.warn("No images received for processing");
            return new ArrayList<>();
        }

        LOGGER.info("Processing {} image(s)", images.size());
        List<String> imageUrls = new ArrayList<>();

        for (FileUpload image : images) {
            String dataUri = imageEncodingService.encodeToDataUri(image);
            imageUrls.add(dataUri);

            // Add custom plate image if conditions are met
            if (plateConfig.shouldUseCustomPlateImage()) {
                String plateDataUri = imageEncodingService.encodeToDataUri(plateConfig.plateImageUrl());
                imageUrls.add(plateDataUri);
                LOGGER.info("Added custom plate image to processing queue");
            }
        }

        return imageUrls;
    }
}

