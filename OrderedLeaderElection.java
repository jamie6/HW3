/*
3)	ORDERED LEADER ELECTION
Assume a system with N elected official threads and one rank thread. 
Each elected official thread has an identifying name and an integer rank value, 
where -∞ is the lowest rank and +∞ is the highest rank, use Random.  
Threads do not previously know the rank value of other threads.  

As threads are being created they should print out there name, rank and who 
they think is the leader, initially they think they are the leader, and notify 
the rank thread that a new elected official has been created, 
using an interrupt. 

When the rank thread is interrupted it will check the 
ranking of all the threads at the time and will only notify all threads if 
there is a new leader using an interrupt. The thread with the largest rank 
value is to be selected as the leader.  You can use any algorithm that selects 
one and only one thread as the leader.
 */
import java.util.Random;
import java.util.LinkedList;
import java.util.HashMap;
/**
 *
 * @author Jamie
 */
public class OrderedLeaderElection
{
    static HashMap<Thread, ElectedOfficial> hm = new HashMap<>();
    static LinkedList<Thread> threads = new LinkedList<>(); // to hold elected officials
    static int n = 5;
    static Random random = new java.util.Random();
    static ElectedOfficial leader;
    static Thread rankThread = new Thread(new Ranking());
    public static void main(String[] args)
    {
        rankThread.start();
    }
    
    public static void addNewElectedOfficials()
    {
        for ( int i = threads.size(); threads.size() < n; i++ )
        {
            ElectedOfficial eo = new ElectedOfficial(random.nextInt(), "Elected Official no. " + String.valueOf(i));
            Thread t = new Thread(eo);
            threads.add(t);
            hm.put(t, eo);
            t.start();
        }
    }
    
    public static class ElectedOfficial implements Runnable
    {
        final int rank;
        final String name;
        public ElectedOfficial(int rank, String name)
        {
            this.rank = rank;
            this.name = name;
        }
        
        public int getRank()
        {
            return rank;
        }
        
        public String getName()
        {
            return name;
        }
        
        public void run()
        {
            System.out.println("I am " + name + ", my rank is " + rank + " and I am the leader.");
            rankThread.interrupt();
        }
    }
    
    public static class Ranking implements Runnable
    {
        public void Rank()
        {
            if ( !threads.isEmpty() )
            {
                threads.sort((a, b)->hm.get(b).rank - hm.get(a).rank);
                if ( leader == null || leader.name.equals(hm.get(threads.getFirst()).getName()) )
                {
                    System.out.println("The leader is " + hm.get(threads.getFirst()).getName() + " with rank " + hm.get(threads.getFirst()).getRank() );
                }
            }
            addNewElectedOfficials();
        }
        public void run()
        {
            try
            {
                Rank();
                Thread.sleep(1000);
            }
            catch ( InterruptedException e )
            {
                rankThread = new Thread(new Ranking());
                rankThread.start();
            }
        }
    }
}
