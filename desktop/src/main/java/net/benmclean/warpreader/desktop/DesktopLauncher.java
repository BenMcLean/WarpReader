package net.benmclean.warpreader.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowAdapter;
import net.benmclean.warpreader.WarpReader;
import net.benmclean.warpreader.WarpViewer;
import net.spookygames.gdx.nativefilechooser.desktop.DesktopFileChooser;

/** Launches the desktop (LWJGL3) application. */
public class DesktopLauncher {
    public static void main(String[] args) {
        createApplication();
    }

    private static Lwjgl3Application createApplication() {
        WarpReader.fileChooser = new DesktopFileChooser();
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("WarpReader");
        config.setWindowedMode(WarpViewer.SCREEN_WIDTH, WarpViewer.SCREEN_HEIGHT);
        config.setResizable(false);
        config.setHdpiMode(Lwjgl3ApplicationConfiguration.HdpiMode.Pixels);
        config.setWindowListener(new Lwjgl3WindowAdapter() {
            @Override
            public void filesDropped(String[] files) {
                if (files != null && files.length > 0) {
                    if (files[0].endsWith(".vox"))
                        WarpReader.load(files[0]);
                }
            }
        });
        return new Lwjgl3Application(new WarpReader(), config);
    }
}