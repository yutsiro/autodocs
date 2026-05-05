package ru.nsu.astakhov.autodocs.ui.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ButtonCommand {
    UPDATE_TABLE("Обновить таблицу"),
    WARNING_TABLE("Предупреждения по таблице"),
    APPLICATION_TEMPLATES("Шаблоны заявлений"),
    CREATE_APPLICATION_TEMPLATE("Создать шаблон заявления"),
    GENERATE_DOCUMENT("Сгенерировать документы"),
    SHORT_GUIDE("Краткое руководство"),
    SELECT_STUDENTS("Выбрать студентов"),
    SELECT_ALL("Выбрать всех"),
    REMOVE_ALL("Убрать всех"),
    GENERATE_ALL("Сгенерировать всех"),
    GENERATE_SELECTED("Сгенерировать выбранных"),
    GENERATE_PROTOCOL("Сгенерировать протокол");

    private final String name;

    public static ButtonCommand fromString(String strCommand) {
        for (ButtonCommand command : ButtonCommand.values()) {
            if (command.getName().equals(strCommand)) {
                return command;
            }
        }
        throw new IllegalArgumentException("No such button command: " + strCommand);
    }
}
