package com.gracelogic.platform.localization.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringConverter {
    private final ObjectMapper mapper = new ObjectMapper();
    private static final String I18N_PREFIX = "i18n";
    private static final String I18N_REGEX = I18N_PREFIX + "\\{(.*?)\\}";

    private static StringConverter instance = null;

    private static Log logger = LogFactory.getLog(StringConverter.class);


    public static StringConverter getInstance() {
        if (instance == null) {
            instance = new StringConverter();
        }
        return instance;
    }

    public String process(String source, Locale currentLocale) throws IOException {
        if (StringUtils.isEmpty(source) || !StringUtils.containsIgnoreCase(source, I18N_PREFIX)) {
            return source;
        }

        Pattern pattern = Pattern.compile(I18N_REGEX);
        Matcher matcher = pattern.matcher(source);
        while (matcher.find()) {
            String element = matcher.group(0);
            String processedElement = processElement(element.substring(I18N_PREFIX.length()), currentLocale);
            source = source.replace(element, processedElement);
        }

        return source;
    }

    private String processElement(String source, Locale currentLocale) {
        try {
            Map<String, String> map = mapper.readValue(source, mapper.getTypeFactory().constructMapType(Map.class, String.class, String.class));
            Map<Locale, String> available = new HashMap<Locale, String>();
            String defaultText = null;
            for (String l : map.keySet()) {
                if ("*".equals(l)) {
                    defaultText = map.get(l);
                } else {
                    final Locale present = Locale.forLanguageTag(l);
                    for (Locale exact : LocaleUtils.localeLookupList(present)) {
                        available.put(exact, l);
                    }
                }
            }

            if (currentLocale == null) {
                return defaultText == null ? "" : defaultText;
            }
            for (Locale locale : LocaleUtils.localeLookupList(currentLocale)) {
                final String availableLocale = available.get(locale);
                if (availableLocale == null) {
                    continue;
                }
                return map.get(availableLocale);
            }
            return defaultText == null ? "" : defaultText;
        } catch (IOException e) {
            logger.error("Failed to deserialize: " + source, e);
            return source;
        }
    }
}
