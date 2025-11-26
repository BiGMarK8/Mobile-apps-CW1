package com.example.gittins_mark_s2429709.worker;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.gittins_mark_s2429709.R;
import com.example.gittins_mark_s2429709.model.CurrencyItem;
import com.example.gittins_mark_s2429709.model.CurrencyRepository;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A background worker responsible for fetching, parsing, and storing currency exchange rate data
 */
public class CurrencyWorker extends Worker {

    // The RSS feed URL for fetching GBP exchange rates
    private static final String FEED_URL = "https://www.fx-exchange.com/gbp/rss.xml";

    // A static map to associate three-letter currency codes with their corresponding flag drawable resources.
    private static final Map<String, Integer> FLAG_MAP = new HashMap<>();
    static {
        // Initialize the map with known currency codes and their flag IDs.
        FLAG_MAP.put("aud", R.drawable.au);
        FLAG_MAP.put("usd", R.drawable.us);
        FLAG_MAP.put("gbp", R.drawable.gb);
        FLAG_MAP.put("cad", R.drawable.ca);
        FLAG_MAP.put("chf", R.drawable.cn);
        FLAG_MAP.put("aed", R.drawable.ae);
        FLAG_MAP.put("ars", R.drawable.ar);
        FLAG_MAP.put("uyu", R.drawable.uy);
        FLAG_MAP.put("bhd", R.drawable.bh);
        FLAG_MAP.put("bnd", R.drawable.bn);
        FLAG_MAP.put("bob", R.drawable.bo);
        FLAG_MAP.put("brl", R.drawable.br);
        FLAG_MAP.put("bwp", R.drawable.bw);
        FLAG_MAP.put("clp", R.drawable.cl);
        FLAG_MAP.put("eur", R.drawable.eu);
        FLAG_MAP.put("jpy", R.drawable.jp);
        FLAG_MAP.put("rub", R.drawable.ru);

    }

    private int getFlagResource(String code) {
        Integer resId = FLAG_MAP.get(code.toLowerCase());
        // Fallback to a default flag if the code is not in the map.
        if (resId == null) {
            resId = R.drawable.defaultflag;
        }
        return resId;
    }

    public CurrencyWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    /**
     * The main entry point for the worker. This method is executed on a background thread
     */
    @NonNull
    @Override
    public Result doWork() {
        try {
            // Download the raw XML from the feed.
            String xml = downloadXml();
            // Parse the XML into a list of CurrencyItem objects
            List<CurrencyItem> parsed = parseXml(xml);
            // Update the repository with the new data
            CurrencyRepository.getInstance().updateRates(parsed);
            // Indicate that the work finished successfully.
            return Result.success();

        } catch (Exception e) {
            Log.e("CurrencyWorker", "Error in Worker: ", e);
            // If any step fails, tell WorkManager to retry the job later.
            return Result.retry();
        }
    }

    private String downloadXml() throws Exception {
        URL url = new URL(FEED_URL);
        URLConnection conn = url.openConnection();
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        StringBuilder sb = new StringBuilder();
        String line;

        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();

        // Clean the downloaded content to ensure it is valid XML.
        return cleanXml(sb.toString());
    }

    /**
     * Cleans the raw string to create a well-formed XML document
     */
    private String cleanXml(String raw) {
        int start = raw.indexOf("<?");
        int end   = raw.indexOf("</rss>");

        return raw.substring(start, end + 6);
    }

    /**
     * Parses the XML string into a list of CurrencyItem objects using XmlPullParser
     */
    private List<CurrencyItem> parseXml(String xml) throws Exception {
        List<CurrencyItem> list = new ArrayList<>();

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();
        xpp.setInput(new StringReader(xml));

        boolean insideItem = false;
        CurrencyItem current = null;
        int event = xpp.getEventType();

        // Loop through the XML document until the end is reached.
        while (event != XmlPullParser.END_DOCUMENT) {
            if (event == XmlPullParser.START_TAG) {
                String tag = xpp.getName();

                if (tag.equalsIgnoreCase("item")) {
                    // Start of a new currency item.
                    insideItem = true;
                    current = new CurrencyItem();

                } else if (insideItem && tag.equalsIgnoreCase("title")) {
                    current.title = xpp.nextText();
                    // Extract the 3-letter currency code from the title string.
                    int s = current.title.lastIndexOf('(');
                    int e = current.title.lastIndexOf(')');
                    if (s != -1 && e != -1) {
                        current.code = current.title.substring(s + 1, e);
                    } else {
                        current.code = current.title.substring(0, 3); // Fallback
                    }
                    current.flagId = getFlagResource(current.code);

                } else if (insideItem && tag.equalsIgnoreCase("description")) {
                    current.description = xpp.nextText();
                    // Extract the numeric exchange rate from the description string.
                    String[] parts = current.description.split("=");
                    if (parts.length > 1) {
                        String cleaned = parts[1].replaceAll("[^0-9.]", "");
                        current.rate = Double.parseDouble(cleaned);
                    }

                } else if (insideItem && tag.equalsIgnoreCase("pubDate")) {
                    current.pubDate = xpp.nextText();
                }

            } else if (event == XmlPullParser.END_TAG) {
                // End of an item tag; add the completed object to the list.
                if (xpp.getName().equalsIgnoreCase("item") && current != null) {
                    list.add(current);
                    insideItem = false;
                }
            }
            // Move to the next XML event.
            event = xpp.next();
        }

        return list;
    }
}
