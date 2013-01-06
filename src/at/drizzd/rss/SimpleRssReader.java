/*
 * RSS reader copied from "Andy Res"'s blog:
 * https://androidresearch.wordpress.com/2012/01/21/creating-a-simple-rss-application-in-android/
 */

package at.drizzd.rss;

import java.util.ArrayList;
import java.net.URL;
import java.io.InputStream;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class SimpleRssReader {
    public static void loadRssFeed(URL url, ArrayList headlines, ArrayList links)
            throws XmlPullParserException, IOException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(false);
        XmlPullParser xpp = factory.newPullParser();

        // We will get the XML from an input stream
        xpp.setInput(getInputStream(url), "UTF_8");

        /* We will parse the XML content looking for the "<title>" tag which appears inside the "<item>" tag.
         * However, we should take in consideration that the rss feed name also is enclosed in a "<title>" tag.
         * As we know, every feed begins with these lines: "<channel><title>Feed_Name</title>...."
         * so we should skip the "<title>" tag which is a child of "<channel>" tag,
         * and take in consideration only "<title>" tag which is a child of "<item>"
         *
         * In order to achieve this, we will make use of a boolean variable.
         */
        boolean insideItem = false;

        // Returns the type of current event: START_TAG, END_TAG, etc..
        int eventType = xpp.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {

                if (xpp.getName().equalsIgnoreCase("item")) {
                    insideItem = true;
                } else if (xpp.getName().equalsIgnoreCase("title")) {
                    if (insideItem)
                        headlines.add(xpp.nextText()); //extract the headline
                } else if (xpp.getName().equalsIgnoreCase("link")) {
                    if (insideItem)
                        links.add(xpp.nextText()); //extract the link of article
                }
            }else if(eventType==XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")){
                insideItem=false;
            }

            eventType = xpp.next(); //move to next element
        }
    }

    public static InputStream getInputStream(URL url) {
       try {
           return url.openConnection().getInputStream();
       } catch (IOException e) {
           return null;
         }
    }
};
