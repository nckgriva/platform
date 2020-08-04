package com.gracelogic.platform.suggestion.service;

import com.gracelogic.platform.suggestion.dto.SuggestedVariant;
import com.gracelogic.platform.suggestion.exception.SuggestionProcessorNotFoundException;
import com.gracelogic.platform.user.dto.AuthorizedUser;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
public class SuggestionServiceImpl implements SuggestionService {
    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public List<SuggestedVariant> process(String processorName, String query, Collection<String> flags, AuthorizedUser executor, Map parameterMap) throws SuggestionProcessorNotFoundException {
        try {
            SuggestionProcessor processor = applicationContext.getBean(processorName, SuggestionProcessor.class);
            return processor.process(query, flags, executor, parameterMap);
        }
        catch (BeansException e) {
            throw new SuggestionProcessorNotFoundException();
        }
    }
}
