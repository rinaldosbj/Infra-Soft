class Car implements Runnable {
    private String direction;
    private Bridge bridge;

    public Car(String direction, Bridge bridge) {
        this.direction = direction;
        this.bridge = bridge;
    }

    @Override
    public void run() {
        try {
            bridge.enterBridge(direction);
            Thread.sleep(2000); // Simulate time to cross the bridge
            bridge.exitBridge(direction);
        } catch (InterruptedException e) { //if the thread is stopped while sleeping gpt who made me put the try catch block
            e.printStackTrace();
        }
    }
}

class Bridge {
    public synchronized void enterBridge(String direction) {
        System.out.println("Car from " + direction + " is entering the bridge.");
    }

    public synchronized void exitBridge(String direction) {
        System.out.println("Car from " + direction + " has exited the bridge.");
    }
}

public class Main implements Escape {
    public static void main(String[] args) {
        Bridge bridge = new Bridge();
        Thread car1 = new Thread(new Car("left", bridge));
        Thread car2 = new Thread(new Car("right", bridge));

        car1.start();
        car2.start();
    }
}