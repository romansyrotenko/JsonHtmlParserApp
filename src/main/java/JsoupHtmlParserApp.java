import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class JsoupHtmlParserApp {

    static String BASE_URL = "http://www.springfieldspringfield.co.uk/";
    static String PAGE_URL_WITH_ALL_EPISODES = "episode_scripts.php?tv-show=the-simpsons";
    static String EPISODE_CONTAINER = ".scrolling-script-container";
    static String EPISODE_NAME = ".main-content-left>h3";
    static String LIST_OF_EPISODES = ".season-episode-title";

    public static void main(String args[]) throws IOException {

        Document doc = Jsoup.connect(BASE_URL + PAGE_URL_WITH_ALL_EPISODES).get();
        Elements allEpisodesUrls = doc.select(LIST_OF_EPISODES);

        for (int i = 0; i < allEpisodesUrls.size(); i++) {
            parsePageToFile(getCurrentEpisodeUrl(allEpisodesUrls.get(i)));
        }
    }

    private static String getCurrentEpisodeUrl(Element element) {
        return BASE_URL + element.attr("href");
    }

    private static void parsePageToFile(String url) throws IOException {

        Document episode = Jsoup.connect(url).get();
        String currentEpisodeName = episode.select(EPISODE_NAME).get(0).text().substring(6);
        String episodePlainText = episode.select(EPISODE_CONTAINER).get(0).text();
        String currentEpisodeNumber = url.substring(url.length() - 6);

        writeStringToFile(currentEpisodeNumber, currentEpisodeName, episodePlainText);

    }

    private static void writeStringToFile(String episodeNumber, String episodeName, String plainText) throws IOException {

        BufferedWriter outputFile = null;

        if (episodeName.contains("?")) {
            String episodeName2 = episodeName.replace("?", "");
            episodeName = episodeName2;
        }

        if (plainText.isEmpty()) {
            return;
        }

        try {
            File file = new File(episodeNumber + " - " + episodeName + ".txt");
            outputFile = new BufferedWriter(new FileWriter(file));
            outputFile.write(plainText);
        } catch ( IOException e ) {
            e.printStackTrace();
        } finally {
            if ( outputFile != null ) {
                outputFile.close();
                System.out.println("File " + episodeNumber + " - " + episodeName + ".txt was successfully saved");
            }
        }
    }

}
