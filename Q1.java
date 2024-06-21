import java.util.Random;

public class Main {
    public static void main(String[] args) {
        Saldo saldo = new Saldo();
        Thread thread1 = new Thread(new ThreadSimulation("Membro 1", saldo));
        Thread thread2 = new Thread(new ThreadSimulation("Membro 2", saldo));
        Thread thread3 = new Thread(new ThreadSimulation("Membro 3", saldo));
    
        thread1.start();
        thread2.start();
        thread3.start();
    }
}

class Saldo {
    private int saldo = 0;

    public synchronized void deposito(int value, String membro) {
        saldo += value;
        System.out.println(membro + " depositou: "+ value);
    }

    public synchronized void saque(int value, String membro) {
        if (saldo - value > 0) {
            saldo -= value;
            System.out.println(membro + " sacou: "+ value);
        }
        else {
            System.out.println("SALDO INSUFICIENTE - saque do valor "+value+", pelo membro "+ membro+" não foi bem sucedida");
        }
    }

    public synchronized int getSaldo() {
        System.out.println("saldo atual: " + saldo);
        return saldo;
    }
}

class ThreadSimulation implements Runnable {
    final private String threadName;
    final private Saldo saldo;

    ThreadSimulation(String threadName, Saldo saldo) {
        this.threadName = threadName;
        this.saldo = saldo;
    }

    @Override
    public void run() {
        for (int i = 0; i < 5; i++) {
            try {
                Random random = new Random();
                int randomOperation = random.nextInt(2);
                int randomValue = random.nextInt(100);
                
                if (randomOperation == 0){
                    saldo.deposito(randomValue,threadName);
                }
                else {
                    saldo.saque(randomValue,threadName);
                }

                saldo.getSaldo();
                
                Thread.sleep(1000); // Pausa de 1 segundo
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
