package DiningPhilosophers;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DiningPhilosophers {

    private static final int NUMBER_OF_PHILOSOPHERS = 5;
    private final Lock[] chopsticks = new ReentrantLock[NUMBER_OF_PHILOSOPHERS];
    private final int[] eatCount = new int[NUMBER_OF_PHILOSOPHERS];

    // Initialize locks and eat count for each chopstick
    public DiningPhilosophers() {
        for (int i = 0; i < NUMBER_OF_PHILOSOPHERS; i++) {
            chopsticks[i] = new ReentrantLock();
            eatCount[i] = 0;
        }
    }

    public void startDining() throws InterruptedException {
        for (int i = 0; i < NUMBER_OF_PHILOSOPHERS; i++) {
            final int philosopherId = i;
            new Thread(() -> dine(philosopherId)).start();
        }
    }

    private void dine(int philosopherId) {
        try {
            while (true) {
                think(philosopherId);
                if (pickupChopsticks(philosopherId)) {
                    eat(philosopherId);
                    putDownChopsticks(philosopherId);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private boolean pickupChopsticks(int philosopherId) throws InterruptedException {
        int leftChopstick = philosopherId;
        int rightChopstick = (philosopherId + 1) % NUMBER_OF_PHILOSOPHERS;

        if (chopsticks[leftChopstick].tryLock()) {
            if (chopsticks[rightChopstick].tryLock()) {
                return true;
            }
            chopsticks[leftChopstick].unlock();
            
            // The above line prevents DEADLOCK, a situation where two processes are stuck,
            // each attempting to acquire a lock that the other has.  Neither process 1 or process 2 can make progress until 
            // one of the processes gives up its resource.
            //
            //  
        }

        // Back-off strategy to prevent live lock
        Thread.sleep((int) (Math.random() * 100));
        return false;
    }

    private void putDownChopsticks(int philosopherId) {
        int leftChopstick = philosopherId;
        int rightChopstick = (philosopherId + 1) % NUMBER_OF_PHILOSOPHERS;

        chopsticks[leftChopstick].unlock();
        chopsticks[rightChopstick].unlock();

        // Increment eat count for the philosopher
        eatCount[philosopherId]++;


    }

    private void eat(int philosopherId) throws InterruptedException {
        System.out.println("Philosopher " + (philosopherId + 1) + " is eating.");
        System.out.println("Eat count for philosopher "+ (philosopherId + 1) + "= " + (eatCount[philosopherId]));
        Thread.sleep(3000); // Simulate eating
    }

    private void think(int philosopherId) throws InterruptedException {
        System.out.println("Philosopher " + (philosopherId + 1) + " is thinking.");
        Thread.sleep(3000); // Simulate thinking
    }

    public static void main(String[] args) throws InterruptedException {
        new DiningPhilosophers().startDining();
    }
}
