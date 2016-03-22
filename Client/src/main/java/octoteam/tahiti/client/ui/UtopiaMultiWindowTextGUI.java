package octoteam.tahiti.client.ui;

import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.TextGUIThreadFactory;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.screen.Screen;

import java.io.IOException;

public class UtopiaMultiWindowTextGUI extends MultiWindowTextGUI {

    public UtopiaMultiWindowTextGUI(TextGUIThreadFactory guiThreadFactory, Screen screen) {
        super(guiThreadFactory, screen);
    }

    @Override
    public synchronized void updateScreen() {
        try {
            super.updateScreen();
        } catch (IOException ignored) {
        }
    }

    public void addOrRemoveWindow(Boolean visible, Window window) {
        if (visible) {
            super.addWindow(window);
        } else {
            super.removeWindow(window);
        }
    }

}
