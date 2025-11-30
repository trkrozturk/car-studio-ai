package org.car.studio;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.car.studio.fal.FalResponse;
import org.car.studio.model.PlateConfiguration;
import org.car.studio.service.ImageGenerationService;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.io.IOException;
import java.util.List;

/**
 * REST API endpoint for automotive image generation
 * Handles HTTP requests and delegates business logic to services
 */
@Path("/generate")
public class ImageGenerationResource {

    @Inject
    ImageGenerationService imageGenerationService;

    /**
     * Generates enhanced automotive photography using AI
     *
     * @param images uploaded car images
     * @param isPlateVisible whether to keep the original license plate visible
     * @param plateImageUrl optional custom plate image
     * @param plateColor optional plate color in hex format
     * @return AI-generated image response
     * @throws IOException if file processing fails
     */
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public FalResponse generate(
            @RestForm("images") List<FileUpload> images,
            @RestForm("isPlateVisible") boolean isPlateVisible,
            @RestForm("plateImageUrl") FileUpload plateImageUrl,
            @RestForm("plateColor") String plateColor,
            @RestForm("version") String version
    ) throws IOException {
        PlateConfiguration plateConfig = new PlateConfiguration(isPlateVisible, plateImageUrl, plateColor);
        return imageGenerationService.generateImage(images, plateConfig, version);
    }
}

