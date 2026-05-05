package ru.nsu.astakhov.autodocs.ui.view;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ProtocolGeneratorFilters {
    COURSE("Курс"),
    SEMESTER("Семестр"),
    DIRECTION("Направление"),
    TYPE("Тип вопроса");

    private final String value;
}
