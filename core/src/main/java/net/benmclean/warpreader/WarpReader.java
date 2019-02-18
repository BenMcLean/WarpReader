package net.benmclean.warpreader;

import com.badlogic.gdx.Game;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. Listens to user input.
 */
public class WarpReader extends Game {
    @Override
    public void create() {
        this.setScreen(new WarpViewer());
    }
}