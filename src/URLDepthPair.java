import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
//Yнифицированный указатель ресурса. Это адрес веб-страницы. Он имеет следующую структуру:
//1) метод доступа к ресурсу;
//2) доменное имя
//3) путь к файлу
//4) данные о файле
//В данной лабораторной работе будет рассмотрен метод доступа «http://».

//класс
//URLDepthPair, каждый экземпляр которого включает в себя поле типа String,
//представляющее URL-адрес, и поле типа int, представляющее глубину поиска.
public class URLDepthPair {
    private static final String URL_PREFIX = "http://";
    private final String URLString;
    private final int depth;

    public URLDepthPair(String URL, int depth){
        this.URLString=URL;
        this.depth=depth;
    }

    public String getHost() throws MalformedURLException {
        URL host = new URL(URLString);
        return host.getHost();
    }

    public String getPath() throws MalformedURLException {
        URL path = new URL(URLString);
        return path.getPath();
    }

    public int getDepth() {
        return depth;
    }

    public String getURL() {
        return URLString;
    }

    public static String getURLPrefix() {
        return URL_PREFIX;
    }

    @Override
    public String toString() {
        return "URLDepthPair{" +
                "url='" + URLString + '\'' +
                ", depth=" + depth +
                '}';
    }

    //Проверяет, был ли уже выполнен поиск по URL-адресу
    public static boolean check(LinkedList<URLDepthPair> resultLink, URLDepthPair pair) {
        boolean hasBeenChecked = true;
        for (URLDepthPair c : resultLink) {
            if (c.getURL().equals(pair.getURL())) {
                hasBeenChecked = false;
                break;
            }
        }
        return hasBeenChecked;
    }
}