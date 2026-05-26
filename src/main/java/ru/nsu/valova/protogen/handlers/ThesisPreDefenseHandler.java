package ru.nsu.valova.protogen.handlers;

import ru.nsu.valova.protogen.config.QuestionConfigItem;
import ru.nsu.valova.protogen.model.Student;
import java.util.List;

public class ThesisPreDefenseHandler extends BaseQuestionHandler {

    public ThesisPreDefenseHandler(QuestionConfigItem config, List<Student> students) {
        super(config, students);
    }

    @Override
    public String getAgendaItemText() {
        if ("магистратура".equals(config.getDegreeLevel())) {
            return "Вопрос о предзащите выпускных квалификационных работ магистрантов кафедры";
        } else {
            return "Вопрос о предзащите выпускных квалификационных работ бакалавров кафедры";
        }
    }

    @Override
    public List<String> getConsideredItems() {
        if ("магистратура".equals(config.getDegreeLevel())) {
            return List.of("Доклады магистрантов по темам выпускных квалификационных работ.");
        } else {
            return List.of("Доклады бакалавров по темам выпускных квалификационных работ.");
        }
    }

    @Override
    public String getDefaultDecisionText() {
        if ("магистратура".equals(config.getDegreeLevel())) {
            return "Утвердить следующие результаты предзащиты выпускных квалификационных работ магистрантов:";
        } else {
            return "Утвердить следующие результаты предзащиты выпускных квалификационных работ бакалавров:";
        }
    }
}