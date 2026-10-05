package ru.atnagullova.cloud_storage.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PathValidationUtils {

    public static boolean isPathDirectoryCheck(Long userId, String path) {

        String objectKey = PathBuilderUtil.buildObjectKey(userId, path);
        return objectKey.endsWith("/");
    }

    public static boolean isValidPath(String path) {

        if (path.equals(null)) {
            return false;
        }
        return true;
    }

}
