package ru.nsu.astakhov.autodocs.document.generator.table;

import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

public class ThesisFrontPageTableProcessor extends TableProcessor {
    public void addCoSupervisor(XWPFTable table) {
        int fontSize = 12;
        XWPFRun run1 = addRow(table, "Соруководитель ВКР", fontSize, false);
        run1.setBold(true);
        addRow(table, "$(соруководительВКР.степень) $(соруководительВКР.звание)", fontSize, true);
        addRow(table, "$(соруководительВКР.должность.работаНГУ)", fontSize, true);
        addRow(table, "$(соруководительвкр.имяКратко)/…………..", fontSize, true);
        addRow(table, "             (ФИО) / (подпись)", 8, false);
        addRow(table, "«……» мая 2026 г.", fontSize, false);
    }

    private XWPFRun addRow(XWPFTable table, String text, int fontSize, boolean isColored) {
        XWPFTableRow row = removeAllCells(table.createRow());
        row.createCell();
        XWPFTableCell cell2 = row.createCell();
        XWPFRun run = addTextInCell(cell2, text, fontSize);
        if (isColored) {
            run.setTextHighlightColor("yellow");
        }
        XWPFParagraph paragraph = (XWPFParagraph) run.getParent();
        // 1 см ≈ 567 twips
        paragraph.setIndentationFirstLine(567);

        return run;
    }
}
