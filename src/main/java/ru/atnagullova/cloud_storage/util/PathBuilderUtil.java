package ru.atnagullova.cloud_storage.util;

import lombok.experimental.UtilityClass;

import java.text.Normalizer;

@UtilityClass
public class PathBuilderUtil {

    public String userRoot(Long userId) {

        return "user-" + userId + "-files/";
    }

    public String buildObjectKey(Long userId, String path) {

        path = Normalizer.normalize(path, Normalizer.Form.NFC);
        return userRoot(userId) + path;
    }

    public String getObjectName(String objectKey) {

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

    public String getParentFolderPath(Long userId, String objectKey) {

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
            return path.substring(0,slash + 1);
        }

        return "";
    }

    public String getTopFolder(String userFolderKey, String originalName) {

        int slash = originalName.indexOf('/');
        String key = null;
        if (slash > 0 ) {
            key = originalName.substring(0, slash + 1);
        }

        return userFolderKey + key;
    }

}
