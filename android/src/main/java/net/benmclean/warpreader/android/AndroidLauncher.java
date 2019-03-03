package net.benmclean.warpreader.android;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import net.benmclean.warpreader.WarpReader;
import net.spookygames.gdx.nativefilechooser.android.AndroidFileChooser;

/** Launches the Android application. */
public class AndroidLauncher extends AndroidApplication {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WarpReader.fileChooser = new AndroidFileChooser(this);
        AndroidApplicationConfiguration configuration = new AndroidApplicationConfiguration();
        initialize(new WarpReader(), configuration);
    }
}