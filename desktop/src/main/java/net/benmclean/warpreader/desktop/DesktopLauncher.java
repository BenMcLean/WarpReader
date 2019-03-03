package net.benmclean.warpreader.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
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
        return new Lwjgl3Application(new WarpReader(), getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("WarpReader");
        config.setWindowedMode(WarpViewer.SCREEN_WIDTH, WarpViewer.SCREEN_HEIGHT);
        config.setResizable(false);
        config.setHdpiMode(Lwjgl3ApplicationConfiguration.HdpiMode.Pixels);
        return config;
    }
}