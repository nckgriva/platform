package com.gracelogic.platform.template.service;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.gracelogic.platform.property.service.PropertyService;
import com.gracelogic.platform.template.dto.LoadedTemplate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TemplateServiceImpl implements TemplateService {
    private static final String SUBJECT_REGEX = "<!-- subject: (.*?) -->";

    @Autowired
    private PropertyService propertyService;

    @Override
    public LoadedTemplate load(String templateName) throws IOException {
        StringBuilder sb = new StringBuilder();
        String uri = String.format("%s/%s.mustache", propertyService.getPropertyValue("template:store_path"), templateName);
        readFile(uri, sb);
        String body = sb.toString();
        String subject = extractSubjectFromBody(body);
        if (!StringUtils.isEmpty(subject)) {
            body = body.replaceFirst(SUBJECT_REGEX, "");
        }

        return new LoadedTemplate(templateName, subject, body);
    }

    @Override
    public String apply(LoadedTemplate template, Map<String, String> params) {
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile(new StringReader(template.getBody()), template.getName());
        return mustache.execute(new StringWriter(), params).toString();
    }

    private static void readFile(String uri, StringBuilder sb) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(uri), Charset.forName("UTF-8"));
        for (String str : lines) {
            sb.append(str).append("\n");
        }
    }

    private static String extractSubjectFromBody(String body) throws IOException {
        Pattern pattern = Pattern.compile(SUBJECT_REGEX);
        Matcher matcher = pattern.matcher(body);
        if (matcher.find()) {
            return matcher.group(1);
        }

        return "";
    }
}
