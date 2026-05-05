package ru.nsu.valova.protogen.handlers;

import ru.nsu.valova.protogen.config.QuestionConfigItem;
import ru.nsu.valova.protogen.model.Student;

import java.util.List;
import java.util.Objects;

public class PracticeEvaluationHandler extends BaseQuestionHandler {

    public PracticeEvaluationHandler(QuestionConfigItem config, List<Student> students) {
        super(config, students);
    }

    @Override
    public String getAgendaItemText() {
        String degreeText;
        if ("магистратура".equals(config.getDegreeLevel())) {
            degreeText = "магистрантов";
        } else {
            degreeText = "бакалавров";
        }
        return String.format("Оценка результатов %s %s %s-го курса %s",
                config.getPracticeTypeGenitive(),
                degreeText,
                config.getCourse(),
                config.getEducationalProfileShort());
    }

    @Override
    public List<String> getConsideredItems() {
        String degreeTextGenitive;
        String degreeTextInstrumental;
        if ("магистратура".equals(config.getDegreeLevel())) {
            degreeTextGenitive = "магистрантов";
            degreeTextInstrumental = "магистрантами";
        } else {
            degreeTextGenitive = "бакалавров";
            degreeTextInstrumental = "бакалаврами";
        }
        return List.of(
                String.format("Отчеты %s %s-го курса об итогах прохождения %s",
                        degreeTextGenitive,
                        config.getCourse(),
                        config.getPracticeTypeGenitive()),
                String.format("Отзывы руководителей практики о прохождении %s %s %s-го курса",
                        config.getPracticeTypeGenitive(),
                        degreeTextInstrumental,
                        config.getCourse())
        );
    }

    @Override
    public String getDefaultDecisionText() {
        String degree = Objects.equals(config.getDegreeLevel(), "бакалавриат") ? "бакалаврам" : "магистрантам";
        return String.format("Утвердить следующие результаты %s %s %s-го курса %s:",
                config.getPracticeTypeGenitive(),
                degree,
                config.getCourse(),
                config.getEducationalProfileShort());
    }
}
