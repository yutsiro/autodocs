package ru.nsu.valova.protogen.ui;

import ru.nsu.valova.protogen.handlers.QuestionHandler;
import ru.nsu.valova.protogen.model.ProtocolData;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class ProtocolPreviewDialog extends JDialog {
    private ProtocolData protocolData;
    private List<QuestionHandler> selectedQuestions;
    private boolean confirmed = false;

    private JTextField protocolNumberField;
    private JTextField dayField;
    private JComboBox<String> monthCombo;
    private JTextField yearField;
    private JTextField chairmanField;
    private JTextField secretaryField;
    private JTextArea decisionsArea;

    private static final String[] MONTHS = {"января", "февраля", "марта", "апреля", "мая", "июня",
            "июля", "августа", "сентября", "октября", "ноября", "декабря"};

    public ProtocolPreviewDialog(JFrame parent, ProtocolData protocolData, List<QuestionHandler> selectedQuestions) {
        super(parent, "Предпросмотр протокола", true);
        this.protocolData = protocolData;
        this.selectedQuestions = selectedQuestions;
        initUI();
        setSize(700, 700);
        setLocationRelativeTo(parent);
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);

        addFieldsPanel(mainPanel);
        addDecisionsPanel(mainPanel);
        addAgendaPanel(mainPanel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton generateButton = new JButton("Сгенерировать");
        JButton cancelButton = new JButton("Отмена");

        generateButton.addActionListener(e -> {
            saveChanges();
            confirmed = true;
            dispose();
        });

        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(generateButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addFieldsPanel(JPanel parent) {
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setBorder(BorderFactory.createTitledBorder("Общие данные"));
        fieldsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        fieldsPanel.add(new JLabel("Номер протокола:"), gbc);
        gbc.gridx = 1;
        protocolNumberField = new JTextField(protocolData.getProtocolNumber(), 15);
        fieldsPanel.add(protocolNumberField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        fieldsPanel.add(new JLabel("Дата:"), gbc);
        gbc.gridx = 1;
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        dayField = new JTextField(protocolData.getDay(), 3);
        monthCombo = new JComboBox<>(MONTHS);
        monthCombo.setSelectedItem(protocolData.getMonth());
        yearField = new JTextField(protocolData.getYear(), 5);

        datePanel.add(dayField);
        datePanel.add(new JLabel(" "));
        datePanel.add(monthCombo);
        datePanel.add(new JLabel(" "));
        datePanel.add(yearField);
        datePanel.add(new JLabel(" г."));
        fieldsPanel.add(datePanel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        fieldsPanel.add(new JLabel("Председатель:"), gbc);
        gbc.gridx = 1;
        chairmanField = new JTextField(protocolData.getChairman(), 30);
        fieldsPanel.add(chairmanField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        fieldsPanel.add(new JLabel("Секретарь:"), gbc);
        gbc.gridx = 1;
        secretaryField = new JTextField(protocolData.getSecretary(), 30);
        fieldsPanel.add(secretaryField, gbc);

        parent.add(fieldsPanel);
        parent.add(Box.createVerticalStrut(10));
    }

    private void addDecisionsPanel(JPanel parent) {
        JPanel decisionsPanel = new JPanel(new BorderLayout());
        decisionsPanel.setBorder(BorderFactory.createTitledBorder("Текст постановлений"));
        decisionsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        StringBuilder sb = new StringBuilder();
        int counter = 1;
        for (QuestionHandler q : selectedQuestions) {
            sb.append(q.getDecisionText()).append("\n\n");
            counter++;
        }

        decisionsArea = new JTextArea(sb.toString(), 10, 40);
        decisionsArea.setLineWrap(true);
        decisionsArea.setWrapStyleWord(true);
        decisionsArea.setFont(new Font("Times New Roman", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(decisionsArea);
        decisionsPanel.add(scrollPane, BorderLayout.CENTER);

        parent.add(decisionsPanel);
        parent.add(Box.createVerticalStrut(10));
    }

    private void addAgendaPanel(JPanel parent) {
        JPanel agendaPanel = new JPanel();
        agendaPanel.setLayout(new BoxLayout(agendaPanel, BoxLayout.Y_AXIS));
        agendaPanel.setBorder(BorderFactory.createTitledBorder("Повестка дня"));
        agendaPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        int counter = 1;
        for (QuestionHandler q : selectedQuestions) {
            JLabel label = new JLabel(counter + ". " + q.getAgendaItemText());
            label.setFont(new Font("Times New Roman", Font.PLAIN, 12));
            agendaPanel.add(label);
            counter++;
        }

        parent.add(agendaPanel);
    }

    private void saveChanges() {
        // Сохраняем общие данные
        protocolData.setProtocolNumber(protocolNumberField.getText().trim());
        protocolData.setDay(dayField.getText().trim());
        protocolData.setMonth((String) monthCombo.getSelectedItem());
        protocolData.setYear(yearField.getText().trim());
        protocolData.setChairman(chairmanField.getText().trim());
        protocolData.setSecretary(secretaryField.getText().trim());

        // Сохраняем отредактированные тексты постановлений
        String[] decisionLines = decisionsArea.getText().split("\n\n");
        for (int i = 0; i < decisionLines.length && i < protocolData.getSelectedQuestions().size(); i++) {
            String text = decisionLines[i].trim();
            if (!text.isEmpty()) {
                protocolData.getSelectedQuestions().get(i).setDecisionText(text);
            }
        }
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public ProtocolData getProtocolData() {
        return protocolData;
    }
}
