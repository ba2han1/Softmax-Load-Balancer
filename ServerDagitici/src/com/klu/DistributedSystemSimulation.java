package com.klu;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;


class Server {
    private int id;
    private double meanLatency;
    private Random random;

    public Server(int id, double startLatency) {
        this.id = id;
        this.meanLatency = startLatency;
        this.random = new Random();
    }

    public double processRequest() {
        double noise = random.nextGaussian() * 5;
        double actualLatency = meanLatency + noise;


        return Math.max(1.0, actualLatency);
    }


    public void drift() {

        double change = (random.nextDouble() - 0.5) * 4;
        this.meanLatency += change;


        if (this.meanLatency < 10) this.meanLatency = 10;
        if (this.meanLatency > 1000) this.meanLatency = 1000;
    }

    public int getId() { return id; }
    public double getTrueMeanLatency() { return meanLatency; }
}


class SoftmaxLoadBalancer {
    private int k;
    private double[] qValues;
    private int[] counts;


    private double temperature;
    private double alpha;

    public SoftmaxLoadBalancer(int k, double temperature, double alpha) {
        this.k = k;
        this.temperature = temperature;
        this.alpha = alpha;
        this.qValues = new double[k];
        this.counts = new int[k];


        for (int i = 0; i < k; i++) {
            qValues[i] = 0.5;
        }
    }


    public int selectServer() {
        double[] probabilities = new double[k];
        double sumExp = 0.0;


        double maxQ = Double.NEGATIVE_INFINITY;
        for (double q : qValues) {
            if (q > maxQ) maxQ = q;
        }


        for (int i = 0; i < k; i++) {
            double expVal = Math.exp((qValues[i] - maxQ) / temperature);
            probabilities[i] = expVal;
            sumExp += expVal;
        }


        double randomValue = Math.random() * sumExp;
        double cumulativeProb = 0.0;

        for (int i = 0; i < k; i++) {
            cumulativeProb += probabilities[i];
            if (randomValue <= cumulativeProb) {
                return i;
            }
        }
        return k - 1;
    }


    public void update(int serverIndex, double latency) {
        counts[serverIndex]++;


        double reward = 1.0 / latency;


        qValues[serverIndex] = qValues[serverIndex] + alpha * (reward - qValues[serverIndex]);
    }

    public double getEstimatedReward(int index) {
        return qValues[index];
    }
}


public class DistributedSystemSimulation {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        int K = 0;

        while (true) {
            System.out.print("Lütfen kümedeki sunucu sayısını (K) giriniz (Örn: 5): ");
            if (scanner.hasNextInt()) {
                K = scanner.nextInt();
                if (K > 0) break;
                else System.out.println("Hata: Sunucu sayısı 0'dan büyük olmalıdır.");
            } else {
                System.out.println("Hata: Lütfen geçerli bir tam sayı giriniz.");
                scanner.next(); // Hatalı girdiyi temizle
            }
        }


        int TOTAL_REQUESTS = 5000;
        double TAU = 0.1;
        double ALPHA = 0.1;

        System.out.println("\nSimülasyon Başlatılıyor...");
        System.out.println("Algoritma: Softmax Action Selection");
        System.out.println("Sunucu Sayısı: " + K);
        System.out.println("Toplam İstek: " + TOTAL_REQUESTS);
        System.out.println("-------------------------------------------------------------");


        SoftmaxLoadBalancer lb = new SoftmaxLoadBalancer(K, TAU, ALPHA);
        List<Server> servers = new ArrayList<>();
        Random rand = new Random();


        for (int i = 0; i < K; i++) {

            double startLatency = 20 + (rand.nextDouble() * 180);
            servers.add(new Server(i, startLatency));
            System.out.printf("Sunucu #%d oluşturuldu (Başlangıç Ort. Latency: %.2f ms)\n", i+1, startLatency);
        }
        System.out.println("-------------------------------------------------------------");


        System.out.printf("%-10s %-10s %-15s %-20s\n", "Request#", "Seçilen S.", "Anlık Latency", "Sunucu Gerçek Hızı");

        double totalLatency = 0;

        for (int t = 1; t <= TOTAL_REQUESTS; t++) {

            int serverIndex = lb.selectServer();
            Server selectedServer = servers.get(serverIndex);


            double latency = selectedServer.processRequest();
            totalLatency += latency;


            lb.update(serverIndex, latency);

            for (Server s : servers) {
                s.drift();
            }

            if (t % 500 == 0) {
                System.out.printf("%-10d %-10d %-15.2f %-20.2f\n",
                        t, serverIndex, latency, selectedServer.getTrueMeanLatency());

                System.out.print("   [Tahmin Edilen Puanlar]: ");
                for(int i=0; i<K; i++) {
                    System.out.printf("S%d:%.4f ", i, lb.getEstimatedReward(i));
                }
                System.out.println("\n");
            }
        }

        System.out.println("-------------------------------------------------------------");
        System.out.println("SİMÜLASYON TAMAMLANDI.");
        System.out.printf("Toplam Ortalama Latency: %.2f ms\n", (totalLatency / TOTAL_REQUESTS));
        System.out.println("-------------------------------------------------------------");

        scanner.close();
    }
}