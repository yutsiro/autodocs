package ru.nsu.astakhov.autodocs.ui.view.panel;

import org.springframework.stereotype.Component;
import ru.nsu.astakhov.autodocs.repository.StudentRepository;
import ru.nsu.astakhov.autodocs.ui.controller.Controller;
import ru.nsu.astakhov.autodocs.ui.controller.handler.ProtocolGeneratorPanelEventHandler;
import ru.nsu.astakhov.autodocs.ui.view.ProtocolGeneratorFilters;
import ru.nsu.astakhov.autodocs.ui.view.component.CustomLabel;
import ru.nsu.astakhov.autodocs.ui.view.font.FontLoader;
import ru.nsu.astakhov.autodocs.ui.view.font.FontType;
import ru.nsu.valova.protogen.config.AppSettings;
import ru.nsu.valova.protogen.config.QuestionConfigItem;
import ru.nsu.valova.protogen.config.QuestionType;
import ru.nsu.valova.protogen.config.QuestionsConfig;
import ru.nsu.valova.protogen.service.ProtocolService;
import ru.nsu.valova.protogen.ui.ProtocolPreviewDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Component
public class ProtocolGeneratorPanel extends Panel {
    private final StudentRepository studentRepository;
    private final QuestionsConfig questionsConfig;
    private final AppSettings appSettings;
    private ProtocolService protocolService;

    private List<QuestionConfigItem> allQuestions;
    private List<QuestionConfigItem> filteredQuestions;
    private List<QuestionCheckBox> questionCheckBoxes;

    private JTextField protocolNumberField;
    private JPanel questionsPanel;

    private final FilterComponent courseFilter;
    private final FilterComponent semesterFilter;
    private final FilterComponent directionFilter;
    private final FilterComponent typeFilter;

    private List<JCheckBox> attendeeCheckBoxes;
    private List<String> attendeesList;

    private CardLayout cardLayout;
    private JPanel mainCardPanel;
    private JPanel questionsCardPanel;
    private JPanel attendeesCardPanel;
    private JButton nextButton;
    private JButton backButton;
    private JButton generateFinalButton;

    public ProtocolGeneratorPanel(Controller controller, StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
        this.questionsConfig = new QuestionsConfig();
        this.appSettings = new AppSettings();
        this.protocolService = new ProtocolService(studentRepository, questionsConfig, appSettings);
        this.questionCheckBoxes = new ArrayList<>();

        this.courseFilter = new FilterComponent(ProtocolGeneratorFilters.COURSE, new String[]{"Все", "1", "2", "3", "4"});
        this.semesterFilter = new FilterComponent(ProtocolGeneratorFilters.SEMESTER, new String[]{"Все", "1", "2", "3", "4", "5", "6", "7", "8"});
        this.directionFilter = new FilterComponent(ProtocolGeneratorFilters.DIRECTION, new String[]{"Все"});
        this.typeFilter = new FilterComponent(ProtocolGeneratorFilters.TYPE, new String[]{"Все", "Оценка практики", "Направление на практику"});

        controller.addListener(this);
        setEventHandler(new ProtocolGeneratorPanelEventHandler(controller, this));

        configurePanel();
    }

    @Override
    protected void configurePanel() {
        setLayout(new BorderLayout(mediumGap, mediumGap));
        setBackground(backgroundColor);
        setBorder(new EmptyBorder(mediumGap, mediumGap, mediumGap, mediumGap));

        loadAttendees();

        cardLayout = new CardLayout();
        mainCardPanel = new JPanel(cardLayout);
        mainCardPanel.setBackground(backgroundColor);
        mainCardPanel.setOpaque(false);

        questionsCardPanel = createQuestionsCardPanel();

        attendeesCardPanel = createAttendeesCardPanel();

        mainCardPanel.add(questionsCardPanel, "QUESTIONS");
        mainCardPanel.add(attendeesCardPanel, "ATTENDEES");

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createFiltersPanel(), BorderLayout.NORTH);
        add(mainCardPanel, BorderLayout.CENTER);
        add(createNavigationButtonsPanel(), BorderLayout.SOUTH);

