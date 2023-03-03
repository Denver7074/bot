package parser.model.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public enum ButtonAudit {
    PART_AUDIT("Частичный аудит"),
    ALL_AUDIT("Полный внешний аудит");
    String value;
}
