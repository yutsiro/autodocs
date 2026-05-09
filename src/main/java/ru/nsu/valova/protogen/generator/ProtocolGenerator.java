package ru.nsu.valova.protogen.generator;

import org.apache.poi.xwpf.usermodel.*;
import ru.nsu.valova.protogen.handlers.InternshipPlacementHandler;
import ru.nsu.valova.protogen.handlers.QuestionHandler;
import ru.nsu.valova.protogen.model.ProtocolData;
import ru.nsu.valova.protogen.model.Student;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class ProtocolGenerator {

    public static void generateProtocol(
            String templatePath,
            String outputPath,
            ProtocolData protocolData
    ) throws IOException {

        try (FileInputStream fis = new FileInputStream(templatePath);
             XWPFDocument document = new XWPFDocument(fis)) {

            Map<String, String> values = new HashMap<>();
            values.put("PROTOCOL_NUMBER", protocolData.getProtocolNumber());
            values.put("DAY", protocolData.getDay());
            values.put("MONTH", protocolData.getMonth());
            values.put("YEAR", protocolData.getYear());
            values.put("CHAIRMAN", protocolData.getChairman());
            values.put("SECRETARY", protocolData.getSecretary());

            values.put("CHAIRMAN_NAME", extractLastName(protocolData.getChairman()));
            values.put("SECRETARY_NAME", extractLastName(protocolData.getSecretary()));

            replaceTextPlaceholders(document, values);

            replaceAttendeesPlaceholder(document, protocolData.getAttendees());

            replacePlaceholderWithNumberedList(document, "{{AGENDA_ITEMS}}",
                    protocolData.getAllAgendaItems());

            replacePlaceholderWithNumberedList(document, "{{CONSIDERED_ITEMS}}",
                    protocolData.getAllConsideredItems());

            removePlaceholders(document, "{{DECISION_TEXT}}", "{{STUDENTS_TABLE}}");
            removeEmptyParagraphsAfterHeader(document, "ПОСТАНОВИЛИ");

            int counter = 1;
            for (QuestionHandler question : protocolData.getSelectedQuestions()) {
                XWPFParagraph decisionPara = document.createParagraph();
                decisionPara.setIndentationLeft(720);
                decisionPara.setIndentationHanging(360);

                XWPFRun decisionRun = decisionPara.createRun();
                setDefaultFont(decisionRun);
                decisionRun.setText(counter + ".     " + question.getDecisionText());

                if (question.getStudents() != null && !question.getStudents().isEmpty()) {
                    if (question instanceof InternshipPlacementHandler) {
                        createInternshipTable(document, question.getStudents());
                    } else {
                        createPracticeEvaluationTable(document, question.getStudents());
                    }
                    document.createParagraph();
                }
                counter++;
            }

            createSignatureTable(document, values.get("CHAIRMAN_NAME"), values.get("SECRETARY_NAME"));

            try (FileOutputStream fos = new FileOutputStream(outputPath)) {
                document.write(fos);
            }
        }
    }

    private static void removePlaceholders(XWPFDocument document, String... placeholders) {
        List<XWPFParagraph> paragraphsToRemove = new ArrayList<>();

        for (XWPFParagraph paragraph : document.getParagraphs()) {
            String text = paragraph.getText();
            if (text != null) {
                for (String placeholder : placeholders) {
                    if (text.contains(placeholder)) {
                        paragraphsToRemove.add(paragraph);
                        break;
                    }
                }
            }
        }

        for (XWPFParagraph paragraph : paragraphsToRemove) {
            document.removeBodyElement(document.getPosOfParagraph(paragraph));
        }
    }

    private static void removeEmptyParagraphsAfterHeader(XWPFDocument document, String headerText) {
        boolean foundHeader = false;

        for (int i = 0; i < document.getParagraphs().size(); i++) {
            XWPFParagraph paragraph = document.getParagraphs().get(i);
            String text = paragraph.getText();

            if (!foundHeader && text != null && text.contains(headerText)) {
                foundHeader = true;
                continue;
            }

            if (foundHeader) {
                if (text == null || text.trim().isEmpty()) {
                    document.removeBodyElement(i);
                    i--;
                } else {
                    break;
                }
            }
        }
    }

    private static void createPracticeEvaluationTable(XWPFDocument document, List<Student> students) {
        if (students == null || students.isEmpty()) return;

        Student sample = students.getFirst();

        XWPFParagraph headerPara = document.createParagraph();
        headerPara.setSpacingBefore(200);
        headerPara.setSpacingAfter(100);
        XWPFRun headerRun = headerPara.createRun();
        setDefaultFont(headerRun);
        headerRun.setBold(true);
        headerRun.setFontSize(12);
        headerRun.setText("Образовательная программа (профиль): " + sample.getEducationalProgram() + ". " + sample.getEducationalProfile());

        XWPFTable table = document.createTable();
        table.setWidth("100%");

        XWPFTableRow headerRow = table.getRow(0);
        if (headerRow == null) headerRow = table.createRow();

        while (headerRow.getTableCells().size() < 6) {
            headerRow.addNewTableCell();
        }

        setHeaderCellWithLineBreak(headerRow.getCell(0), "ФИО студента");
        setHeaderCellWithLineBreak(headerRow.getCell(1), "Курс, группа");
        setHeaderCellWithLineBreak(headerRow.getCell(2), "Название организации, структурного подразделения, номер кабинета");
        setHeaderCellWithLineBreak(headerRow.getCell(3), "Руководитель от НГУ", "(ФИО, должность, степень, звание)");
        setHeaderCellWithLineBreak(headerRow.getCell(4), "Руководитель от института", "(ФИО, должность, степень, звание)");
        setHeaderCellWithLineBreak(headerRow.getCell(5), "Оценка");

        for (Student student : students) {
            XWPFTableRow row = table.createRow();
            while (row.getTableCells().size() < 6) {
                row.addNewTableCell();
            }

            setDataCell(row.getCell(0), student.getFullName());
            setDataCell(row.getCell(1), student.getCourseGroup());
            setDataCell(row.getCell(2), student.getPracticeBase());
            setDataCell(row.getCell(3), student.getFullNsuSupervisor());
            setDataCell(row.getCell(4), student.getFullInstituteSupervisor());
            setDataCell(row.getCell(5), "");
        }
    }

    private static void setHeaderCellWithLineBreak(XWPFTableCell cell, String text) {
        setHeaderCellWithLineBreak(cell, text, null);
    }

    private static void setHeaderCellWithLineBreak(XWPFTableCell cell, String line1, String line2) {
        while (cell.getParagraphs().size() > 0) {
            cell.removeParagraph(0);
            cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        }
        XWPFParagraph paragraph = cell.addParagraph();
        paragraph.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun run = paragraph.createRun();
        setTableFont(run);
        run.setBold(true);
        run.setText(line1);

        if (line2 != null && !line2.isEmpty()) {
            run.addBreak();
            run.setText(line2);
        }
    }

    private static void setDataCell(XWPFTableCell cell, String text) {
        cell.setText(text != null ? text : "");
        cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        setCellTextCenter(cell);
    }

    private static void createInternshipTable(XWPFDocument document, List<Student> students) {
        if (students == null || students.isEmpty()) return;

        Map<String, List<Student>> groupedStudents = new LinkedHashMap<>();

        for (Student student : students) {
            String key = student.getCourse() + "|" +
                    student.getPracticeType() + "|" +
                    student.getEducationalProfile();
            groupedStudents.computeIfAbsent(key, k -> new ArrayList<>()).add(student);
        }

        for (Map.Entry<String, List<Student>> entry : groupedStudents.entrySet()) {
            Student sample = entry.getValue().get(0);

            XWPFParagraph headerPara = document.createParagraph();
            headerPara.setSpacingBefore(200);
            XWPFRun headerRun = headerPara.createRun();
            setDefaultFont(headerRun);
            headerRun.setBold(true);
            headerRun.setText(String.format("%s. %s %s семестр",
                    getCourseText(sample.getCourse()),
                    sample.getEducationalProfile(),
                    getSemesterFromCourse(sample.getCourse())
            ));

            XWPFTable table = document.createTable();
            table.setWidth("100%");

            XWPFTableRow headerRow = table.getRow(0);
            addCell(headerRow, 0, "ФИО студента");
            addCell(headerRow, 1, "Образовательная программа");
            addCell(headerRow, 2, "Курс");
            addCell(headerRow, 3, "Вид практики");
            addCell(headerRow, 4, "Название организации");
            addCell(headerRow, 5, "Руководитель от НГУ");
            addCell(headerRow, 6, "Руководитель от организации");

            for (Student student : entry.getValue()) {
                XWPFTableRow row = table.createRow();
                addCell(row, 0, student.getFullName());
                addCell(row, 1, student.getEducationalProfile());
                addCell(row, 2, student.getCourseGroup());
                addCell(row, 3, student.getPracticeType());
                addCell(row, 4, student.getPracticeBase());
                addCell(row, 5, student.getFullNsuSupervisor());
                addCell(row, 6, student.getFullInstituteSupervisor());
            }

            document.createParagraph();
        }
    }

    private static void addCell(XWPFTableRow row, int index, String text) {
        XWPFTableCell cell;
        if (row.getTableCells().size() > index) {
            cell = row.getCell(index);
        } else {
            cell = row.addNewTableCell();
        }
        cell.setText(text != null ? text : "");
        for (XWPFParagraph p : cell.getParagraphs()) {
            p.setAlignment(ParagraphAlignment.LEFT);
        }
    }

    private static String getCourseText(int course) {
        if (course == 3) return "Бакалавриат 3 курс";
        if (course == 4) return "Бакалавриат 4 курс";
        if (course == 2) return "Магистратура 2 курс";
        return course + " курс";
    }

    private static String getSemesterFromCourse(int course) {
        if (course == 3) return "5";
        if (course == 4) return "7";
        if (course == 2) return "3";
        return "";
    }

    private static String extractLastName(String fullTitleAndName) {
        if (fullTitleAndName == null || fullTitleAndName.isEmpty()) return "";
        String[] parts = fullTitleAndName.trim().split("\\s+");
        int start = Math.max(0, parts.length - 3);
        StringBuilder result = new StringBuilder();
        for (int i = start; i < parts.length; i++) {
            if (i > start) result.append(" ");
            result.append(parts[i]);
        }
        return result.toString();
    }

    private static void replaceTextPlaceholders(XWPFDocument document,
                                                Map<String, String> values) {
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            replaceInParagraph(paragraph, values);
        }
        for (XWPFTable table : document.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph paragraph : cell.getParagraphs()) {
                        replaceInParagraph(paragraph, values);
                    }
                }
            }
        }
    }

    private static void replaceInParagraph(XWPFParagraph paragraph,
                                           Map<String, String> values) {
        if (paragraph.getRuns() == null || paragraph.getRuns().isEmpty()) {
            return;
        }

        for (XWPFRun run : paragraph.getRuns()) {
            String text = run.getText(0);
            if (text == null) continue;

            String newText = text;
            for (Map.Entry<String, String> entry : values.entrySet()) {
                String placeholder = "{{" + entry.getKey() + "}}";
                if (newText.contains(placeholder)) {
                    String replacement = entry.getValue() != null ? entry.getValue() : "";
                    newText = newText.replace(placeholder, replacement);
                }
            }

            if (!newText.equals(text)) {
                run.setText(newText, 0);
            }
        }
    }

    private static void replacePlaceholderWithNumberedList(XWPFDocument document,
                                                           String placeholder,
                                                           List<String> items) {
        XWPFParagraph placeholderParagraph = null;
        int placeholderPos = -1;

        for (int i = 0; i < document.getParagraphs().size(); i++) {
            XWPFParagraph paragraph = document.getParagraphs().get(i);
            if (paragraph.getText() != null && paragraph.getText().contains(placeholder)) {
                placeholderParagraph = paragraph;
                placeholderPos = i;
                break;
            }
        }

        if (placeholderParagraph == null) return;

        String styleId = placeholderParagraph.getStyle();
        var cursor = placeholderParagraph.getCTP().newCursor();
        document.removeBodyElement(placeholderPos);

        if (items != null && !items.isEmpty()) {
            int index = 1;
            for (String item : items) {
                XWPFParagraph newParagraph = document.insertNewParagraph(cursor);
                if (styleId != null && !styleId.isEmpty()) {
                    newParagraph.setStyle(styleId);
                }

                newParagraph.setIndentationLeft(720);
                newParagraph.setIndentationHanging(360);

                newParagraph.setSpacingBefore(0);
                newParagraph.setSpacingAfter(0);

                XWPFRun run = newParagraph.createRun();
                setDefaultFont(run);
                run.setText(index + ".     " + item);

                cursor.toNextToken();
                index++;
            }
        }
        cursor.dispose();
    }

    private static void replaceAttendeesPlaceholder(XWPFDocument document, List<String> attendees) {
        if (attendees == null || attendees.isEmpty()) {
            removePlaceholders(document, "{{ATTENDEES}}");
            removeEmptyParagraphsAfterHeader(document, "ПРИСУТСТВОВАЛИ");
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < attendees.size(); i++) {
            sb.append(attendees.get(i));
            if (i < attendees.size() - 1) {
                sb.append(", ");
            }
        }
        String attendeesText = sb.toString();

        for (int i = 0; i < document.getParagraphs().size(); i++) {
            XWPFParagraph paragraph = document.getParagraphs().get(i);
            String text = paragraph.getText();
            if (text != null && text.contains("{{ATTENDEES}}")) {
                String newText = text.replace("{{ATTENDEES}}", attendeesText);

                document.removeBodyElement(i);

                XWPFParagraph newParagraph = document.insertNewParagraph(document.getParagraphs().get(i).getCTP().newCursor());
                XWPFRun run = newParagraph.createRun();
                setDefaultFont(run);
                run.setText(newText);
                break;
            }
        }

        for (XWPFTable table : document.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph paragraph : cell.getParagraphs()) {
                        String text = paragraph.getText();
                        if (text != null && text.contains("{{ATTENDEES}}")) {
                            String newText = text.replace("{{ATTENDEES}}", attendeesText);
                            while (paragraph.getRuns().size() > 0) {
                                paragraph.removeRun(0);
                            }
                            XWPFRun run = paragraph.createRun();
                            setDefaultFont(run);
                            run.setText(newText);
                        }
                    }
                }
            }
        }
    }

    private static void createSignatureTable(XWPFDocument document, String chairmanName, String secretaryName) {
        XWPFTable table = document.createTable(1, 2);
        table.setWidth("100%");

        table.setBottomBorder(XWPFTable.XWPFBorderType.NONE, 0, 0, null);
        table.setLeftBorder(XWPFTable.XWPFBorderType.NONE, 0, 0, null);
        table.setRightBorder(XWPFTable.XWPFBorderType.NONE, 0, 0, null);
        table.setTopBorder(XWPFTable.XWPFBorderType.NONE, 0, 0, null);
        table.setInsideHBorder(XWPFTable.XWPFBorderType.NONE, 0, 0, null);
        table.setInsideVBorder(XWPFTable.XWPFBorderType.NONE, 0, 0, null);

        XWPFTableCell leftCell = table.getRow(0).getCell(0);
        leftCell.setText("Председатель  ___________ " + (chairmanName != null ? chairmanName : ""));

        XWPFTableCell rightCell = table.getRow(0).getCell(1);
        rightCell.setText("Секретарь  ___________ " + (secretaryName != null ? secretaryName : ""));

        for (XWPFParagraph p : leftCell.getParagraphs()) {
            for (XWPFRun run : p.getRuns()) {
                setSignatureFont(run);
            }
        }
        for (XWPFParagraph p : rightCell.getParagraphs()) {
            for (XWPFRun run : p.getRuns()) {
                setSignatureFont(run);
            }
        }
    }

    private static void setDefaultFont(XWPFRun run) {
        run.setFontFamily("Times New Roman");
        run.setFontSize(13);
    }

    private static void setTableFont(XWPFRun run) {
        run.setFontFamily("Times New Roman");
        run.setFontSize(10);
    }

    private static void setSignatureFont(XWPFRun run) {
        run.setFontFamily("Times New Roman");
        run.setFontSize(12);
    }

    private static void setCellTextCenterAndBold(XWPFTableCell cell) {
        for (XWPFParagraph p : cell.getParagraphs()) {
            p.setAlignment(ParagraphAlignment.CENTER);
            for (XWPFRun run : p.getRuns()) {
                run.setBold(true);
                setTableFont(run);
            }
            if (p.getRuns().isEmpty()) {
                XWPFRun run = p.createRun();
                run.setBold(true);
                setTableFont(run);
            }
        }
    }

    private static void setCellTextCenter(XWPFTableCell cell) {
        for (XWPFParagraph p : cell.getParagraphs()) {
            p.setAlignment(ParagraphAlignment.CENTER);
            for (XWPFRun run : p.getRuns()) {
                setTableFont(run);
            }
            if (p.getRuns().isEmpty()) {
                XWPFRun run = p.createRun();
                setTableFont(run);
            }
        }
    }
}
