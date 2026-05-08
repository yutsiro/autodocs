package ru.nsu.astakhov.autodocs.integration.google.scanner;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ThesisColumnType {
    FULL_NAME(0),
    EMAIL(1),
    PHONE_NUMBER(2),
    GROUP_NAME(3),
    EDU_PROGRAM(4),
    SPECIALIZATION(5),
    ORDER_ON_APPROVAL_TOPIC(6),
    ORDER_ON_CORRECTION_TOPIC(7),
    THESIS_SUPERVISOR_NAME(8),
    THESIS_CO_SUPERVISOR_NAME(9),
    THESIS_CONSULTANT_NAME(10),
    THESIS_TOPIC(11),
    ACTUAL_SUPERVISOR(12),

    // Only for masters
    REVIEWER(13),

    // Only for 4th bachelors and 2nd masters
    THESIS_CO_SUPERVISOR_DEGREE(14),
    THESIS_CO_SUPERVISOR_TITLE(15),
    THESIS_CO_SUPERVISOR_POSITION_AND_JOB(16);

    private final int columnNumber;
}
