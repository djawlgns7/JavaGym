package util;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class SoundUtil {

    private static MediaPlayer mediaPlayer;

    public static void play(String mp3Path) {
        String path = SoundUtil.class.getResource("/mp3/" + mp3Path + ".mp3").toExternalForm();

        Media media = new Media(path);
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.play();
    }
}
