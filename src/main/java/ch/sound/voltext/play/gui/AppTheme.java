package ch.sound.voltext.play.gui;

import java.net.URL;
import java.util.Objects;

import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.text.Font;

/**
 * Dresses windows in the look shared by the home tools, and registers the
 * bundled font the stylesheets ask for.
 *
 * <p>Two stylesheets are attached to every scene: the theme, which every one of
 * the home tools carries an identical copy of, and {@code css/stats-app.css},
 * which holds what only these windows have — the titled frames and the grid of
 * days. The theme goes on first so that where the two speak about the same
 * class this application has the last word.
 *
 * <p>This application had no stylesheet of its own before and was drawn in
 * whatever the toolkit came with, which is why there is no spare look kept
 * beside the theme here as there is in the other home tools.
 *
 * <p>JavaFX has no stylesheet rule that can pull in a font file, so the file
 * has to be handed to the toolkit from code before any stylesheet naming that
 * family is resolved. Loading it here means it happens the first time anything
 * in the application is styled, which is always before a window is drawn.
 *
 * <p>What goes wrong here is reported on the error stream rather than through a
 * logging framework, this application carrying none.
 *
 * @author Filipe Cristino
 * @since 1.1.0
 */
public final class AppTheme {

    /** Classpath location of the look shared by the home tools. */
    public static final String THEME_STYLESHEET_RESOURCE = "/css/theme.css";

    /** Classpath location of the stylesheet holding what only these windows have. */
    public static final String APP_STYLESHEET_RESOURCE = "/css/stats-app.css";

    /** Classpath location of the packaged font every window is drawn in. */
    public static final String FONT_RESOURCE = "/font/NotoSansJP-Regular.ttf";

    /**
     * Size the font is registered at. It only decides the size of the one
     * instance created on loading; the stylesheets set every size actually
     * drawn.
     */
    private static final double FONT_LOAD_SIZE = 12.0;

    static {
        loadBundledFont();
    }

    /**
     * Utility class — instantiation is not intended.
     */
    private AppTheme() {
    }

    /**
     * Hands the packaged font to the toolkit so the stylesheets can name it. A
     * font that cannot be found or read is reported and then left alone, since
     * the window reads perfectly well in whatever the toolkit falls back to and
     * a missing font is no reason to refuse to start. A song title in Japanese
     * is the reason the font is carried at all.
     */
    private static void loadBundledFont() {
        URL resource = AppTheme.class.getResource(FONT_RESOURCE);
        if (Objects.isNull(resource)) {
            System.err.println("Bundled font " + FONT_RESOURCE
                    + " is not on the classpath, falling back to an installed font");
            return;
        }
        Font loaded = Font.loadFont(resource.toExternalForm(), FONT_LOAD_SIZE);
        if (Objects.isNull(loaded)) {
            System.err.println("Bundled font " + FONT_RESOURCE
                    + " could not be read, falling back to an installed font");
        }
    }

    /**
     * Dresses a scene in the shared look.
     *
     * @param scene the scene to style
     * @throws IllegalArgumentException if the scene is {@code null}
     */
    public static void apply(Scene scene) {
        if (Objects.isNull(scene)) {
            throw new IllegalArgumentException("scene must not be null");
        }
        dress(scene.getStylesheets());
    }

    /**
     * Puts the theme and the stylesheet of this application on a list of
     * stylesheets, in that order, leaving a list that already carries them
     * unchanged rather than carrying either of them twice.
     *
     * @param stylesheets the list of stylesheets to dress
     */
    private static void dress(ObservableList<String> stylesheets) {
        addOnce(stylesheets, THEME_STYLESHEET_RESOURCE);
        addOnce(stylesheets, APP_STYLESHEET_RESOURCE);
    }

    /**
     * Adds a stylesheet to a list unless it is already on it. A stylesheet
     * missing from the classpath is reported and skipped, a window drawn in the
     * toolkit's own look being better than no window at all.
     *
     * @param stylesheets the list to add to
     * @param resource the classpath location of the stylesheet
     */
    private static void addOnce(ObservableList<String> stylesheets, String resource) {
        URL url = AppTheme.class.getResource(resource);
        if (Objects.isNull(url)) {
            System.err.println("Stylesheet " + resource + " is not on the classpath");
            return;
        }
        String externalForm = url.toExternalForm();
        if (!stylesheets.contains(externalForm)) {
            stylesheets.add(externalForm);
        }
    }
}
