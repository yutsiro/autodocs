package ru.nsu.valova.protogen.handlers;

import ru.nsu.valova.protogen.config.QuestionConfigItem;
import ru.nsu.valova.protogen.model.Student;

import java.util.List;

public abstract class BaseQuestionHandler implements QuestionHandler {
    protected QuestionConfigItem config;
    protected List<Student> students;
    protected String decisionText;

    public BaseQuestionHandler(QuestionConfigItem config, List<Student> students) {
        this.config = config;
        this.students = students;
        this.decisionText = null;
    }

    @Override
    public String getQuestionCode() {
        return config.getCode();
    }

    @Override
    public String getQuestionDescription() {
        return String.format("%s курс, %s, %s, %s",
                config.getCourse(),
                config.getDegreeLevel(),
                config.getEducationalProfileShort(),
                config.getPracticeTypeNominative());
    }

    @Override
    public List<Student> getStudents() {
        return students;
    }

    public QuestionConfigItem getConfig() {
        return config;
    }

    @Override
    public String getDecisionText() {
        if (decisionText != null) {
            return decisionText;
        }
        return getDefaultDecisionText();
    }

    protected abstract String getDefaultDecisionText();

    public void setDecisionText(String decisionText) {
        this.decisionText = decisionText;
    }
}
