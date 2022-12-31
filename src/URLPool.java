import java.util.LinkedList;
// В утилитах нам понадобятся списки. Списки похожи на массивы объектов, за исключением того, что они
// могут легко менять размерность при необходимости, и в них не используются
// скобки для поиска отдельных элементов. В этой работе нам больше подойдёт реализация LinkedList списка, так как
// мы фактически оперируем цепочкой ссылок

//URLPool, который будет хранить список
//всех URL-адресов для поиска, а также относительный "уровень" каждого из
//этих URL-адресов (также известный как "глубина поиска").
public class URLPool {
    //списки всех адресов и относительных глубину поиска для свободных и обработанных адресов
    LinkedList<URLDepthPair> foundedLink;
    LinkedList<URLDepthPair> viewedLinks;

    int maxDepth;
    //количество потоков в ожидании
    int waiters;

    public URLPool(int maxDepth){
        this.maxDepth = maxDepth;
        foundedLink = new LinkedList<>();
        viewedLinks = new LinkedList<>();
        waiters = 0;
    }

    //способ получения пары
    //synchronized необходим для того, чтобы с этим методом взаимодействовал
    //только один поток
    public synchronized URLDepthPair getURL(){
        //если ни один адрес недоступен, то режим ожидания
        while(foundedLink.size() == 0){
            waiters++;
            try{
                wait();
                //освобождает монитор и переводит вызывающий поток в состояние ожидания до тех пор, пока другой поток не вызовет метод notify()
            }
            catch (InterruptedException e){
                System.out.println("Interrupted Exception");
            }
            waiters--;
        }
        return foundedLink.removeFirst();
    }
    //также синхронизируем url
    public synchronized void addURL(URLDepthPair pair){
        if(URLDepthPair.check(viewedLinks, pair)){
            viewedLinks.add(pair);
            if (pair.getDepth() < maxDepth){
                foundedLink.add(pair);
                //продолжаем работу потока, к которому ранее был вызван wait
                //в случае, когда новый адрес добавлен к пулу
                notify();
                //продолжает работу потока, у которого ранее был вызван метод wait()
            }
        }
    }

    public int getWait() {
        return waiters;
    }

    public LinkedList<URLDepthPair> getViewedLinks(){
        return viewedLinks;
    }
}
