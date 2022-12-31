import java.util.LinkedList;

public class Crawler {

    /** Вывод результата **/
    public static void showResult(LinkedList<URLDepthPair> list){
        for (URLDepthPair c:list){
            System.out.println("Depth : "+c.getDepth() + "\tLink : "+c.getURL());
        }
    }

    /** Функция main должна выполнять следующие задачи:
    1). Обработать аргументы командной строки. Сообщить пользователю о
    любых ошибках ввода.
    2). Создать экземпляр пула URL-адресов и поместить указанный
    пользователем URL-адрес в пул с глубиной 0.
    3). Создать указанное пользователем количество задач (и потоков для их
    выполнения) для веб-сканера. Каждой задаче поискового робота нужно дать
    ссылку на созданный пул URL-адресов.
    4). Ожидать завершения веб-сканирования.
    5) Вывести получившийся список URL-адресов, которые были найдены. **/
    public static void main(String[] args) {

        //создаем пул потоков, добавляем пару
        URLPool pool = new URLPool(Integer.parseInt(args[1]));
        //добавим указанный пользователем адрес в пул с глубиной 0
        pool.addURL(new URLDepthPair(args[0],0));
        //создаем количество задач(и потоков для их выполнения)
        //каждой задаче даем ссылку на созданный пул
        for(int i = 0; i < Integer.parseInt(args[2]); i ++){
            CrawlerTask c = new CrawlerTask(pool);
            Thread t = new Thread(c);
            t.start();
        }
        while(Integer.parseInt(args[2]) != pool.getWait()){
            try{
                //проверка по таймеру
                Thread.sleep(500);
            }
            catch (InterruptedException e) {
                System.out.println(e.getLocalizedMessage());
            }
        }
        try{
            showResult(pool.getViewedLinks());
        }
        catch(NullPointerException e){
            System.out.println(e + "\n" + "usage: java Crawler " + args[0] + " " + args[1] + " " + args[2]);
        }
        System.exit(0);
    }
}


