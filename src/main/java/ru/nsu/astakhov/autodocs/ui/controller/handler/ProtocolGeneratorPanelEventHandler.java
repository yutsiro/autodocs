package ru.nsu.astakhov.autodocs.ui.controller.handler;

import lombok.RequiredArgsConstructor;
import ru.nsu.astakhov.autodocs.ui.controller.ButtonCommand;
import ru.nsu.astakhov.autodocs.ui.controller.Controller;
import ru.nsu.astakhov.autodocs.ui.view.panel.ProtocolGeneratorPanel;

import javax.swing.JButton;
import java.awt.event.ActionEvent;

@RequiredArgsConstructor
public class ProtocolGeneratorPanelEventHandler implements EventHandler {
    private final Controller controller;
    private final ProtocolGeneratorPanel panel;

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source instanceof JButton) {
            String command = e.getActionCommand();
            ButtonCommand buttonCommand = ButtonCommand.fromString(command);

            if (buttonCommand == ButtonCommand.GENERATE_PROTOCOL) {
                panel.generateProtocol();
            }
        }
    }
}
