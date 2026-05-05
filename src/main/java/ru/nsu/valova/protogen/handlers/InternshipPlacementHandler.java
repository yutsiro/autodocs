package ru.nsu.valova.protogen.handlers;

import ru.nsu.valova.protogen.config.QuestionConfigItem;
import ru.nsu.valova.protogen.model.Student;

import java.util.List;

public class InternshipPlacementHandler extends BaseQuestionHandler {
    private final String academicYear;

    public InternshipPlacementHandler(QuestionConfigItem config, List<Student> students, String academicYear) {
        super(config, students);
        this.academicYear = academicYear;
    }

    @Override
    public String getAgendaItemText() {
        return String.format("Вопрос о направлении студентов на %s в %s семестре %s уч.г.",
                config.getPracticeTypeNominative().toLowerCase(),
                getSemesterText(),
                academicYear);
    }

    @Override
    public List<String> getConsideredItems() {
        return List.of("Заявления студентов о направлении на практику");
    }

    @Override
    public String getDecisionText() {
        return "Принять за студентами места практики и руководителей от НГУ и от места практики:";
    }

    private String getSemesterText() {
        int semester = config.getSemester();
        if (semester % 2 == 0) {
            return (semester / 2) + "-м";
        }
        return ((semester + 1) / 2) + "-м";
    }

    @Override
    protected String getDefaultDecisionText() {
        return "Принять за студентами места практики и руководителей от НГУ и от места практики:";
    }
}
