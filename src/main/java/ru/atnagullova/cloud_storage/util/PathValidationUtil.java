package ru.atnagullova.cloud_storage.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PathValidationUtil {

    public boolean isPathDirectoryCheck(Long userId, String path) {

        String objectKey = PathBuilderUtil.buildObjectKey(userId, path);
        return objectKey.endsWith("/");
    }

    public boolean isValidPath(String path) {

        return path != null;
    }

}
