import java.io.*;
// Обеспечивает системный ввод и вывод через потоки данных, сериализацию и файловую систему.
// Если не указано иное, передаёт нулевой аргумент конструктору или методу в любом классе
// или интерфейсе в этом пакете -> приводит к NullPointerException выбросу
import java.net.*;
// Набор пакетов для обеспечения безопасных Интернет-коммуникаций.
// В данной работе мы используем его преимущественно для работы с сокетами
// Сокеты - это средства для установления канала связи между машинами по сети.


//Принцип
//работы веб-сканера заключается в следующем:
//1). Получение пары URL-Depth из пула, ожидая в случае, если пара не
//будет сразу доступна.
//2). Получение веб-страницы по URL-адресу.
//3). Поиск на странице других URL-адресов. Для каждого найденного
//URL-адреса, необходимо добавить новую пару URL-Depth к пулу URL-адресов.
//Новая пара должна иметь глубину на единицу больше, чем глубина текущего
//URL-адреса, по которому происходит сканирование.
//4). Переход к шагу 1.
//Данный цикл должен продолжаться до тех пор, пока в пуле не останется
//пар URL-Depth.
public class CrawlerTask implements Runnable {
    //Этот интерфейс предназначен для предоставления общего протокола для объектов, которые хотят выполнять код, пока они активны.
    // Например, Runnable реализуется классом Thread. Быть активным просто означает, что поток был запущен и еще не был остановлен.

    URLPool URLPool;

    public CrawlerTask(URLPool pool){
        URLPool = pool;
    }

    /** Формулирование запроса **/
    public static void request(PrintWriter out, URLDepthPair pair) throws MalformedURLException {
        String request = "GET " + pair.getPath() + " HTTP/1.1\r\nHost:" + pair.getHost() + "\r\nConnection: Close\r\n";
        //Hyper Text Transfer Protocol (Протокол передачи
        //гипертекста). Это стандартный текстовый протокол, используемый для
        //передачи данных веб-страницы через Интернет. Последней спецификацией
        //HTTP является версия 1.1, которую будет использована в данной лабораторной
        //работе.
        out.println(request);
        out.flush();
    }

    /**1). Получение пары URL-Depth из пула, ожидая в случае, если пара не
     будет сразу доступна.
     2). Получение веб-страницы по URL-адресу.
     3). Поиск на странице других URL-адресов. Для каждого найденного
     URL-адреса, необходимо добавить новую пару URL-Depth к пулу URL-адресов.
     Новая пара должна иметь глубину на единицу больше, чем глубина текущего
     URL-адреса, по которому происходит сканирование.
     4). Переход к шагу 1.**/
    @Override
    public void run() {
        while (true) {
            //получение пары из пула
            URLDepthPair currentPair = URLPool.getURL();
            try {
                //создаем сокет и получаем веб-страницу
                Socket mySocket = new Socket(currentPair.getHost(), 80);
                mySocket.setSoTimeout(1000);
                try {

                    PrintWriter out = new PrintWriter(mySocket.getOutputStream(), true);
                    //Этот метод позволяет сокету получать данные с другой стороны
                    //соединения.
                    BufferedReader in =  new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
                    //Этот метод позволяет сокету отправлять данные на другую сторону
                    //соединения
                    request(out,currentPair);
                    String line = in.readLine();
                    while (line != null){
                        //<a href="[любой_URL-адрес_начинающийся_с_http://]">
                        if (line.contains(URLDepthPair.getURLPrefix()) && line.indexOf('"') != -1) {
                            StringBuilder currentLink = new StringBuilder();
                            int i = line.indexOf(URLDepthPair.getURLPrefix());
                            while (line.charAt(i) != '"' && line.charAt(i) != ' ') {
                                currentLink.append(line.charAt(i));
                                i++;
                            }
                            //для каждого найденного URL создаем новую пару
                            //и добавляем ее к пулу адресов, увеличивая глубину исходной пары на 1
                            System.out.println(" > Found new link: "+ currentLink);
                            URLDepthPair newPair = new URLDepthPair(currentLink.toString(), currentPair.getDepth() + 1);
                            URLPool.addURL(newPair);
                        }
                        line = in.readLine();
                    }
                    mySocket.close();
                } catch (SocketTimeoutException e) {
                    mySocket.close();
                }
            }
            catch (IOException e) {
                System.out.println("IOException caught"+ e.getLocalizedMessage());
            }
        }
    }
}