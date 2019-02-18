package net.benmclean.warpreader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import net.benmclean.utils.AtlasRepacker;
import net.benmclean.utils.Palette4;

public class MenuScreen extends ScreenAdapter {
    public static final int VIRTUAL_WIDTH = 852;
    public static final int VIRTUAL_HEIGHT = 480;

    private Skin skin;
    private Stage stage;

    public MenuScreen() {
        stage = new Stage(new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT));
        skin = new Skin(
                Gdx.files.internal("DOS/uiskin.json"),
                AtlasRepacker.repackAtlas(
                        new TextureAtlas(Gdx.files.internal("DOS/uiskin.atlas")),
                        Palette4.blueUI().get()
                )
        );

        final VerticalGroup group = new VerticalGroup();
        group.space(16);

        final TextField title = new TextField("MAIN MENU", skin);
        group.addActor(title);

        final CheckBox checkBox = new CheckBox("Apply Shader", skin);
//        checkBox.addListener(new ClickListener() {
//            @Override
//            public void clicked(InputEvent event, float x, float y) {
//                setShader(checkBox.isChecked());
//            }
//        });
        group.addActor(checkBox);

        final CheckBox debugCheckBox = new CheckBox("Enable Debug Rendering", skin);
        debugCheckBox.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stage.setDebugAll(debugCheckBox.isChecked());
            }
        });
        group.addActor(debugCheckBox);

        final Window window = new Window("", skin);
        window.add(group);
        window.setSize(window.getPrefWidth(), window.getPrefHeight());
        stage.addActor(window);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
    }
}
