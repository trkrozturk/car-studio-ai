package org.car.studio;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import org.car.studio.fal.FalClient;
import org.car.studio.fal.FalRequest;
import org.car.studio.fal.FalResponse;

@Path("/generate")
public class ImageGenerationResource {

    @Inject
    @RestClient
    FalClient falClient;

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public FalResponse generate(
            @RestForm String prompt,
            @RestForm("images") List<FileUpload> images
    ) throws IOException {
        System.out.println("Received prompt: " + prompt);
        
        List<String> imageUrls = new ArrayList<>();
        
        if (images != null) {
            System.out.println("Received " + images.size() + " images");
            for (FileUpload image : images) {
                // Read file content
                byte[] fileContent = Files.readAllBytes(image.uploadedFile());
                
                // Convert to Base64
                String base64 = Base64.getEncoder().encodeToString(fileContent);
                
                // Determine content type
                String contentType = image.contentType();
                if (contentType == null || contentType.isEmpty()) {
                    contentType = "image/png"; // Default fallback
                }
                
                // Create Data URI
                String dataUri = "data:" + contentType + ";base64," + base64;
                imageUrls.add(dataUri);
            }
        } else {
            System.out.println("No images received");
        }

        // Create Request
        FalRequest request = new FalRequest();
        request.prompt = prompt;
        request.image_urls = imageUrls;
        // Defaults from DTO (num_images=1, etc.) are used

        try {
            // Call API
            return falClient.edit(request);
        } catch (org.jboss.resteasy.reactive.ClientWebApplicationException e) {
            String body = e.getResponse().readEntity(String.class);
            System.err.println("Fal.ai API Error Body: " + body);
            throw e;
        }
    }
}

