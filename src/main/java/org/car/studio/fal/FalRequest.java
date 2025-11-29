package org.car.studio.fal;

import java.util.List;

public class FalRequest {
    public String prompt;
    public int num_images = 1;
    public String aspect_ratio = "auto";
    public String output_format = "png";
    public List<String> image_urls;
    public String resolution = "1K";

    public FalRequest() {}

    public FalRequest(String prompt, List<String> image_urls) {
        this.prompt = prompt;
        this.image_urls = image_urls;
    }
}

