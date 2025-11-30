package org.car.studio.model;

import org.jboss.resteasy.reactive.multipart.FileUpload;

/**
 * Value object representing license plate configuration options
 */
public record PlateConfiguration(boolean isPlateVisible, FileUpload plateImageUrl, String plateColor) {

    public boolean hasPlateImage() {
        return plateImageUrl != null;
    }

    public boolean hasPlateColor() {
        return plateColor != null && !plateColor.isEmpty();
    }

    /**
     * Determines if a custom plate image should be used
     * Priority: color > image > hide
     */
    public boolean shouldUseCustomPlateImage() {
        return !isPlateVisible && !hasPlateColor() && hasPlateImage();
    }
}

