package ru.nsu.astakhov.autodocs.document.generator.table;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;

import java.util.List;
import java.util.Map;

public class ThesisSupervisorReviewTableProcessor extends TableProcessor {
    private XWPFTable table;

    public void addCompetencies(XWPFTable table, Map<String, List<String>> competencies) {
        for (String key : competencies.keySet()) {
            addCompetence(table, key, competencies.get(key));
        }
    }

    public void addCoSupervisor(XWPFTable table) {
        this.table = table;

        addRow("Соруководитель", false, "", false);
        addRow("$(соруководительВКР.имя),", true, "_____________", false);
        addRow("$(соруководительвкр.должность.работангу)", true, "                подпись", false);
        // TODO: убрать явную дату
        addRow("«31» мая 2026 г.", false, "", false);
    }

    private void addRow(String text1, boolean isColored1, String text2, boolean isColored2) {
        int fontSize = 12;

        XWPFTableRow row = removeAllCells(table.createRow());
        addCell(row, text1, isColored1, fontSize);
        addCell(row, text2, isColored2, fontSize);
    }

    private void addCell(XWPFTableRow row, String text, boolean isColored, int fontSize) {
        // TODO: ну это пиздец :D
        if (text.equals("                подпись")) {
            fontSize = 8;
        }

        XWPFTableCell cell = row.createCell();
        XWPFRun run = addTextInCell(cell, text, fontSize);
        if (isColored) {
            run.setTextHighlightColor("yellow");
        }
        removeIndentation(run);
    }

    private void addCompetence(
            XWPFTable table,
            String coreCompetence,
            List<String> competencies
    ) {

        XWPFTableRow coreRow = table.createRow();
        removeAllCells(coreRow);

        XWPFTableCell coreCell = coreRow.createCell();
        setCellBorders(coreCell);
        XWPFRun run = addTextInCell(coreCell, coreCompetence);
        removeIndentation(run);
        run.setBold(true);

        XWPFTableCell coreCell2 = coreRow.createCell();
        setCellBorders(coreCell2);
        XWPFRun run2 = addTextInCell(coreCell2, "5");
        removeIndentation(run2);
        run2.setTextHighlightColor("yellow");
        XWPFParagraph paragraph = (XWPFParagraph) run2.getParent();
        paragraph.setAlignment(ParagraphAlignment.CENTER);

        CTTc cttCell1 = coreCell2.getCTTc();
        CTTcPr tcPr1 = cttCell1.isSetTcPr() ? cttCell1.getTcPr() : cttCell1.addNewTcPr();
        tcPr1.addNewVMerge().setVal(STMerge.RESTART);

        for (String competence : competencies) {
            XWPFTableRow row  = table.createRow();
            removeAllCells(row);

            XWPFTableCell cell1 = row.createCell();
            XWPFRun run3 = addTextInCell(cell1, competence);
            removeIndentation(run3);
            setCellBorders(cell1);

            XWPFTableCell cell2 = row.createCell();
            setCellBorders(cell2);
            CTTc cttCell2 = cell2.getCTTc();
            CTTcPr tcPr2 = cttCell2.isSetTcPr() ? cttCell2.getTcPr() : cttCell2.addNewTcPr();
            tcPr2.addNewVMerge().setVal(STMerge.CONTINUE);
        }
    }
}
