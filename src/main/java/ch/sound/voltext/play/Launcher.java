package ch.sound.voltext.play;

/**
 * Fat-jar entry point that delegates to {@link SoundVoltexStatsApp}.
 *
 * <p>A class that does NOT extend {@code javafx.application.Application} must be
 * used as the JAR manifest {@code Main-Class} when running from a shaded (fat) JAR.
 * If {@code SoundVoltexStatsApp} itself were the manifest main class, the JVM's
 * module-system check would reject it because JavaFX is on the class path rather
 * than the module path inside a fat JAR.
 */
public class Launcher {

    public static void main(String[] args) {
        SoundVoltexStatsApp.main(args);
    }
}
