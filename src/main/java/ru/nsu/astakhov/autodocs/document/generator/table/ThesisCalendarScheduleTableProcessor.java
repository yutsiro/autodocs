package ru.nsu.astakhov.autodocs.document.generator.table;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.springframework.data.util.Pair;

import java.util.List;

public class ThesisCalendarScheduleTableProcessor extends TableProcessor {
    public void addListTopic(XWPFTable table, List<Pair<String, Boolean>> topics) {
        int globalNumber = 8;
        for (Pair<String, Boolean> topic : topics) {
            globalNumber = addTopic(table, topic, globalNumber);
        }
    }

    private int addTopic(XWPFTable table, Pair<String, Boolean> topic, int globalNumber) {
        XWPFTableRow row = removeAllCells(table.createRow());

        XWPFTableCell cell1 = row.createCell();
        addTextInCell(cell1, String.valueOf(globalNumber));
        setCellBorders(cell1);
        XWPFParagraph paragraph1 = cell1.getParagraphs().getFirst();
        paragraph1.setAlignment(ParagraphAlignment.CENTER);
        cell1.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        removeIndent(paragraph1);

        XWPFTableCell cell2 = row.createCell();
        addTextInCell(cell2, topic.getFirst());
        setCellBorders(cell2);
        removeIndent(cell2.getParagraphs().getFirst());

        XWPFTableCell cell3 = row.createCell();
        if (!topic.getSecond()) {
            addTextInCell(cell3, "-");
        }
        else {
            XWPFRun dateRun = addTextInCell(cell3, "ДД.ММ.ГГГГ");
            dateRun.setTextHighlightColor("yellow");
            removeIndentation(dateRun);
        }
        setCellBorders(cell3);

        XWPFTableCell cell4 = row.createCell();
        if (!topic.getSecond()) {
            addTextInCell(cell4, "-");
        }
        else {
            XWPFRun run = addTextInCell(cell4, "Выполнено");
            removeIndentation(run);
        }
        setCellBorders(cell4);

        return ++globalNumber;
    }
}
