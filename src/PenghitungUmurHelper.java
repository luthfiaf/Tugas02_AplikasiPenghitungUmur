import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.Period;
import java.util.function.Supplier;
import javax.swing.JTextArea;
import org.json.JSONArray;
import org.json.JSONObject;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author ASUS
 */

public class PenghitungUmurHelper {
    // Menghitung umur secara detail (tahun, bulan, hari)
public String hitungUmurDetail(LocalDate lahir, LocalDate sekarang) {
Period period = Period.between(lahir, sekarang);
return period.getYears() + " tahun, " + period.getMonths() + "bulan, " + period.getDays() + " hari";
}
// Menghitung hari ulang tahun berikutnya
public LocalDate hariUlangTahunBerikutnya(LocalDate lahir, LocalDate
sekarang) {
LocalDate ulangTahunBerikutnya =
lahir.withYear(sekarang.getYear());
if (!ulangTahunBerikutnya.isAfter(sekarang)) {
ulangTahunBerikutnya = ulangTahunBerikutnya.plusYears(1);
}
return ulangTahunBerikutnya;
}
// Menerjemahkan teks hari ke bahasa Indonesia
public String getDayOfWeekInIndonesian(LocalDate date) {
switch (date.getDayOfWeek()) {
case MONDAY:
return "Senin";
case TUESDAY:
return "Selasa";
case WEDNESDAY:
return "Rabu";
case THURSDAY:
return "Kamis";
case FRIDAY:
return "Jumat";
case SATURDAY:
return "Sabtu";
case SUNDAY:
return "Minggu";
default:
return "";
}
}
 public void getPeristiwaBarisPerBaris(LocalDate tanggal, JTextArea txtAreaPeristiwa, Supplier<Boolean> shouldStop) {
        try {
            String urlString = "https://byabbe.se/on-this-day/" + tanggal.getMonthValue() + "/" + tanggal.getDayOfMonth() + "/events.json";
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            conn.disconnect();

            JSONObject json = new JSONObject(content.toString());
            JSONArray events = json.getJSONArray("events");
            for (int i = 0; i < events.length(); i++) {
                JSONObject event = events.getJSONObject(i);
                String year = event.getString("year");
                String description = event.getString("description");
                String translatedDescription = translateToIndonesian(description);
                String peristiwa = year + ": " + translatedDescription;
                
                javax.swing.SwingUtilities.invokeLater(() ->
                txtAreaPeristiwa.append(peristiwa + "\n"));
            }
        } catch (Exception e) {
            txtAreaPeristiwa.setText("Gagal mendapatkan data peristiwa.");
        }
    }
    
    public String translateToIndonesian(String text) {
        try {
            String encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8.toString());
            String urlString = "https://lingva.ml/api/v1/en/id/" + encodedText;
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            Thread.sleep(2000);

            StringBuilder content;
            try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                content = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
            }
            conn.disconnect();

            JSONObject json = new JSONObject(content.toString());
            String translation = json.getString("translation");
            
            return translation.replace("+", " ");
        } catch (Exception e) {
            return text + " (Gagal diterjemahkan)";
        }
    }   
}
