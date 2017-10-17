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

public class StringConverter {
    private final ObjectMapper mapper = new ObjectMapper();
    private static final String I18N_PREFIX = "i18n";
    private static final String I18N_BEGIN_BRACKET = "{";
    private static final String I18N_END_BRACKET = "}";

    private static StringConverter instance = null;

    private static Log logger = LogFactory.getLog(StringConverter.class);


    public static StringConverter getInstance() {
        if (instance == null) {
            instance = new StringConverter();
        }
        return instance;
    }

    public String process(String source, Locale currentLocale) {
        if (StringUtils.isEmpty(source) || !StringUtils.containsIgnoreCase(source, I18N_PREFIX)) {
            return source;
        }

        try {
            StringBuilder sb = new StringBuilder(source);
            int index = 0;
            while (index != -1) {
                index = processNext(sb, index, currentLocale);
            }
            return sb.toString();
        }
        catch (Exception e) {
            logger.warn("Exception to localize string", e);
            return source;
        }
    }

    private int processNext(StringBuilder sb, int start, Locale currentLocale) {
        int index = StringUtils.indexOf(sb, I18N_PREFIX, start);
        if (index != -1) {
            int startPos = index + I18N_PREFIX.length();
            int endPos = StringUtils.indexOf(sb, I18N_END_BRACKET, startPos);
            if (endPos != -1 && startPos < endPos) {
                endPos += I18N_END_BRACKET.length();

                String element = sb.substring(startPos, endPos);
                String processedElement = processElement(encode(element), currentLocale);
                String decodedElement = decode(processedElement);
                sb.replace(index, endPos, decodedElement);

                return index + decodedElement.length();
            }
        }
        return index;
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

    private static String encode(String str) {
        return StringUtils.replace(str, "\n", "\\n");
    }

    private static String decode(String str) {
        return StringUtils.replace(str, "\\n", "\n");
    }
}
