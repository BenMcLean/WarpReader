package net.benmclean.warpreader;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import warpwriter.Coloring;
import warpwriter.ModelMaker;
import warpwriter.model.IModel;
import warpwriter.model.color.Colorizer;
import warpwriter.model.fetch.ArrayModel;
import warpwriter.model.fetch.BoxModel;
import warpwriter.model.fetch.ColorFetch;
import warpwriter.view.VoxelSprite;
import warpwriter.view.render.VoxelSpriteBatchRenderer;

public class WarpViewer extends InputAdapter implements ApplicationListener, GestureDetector.GestureListener, Screen {

    /**
     * This is the default vertex shader from libGDX.
     */
    public static final String vertexShader = "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
            + "attribute vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
            + "attribute vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
            + "uniform mat4 u_projTrans;\n" //
            + "varying vec4 v_color;\n" //
            + "varying vec2 v_texCoords;\n" //
            + "\n" //
            + "void main()\n" //
            + "{\n" //
            + "   v_color = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
            + "   v_color.a = v_color.a * (255.0/254.0);\n" //
            + "   v_texCoords = " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
            + "   gl_Position =  u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
            + "}\n";

    /**
     * This fragment shader draws a black outline around things.
     */
    public static final String fragmentShader = "#ifdef GL_ES\n" +
            "#define LOWP lowp\n" +
            "precision mediump float;\n" +
            "#else\n" +
            "#define LOWP\n" +
            "#endif\n" +
            "varying vec2 v_texCoords;\n" +
            "varying vec4 v_color;\n" +
            "uniform float outlineH;\n" +
            "uniform float outlineW;\n" +
            "uniform sampler2D u_texture;\n" +
            "void main()\n" +
            "{\n" +
            "   vec2 offsetx;\n" +
            "   offsetx.x = outlineW;\n" +
            "   vec2 offsety;\n" +
            "   offsety.y = outlineH;\n" +
            "   float alpha = texture2D( u_texture, v_texCoords ).a;\n" +
            "   alpha = max(alpha, texture2D( u_texture, v_texCoords + offsetx).a);\n" +
            "   alpha = max(alpha, texture2D( u_texture, v_texCoords - offsetx).a);\n" +
            "   alpha = max(alpha, texture2D( u_texture, v_texCoords + offsety).a);\n" +
            "   alpha = max(alpha, texture2D( u_texture, v_texCoords - offsety).a);\n" +
            "   gl_FragColor = v_color * texture2D( u_texture, v_texCoords );\n" +
            "   gl_FragColor.a = alpha;\n" +
            "}";

    public static final int backgroundColor = Color.rgba8888(Color.DARK_GRAY);
    public static final int SCREEN_WIDTH = 1280;
    public static final int SCREEN_HEIGHT = 720;
    public static final int VIRTUAL_WIDTH = 848;
    public static final int VIRTUAL_HEIGHT = 480;
    protected SpriteBatch batch;
    protected Viewport worldView;
    protected Viewport screenView;
    protected BitmapFont font;
    protected FrameBuffer buffer;
    protected Texture screenTexture;
    protected TextureRegion screenRegion;
    protected ModelMaker maker;
    protected VoxelSprite voxelSprite;
    protected VoxelSpriteBatchRenderer batchRenderer;
    protected ShaderProgram shader;
    protected ShaderProgram defaultShader;
    protected boolean box = false;
    protected boolean paused = false;
    protected InputMultiplexer multiplexer;
    public Game game;

    public WarpViewer() {
        create();
    }

    @Override
    public void create() {
        font = new BitmapFont(Gdx.files.internal("PxPlus_IBM_VGA_8x16.fnt"));
        batch = new SpriteBatch();
        worldView = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        screenView = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        buffer = new FrameBuffer(Pixmap.Format.RGBA8888, VIRTUAL_WIDTH, VIRTUAL_HEIGHT, false, false);
        screenRegion = new TextureRegion();
        screenView.getCamera().position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);
        screenView.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.enableBlending();

        maker = new ModelMaker(12345, Colorizer.RinsedColorizer);
        batchRenderer = new VoxelSpriteBatchRenderer(batch);
//        batchRenderer.set(batchRenderer.color().set(VoxelColor.AuroraTwilight)); // comment out to use Rinsed
        voxelSprite = new VoxelSprite()
                .set(batchRenderer)
                .setOffset(VIRTUAL_WIDTH / 2, 3);

        voxelSprite.set(new ArrayModel(maker.warriorRandom()));
        //makeModel();

        defaultShader = SpriteBatch.createDefaultShader();
        shader = new ShaderProgram(vertexShader, fragmentShader);
        if (!shader.isCompiled()) throw new GdxRuntimeException("Couldn't compile shader: " + shader.getLog());

        multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(this);
        multiplexer.addProcessor(new GestureDetector(this));
    }

    public void makeModel() {
        voxelSprite.set(
                box ?
                        new BoxModel(model(), ColorFetch.color(Coloring.rinsed("Powder Blue 3")))
                        : model()
        );
    }

    public IModel model() {
        return new ArrayModel(maker.shipLargeNoiseColorized());
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void render(float delta) {
        render();
    }

    @Override
    public void resize(final int width, final int height) {
        screenView.update(width, height);
    }

    @Override
    public void render() {
        if (!paused) {
            buffer.begin();
            Gdx.gl.glClearColor(0, 0, 0, 0);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            worldView.apply();
            worldView.getCamera().position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);
            worldView.update(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
            batch.setProjectionMatrix(worldView.getCamera().combined);
            batch.begin();

//            font.draw(batch, StringKit.join(", ", voxelSprite.getModel().sizeX(), voxelSprite.getModel().sizeY(), voxelSprite.getModel().sizeZ()) + " (original)", 0, 80);
//            font.draw(batch, voxelSprite.turnModel().sizeX() + ", " + voxelSprite.turnModel().sizeY() + ", " + voxelSprite.turnModel().sizeZ() + " (modified)", 0, 60);
//            font.draw(batch, StringKit.join(", ", voxelSprite.turnModel().turner().rotation()) + " (rotation)", 0, 40);
            font.draw(batch, Gdx.graphics.getFramesPerSecond() + " FPS", 0, 20);

            voxelSprite.render();

            batch.setColor(-0x1.fffffep126f); // white as a packed float, resets any color changes that the renderer made
            batch.end();
            buffer.end();
            Gdx.gl.glClearColor(
                    ((backgroundColor >> 24) & 0xff) / 255f,
                    ((backgroundColor >> 16) & 0xff) / 255f,
                    ((backgroundColor >> 8) & 0xff) / 255f,
                    (backgroundColor & 0xff) / 255f
            );
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            screenView.apply();
            batch.setProjectionMatrix(screenView.getCamera().combined);
            batch.begin();
            screenTexture = buffer.getColorBufferTexture();
            screenTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            screenRegion.setRegion(screenTexture);
            screenRegion.flip(false, true);

            batch.setShader(shader);
            shader.setUniformf("outlineH", 1f / VIRTUAL_HEIGHT);
            shader.setUniformf("outlineW", 1f / VIRTUAL_WIDTH);

            batch.draw(screenRegion, 0, 0);
            batch.setShader(defaultShader);
            batch.end();
        }
    }

    @Override
    public void pause() {
        paused = true;
    }

    @Override
    public void resume() {
        paused = false;
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (font != null) font.dispose();
        if (buffer != null) buffer.dispose();
        if (screenTexture != null) screenTexture.dispose();
        if (voxelSprite != null) voxelSprite.dispose();
        if (batchRenderer != null) batchRenderer.dispose();
        if (shader != null) shader.dispose();
        if (defaultShader != null) defaultShader.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.NUM_0:
                voxelSprite.setAngle(1);
                break;
            case Input.Keys.MINUS:
                voxelSprite.setAngle(2);
                break;
            case Input.Keys.EQUALS:
                voxelSprite.setAngle(3);
                break;
            case Input.Keys.U:
                voxelSprite.clockX();
                break;
            case Input.Keys.I:
                voxelSprite.clockY();
                break;
            case Input.Keys.O:
                voxelSprite.clockZ();
                break;
            case Input.Keys.J:
                voxelSprite.counterX();
                break;
            case Input.Keys.K:
                voxelSprite.counterY();
                break;
            case Input.Keys.L:
                voxelSprite.counterZ();
                break;
            case Input.Keys.R:
                voxelSprite.reset();
                break;
            case Input.Keys.P:
                makeModel();
                break;
            case Input.Keys.B:
                box = !box;
                makeModel();
                break;
            case Input.Keys.G:
                batchRenderer.color().set(batchRenderer.color().direction().counter());
                break;
            case Input.Keys.H:
                batchRenderer.color().set(batchRenderer.color().direction().clock());
                break;
            case Input.Keys.T: // try again
                voxelSprite.reset();
                break;
            case Input.Keys.M:
                game.setScreen(WarpReader.menuScreen);
                break;
            case Input.Keys.ESCAPE:
                Gdx.app.exit();
                break;
        }
        return true;
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        voxelSprite.renderer().color().set(
                voxelSprite.renderer().color().direction().clock()
        );
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    public static final float touchThreshold = 0f;
    boolean panning = false;
    float panX = 0, panY = 0;

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        if (!panning) {
            panX = x;
            panY = y;
            panning = true;
        }
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        panning = false;
        final float deltaX = x - panX, deltaY = y - panY;
        if (Math.abs(deltaX) > Math.abs(deltaY)) {
            if (deltaX > touchThreshold) {
                voxelSprite.clockZ();
            } else if (deltaX < touchThreshold * -1f) {
                voxelSprite.counterZ();
            }
        } else {
            if (voxelSprite.angle() < 3 && deltaY > touchThreshold) {
                voxelSprite.setAngle(voxelSprite.angle() + 1);
            } else if (voxelSprite.angle() > 1 && deltaY < touchThreshold * -1f) {
                voxelSprite.setAngle(voxelSprite.angle() - 1);
            }
        }
        return false;
    }

    public final static float minScale = 1f;
    public final static float maxScale = 5f;

    @Override
    public boolean zoom(float initialDistance, float distance) {
        final float delta = (distance - initialDistance) / VIRTUAL_HEIGHT;
        if (voxelSprite.scaleX() + delta > maxScale)
            voxelSprite.setScale(maxScale);
        else if (voxelSprite.scaleX() + delta < minScale)
            voxelSprite.setScale(minScale);
        else
            voxelSprite.addScale(delta, delta);
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

    @Override
    public void pinchStop() {

    }

}
