package net.benmclean.warpreader;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import net.benmclean.utils.AtlasRepacker;
import net.benmclean.utils.Palette4;
import net.spookygames.gdx.nativefilechooser.NativeFileChooserCallback;
import net.spookygames.gdx.nativefilechooser.NativeFileChooserConfiguration;

import java.io.File;
import java.io.FilenameFilter;

public class MenuScreen extends ScreenAdapter {
    public static final int VIRTUAL_WIDTH = 640;
    public static final int VIRTUAL_HEIGHT = 360;

    public Game game;
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

        final Label title = new Label("MAIN MENU", skin);
        group.addActor(title);

        final TextButton screenButton = new TextButton("Model viewer", skin);
        screenButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(WarpReader.warpViewer);
            }
        });
        group.addActor(screenButton);

        if (WarpReader.fileChooser != null) {
            final TextButton loadButton = new TextButton("Load model", skin);
            loadButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    NativeFileChooserConfiguration conf = new NativeFileChooserConfiguration();

// Starting from user's dir
                    conf.directory = Gdx.files.absolute(System.getProperty("user.home"));

//                    conf.mimeFilter = "audio/*";
                    conf.nameFilter = new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String name) {
                            return name.endsWith("vox");
                        }
                    };

// Add a nice title
                    conf.title = "Choose .VOX model";

                    WarpReader.fileChooser.chooseFile(conf, new NativeFileChooserCallback() {
                        @Override
                        public void onFileChosen(FileHandle file) {
                            // Do stuff with file, yay!
                        }

                        @Override
                        public void onCancellation() {
                            // Warn user how rude it can be to cancel developer's effort
                        }

                        @Override
                        public void onError(Exception exception) {
                            // Handle error (hint: use exception type)
                        }
                    });
                }
            });
            group.addActor(loadButton);
        }

        final CheckBox debugCheckBox = new CheckBox("Enable Debug Rendering", skin);
        debugCheckBox.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stage.setDebugAll(debugCheckBox.isChecked());
            }
        });
        group.addActor(debugCheckBox);

        final TextButton exitButton = new TextButton("Exit Program", skin);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
        group.addActor(exitButton);

        final Window window = new Window("", skin);
        window.add(group);
        window.setSize(window.getPrefWidth(), window.getPrefHeight());
//        window.align(Align.center);
//        window.align(Align.top);
        window.setOrigin(window.getWidth() / 2, window.getHeight() / 2);
        window.setPosition((VIRTUAL_WIDTH - window.getWidth()) / 2, (VIRTUAL_HEIGHT - window.getHeight()) / 2);
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
