package com.gracelogic.platform.user.dto;

import com.gracelogic.platform.db.dto.IdObjectModel;
import com.gracelogic.platform.user.model.UserSetting;

public class UserSettingDTO extends IdObjectModel {
    private String key;
    private String value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static UserSettingDTO prepare(UserSetting userSetting) {
        UserSettingDTO model = new UserSettingDTO();
        IdObjectModel.prepare(model, userSetting);

        model.setKey(userSetting.getKey());
        model.setValue(userSetting.getValue());

        return model;
    }
}
