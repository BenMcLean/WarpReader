package net.benmclean.warpreader;

import com.badlogic.gdx.Game;

public class WarpReader extends Game {
    public static MenuScreen menuScreen;
    public static WarpViewer warpViewer;

    @Override
    public void create() {
        menuScreen = new MenuScreen();
        menuScreen.game = this;
        warpViewer = new WarpViewer();
        warpViewer.game = this;
        this.setScreen(warpViewer);
    }
}