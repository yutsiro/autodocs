package ru.nsu.valova.protogen.handlers;

import ru.nsu.valova.protogen.config.QuestionConfigItem;
import ru.nsu.valova.protogen.config.QuestionType;
import ru.nsu.valova.protogen.model.Student;

import java.time.LocalDate;
import java.util.List;

public class QuestionHandlerFactory {
    private final String academicYear;

    public QuestionHandlerFactory() {
        int currentYear = LocalDate.now().getYear();
        int nextYear = currentYear + 1;
        this.academicYear = currentYear + "/" + nextYear;
    }

    public QuestionHandler createHandler(QuestionConfigItem config, List<Student> students) {
        if (config.getType() == QuestionType.PRACTICE_EVALUATION) {
            return new PracticeEvaluationHandler(config, students);
        } else if (config.getType() == QuestionType.INTERNSHIP_PLACEMENT) {
            return new InternshipPlacementHandler(config, students, academicYear);
        }
        throw new IllegalArgumentException("Unknown question type: " + config.getType());
    }
}
