package ru.nsu.astakhov.autodocs.document.generator.table;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

public class ThesisAssignmentTableProcessor extends TableProcessor {
    public void addCoSupervisor(XWPFTable table) {
        XWPFTableRow row1 = removeAllCells(table.createRow());
        XWPFTableCell row1Cell1 = row1.createCell();
        XWPFRun run1 = addTextInCell(row1Cell1, "Соруководитель ВКР", 12);
        removeIndentation(run1);
        row1.createCell();

        XWPFTableRow row2 = removeAllCells(table.createRow());
        XWPFTableCell row2Cell1 = row2.createCell();
        XWPFRun run2 = addTextInCell(row2Cell1, "$(соруководительВКР.должность.работаНГУ)", 12);
        removeIndentation(run2);
        run2.setTextHighlightColor("yellow");
        row2.createCell();

        XWPFTableRow row3 = removeAllCells(table.createRow());
        XWPFTableCell row3Cell1 = row3.createCell();
        XWPFRun run3 = addTextInCell(row3Cell1, "$(соруководительВКР.степень.звание)", 12);
        removeIndentation(run3);
        run3.setTextHighlightColor("yellow");
        row3.createCell();

        XWPFTableRow row4 = removeAllCells(table.createRow());
        XWPFTableCell row4Cell1 = row4.createCell();
        XWPFRun run4 = addTextInCell(row4Cell1, "$(соруководительвкр.имяКратко)/…………..", 12);
        removeIndentation(run4);
        run4.setTextHighlightColor("yellow");
        row4.createCell();

        XWPFTableRow row5 = removeAllCells(table.createRow());
        XWPFTableCell row5Cell1 = row5.createCell();
        XWPFRun run5 = addTextInCell(row5Cell1, "           (ФИО) / (подпись)", 8);
        removeIndentation(run5);
        row5.createCell();

        XWPFTableRow row6 = removeAllCells(table.createRow());
        XWPFTableCell row6Cell1 = row6.createCell();
        XWPFRun run6 = addTextInCell(row6Cell1, "$(общаяДатаПодписи)", 12);
        removeIndentation(run6);
        row6.createCell();
    }

    public void addThesisConsultant(XWPFTable table) {
        String text1 = "Консультанты по разделам ВКР (при необходимости, с указанием разделов):";
        String text2 = "………………………………";
        String text3 = ", $(консультантВКР)";
        String text4 = "(раздел, ФИО)";
        XWPFTableRow row = removeAllCells(table.createRow());

        XWPFTableCell cell = row.createCell();
        cell.getParagraphs().getFirst().setAlignment(ParagraphAlignment.LEFT);

        addTextInCell(cell, text1, 12);
        addTextInCell(cell, "").addBreak();
        XWPFRun run2 = addTextInCell(cell, text2, 12);
        run2.setTextHighlightColor("yellow");
        addTextInCell(cell, text3, 12);
        addTextInCell(cell, "").addBreak();
        addTextInCell(cell, text4, 8);
    }
}
