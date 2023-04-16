//
// Name                 Andrew McGowan
// Student ID           S2033682
// Programme of Study   Computing
//

package org.me.gcu.mdcw1;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EarthquakeXmlParser {

    public static List<Earthquake> parseXmlData(String xmlData) {
        List<Earthquake> earthquakes = new ArrayList<>();
        Earthquake currentEarthquake = null;
        boolean insideItem = false;

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();

            xpp.setInput(new StringReader(xmlData));
            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    String tagName = xpp.getName();

                    if (tagName.equalsIgnoreCase("item")) {
                        insideItem = true;
                        currentEarthquake = new Earthquake();
                    } else if (insideItem) {
                        if (tagName.equalsIgnoreCase("title")) {
                            String titleText = xpp.nextText();
                            currentEarthquake.setTitle(titleText);

                            // Extract magnitude from the title
                            Pattern magnitudePattern = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+");
                            Matcher magnitudeMatcher = magnitudePattern.matcher(titleText);
                            if (magnitudeMatcher.find()) {
                                currentEarthquake.setMagnitude(Float.parseFloat(magnitudeMatcher.group(0)));
                            }
                        } else if (tagName.equalsIgnoreCase("description")) {
                            String text = xpp.nextText();
                            currentEarthquake.setDescription(text);

                            // Extract latitude and longitude from the description
                            Pattern latLngPattern = Pattern.compile("(-?\\d+\\.\\d+)");
                            Matcher latLngMatcher = latLngPattern.matcher(text);
                            if (latLngMatcher.find()) {
                                currentEarthquake.setLatitude(Double.parseDouble(latLngMatcher.group(1)));
                            }
                            if (latLngMatcher.find()) {
                                currentEarthquake.setLongitude(Double.parseDouble(latLngMatcher.group(1)));
                            }
                        } else if (tagName.equalsIgnoreCase("link")) {
                            currentEarthquake.setLink(xpp.nextText());
                        } else if (tagName.equalsIgnoreCase("pubDate")) {
                            currentEarthquake.setPubDate(xpp.nextText());
                        }
                    }
                } else if (eventType == XmlPullParser.END_TAG) {
                    String tagName = xpp.getName();
                    if (tagName.equalsIgnoreCase("item")) {
                        insideItem = false;
                        earthquakes.add(currentEarthquake);
                    }
                }

                eventType = xpp.next();
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

        return earthquakes;
    }
}
