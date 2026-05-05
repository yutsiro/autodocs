package ru.nsu.astakhov.autodocs.ui.controller.handler;

import lombok.RequiredArgsConstructor;
import ru.nsu.astakhov.autodocs.ui.controller.ButtonCommand;
import ru.nsu.astakhov.autodocs.ui.controller.Controller;
import ru.nsu.astakhov.autodocs.ui.view.panel.GeneratorPanel;
import ru.nsu.astakhov.autodocs.ui.view.panel.HelpPanel;
import ru.nsu.astakhov.autodocs.ui.view.panel.NavigationPanel;
import ru.nsu.astakhov.autodocs.ui.view.panel.WarningsPanel;
import ru.nsu.astakhov.autodocs.ui.view.panel.ProtocolGeneratorPanel;

import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;

@RequiredArgsConstructor
public class NavigationPanelEventHandler implements EventHandler {
    private final Controller controller;
    private final NavigationPanel panel;

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        ButtonCommand buttonCommand = ButtonCommand.fromString(command);

        switch (buttonCommand) {
            case UPDATE_TABLE                   -> controller.updateTable(JOptionPane.getFrameForComponent(panel));
            case WARNING_TABLE                  -> controller.setPanel(WarningsPanel.class);
            case GENERATE_DOCUMENT              -> controller.setPanel(GeneratorPanel.class);
            case SHORT_GUIDE                    -> controller.setPanel(HelpPanel.class);
            case GENERATE_PROTOCOL              -> controller.setPanel(ProtocolGeneratorPanel.class);
        }
    }
}
