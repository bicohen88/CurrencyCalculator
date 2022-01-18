package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Datasource {
    private ObservableList<Currency> fullData;
    private Currency base;
    private String updateDate;
    private String updateTime;
    private static Datasource instance = new Datasource();
    private static String key = "Enter your access key here";
    private static String apiUrl = "http://api.exchangeratesapi.io/v1/latest?access_key=" + key + "&base=";
    HashSet<String> defaultList;
    private ObservableList<Currency> currencies;
    private ReentrantLock lock = new ReentrantLock();

    private Datasource() {

    }

    public static Datasource getInstance() {
        return instance;
    }

    public ObservableList<Currency> getFullData() {
        return fullData;
    }

    public ObservableList<Currency> getCurrencies() {
        return currencies;
    }

    public Currency getBase() {
        return base;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void open() {

        File file = new File("currencies.txt");
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String s = reader.readLine();
                this.base = new Currency(s.substring(0, 3), 1f);
                this.defaultList = new HashSet<>(Arrays.asList(s.substring(4, s.length() - 1).split(", ")));
            } catch (IOException e) {

            }
        } else {
            this.defaultList = new HashSet<>(List.of("AED", "EUR", "USD", "CAD", "INR"));
            this.base = new Currency("EUR", 1.0f);
        }
        this.fullData = FXCollections.observableArrayList();
        update();
        this.currencies = FXCollections.observableArrayList();
        for (Currency c : fullData) {
            if (defaultList.contains(c.toString())) {
                currencies.add(c);
            }
        }
    }

    public boolean update() {
        String result = "";
        // Sample download result
//        result = "{\"success\":true,\"timestamp\":1626183123,\"base\":\"EUR\",\"date\":\"2021-07-13\",\"rates\":" +
//                "{\"AED\":4.336723,,\"AFN\":95.037279,\"ALL\":122.756577,\"AMD\":585.838005,\"ANG\":2.120811,\"AOA\":760.061064,\"ARS\":113.662305,\"AUD\":1.580956,\"AWG\":2.127697,\"AZN\":2.006693,\"BAM\":1.958687,\"BBD\":2.385579,\"BDT\":100.198189,\"BGN\":1.957369,\"BHD\":0.445605,\"BIF\":2347.559354,\"BMD\":1.182054,\"BND\":1.601743,\"BOB\":8.158299,\"BRL\":6.00188,\"BSD\":1.181523,\"BTC\":3.5997727e-5,\"BTN\":88.058936,\"BWP\":13.098705,\"BYN\":3.024799,\"BYR\":23168.259485,\"BZD\":2.381574,\"CAD\":1.477739,\"CDF\":2369.431797,\"CHF\":1.083075,\"CLF\":0.032075,\"CLP\":884.921346,\"CNY\":7.632517,\"COP\":4492.739233,\"CRC\":732.648586,\"CUC\":1.182054,\"CUP\":31.324432,\"CVE\":110.817182,\"CZK\":25.637584,\"DJF\":210.33589,\"DKK\":7.437188,\"DOP\":67.489574,\"DZD\":158.920529,\"EGP\":18.534979,\"ERN\":17.735834,\"ETB\":51.992663,\"EUR\":1,\"FJD\":2.44981,\"FKP\":0.854343,\"GBP\":0.851493,\"GEL\":3.717525,\"GGP\":0.854343,\"GHS\":7.0155,\"GIP\":0.854343,\"GMD\":60.461741,\"GNF\":11613.681166,\"GTQ\":9.153651,\"GYD\":247.190534,\"HKD\":9.180955,\"HNL\":28.34536,\"HRK\":7.493158,\"HTG\":111.652793,\"HUF\":358.911793,\"IDR\":17115.847208,\"ILS\":3.859832,\"IMP\":0.854343,\"INR\":88.088676,\"IQD\":1726.389948,\"IRR\":49770.385728,\"ISK\":146.100378,\"JEP\":0.854343,\"JMD\":181.384554,\"JOD\":0.838081,\"JPY\":130.146554,\"KES\":127.603447,\"KGS\":100.144917,\"KHR\":4826.326713,\"KMF\":493.920957,\"KPW\":1063.848657,\"KRW\":1355.472924,\"KWD\":0.355385,\"KYD\":0.984636,\"KZT\":504.401805,\"LAK\":11235.424022,\"LBP\":1810.906899,\"LKR\":235.121119,\"LRD\":202.780767,\"LSL\":17.399939,\"LTL\":3.490299,\"LVL\":0.715012,\"LYD\":5.336986,\"MAD\":10.595346,\"MDL\":21.284654,\"MGA\":4450.433667,\"MKD\":61.631896,\"MMK\":1944.770949,\"MNT\":3366.625376,\"MOP\":9.453468,\"MRO\":421.993094,\"MUR\":50.88587,\"MVR\":18.215268,\"MWK\":945.64329,\"MXN\":23.534353,\"MYR\":4.954576,\"MZN\":75.154527,\"NAD\":17.399751,\"NGN\":485.824013,\"NIO\":41.619953,\"NOK\":10.319019,\"NPR\":140.895692,\"NZD\":1.680195,\"OMR\":0.455132,\"PAB\":1.181523,\"PEN\":4.701023,\"PGK\":4.138667,\"PHP\":59.326754,\"PKR\":188.451355,\"PLN\":4.574159,\"PYG\":8096.408814,\"QAR\":4.303799,\"RON\":4.927513,\"RSD\":117.702169,\"RUB\":87.634535,\"RWF\":1185.00919,\"SAR\":4.43314,\"SBD\":9.498263,\"SCR\":16.523148,\"SDG\":528.378401,\"SEK\":10.195819,\"SGD\":1.599709,\"SHP\":0.854343,\"SLL\":12121.963906,\"SOS\":691.501917,\"SRD\":25.129254,\"STD\":24350.105536,\"SVC\":10.338078,\"SYP\":1486.274277,\"SZL\":17.399584,\"THB\":38.573967,\"TJS\":13.475054,\"TMT\":4.14901,\"TND\":3.300884,\"TOP\":2.670495,\"TRY\":10.170015,\"TTD\":8.024003,\"TWD\":33.09527,\"TZS\":2733.948017,\"UAH\":32.245494,\"UGX\":4182.500281,\"USD\":1.182054,\"UYU\":51.909712,\"UZS\":12577.055747,\"VEF\":252758881467.2516,\"VND\":27189.656793,\"VUV\":130.166257,\"WST\":3.016195,\"XAF\":656.91791,\"XAG\":0.045011,\"XAU\":0.000649,\"XCD\":3.19456,\"XDR\":0.829213,\"XOF\":653.084226,\"XPF\":119.94891,\"YER\":295.927153,\"ZAR\":17.299633,\"ZMK\":10639.972143,\"ZMW\":26.78767,\"ZWL\":380.621688}}";
        try {
            URLConnection connection = new URL(apiUrl + this.base.toString()).openConnection();
            InputStream is = connection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            result = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!result.matches("^\\{\"success.*")) {
            return false;
        }
        Pattern pattern = Pattern.compile("timestamp\":(\\d*),\"base\":\"(.*?)\".*?date\":\"(.*?)\".*?rates\":\\{(.*)}}$");
        Matcher matcher = pattern.matcher(result);
        matcher.find();
        this.updateTime = matcher.group(1);
        String base = matcher.group(2);
        this.updateDate = matcher.group(3);
        String s = matcher.group(4);
        Pattern p = Pattern.compile("\"(\\D*)\":(\\d+\\.?\\d*)");
        Matcher m = p.matcher(s);
        lock.lock();
        System.out.println("update acquired lock");
        while (m.find()) {
            String name = m.group(1);
            Float price = Float.valueOf(m.group(2));
            if (name.equals(base)) {
                this.base = new Currency(name, price);
            }
            Currency c = new Currency(name, price);
            if (fullData.contains(c)) {
                fullData.get(fullData.indexOf(c)).setPrice(price);
            } else {
                fullData.add(c);
            }
        }
        lock.unlock();
        System.out.println("Update released lock " + lock.getHoldCount());
        System.out.println(new Date().toString());
        return true;
    }

    public void setBase(Currency newCurrency) {
        if (newCurrency != base) {
            float updatePrice = newCurrency.getPrice();
            lock.lock();
            for (Currency c : this.fullData) {
                c.setPrice(c.getPrice() / updatePrice);
            }
            this.base = newCurrency;
            lock.unlock();
        }

    }

    public void convert(float quantity) {
        lock.lock();
        System.out.println("Convert has the lock");
        for (Currency c : this.fullData) {
            c.setConverted(quantity);
        }
        lock.unlock();
        System.out.println("Convert has released the lock " + lock.getHoldCount());
    }
}
