package org.car.studio.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.car.studio.fal.FalClient;
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
    FalClient falClient;

    @Inject
    PromptBuilderService promptBuilderService;

    @Inject
    ImagePreparationService imagePreparationService;

    /**
     * Generates enhanced automotive photography using AI
     *
     * @param images uploaded car images
     * @param plateConfig license plate configuration
     * @return AI-generated image response
     * @throws IOException if file processing fails
     */
    public FalResponse generateImage(List<FileUpload> images, PlateConfiguration plateConfig) throws IOException {
        logRequestDetails(plateConfig);

        String prompt = promptBuilderService.buildPrompt(plateConfig);
        List<String> imageUrls = imagePreparationService.prepareImageUrls(images, plateConfig);

        FalRequest request = buildFalRequest(prompt, imageUrls);

        return callFalApi(request);
    }

    private void logRequestDetails(PlateConfiguration plateConfig) {
        LOGGER.info("Starting image generation with configuration:");
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

    private FalResponse callFalApi(FalRequest request) {
        try {
            LOGGER.info("Calling Fal.ai API for image generation");
            return falClient.edit(request);
        } catch (ClientWebApplicationException e) {
            String errorBody = e.getResponse().readEntity(String.class);
            LOGGER.error("Fal.ai API Error: {}", errorBody);
            throw e;
        }
    }
}

