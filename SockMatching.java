/*
1)	SOCK MATCHING
There are four threads that each make a random number of socks (1-100).  
Each sock thread produces a sock that is one of four colors, 
Red, Green, Blue, Orange.  The socks are then passed to a single matching 
thread.  The matching thread finds two socks that are the same color.  
It then passes the pair of socks to the washer thread.  The washer thread 
destroys the socks. In the console announce which thread is printing and what 
occurred: (Make sure your program ends.  When there is no more work to finish 
it should terminate) 

a)	EXAMPLE OUTPUT
Red SockThread: Produced 4 of 35 Red Socks
Green SockThread: Produced 7 of 19 Green Socks
Matching Thread: Send Blue Socks to Washer. Total socks 234. Total inside queue 3
Washer Thread: Destroyed Blue socks

 */
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
/**
 *
 * @author Jamie
 */
public class SockMatching
{
    static ReadWriteLock lock = new ReentrantReadWriteLock();
    static LinkedList<Sock> socks = new LinkedList<>();
    public static void main(String[] args) throws InterruptedException
    {
        Thread threadRed = new Thread(new SockThread("Red"));
        threadRed.setName("Red");
        Thread threadGreen = new Thread(new SockThread("Green"));
        threadGreen.setName("Green");
        Thread threadBlue = new Thread(new SockThread("Blue"));
        threadBlue.setName("Blue");
        Thread threadOrange = new Thread(new SockThread("Orange"));
        threadOrange.setName("Orange");
        
        threadRed.start();
        threadGreen.start();
        threadBlue.start();
        threadOrange.start();
        
        while ( threadRed.isAlive() || threadGreen.isAlive() || threadBlue.isAlive() || threadOrange.isAlive() )
        {
            Thread.sleep(1000); // wait one second
        }
        Executor executor = Executors.newCachedThreadPool();
        executor.execute(new Matching());
        ThreadPoolExecutor pool = (ThreadPoolExecutor) executor;
        pool.shutdown();
    }
    
    public static class SockThread implements Runnable
    {
        int count = 0;
        String color;
        Random random;
        int total = 0;
        
        public SockThread(String color)
        {
            random = new Random();
            this.color = color;
        }
        
        public void run()
        {
            total = random.nextInt(100)+1;
            for ( int i = 0; i < total; i++ )
            {
                socks.add(new Sock(color));
                count++;
                threadMessage("Produced " + count + " of " + total + " " + color + " Socks");
            }
        }
    }
    
    public static class Matching implements Runnable
    {
        public void run()
        {
            try 
            {
                boolean matchFound = false;
                while ( socks.size() > 1 || !matchFound)
                {
                    Sock match1, match2;
                    matchFound = false;
                    for ( int i = 0; i < socks.size(); i++ )
                    {
                        match1 = socks.get(i);
                        for ( int j = 1; j < socks.size(); j++ )
                        {
                            match2 = socks.get(j);
                            if ( match1.equals(match2) )
                            {
                                matchFound = true;
                                Thread washing = new Thread(new Washing(match1, match2));
                                washing.setName("Washer Thread");
                                threadMessage("Send " + socks.get(i) + " Socks to Washer. Total socks " + socks.size());
                                washing.start();
                                break;
                            }
                        }
                        if ( matchFound ) break;
                    }
                }
            }
            catch ( java.lang.NullPointerException e )
            {
                System.out.println("Null pointer error: " + e.getMessage());
            }
            catch ( java.lang.IndexOutOfBoundsException e )
            {
                System.out.println("Index out of bounds error: " + e.getMessage());
            }
        }
    }
    
    public static class Washing implements Runnable
    {
        Sock match1, match2;
        
        public Washing( Sock match1, Sock match2 )
        {
            this.match1 = match1;
            this.match2 = match2;
        }
        
        public void run()
        {
            lock.readLock().lock();
            threadMessage("Destroyed " + match1 + " sock");
            socks.remove(match1);
            socks.remove(match2);
            lock.readLock().unlock();
        }
    }
    
    public static class Sock
    {
        String color;
        public Sock(String color)
        {
            this.color = color;
        }
        
        @Override
        public boolean equals( Object o )
        {
            return o instanceof Sock && color.equals(((Sock)o).color);
        }
        
        @Override
        public String toString()
        {
            return color;
        }
    }
    
    static void threadMessage(String message)
    {
        String threadName = Thread.currentThread().getName();
        System.out.format("%s: %s%n", threadName, message);
    }
}
