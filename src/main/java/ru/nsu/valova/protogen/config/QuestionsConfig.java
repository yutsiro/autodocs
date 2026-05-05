package ru.nsu.valova.protogen.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestionsConfig {
    private static final String QUESTIONS_FILE = "src/main/resources/questions.json";
    private List<QuestionConfigItem> questions;
    private ObjectMapper objectMapper;

    public QuestionsConfig() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.questions = new ArrayList<>();
        loadQuestions();
    }

    public void loadQuestions() {
        File file = new File(QUESTIONS_FILE);

        if (!file.exists()) {
            setDefaultQuestions();
            saveQuestions();
            System.out.println("Создан файл конфигурации вопросов: " + QUESTIONS_FILE);
            return;
        }

        try {
            Map<String, List<QuestionConfigItem>> map = objectMapper.readValue(file,
                    new TypeReference<Map<String, List<QuestionConfigItem>>>() {});
            questions = map.get("questions");
            System.out.println("Вопросы загружены из файла: " + QUESTIONS_FILE);
        } catch (IOException e) {
            System.err.println("Ошибка при загрузке вопросов: " + e.getMessage());
            setDefaultQuestions();
        }
    }

    public void saveQuestions() {
        try {
            Map<String, List<QuestionConfigItem>> map = new HashMap<>();
            map.put("questions", questions);
            objectMapper.writeValue(new File(QUESTIONS_FILE), map);
            System.out.println("Вопросы сохранены в файл: " + QUESTIONS_FILE);
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении вопросов: " + e.getMessage());
        }
    }

    private void setDefaultQuestions() {
        questions = new ArrayList<>();

        questions.add(new QuestionConfigItem(
                "1",
                QuestionType.PRACTICE_EVALUATION,
                3,
                5,
                "Программная инженерия и компьютерные науки",
                "ПИиКН",
                "бакалавриат",
                "Учебная практика, научно-исследовательская работа (получение первичных навыков научно-исследовательской работы)",
                "учебной практики (научно-исследовательской работы (получения первичных навыков научно-исследовательской работы))",
                4
        ));

        questions.add(new QuestionConfigItem(
                "2",
                QuestionType.PRACTICE_EVALUATION,
                3,
                6,
                "Программная инженерия и компьютерные науки",
                "ПИиКН",
                "бакалавриат",
                "Учебная практика (эксплуатационная практика)",
                "учебной практики (эксплуатационной практики)",
                4
        ));

        questions.add(new QuestionConfigItem(
                "3",
                QuestionType.PRACTICE_EVALUATION,
                4,
                7,
                "Программная инженерия и компьютерные науки",
                "ПИиКН",
                "бакалавриат",
                "Производственная практика (научно-исследовательская работа)",
                "производственной практики (научно-исследовательской работы)",
                5
        ));

        questions.add(new QuestionConfigItem(
                "4",
                QuestionType.PRACTICE_EVALUATION,
                4,
                8,
                "Программная инженерия и компьютерные науки",
                "ПИиКН",
                "бакалавриат",
                "Производственная практика (преддипломная практика)",
                "производственной практики (преддипломной практики)",
                5
        ));

        questions.add(new QuestionConfigItem(
                "5",
                QuestionType.PRACTICE_EVALUATION,
                4,
                7,
                "Компьютерные науки и системотехника",
                "КНиС",
                "бакалавриат",
                "Учебная практика (ознакомительная практика)",
                "учебной практики (ознакомительной практики)",
                5
        ));

        questions.add(new QuestionConfigItem(
                "6",
                QuestionType.PRACTICE_EVALUATION,
                4,
                8,
                "Компьютерные науки и системотехника",
                "КНиС",
                "бакалавриат",
                "Производственная практика (технологическая (проектно-технологическая) практика)",
                "производственной практики (технологической (проектно-технологической) практики)",
                5
        ));

        questions.add(new QuestionConfigItem(
                "7",
                QuestionType.PRACTICE_EVALUATION,
                4,
                8,
                "Компьютерные науки и системотехника",
                "КНиС",
                "бакалавриат",
                "Производственная практика (научно-исследовательская работа)",
                "производственной практики (научно-исследовательской работы)",
                5
        ));
    }

    public List<QuestionConfigItem> getQuestions() {
        return new ArrayList<>(questions);
    }

    public void setQuestions(List<QuestionConfigItem> questions) {
        this.questions = questions;
        saveQuestions();
    }

    public void addQuestion(QuestionConfigItem question) {
        questions.add(question);
        saveQuestions();
    }
}
