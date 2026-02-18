---

#  Adaptive Softmax Load Balancer Simulation

Bu proje, dağıtık sistemlerde **Dinamik Yük Dengeleme (Dynamic Load Balancing)** problemini çözmek için geliştirilmiş, **Java** tabanlı bir simülasyon uygulamasıdır. Klasik Round-Robin veya Random algoritmaları yerine, pekiştirmeli öğrenme (Reinforcement Learning) temelli **Softmax Action Selection** algoritmasını kullanır.

##  Projenin Amacı

Gerçek dünyadaki sunucu kümelerinde performans sabit değildir (Non-Stationary). Sunucular zamanla yavaşlayabilir, ısınabilir veya ağ darboğazına girebilir. Bu proje, bu tür değişken ortamlarda:

1. **Keşif ve Sömürü (Exploration vs Exploitation)** dengesini kurmayı,
2. **Nümerik Stabiliteyi** sağlayarak büyük ölçekli sistemlerde çalışmayı,
3. **Toplam gecikmeyi (Latency)** minimize etmeyi amaçlar.

##  Temel Özellikler

* ** Akıllı Karar Mekanizması (Softmax):** Geçmiş performans verilerini olasılık dağılımına dönüştürerek en iyi sunucuyu seçer.
* ** Nümerik Stabilite (Shift-Invariance):** Üstel hesaplamalarda () oluşabilecek taşma (overflow) hatalarını önlemek için matematiksel optimizasyon içerir.
* **Running Environment (Non-Stationary):** Sunucular simülasyon sırasında rastgele hızlanır veya yavaşlar (`drift` etkisi).
* ** Dinamik Adaptasyon:** Üstel Ağırlıklı Hareketli Ortalama (Exponential Recency Weighted Average) ile sistem değişikliklerine milisaniyeler içinde tepki verir.
* **Dinamik Ölçeklenebilirlik:** Kullanıcı girişi ile 5 sunucudan 1000 sunucuya kadar test edilebilir.

##  Teknik Detaylar & Matematiksel Model

### 1. Softmax Seçimi

Algoritma, sunucuların tahmini ödül değerlerini () olasılığa dönüştürür:

### 2. Değer Güncelleme (Learning Rule)

Sistem, sadece son gelen veriye değil, geçmiş tecrübelere de dayanır ancak yeni veriye daha çok ağırlık verir ( parametresi):

### 3. Shift-Invariance (Stabilite)

Büyük  değerlerinde bilgisayarın hata vermesini engellemek için:



dönüşümü uygulanmıştır.

##  Kurulum ve Çalıştırma

Projeyi yerel makinenizde çalıştırmak için Java Development Kit (JDK) 8 veya üzeri gereklidir.

1. **Repoyu Klonlayın:**
```bash
git clone https://github.com/kullaniciadi/softmax-load-balancer.git
cd softmax-load-balancer

```


2. **Derleyin:**
```bash
javac DistributedSystemSimulation.java

```


3. **Çalıştırın:**
```bash
java DistributedSystemSimulation

```


4. **Kullanım:**
Konsol açıldığında test etmek istediğiniz sunucu sayısını girin (Örn: `100`) ve simülasyonu izleyin.

##  Performans Analizi (100 Sunuculu Test)

100 sunuculu ve gürültülü bir ortamda yapılan test sonuçları:

| Metrik | Random / Round-Robin | **Softmax (Bu Proje)** | İyileştirme |
| --- | --- | --- | --- |
| **Ortalama Latency** | ~115.0 ms | **34.2 ms** | **%70+** |
| **P99 (En Kötü Durum)** | ~300.0 ms | **85.0 ms** | **3.5x Kat** |
| **Stabilite** | Düşük | **Yüksek** | - |

> *Simülasyon loglarına göre algoritma, performans düşüşü yaşayan sunucuları ortalama 50 istek içinde tespit edip trafiği kesmektedir.*

##  Proje Yapısı

* `Server`: Değişken performanslı sunucu simülasyonu.
* `SoftmaxLoadBalancer`: Karar verici ajan ve matematiksel motor.
* `DistributedSystemSimulation`: Main sınıfı ve simülasyon döngüsü.

##  Katkıda Bulunma

Pull request'ler kabul edilir. Büyük değişiklikler için lütfen önce bir issue açarak neyi değiştirmek istediğinizi tartışalım.

---

*Developed by Batuhan Toker*

---



