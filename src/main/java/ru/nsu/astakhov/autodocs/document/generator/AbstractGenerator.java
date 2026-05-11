package ru.nsu.astakhov.autodocs.document.generator;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractGenerator<T> {
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\$\\(([^)]+)\\)");

    protected abstract void applyReplacement(XWPFRun run, Matcher matcher, T config, StringBuilder result);
    protected abstract void applyTableReplacement(XWPFRun run, XWPFTable table, Matcher matcher, T config, StringBuilder result);

    protected void processDocument(XWPFDocument document, T config) {
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            if (containsAny(paragraph.getText())) {
                processParagraph(paragraph, config);
            }
        }

        for (XWPFTable table : document.getTables()) {
            Set<XWPFParagraph> processed = new HashSet<>();
            boolean hasChanges = true;

            while (hasChanges) {
                hasChanges = false;
                List<XWPFParagraph> paragraphsToProcess = new ArrayList<>();
                for (XWPFTableRow row : table.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        for (XWPFParagraph paragraph : cell.getParagraphs()) {
                            if (!processed.contains(paragraph) && containsAny(paragraph.getText())) {
                                paragraphsToProcess.add(paragraph);
                                processed.add(paragraph);
                                hasChanges = true;
                            }
                        }
                    }
                }

                for (XWPFParagraph paragraph : paragraphsToProcess) {
                    processTableParagraph(table, paragraph, config);
                }
            }


        }
    }

    private boolean containsAny(String text) {
        if (text == null) {
            return false;
        }
        return PLACEHOLDER_PATTERN.matcher(text).find();
    }

    private void processParagraph(XWPFParagraph paragraph, T config) {
        processParagraphInternal(paragraph, config, null);
    }

    private void processTableParagraph(XWPFTable table, XWPFParagraph paragraph, T config) {
        processParagraphInternal(paragraph, config, table);
    }

    private void processParagraphInternal(XWPFParagraph paragraph, T config, XWPFTable table) {
        for (XWPFRun run : paragraph.getRuns()) {
            String text = run.text();
            if (text == null) continue;

            Matcher matcher = PLACEHOLDER_PATTERN.matcher(text);
            StringBuilder result = new StringBuilder();

            while (matcher.find()) {
                if (table == null) {
                    applyReplacement(run, matcher, config, result);
                }
                else {
                    applyTableReplacement(run, table, matcher, config, result);
                }
            }
            // TODO: проблема в DocumentGenerator::applyDefaultReplacement с заменой на пустоту
            // TODO: из-за этой проверки. Узнать, зачем изначально она добавлена.
            // TODO: Убрать, если не нужна. Убрать кринж в DocumentGenerator::applyDefaultReplacement
            if (!result.isEmpty()) {
                matcher.appendTail(result);

                run.getCTR().setTArray(new CTText[0]);
                String[] lines = result.toString().split("\n", -1);

                for (int i = 0; i < lines.length; i++) {
                    if (i > 0) run.addBreak();
                    run.setText(lines[i]);
                }
            }
        }
    }
}
