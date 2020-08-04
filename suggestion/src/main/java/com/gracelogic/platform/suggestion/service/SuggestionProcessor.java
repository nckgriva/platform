package com.gracelogic.platform.suggestion.service;

import com.gracelogic.platform.suggestion.dto.SuggestedVariant;
import com.gracelogic.platform.user.dto.AuthorizedUser;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface SuggestionProcessor {
    List<SuggestedVariant> process(String query, Collection<String> flags, AuthorizedUser executor, Map parameterMap);
}
