# Süper Lig Otomasyonu (LeagueManager)

Bu proje, bir futbol liginin yönetimini ve takibini sağlayan modern bir **JavaFX** masaüstü uygulamasıdır. Kullanıcılar takımları yönetebilir, lig fikstürünü oluşturabilir ve canlı maç takiplerini gerçekleştirebilirler.

## 🚀 Özellikler

- 🔐 **Kullanıcı Girişi & Oturum Yönetimi**: Kişiselleştirilmiş kullanıcı deneyimi ve oturum saklama.
- 📊 **Gösterge Paneli (Dashboard)**: Lige genel bakış, istatistikler ve hızlı erişim.
- ⚽ **Takım Yönetimi**: Takım ekleme, silme, düzenleme ve detaylı liste görünümü.
- 📅 **Fikstür Sistemi**: Otomatik veya manuel fikstür oluşturma ve hafta hafta takip.
- 🏆 **Lig Tablosu (Puan Durumu)**: Puan, galibiyet, mağlubiyet, beraberlik ve averaj hesaplamalarıyla güncel sıralama.
- ⏱️ **Canlı Maç Takibi**: Oynanan maçların anlık takibi ve sonuçların kaydedilmesi.
- 🎨 **Modern Arayüz**: CSS ile zenginleştirilmiş, kullanıcı dostu ve şık tasarım.

## 🛠️ Kullanılan Teknolojiler

- **Dil:** Java 25
- **Arayüz:** JavaFX (FXML & CSS)
- **Bağımlılık Yönetimi:** Maven
- **Test:** JUnit 5

## ⚙️ Kurulum ve Çalıştırma

### Gereksinimler

- JDK 25 veya üzeri
- Maven

### Projeyi Çalıştırma

1. Projeyi bilgisayarınıza indirin:
   ```bash
   git clone <repository-url>
   cd Musabaka-Takip-Otomasyonu
   ```

2. Maven ile bağımlılıkları yükleyin ve uygulamayı başlatın:
   ```bash
   mvn clean javafx:run
   ```

## 📂 Proje Yapısı

- `src/main/java`: Kaynak kodlar (Controllers, Models, Application).
- `src/main/resources`: FXML dosyaları, resimler ve CSS stilleri.
- `league_data.dat`: Verilerin kalıcı olarak saklandığı veri dosyası.

## 📄 Lisans

Bu proje eğitim amaçlı geliştirilmiştir. Tüm hakları saklıdır.
