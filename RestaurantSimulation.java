import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream; // Import statement for IntStream

public class RestaurantSimulation {
    private static final boolean[] seats = new boolean[5];
    private static final Lock seatLock = new ReentrantLock();
    private static int waitingCustomers = 0; 

    public static void main(String[] args) {
        Thread[] customers = new Thread[100];
        for (int i = 0; i < customers.length; i++) {
            final int customerId = i;
            customers[i] = new Thread(() -> {
                try {
                    arriveAndSeat(customerId);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            customers[i].start();
        }
    }
    
    private static void arriveAndSeat(int customerId) throws InterruptedException {
        while (true) {
            boolean seated = false;
            seatLock.lock(); // Lock to ensure exclusive access to seats
            try {
                for (int i = 0; i < seats.length; i++) {
                    if (!seats[i]) { 
                        seats[i] = true; 
                        System.out.println("Customer " + customerId + " seated at seat " + i);
                        seated = true;
                        break;
                    }
                }
            } finally {
                seatLock.unlock(); // Ensure the lock is always released
            }
            if (seated) {
                Thread.sleep(1000); // Customers dine for some time
                leaveRestaurant();
                break;
            } else {
                // If no seat was available, wait a bit before trying again
                Thread.sleep(100); // Reduce CPU usage
            }
        }
    }

    private static void leaveRestaurant() {
        seatLock.lock(); // Lock to ensure exclusive access to modify seats
        try {
            // Check if all seats are occupied, indicating all are dining together
            boolean allOccupied = IntStream.range(0, seats.length).mapToObj(i -> seats[i]).allMatch(s -> s);
            if (allOccupied) {
                Arrays.fill(seats, false); // Make all seats available
                System.out.println("All customers have left. Seats are now available.");
            }
        } finally {
            seatLock.unlock(); // Ensure the lock is always released
        }
    }
}