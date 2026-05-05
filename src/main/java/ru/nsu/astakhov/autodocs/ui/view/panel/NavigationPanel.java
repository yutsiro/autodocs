package ru.nsu.astakhov.autodocs.ui.view.panel;

import ru.nsu.astakhov.autodocs.ui.controller.ButtonCommand;
import ru.nsu.astakhov.autodocs.ui.controller.Controller;
import ru.nsu.astakhov.autodocs.ui.controller.handler.NavigationPanelEventHandler;
import ru.nsu.astakhov.autodocs.ui.view.font.FontLoader;
import ru.nsu.astakhov.autodocs.ui.view.font.FontType;
import ru.nsu.astakhov.autodocs.ui.view.logo.LogoLoader;
import ru.nsu.astakhov.autodocs.ui.view.logo.LogoType;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

@org.springframework.stereotype.Component
public class NavigationPanel extends Panel {
    public NavigationPanel(Controller controller) {
        controller.addListener(this);
        setEventHandler(new NavigationPanelEventHandler(controller,this));

        configurePanel();
    }

    @Override
    protected void configurePanel() {
        setLayout(new BorderLayout());

        setBackground(primaryColor);
        setForeground(textColor);
        setFont(FontLoader.loadFont(FontType.ADWAITA_SANS_REGULAR, textSize));

        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 2, focusColor),
                BorderFactory.createLineBorder(primaryColor, smallGap)
        ));

        add(createButtonPanel(), BorderLayout.CENTER);
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        buttonPanel.setBackground(primaryColor);

        List<Component> components = new ArrayList<>(List.of(
                createButton(ButtonCommand.UPDATE_TABLE.getName()),
                createButton(ButtonCommand.WARNING_TABLE.getName()),
                createSeparator(),
                createButton(ButtonCommand.APPLICATION_TEMPLATES.getName()),
                createButton(ButtonCommand.CREATE_APPLICATION_TEMPLATE.getName()),
                createSeparator(),
                createButton(ButtonCommand.GENERATE_DOCUMENT.getName()),
                createButton(ButtonCommand.GENERATE_PROTOCOL.getName()),
                Box.createVerticalGlue(),
                createLogoLabel()
        ));

        for (Component component : components) {
            buttonPanel.add(component);
            buttonPanel.add(Box.createVerticalStrut(smallGap));
        }

        JButton guideButton = createButton(ButtonCommand.SHORT_GUIDE.getName());
        guideButton.setFont(FontLoader.loadFont(FontType.ADWAITA_SANS_REGULAR, menuTextSize));

        buttonPanel.add(guideButton);

        return buttonPanel;
    }

    private JSeparator createSeparator() {
        JSeparator separator = new JSeparator();


        separator.setOpaque(true);
        separator.setForeground(focusColor);
        separator.setBackground(focusColor);
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, smallGap));

        return separator;
    }

    private JLabel createLogoLabel() {
        int widthLogo = 350;
        int heightLogo = 100;

        return LogoLoader.loadLogo(LogoType.HORIZONTAL_LOGO, widthLogo, heightLogo);
    }

    @Override
    public void onTableUpdate(String updateStatus) {
        // no operation
    }

    @Override
    public void onDocumentGeneration(String generateStatus) {
        // no operation
    }
}
