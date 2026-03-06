package com.socialplatform.core.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportReason {

    SPAM("Repetitive or promotional content"),
    HATE_SPEECH("Hateful speech (race, religion, gender, etc.)"),
    SELF_HARM("Content that promotes self-harm (Critical priority)"),
    INAPPROPRIATE_CONTENT("Nudity, violence or other sensitive/inappropriate content"),
    HARASSMENT("Harassment, bullying, or personal attacks"),
    FALSE_INFORMATION("Misinformation or \"fake news\""),
    ILLEGAL_ACTIVITY("Illegal activities such as sale of substances, weapons, or fraud"),
    OTHER("Uncategorized cases (requires additional description)");

    private final String description;
}
