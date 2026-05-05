package ru.nsu.astakhov.autodocs.ui.view.panel;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.swing.JFrame;
import java.awt.BorderLayout;

@Component
@RequiredArgsConstructor
public class PanelManager {
    private final ApplicationContext applicationContext;
    @Setter
    private JFrame window;
    private Panel centerPanel;
    private Panel westPanel;
    private Panel southPanel;

    public <P extends Panel> void setPanel(Class<P> requiredType) {
        Panel panel = applicationContext.getBean(requiredType);

        if (panel instanceof WarningsPanel
                || panel instanceof PlaceholderPanel
                || panel instanceof GeneratorPanel
                || panel instanceof StudentListPanel
                || panel instanceof HelpPanel
                || panel instanceof ProtocolGeneratorPanel) {
            setCenterPanel(panel);
        }
        else if (panel instanceof NavigationPanel) {
            setWestPanel(panel);
        }
        else if (panel instanceof BottomPanel) {
            setSouthPanel(panel);
        }

        window.revalidate();
        window.repaint();
    }

    public <P extends Panel> P getPanel(Class<P> requiredType) {
        return applicationContext.getBean(requiredType);
    }

    private void setCenterPanel(Panel panel) {
        if (centerPanel != null) {
            window.remove(centerPanel);
        }
        window.add(panel, BorderLayout.CENTER);
        centerPanel = panel;
    }

    private void setWestPanel(Panel panel) {
        if (westPanel != null) {
            window.remove(westPanel);
        }
        window.add(panel, BorderLayout.WEST);
        westPanel = panel;
    }

    private void setSouthPanel(Panel panel) {
        if (southPanel != null) {
            window.remove(southPanel);
        }
        window.add(panel, BorderLayout.SOUTH);
        southPanel = panel;
    }
}
