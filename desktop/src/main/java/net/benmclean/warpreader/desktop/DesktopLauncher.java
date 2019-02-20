package net.benmclean.warpreader.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import net.benmclean.warpreader.WarpReader;

/** Launches the desktop (LWJGL3) application. */
public class DesktopLauncher {
    public static void main(String[] args) {
        createApplication();
    }

    private static Lwjgl3Application createApplication() {
        return new Lwjgl3Application(new WarpReader(), getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("WarpReader");
        config.setWindowedMode(640, 480);
        config.setHdpiMode(Lwjgl3ApplicationConfiguration.HdpiMode.Pixels);
//        for (int size : new int[] { 128, 64, 32, 16 }) {
//            configuration.addIcon("libgdx" + size + ".png", FileType.Internal);
//        }
        return config;
    }
}