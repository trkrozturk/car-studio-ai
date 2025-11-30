package org.car.studio.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.car.studio.model.PlateConfiguration;

/**
 * Service responsible for building AI prompts for automotive photography
 */
@ApplicationScoped
public class PromptBuilderService {

    private static final String BASE_PROMPT =
        "Professional automotive photography of car. Dont change car angle and camera rotation or angle. " +
        "CRITICAL REQUIREMENTS: " +
        "- Maintain EXACT original camera angle and perspective " +
        "- Keep ALL car details, curves, and body lines identical " +
        "- Preserve original car proportions and dimensions ";

    private static final String QUALITY_REQUIREMENTS =
        "- Remove any visible shadows or dirt on car surface " +
        "- Keep all brand logos, emblems, and badges untouched and clearly visible " +
        "- Natural depth of field with car in sharp focus " +
        "- Soft, professional studio lighting with gentle highlights on car body " +
        "- Background slightly blurred (bokeh effect) to emphasize the car " +
        "LIGHTING & ATMOSPHERE: " +
        "- Soft diffused lighting from multiple angles " +
        "- Natural-looking shadows beneath the car " +
        "- Gentle highlights on car curves and edges " +
        "- No harsh shadows or overexposure " +
        "- Clean, professional dealership quality " +
        "OUTPUT QUALITY: " +
        "photorealistic, commercial automotive photography, sharp details, natural colors, " +
        "professional grade, magazine quality, dealership standard";

    /**
     * Builds a complete prompt for image generation
     *
     * @param plateConfig the plate configuration
     * @return complete prompt string
     */
    public String buildPrompt(PlateConfiguration plateConfig) {
        String plateInstruction = buildPlateInstruction(plateConfig);
        return BASE_PROMPT + plateInstruction + QUALITY_REQUIREMENTS;
    }

    private String buildPlateInstruction(PlateConfiguration plateConfig) {
        if (plateConfig.isPlateVisible()) {
            return "- Keep license plate visible and clear ";
        }

        // Prioritize color over image if both are provided
        if (plateConfig.hasPlateColor()) {
            return "- Replace the license plate with a solid color plate using hex color " +
                   plateConfig.plateColor() + " ";
        }

        if (plateConfig.hasPlateImage()) {
            return "- Replace the license plate with the attached plate image ";
        }

        return "- Hide or blur license plate naturally ";
    }
}

