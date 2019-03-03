package net.benmclean.warpreader;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.files.FileHandle;
import net.spookygames.gdx.nativefilechooser.NativeFileChooser;

import java.io.File;

public class WarpReader extends Game {
    public static MenuScreen menuScreen;
    public static WarpViewer warpViewer;
    public static NativeFileChooser fileChooser;

    @Override
    public void create() {
        menuScreen = new MenuScreen();
        menuScreen.game = this;
        warpViewer = new WarpViewer();
        warpViewer.game = this;
        this.setScreen(warpViewer);
    }

    public static void load(String name) {
        warpViewer.load(name);
    }

    public static void load(File file) {
        warpViewer.load(file);
    }
}