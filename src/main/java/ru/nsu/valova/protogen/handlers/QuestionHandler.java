package ru.nsu.valova.protogen.handlers;

import ru.nsu.valova.protogen.model.Student;
import java.util.List;

public interface QuestionHandler {
    String getAgendaItemText();             // Текст для повестки дня
    List<String> getConsideredItems();      // Список пунктов "Рассматривали"
    String getDecisionText();               // Текст для "Постановили"
    List<Student> getStudents();            // Студенты для этого вопроса (если есть)
    String getQuestionCode();               // Код вопроса для выбора (например "1")
    String getQuestionDescription();        // Описание для меню

    void setDecisionText(String text);
}
