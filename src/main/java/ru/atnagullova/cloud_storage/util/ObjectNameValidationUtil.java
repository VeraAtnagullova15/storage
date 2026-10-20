package ru.atnagullova.cloud_storage.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ObjectNameValidationUtil {

    public boolean isNameValid(String name) {

        return name != null && name.matches("[A-Za-z0-9А-Яа-яЁё _.,'’()\\[\\]+&#№!@$%=~-]+");
    }
}
