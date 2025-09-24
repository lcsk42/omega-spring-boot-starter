package com.lcsk42.frameworks.starter.json.core.enums;

import com.lcsk42.frameworks.starter.convention.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BigNumberSerializeMode implements BaseEnum<String> {

    FLEXIBLE("flexible", "Flexible number serialization (auto-detect best format)"),
    TO_STRING("toString",
            "Force convert numbers to string (safe for precision)"),
    NO_OPERATION("noOperation",
            "Skip serialization (keep original format)"),
            ;

    private final String value;
    private final String description;
}
