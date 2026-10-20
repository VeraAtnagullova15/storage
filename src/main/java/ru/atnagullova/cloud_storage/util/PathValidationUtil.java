package ru.atnagullova.cloud_storage.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.text.Normalizer;

@UtilityClass
public class PathValidationUtil {

    public boolean isPathDirectoryCheck(Long userId, String path) {

        String objectKey = PathBuilderUtil.buildObjectKey(userId, path);
        return objectKey.endsWith("/");
    }

    public boolean isValidPath(String path) {

        if (path == null) {
            return false;
        }
        path = Normalizer.normalize(path, Normalizer.Form.NFC);
        if (path.isEmpty()) {
            return true;
        }

        String pathLine = path.endsWith("/") ? path.substring(0, path.length() - 1) : path;
        for (String partOfPath : pathLine.split("/", -1)) {
            if (partOfPath.equals(".") || partOfPath.equals("..")) {
                return false;
            }
            if (!ObjectNameValidationUtil.isNameValid(partOfPath)) {
                return false;
            }
        }

        return true;
    }

}
