import java.util.Random;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Q3 {
    public static void main(String[] args) {
        Barbearia barbearia = new Barbearia(3); // barbearia com 3 cadeiras

        // Cria e inicia 100 clientes que tentam entrar na barbearia
        for (int i = 0; i < 100; i++) {
            new Thread(new Cliente("Cliente" + i, barbearia)).start();
        }
    }

    static class Barbearia {
        private final int maxCadeiras;
        private int cadeirasLivres;
        private final Queue<String> filaClientes = new LinkedList<>();
        private final Lock lock = new ReentrantLock();
        private final Condition clienteCondicao = lock.newCondition();
        private boolean barbeiroDormindo = true;
        private boolean cortandoCabelo = false;

        Barbearia(int numCadeiras) {
            this.maxCadeiras = numCadeiras;
            this.cadeirasLivres = numCadeiras;
        }

        public void entrarNaBarbearia(String cliente) {
            lock.lock(); // primeira região crítica: ver se tem espaço nas cadeiras
            try {
                // Verifica se a barbearia está lotada
                if (filaClientes.size() >= maxCadeiras) {
                    System.out.println(cliente + " foi embora porque a barbearia está lotada.");
                    return;
                }

                // Adiciona o cliente à fila e decrementa o número de cadeiras livres
                filaClientes.add(cliente);
                cadeirasLivres--;
                System.out.println(cliente + " entrou e sentou. Cadeiras disponíveis: " + cadeirasLivres);

                // Acorda o barbeiro se ele estiver dormindo
                if (barbeiroDormindo) {
                    barbeiroDormindo = false;
                    System.out.println(cliente + " acordou o barbeiro.");
                    clienteCondicao.signal(); // Continua a thread que acordou o barbeiro
                }

                // Espera até ser a vez do cliente e o barbeiro estar disponível
                while (!cliente.equals(filaClientes.peek()) || cortandoCabelo) {
                    clienteCondicao.await(); // segunda região crítica: cortar o cabelo
                }

                // Inicia o corte de cabelo
                cortandoCabelo = true;
                filaClientes.poll(); // remove o primeiro cliente da fila
                cadeirasLivres++;
                System.out.println(cliente + " está cortando o cabelo. Cadeiras disponíveis: " + cadeirasLivres);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock(); // permite mais pessoas entrem na barbearia
            }

            try {
                // Simula o tempo necessário para cortar o cabelo
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            lock.lock();
            try {
                System.out.println(cliente + " terminou o corte.");
                cortandoCabelo = false;

                // Verifica se há mais clientes na fila
                if (filaClientes.isEmpty()) {
                    barbeiroDormindo = true;
                    System.out.println("Barbeiro está dormindo.");
                } else {
                    clienteCondicao.signalAll(); // Continua as outras threads
                }
            } finally {
                lock.unlock(); // permite mais pessoas entrem na barbearia
            }
        }
    }

    static class Cliente implements Runnable {
        private final String nome;
        private final Barbearia barbearia;

        Cliente(String nome, Barbearia barbearia) {
            this.nome = nome;
            this.barbearia = barbearia;
        }

        @Override
        public void run() {
            Random random = new Random();
            try {
                // Pausa aleatória antes de ir para a barbearia
                Thread.sleep(random.nextInt(25000));
                // Tenta entrar na barbearia
                barbearia.entrarNaBarbearia(nome);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
