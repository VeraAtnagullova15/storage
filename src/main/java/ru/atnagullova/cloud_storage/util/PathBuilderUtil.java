package ru.atnagullova.cloud_storage.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PathBuilderUtil {

    public static String userRoot(Long userId) {

        return "user-" + userId + "-files/";
    }

    public static String buildObjectKey(Long userId, String path) {

        return userRoot(userId) + path;
    }

    public static String getObjectName(String objectKey) {

        String name;
        if (objectKey.endsWith("/")) {
            name = objectKey.substring(0, objectKey.length() -1);
        } else {
            name = objectKey;
        }

        int slash = name.lastIndexOf('/');
        if (slash > 0) {
            return name.substring(slash + 1);
        }

        return name;
    }

    public static String getParentFolderPath(Long userId, String objectKey) {

        String root = userRoot(userId);
        String pathWithoutRoot = objectKey.substring(root.length());

        String path;
        if (pathWithoutRoot.endsWith("/")) {
            path = pathWithoutRoot.substring(0, pathWithoutRoot.length() - 1);
        } else {
            path = pathWithoutRoot;
        }

        int slash = path.lastIndexOf('/');
        if (slash != -1) {
            return path.substring(slash + 1);
        }

        return "";
    }

}
