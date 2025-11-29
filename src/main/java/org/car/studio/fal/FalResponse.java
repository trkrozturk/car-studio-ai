package org.car.studio.fal;

import java.util.List;

public class FalResponse {
    public List<ImageFile> images;
    public String description;

    public static class ImageFile {
        public String url;
        public String content_type;
        public String file_name;
        public long file_size;
        public int width;
        public int height;
    }
}

