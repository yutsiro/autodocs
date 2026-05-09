package ru.nsu.astakhov.autodocs.ui.view.component;

import lombok.Getter;
import ru.nsu.astakhov.autodocs.document.PreparedTemplateInfo;
import ru.nsu.astakhov.autodocs.ui.configs.ConfigConstants;
import ru.nsu.astakhov.autodocs.ui.configs.ConfigManager;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class FileBox extends JPanel {
    @Getter
    private final PreparedTemplateInfo preparedTemplateInfo;
    private final JLabel indicator;
    @Getter
    private boolean isActive;

    private final int smallGap;
    private final int titleTextSize;

    private final Color primaryColor;
    private final Color focusColor;

    public FileBox(PreparedTemplateInfo preparedTemplateInfo) {
        this.preparedTemplateInfo = preparedTemplateInfo;

        this.smallGap = Integer.parseInt(ConfigManager.getSetting(ConfigConstants.GAP_SMALL));
        this.titleTextSize = Integer.parseInt(ConfigManager.getSetting(ConfigConstants.TITLE_SIZE));

        this.primaryColor = ConfigManager.parseHexColor(ConfigManager.getSetting(ConfigConstants.PRIMARY_COLOR));
        this.focusColor = ConfigManager.parseHexColor(ConfigManager.getSetting(ConfigConstants.FOCUS_COLOR));

        this.indicator = createIndicator();
        this.isActive = false;

        configureFileBox();
    }

    private List<String> parseFileDescription(String fileDescription) {
        return List.of(fileDescription.split("\n"));
    }

    private void configureFileBox() {
        setLayout(new BorderLayout());

        setBackground(focusColor);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(primaryColor),
                BorderFactory.createEmptyBorder(smallGap, smallGap, smallGap, smallGap)
        ));

        add(createContentPanel(), BorderLayout.CENTER);
        add(indicator, BorderLayout.EAST);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isEnabled()) {
                    changeActive();
                }
            }
        });
    }

    private JLabel createIndicator() {
        JLabel indicator = new JLabel("•");
        indicator.setFont(indicator.getFont().deriveFont(Font.BOLD, titleTextSize));
        indicator.setForeground(focusColor);

        return indicator;
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        int borderSize = 2 * smallGap;
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(borderSize, borderSize, borderSize, borderSize));

        List<String> partsDescription = parseFileDescription(preparedTemplateInfo.getDisplayName());

        for (int i = 0; i != partsDescription.size(); ++i) {
            if (i == 0) {
                JLabel firstPanel = new CustomLabel(partsDescription.get(i));
                firstPanel.setFont(firstPanel.getFont().deriveFont(Font.BOLD));
                contentPanel.add(firstPanel);
            }
            else {
                contentPanel.add(new CustomLabel(partsDescription.get(i)));
            }
        }

        return contentPanel;
    }

    private void changeActive() {
        isActive = !isActive;

        if (isActive) {
            indicator.setForeground(Color.GREEN);
            Color highlighted;
            Color borderHighlight;

            if (ConfigManager.isLightTheme()) {
                highlighted = lightenColor(focusColor);
                borderHighlight = primaryColor;
            }
            else {
                highlighted = darkenColor(focusColor);
                borderHighlight = darkenColor(primaryColor);
            }

            setBackground(highlighted);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(borderHighlight),
                    BorderFactory.createEmptyBorder(smallGap, smallGap, smallGap, smallGap)
            ));
        }
        else {
            indicator.setForeground(focusColor);
            setBackground(focusColor);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(primaryColor),
                    BorderFactory.createEmptyBorder(smallGap, smallGap, smallGap, smallGap)
            ));
        }
    }

    private Color darkenColor(Color color) {
        float factor = 0.85f;
        int r = Math.max(0, (int) (color.getRed()   * factor));
        int g = Math.max(0, (int) (color.getGreen() * factor));
        int b = Math.max(0, (int) (color.getBlue()  * factor));

        return new Color(r, g, b);
    }

    private Color lightenColor(Color color) {
        float factor = 1.3f;
        int r = Math.min(255, (int) (color.getRed()   * factor));
        int g = Math.min(255, (int) (color.getGreen() * factor));
        int b = Math.min(255, (int) (color.getBlue()  * factor));

        return new Color(r, g, b);
    }
}
