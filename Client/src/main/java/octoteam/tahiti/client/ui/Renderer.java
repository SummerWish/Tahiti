package octoteam.tahiti.client.ui;

import com.google.common.eventbus.EventBus;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import octoteam.tahiti.client.event.UIOnLoginCommandEvent;
import octoteam.tahiti.client.event.UIOnSendCommandEvent;

import java.io.IOException;
import java.util.Arrays;

public class Renderer {

    private static final String LOGIN_DIALOG_VISIBLE = "login.dialog.visible";
    private static final String LOGIN_STATEDIALOG_TEXT = "login.statedialog.text";
    private static final String LOGIN_STATEDIALOG_VISIBLE = "login.statedialog.visible";
    private static final String MAIN_WINDOW_VISIBLE = "main.window.visible";

    private Screen screen;

    private UtopiaMultiWindowTextGUI gui;

    private EventBus eventBus = new EventBus();

    private Store store = new Store();

    public Renderer() throws IOException, InterruptedException {
        Terminal terminal = new DefaultTerminalFactory().createTerminal();
        screen = new TerminalScreen(terminal);
        screen.startScreen();
        gui = new UtopiaMultiWindowTextGUI(new SeparateTextGUIThread.Factory(), screen);
        gui.setEOFWhenNoWindows(false);
        ((AsynchronousTextGUIThread) gui.getGUIThread()).start();

        initLoginDialog();
        initLoginStateDialog();
        initMainWindow();
    }

    private void initLoginDialog() {
        Panel panel = new Panel();
        panel
                .setLayoutManager(
                        new GridLayout(2)
                                .setBottomMarginSize(1)
                                .setTopMarginSize(1)
                                .setLeftMarginSize(3)
                                .setRightMarginSize(3)
                );

        panel.addComponent(new Label("Username"));
        TextBox txtUsername = new TextBox().addTo(panel);

        panel.addComponent(new Label("Password"));
        TextBox txtPassword = new TextBox().addTo(panel);

        panel.addComponent(new EmptySpace(new TerminalSize(0, 0)));

        new Button("Login", () -> {
            eventBus.post(new UIOnLoginCommandEvent(txtUsername.getText(), txtPassword.getText()));
        }).addTo(panel);

        BasicWindow dialog = new BasicWindow("Login to Tahiti");
        dialog.setComponent(panel);
        dialog.setHints(Arrays.asList(Window.Hint.CENTERED));

        store.init(LOGIN_DIALOG_VISIBLE, false);
        store.observe(LOGIN_DIALOG_VISIBLE, v -> {
            gui.addOrRemoveWindow((Boolean) v, dialog);
            gui.updateScreen();
            return null;
        });
    }

    private void initLoginStateDialog() {
        Panel panel = new Panel();
        panel
                .setLayoutManager(
                        new GridLayout(1)
                                .setBottomMarginSize(1)
                                .setTopMarginSize(1)
                                .setLeftMarginSize(3)
                                .setRightMarginSize(3)
                );

        Label label = new Label("");
        panel.addComponent(label);
        BasicWindow dialog = new BasicWindow();
        dialog.setComponent(panel);
        dialog.setHints(Arrays.asList(Window.Hint.CENTERED));

        store.init(LOGIN_STATEDIALOG_VISIBLE, false);
        store.init(LOGIN_STATEDIALOG_TEXT, "");
        store.observe(LOGIN_STATEDIALOG_TEXT, v -> {
            label.setText((String) v);
            return null;
        });
        store.observe(LOGIN_STATEDIALOG_VISIBLE, v -> {
            gui.addOrRemoveWindow((Boolean) v, dialog);
            gui.updateScreen();
            return null;
        });
    }

    private void initMainWindow() {
        Panel panel = new Panel();
        panel
                .setLayoutManager(
                        new GridLayout(1)
                                .setBottomMarginSize(0)
                                .setTopMarginSize(0)
                                .setLeftMarginSize(1)
                                .setRightMarginSize(1)
                                .setVerticalSpacing(1)
                );

        Panel msgPanel = new Panel();
        msgPanel
                .setLayoutData(GridLayout.createHorizontallyFilledLayoutData(1))
                .setLayoutManager(
                        new GridLayout(2)
                                .setLeftMarginSize(0)
                                .setRightMarginSize(0)
                );

        TextBox history = new TextBox();
        history
                .setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.FILL, GridLayout.Alignment.FILL, true, true))
                .setReadOnly(true)
                .addTo(panel);
        msgPanel.addTo(panel);

        TextBox msg = new TextBox()
                .setLayoutData(GridLayout.createHorizontallyFilledLayoutData(1))
                .addTo(msgPanel);

        new Button("Send", () -> {
            eventBus.post(new UIOnSendCommandEvent(msg.getText()));
            msg
                    .setText("")
                    .takeFocus();
        }).addTo(msgPanel);

        BasicWindow dialog = new BasicWindow("Tahiti");
        dialog.setComponent(panel);
        dialog.setHints(Arrays.asList(Window.Hint.CENTERED, Window.Hint.FULL_SCREEN));

        store.init(MAIN_WINDOW_VISIBLE, false);
        store.observe(MAIN_WINDOW_VISIBLE, v -> {
            gui.addOrRemoveWindow((Boolean) v, dialog);
            gui.updateScreen();
            return null;
        });
    }

    public void shutdown() throws IOException {
        AsynchronousTextGUIThread guiThread = (AsynchronousTextGUIThread) gui.getGUIThread();
        guiThread.stop();
        screen.stopScreen();
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public void actionShowMessageDialog(String title, String text, MessageDialogButton... buttons) {
        MessageDialog.showMessageDialog(gui, title, text, buttons);
    }

    public void actionShowLoginDialog() {
        store.update(() -> {
            store.put(LOGIN_DIALOG_VISIBLE, true);
        });
    }

    public void actionHideLoginDialog() {
        store.update(() -> {
            store.put(LOGIN_DIALOG_VISIBLE, false);
        });
    }

    public void actionShowLoginStateDialog(String text) {
        store.update(() -> {
            store.put(LOGIN_STATEDIALOG_TEXT, text);
            store.put(LOGIN_STATEDIALOG_VISIBLE, true);
        });
    }

    public void actionHideLoginStateDialog() {
        store.update(() -> {
            store.put(LOGIN_STATEDIALOG_VISIBLE, false);
        });
    }

    public void actionShowMainWindow() {
        store.update(() -> {
            store.put(MAIN_WINDOW_VISIBLE, true);
        });
    }

    public void actionHideMainWindow() {
        store.update(() -> {
            store.put(MAIN_WINDOW_VISIBLE, false);
        });
    }

}
