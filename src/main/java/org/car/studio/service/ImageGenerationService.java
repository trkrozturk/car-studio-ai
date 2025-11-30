package org.car.studio.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.car.studio.fal.FalClientV1;
import org.car.studio.fal.FalClientV2;
import org.car.studio.fal.FalRequest;
import org.car.studio.fal.FalResponse;
import org.car.studio.model.PlateConfiguration;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.ClientWebApplicationException;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * Service responsible for orchestrating the image generation workflow
 */
@ApplicationScoped
public class ImageGenerationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageGenerationService.class);

    @Inject
    @RestClient
    FalClientV1 falClientV1;

    @Inject
    @RestClient
    FalClientV2 falClientV2;

    @Inject
    PromptBuilderService promptBuilderService;

    @Inject
    ImagePreparationService imagePreparationService;

    /**
     * Generates enhanced automotive photography using AI
     *
     * @param images uploaded car images
     * @param plateConfig license plate configuration
     * @param aiVersion AI version to use (v1 or v2)
     * @return AI-generated image response
     * @throws IOException if file processing fails
     */
    public FalResponse generateImage(List<FileUpload> images, PlateConfiguration plateConfig, String aiVersion) throws IOException {
        logRequestDetails(plateConfig, aiVersion);

        String prompt = promptBuilderService.buildPrompt(plateConfig);
        List<String> imageUrls = imagePreparationService.prepareImageUrls(images, plateConfig);

        FalRequest request = buildFalRequest(prompt, imageUrls);

        return callFalApi(request, aiVersion);
    }

    private void logRequestDetails(PlateConfiguration plateConfig, String aiVersion) {
        LOGGER.info("Starting image generation with configuration:");
        LOGGER.info("  - AI Version: {}", aiVersion);
        LOGGER.info("  - Plate visible: {}", plateConfig.isPlateVisible());
        LOGGER.info("  - Plate color: {}", plateConfig.plateColor());
        LOGGER.info("  - Has plate image: {}", plateConfig.hasPlateImage());
    }

    private FalRequest buildFalRequest(String prompt, List<String> imageUrls) {
        FalRequest request = new FalRequest();
        request.prompt = prompt;
        request.image_urls = imageUrls;
        return request;
    }

    private FalResponse callFalApi(FalRequest request, String aiVersion) {
        try {
            LOGGER.info("Calling Fal.ai API for image generation with version: {}", aiVersion);

            if ("v2".equalsIgnoreCase(aiVersion)) {
                return falClientV2.edit(request);
            }

            if (aiVersion != null && !"v1".equalsIgnoreCase(aiVersion)) {
                LOGGER.warn("Unknown AI version '{}', defaulting to v1", aiVersion);
            }

            return falClientV1.edit(request);
        } catch (ClientWebApplicationException e) {
            String errorBody = e.getResponse().readEntity(String.class);
            LOGGER.error("Fal.ai API Error: {}", errorBody);
            throw e;
        }
    }
}