        loadQuestions();
        cardLayout.show(mainCardPanel, "QUESTIONS");
    }

    private JPanel createNavigationButtonsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(smallGap, 0, smallGap, 0));

        backButton = new JButton("Назад");
        styleButton(backButton, new Color(100, 100, 100), Color.WHITE);
        backButton.setFont(FontLoader.loadFont(FontType.ADWAITA_SANS_REGULAR, textSize + 1));
        backButton.addActionListener(e -> {
            cardLayout.show(mainCardPanel, "QUESTIONS");
            backButton.setVisible(false);
            nextButton.setVisible(true);
            generateFinalButton.setVisible(false);
        });

        nextButton = new JButton("Далее");
        styleButton(nextButton, new Color(221, 192, 210), Color.BLACK);
        nextButton.setFont(FontLoader.loadFont(FontType.ADWAITA_SANS_REGULAR, textSize + 1));
        nextButton.addActionListener(e -> {
            if (getSelectedQuestions().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Выберите хотя бы один вопрос",
                        "Предупреждение", JOptionPane.WARNING_MESSAGE);
                return;
            }
            cardLayout.show(mainCardPanel, "ATTENDEES");
            nextButton.setVisible(false);
            backButton.setVisible(true);
            generateFinalButton.setVisible(true);
        });

        generateFinalButton = new JButton("Сгенерировать протокол");
        styleButton(generateFinalButton, new Color(221, 192, 210), Color.BLACK);
        generateFinalButton.setFont(FontLoader.loadFont(FontType.ADWAITA_SANS_REGULAR, textSize + 1));
        generateFinalButton.addActionListener(e -> generateProtocol());
        generateFinalButton.setVisible(false);

        panel.add(Box.createHorizontalGlue());
        panel.add(backButton);
        panel.add(Box.createHorizontalStrut(mediumGap));
        panel.add(nextButton);
        panel.add(Box.createHorizontalStrut(mediumGap));
        panel.add(generateFinalButton);
        panel.add(Box.createHorizontalGlue());

        return panel;
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, smallGap, smallGap));
        headerPanel.setOpaque(false);

        JLabel titleLabel = new CustomLabel("Генератор протоколов заседаний кафедры");
        titleLabel.setFont(FontLoader.loadFont(FontType.ADWAITA_SANS_REGULAR, titleTextSize));

        JLabel protocolLabel = new CustomLabel("Номер протокола:");
        protocolLabel.setFont(FontLoader.loadFont(FontType.ADWAITA_SANS_REGULAR, textSize));

        protocolNumberField = new JTextField(protocolService.generateProtocolNumber(), 15);
        protocolNumberField.setFont(FontLoader.loadFont(FontType.ADWAITA_SANS_REGULAR, textSize));

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createHorizontalStrut(mediumGap));
        headerPanel.add(protocolLabel);
        headerPanel.add(protocolNumberField);

        return headerPanel;
    }

    private JPanel createFiltersPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setBackground(focusColor);
        panel.setBorder(BorderFactory.createLineBorder(focusColor, mediumGap));

        panel.add(Box.createHorizontalGlue());
        panel.add(courseFilter.filterPanel);
        panel.add(Box.createHorizontalStrut(mediumGap));
        panel.add(semesterFilter.filterPanel);
        panel.add(Box.createHorizontalStrut(mediumGap));
        panel.add(directionFilter.filterPanel);
        panel.add(Box.createHorizontalStrut(mediumGap));
        panel.add(typeFilter.filterPanel);
        panel.add(Box.createHorizontalGlue());

        return panel;
    }

    private JScrollPane createQuestionsPanel() {
        questionsPanel = new JPanel();
        questionsPanel.setLayout(new BoxLayout(questionsPanel, BoxLayout.Y_AXIS));
        questionsPanel.setBackground(backgroundColor);

        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(backgroundColor);
        wrapperPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(focusColor),
                "Выберите вопросы повестки дня",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                FontLoader.loadFont(FontType.ADWAITA_SANS_REGULAR, textSize),
                textColor
        ));

        JScrollPane scrollPane = new JScrollPane(questionsPanel);
        scrollPane.setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(backgroundColor);
        scrollPane.getVerticalScrollBar().setUnitIncrement(smallGap);

        wrapperPanel.add(scrollPane, BorderLayout.CENTER);

        return scrollPane;
    }

    private JPanel createQuestionsCardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(backgroundColor);
        panel.setOpaque(false);

        JScrollPane scrollPane = createQuestionsPanel();
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createAttendeesCardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(backgroundColor);
        panel.setOpaque(false);

        JLabel titleLabel = new JLabel("Выберите присутствующих:");
        titleLabel.setFont(FontLoader.loadFont(FontType.ADWAITA_SANS_REGULAR, titleTextSize));
        titleLabel.setBorder(new EmptyBorder(0, 0, mediumGap, 0));

        JPanel attendeesContentPanel = new JPanel();
        attendeesContentPanel.setLayout(new BoxLayout(attendeesContentPanel, BoxLayout.Y_AXIS));
        attendeesContentPanel.setBackground(backgroundColor);
        attendeesContentPanel.setOpaque(false);

        for (String attendee : attendeesList) {
            JCheckBox checkBox = new JCheckBox(attendee);
            checkBox.setFont(FontLoader.loadFont(FontType.ADWAITA_SANS_REGULAR, textSize + 1));
            checkBox.setBackground(backgroundColor);
            checkBox.setOpaque(false);
            checkBox.setBorder(new EmptyBorder(8, 20, 8, 20));
            attendeeCheckBoxes.add(checkBox);
            attendeesContentPanel.add(checkBox);
        }

        JButton selectAllButton = new JButton("Выбрать всех");
        selectAllButton.setFont(FontLoader.loadFont(FontType.ADWAITA_SANS_REGULAR, textSize));
        selectAllButton.addActionListener(e -> {
            for (JCheckBox cb : attendeeCheckBoxes) {
                cb.setSelected(true);
            }
        });

        JButton clearAllButton = new JButton("Снять всех");
        clearAllButton.setFont(FontLoader.loadFont(FontType.ADWAITA_SANS_REGULAR, textSize));
        clearAllButton.addActionListener(e -> {
            for (JCheckBox cb : attendeeCheckBoxes) {
                cb.setSelected(false);
            }
        });

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, smallGap, smallGap));
        buttonsPanel.setOpaque(false);
        buttonsPanel.add(selectAllButton);
        buttonsPanel.add(Box.createHorizontalStrut(smallGap));
        buttonsPanel.add(clearAllButton);

        JScrollPane scrollPane = new JScrollPane(attendeesContentPanel);
        scrollPane.setOpaque(false);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(focusColor),
                "Список присутствующих",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                FontLoader.loadFont(FontType.ADWAITA_SANS_REGULAR, textSize),
                textColor
        ));
        scrollPane.getViewport().setBackground(backgroundColor);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonsPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadQuestions() {
        allQuestions = protocolService.getAvailableQuestions();
        updateDirectionFilter();
        applyFilters();
    }

    private void loadAttendees() {
        attendeesList = appSettings.getDefaultAttendees();
        attendeeCheckBoxes = new ArrayList<>();
    }

    private void updateDirectionFilter() {
        if (allQuestions == null || allQuestions.isEmpty()) {
            return;
        }

        Set<String> directions = new LinkedHashSet<>();
        directions.add("Все");
        for (QuestionConfigItem q : allQuestions) {
            directions.add(q.getEducationalProfileShort());
        }

        String[] directionArray = directions.toArray(new String[0]);
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(directionArray);

        String currentSelection = (String) directionFilter.filterComboBox.getSelectedItem();
        directionFilter.filterComboBox.setModel(model);

        if (currentSelection != null && directions.contains(currentSelection)) {
            directionFilter.filterComboBox.setSelectedItem(currentSelection);
        } else {
            directionFilter.filterComboBox.setSelectedIndex(0);
        }
    }

    private void applyFilters() {
        if (allQuestions == null) {
            return;
        }

        String selectedCourse = (String) courseFilter.filterComboBox.getSelectedItem();
        String selectedSemester = (String) semesterFilter.filterComboBox.getSelectedItem();
        String selectedDirection = (String) directionFilter.filterComboBox.getSelectedItem();
        String selectedType = (String) typeFilter.filterComboBox.getSelectedItem();

        filteredQuestions = new ArrayList<>(allQuestions);

        if (selectedCourse != null && !"Все".equals(selectedCourse)) {
            int course = Integer.parseInt(selectedCourse);
            filteredQuestions.removeIf(q -> q.getCourse() != course);
        }

        if (selectedSemester != null && !"Все".equals(selectedSemester)) {
            int semester = Integer.parseInt(selectedSemester);
            filteredQuestions.removeIf(q -> q.getSemester() != semester);
        }

        if (selectedDirection != null && !"Все".equals(selectedDirection)) {
            filteredQuestions.removeIf(q -> !selectedDirection.equals(q.getEducationalProfileShort()));
        }

        if (selectedType != null && !"Все".equals(selectedType)) {
            if ("Оценка практики".equals(selectedType)) {
                filteredQuestions.removeIf(q -> q.getType() != QuestionType.PRACTICE_EVALUATION);
            } else if ("Направление на практику".equals(selectedType)) {
                filteredQuestions.removeIf(q -> q.getType() != QuestionType.INTERNSHIP_PLACEMENT);
            }
        }

        displayQuestions();
    }

    private void displayQuestions() {
        questionsPanel.removeAll();
        questionCheckBoxes.clear();

        if (filteredQuestions == null || filteredQuestions.isEmpty()) {
            showEmptyMessage();
            questionsPanel.revalidate();
            questionsPanel.repaint();
            return;
        }

        List<QuestionConfigItem> relevantQuestions = new ArrayList<>();
        List<QuestionConfigItem> otherQuestions = new ArrayList<>();

        for (QuestionConfigItem q : filteredQuestions) {
            if (isRelevantSemester(q.getSemester())) {
                relevantQuestions.add(q);
            } else {
                otherQuestions.add(q);
            }
        }

        addQuestionsToPanel(relevantQuestions, false);

        if (!otherQuestions.isEmpty()) {
            addSeparator();
            addNotRelevantHeader();
            addQuestionsToPanel(otherQuestions, true);
        }

        questionsPanel.revalidate();
        questionsPanel.repaint();
    }

    private void showEmptyMessage() {
        JLabel emptyLabel = new JLabel("Нет вопросов, соответствующих выбранным фильтрам");
        emptyLabel.setFont(FontLoader.loadFont(FontType.ADWAITA_SANS_ITALIC, textSize));
        emptyLabel.setForeground(new Color(120, 120, 120));
        emptyLabel.setAlignmentX(CENTER_ALIGNMENT);
        questionsPanel.add(emptyLabel);
    }

    private void addSeparator() {
        questionsPanel.add(Box.createVerticalStrut(mediumGap));

        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(120, 120, 120));
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        questionsPanel.add(separator);

        questionsPanel.add(Box.createVerticalStrut(mediumGap));
    }

    private void addNotRelevantHeader() {
        JLabel notRelevantLabel = new JLabel("Вопросы следующих периодов:");
        notRelevantLabel.setFont(FontLoader.loadFont(FontType.ADWAITA_SANS_ITALIC, textSize));
        notRelevantLabel.setForeground(new Color(130, 130, 130));
        questionsPanel.add(notRelevantLabel);
        questionsPanel.add(Box.createVerticalStrut(smallGap));
    }

    private void addQuestionsToPanel(List<QuestionConfigItem> questions, boolean isNotRelevant) {
        for (QuestionConfigItem q : questions) {
            StringBuilder description = new StringBuilder();
            description.append(getCourseText(q.getCourse()));
            description.append(" ").append(getDegreeLevelText(q));
            if (q.getEducationalProfileShort() != null && !q.getEducationalProfileShort().isEmpty()) {
                description.append(" - ").append(q.getEducationalProfileShort());
            }
            description.append(" - ").append(q.getSemester());
            if (q.getPracticeTypeNominative() != null && !q.getPracticeTypeNominative().isEmpty()) {
                description.append(" - ").append(q.getPracticeTypeNominative());
            }

            JCheckBox checkBox = new JCheckBox(description.toString());
            checkBox.setFont(FontLoader.loadFont(FontType.ADWAITA_SANS_REGULAR, textSize + 1));
            checkBox.setBackground(backgroundColor);
            checkBox.setOpaque(false);
            checkBox.setBorder(new EmptyBorder(6, 12, 6, 12));

            if (isNotRelevant) {
                checkBox.setForeground(new Color(120, 120, 120));
            } else {
                checkBox.setForeground(textColor);
            }

            questionCheckBoxes.add(new QuestionCheckBox(checkBox, q));
            questionsPanel.add(checkBox);
        }
    }

    private String getCourseText(int course) {
        if (course == 1) return "1 курс";
        if (course == 2) return "2 курс";
        if (course == 3) return "3 курс";
        if (course == 4) return "4 курс";
        return course + " курс";
    }

    private String getDegreeLevelText(QuestionConfigItem q) {
        if ("магистратура".equals(q.getDegreeLevel())) {
            return "магистратура";
        }
        return "бакалавриат";
    }

    private int getCurrentSemesterType() {
        int month = LocalDate.now().getMonthValue();
        if (month >= 9 && month <= 12) {
            return 1;
        } else {
            return 2;
        }
    }

    private boolean isRelevantSemester(int questionSemester) {
        int currentType = getCurrentSemesterType();
        if (currentType == 1) {
            return questionSemester % 2 == 1;
        } else {
            return questionSemester % 2 == 0;
        }
    }

    public void generateProtocol() {
        System.out.println("generateProtocol() вызван");

        List<QuestionConfigItem> selectedQuestions = getSelectedQuestions();
        List<String> selectedAttendees = getSelectedAttendees();

        System.out.println("Выбрано вопросов: " + selectedQuestions.size());
        System.out.println("Выбрано присутствующих: " + selectedAttendees.size());

        if (selectedQuestions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Выберите хотя бы один вопрос",
                    "Предупреждение", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String protocolNumber = protocolNumberField.getText().trim();
        if (protocolNumber.isEmpty()) {
            protocolNumber = protocolService.generateProtocolNumber();
            protocolNumberField.setText(protocolNumber);
        }

        generateFinalButton.setEnabled(false);
        generateFinalButton.setText("Загрузка...");

        final String finalProtocolNumber = protocolNumber;
        final List<QuestionConfigItem> finalSelectedQuestions = new ArrayList<>(selectedQuestions);

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            private ru.nsu.valova.protogen.service.ProtocolService.ProtocolDataPreview previewData;
            private Exception error;

            @Override
            protected Void doInBackground() {
                try {
                    previewData = protocolService.prepareProtocolData(finalProtocolNumber, finalSelectedQuestions, getSelectedAttendees());
                } catch (IOException e) {
                    error = e;
                }
                return null;
            }

            @Override
            protected void done() {
                generateFinalButton.setEnabled(true);
                generateFinalButton.setText("Сгенерировать протокол");

                if (error != null) {
                    JOptionPane.showMessageDialog(ProtocolGeneratorPanel.this,
                            "Ошибка при загрузке данных: " + error.getMessage(),
                            "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                ProtocolPreviewDialog previewDialog = new ProtocolPreviewDialog(
                        null,
                        previewData.protocolData,
                        previewData.handlers
                );
                previewDialog.setVisible(true);

                if (previewDialog.isConfirmed()) {
                    try {
                        String outputDirectory = appSettings.getProtocolOutputPath();
                        String templatePath = appSettings.getDefaultTemplatePath();

                        appSettings.setLastProtocolNumberIfValid(previewData.protocolData.getProtocolNumber());

                        String outputPath = protocolService.generateFinalProtocol(outputDirectory, templatePath);

                        JOptionPane.showMessageDialog(ProtocolGeneratorPanel.this,
                                "Протокол успешно создан!\nСохранен в: " + outputPath,
                                "Готово", JOptionPane.INFORMATION_MESSAGE);

                        protocolNumberField.setText(protocolService.generateProtocolNumber());
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(ProtocolGeneratorPanel.this,
                                "Ошибка при генерации: " + ex.getMessage(),
                                "Ошибка", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        };
        worker.execute();
    }

    private List<QuestionConfigItem> getSelectedQuestions() {
        List<QuestionConfigItem> selected = new ArrayList<>();
        for (QuestionCheckBox qcb : questionCheckBoxes) {
            if (qcb.isSelected()) {
                selected.add(qcb.getQuestion());
            }
        }
        return selected;
    }

    private List<String> getSelectedAttendees() {
        List<String> selected = new ArrayList<>();
        for (JCheckBox checkBox : attendeeCheckBoxes) {
            if (checkBox.isSelected()) {
                selected.add(checkBox.getText());
            }
        }
        return selected;
    }

    private class FilterComponent {
        private final ProtocolGeneratorFilters filterType;
        private final JComboBox<String> filterComboBox;
        private final JPanel filterPanel;

        public FilterComponent(ProtocolGeneratorFilters filterType, String[] items) {
            this.filterType = filterType;
            this.filterComboBox = createFilterComboBox(items);
            this.filterPanel = createPanelFromFilter(filterComboBox);
        }

        private JComboBox<String> createFilterComboBox(String[] items) {
            JComboBox<String> comboBox = new JComboBox<>(items);
            comboBox.setFont(FontLoader.loadFont(FontType.ADWAITA_SANS_REGULAR, textSize));
            comboBox.setBackground(backgroundColor);
            comboBox.setForeground(textColor);
            comboBox.addActionListener(e -> applyFilters());
            return comboBox;
        }

        private JPanel createPanelFromFilter(JComboBox<String> comboBox) {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setOpaque(false);

            JLabel label = new CustomLabel(filterType.getValue());
            label.setBorder(BorderFactory.createEmptyBorder(0, smallGap / 2, smallGap / 2, 0));

            panel.add(label, BorderLayout.NORTH);
            panel.add(comboBox, BorderLayout.CENTER);
            return panel;
        }
    }

    private static class QuestionCheckBox {
        private final JCheckBox checkBox;
        private final QuestionConfigItem question;

        QuestionCheckBox(JCheckBox checkBox, QuestionConfigItem question) {
            this.checkBox = checkBox;
            this.question = question;
        }

        boolean isSelected() {
            return checkBox.isSelected();
        }

        QuestionConfigItem getQuestion() {
            return question;
        }
    }

    private void styleButton(JButton button, Color bgColor, Color fgColor) {
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgColor.darker()),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    @Override
    public void onTableUpdate(String updateStatus) {
    }

    @Override
    public void onDocumentGeneration(String generateStatus) {
    }
}
