package com.gracelogic.platform.user.model;

import java.util.HashMap;
import java.util.Map;

public class FilledForm {
    private Map<String, String> values = new HashMap<>();

    public Map<String, String> getValues() {
        return values;
    }

    public String getValue(String key) {
        return values.get(key);
    }

    public void setValue(String key, String value) {
        values.put(key, value);
    }
}
