package net.benmclean.warpreader;

import com.badlogic.gdx.Game;
import net.spookygames.gdx.nativefilechooser.NativeFileChooser;

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
}