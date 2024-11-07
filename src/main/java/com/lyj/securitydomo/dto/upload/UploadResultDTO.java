package com.lyj.securitydomo.dto.upload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Random;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UploadResultDTO {
    private String uuid;
    private String fileName;
    private int pno;

    // 이미지 링크를 반환하는 메서드
    public String getLink() {
        return (uuid != null && fileName != null)
                ? "/view/s_" + uuid + "_" + fileName
                : getRandomImage();
    }

    // 기본 랜덤 이미지를 반환하는 메서드
    public static String getRandomImage() {
        String[] defaultImages = {
                "https://dummyimage.com/450x300/dee2e6/6c757d.jpg",
                "https://dummyimage.com/450x300/ced4da/495057.jpg",
                "https://dummyimage.com/450x300/e9ecef/adb5bd.jpg"
        };
        Random random = new Random();
        return defaultImages[random.nextInt(defaultImages.length)];
    }
}