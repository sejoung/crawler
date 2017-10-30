package kr.co.killers.crawler.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CrawlerUtil {
    public static Map<String, Object> getEnlipleMinVersion(Document document) {
        Elements elements = document.select("script[src]");

        Map<String, Object> data = new HashMap<String, Object>();
        Map<String, Object> scriptData = new HashMap<String, Object>();

        data.put("jsversion", "no");

        for (Element element : elements) {
            if (element.attr("src").contains("enliple_min2")) {
                data.put("jsversion", "3.1");

                getEnlipleAsyncCheck(element, scriptData);

            } else if (element.attr("src").contains("enliple_min")) {
                data.put("jsversion", "3.0");
                getEnlipleAsyncCheck(element, scriptData);

            }

            data.put("scriptData", scriptData);
        }
        return data;
    }

    public static void getEnlipleAsyncCheck(Element element, Map<String, Object> scriptData) {

        if (element.hasAttr("async")) {
            if (element.attr("async").contains("true") || element.attr("async").contains("") || element.attr("async").contains("async")) {

                if (scriptData.get("비동기") != null) {
                    scriptData.put("비동기", (int) scriptData.get("비동기") + 1);
                } else {
                    scriptData.put("비동기", 1);
                }
            } else if (element.attr("async").contains("false")) {

                if (scriptData.get("동기") != null) {
                    scriptData.put("동기", (int) scriptData.get("동기") + 1);
                } else {
                    scriptData.put("동기", 1);
                }

            }

        } else {

            if (scriptData.get("동기") != null) {
                scriptData.put("동기", (int) scriptData.get("동기") + 1);
            } else {
                scriptData.put("동기", 1);
            }

        }
    }

    public static void getEnlipleScriptType(Map<String, Object> map, Elements els, String jsversion) {
        for (Iterator<Element> iterator = els.iterator(); iterator.hasNext();) {
            Element element = iterator.next();

            String data = element.data();
            // String data =
            // element.data().replaceAll("(?:/\\*(?:[^*]|(?:\\*+[^*/]))*\\*+/)|(?://.*)",
            // "");
            if (data.indexOf("window.attachEvent(\"onload\"") > 0 || data.indexOf("window.onload") > 0 || data.indexOf("window.addEventListener(\"onload\"") > 0 || data.indexOf("window.attachEvent('onload'") > 0 || data.indexOf("window.addEventListener('onload'") > 0) {

                if (map.get("cnt") != null && (int) map.get("cnt") > 0) {
                    map.put("type", map.get("type") + ", 2");
                    map.put("desc", "여러개");
                    break;
                } else {
                    map.put("type", "2");
                    map.put("cnt", 1);
                }

            } else if (data.indexOf("$(window).load") > 0 || data.indexOf("$(window).on") > 0) {

                if (map.get("cnt") != null && (int) map.get("cnt") > 0) {
                    map.put("type", map.get("type") + ", 3");
                    map.put("desc", "여러개");
                    break;
                } else {
                    map.put("type", "3");
                    map.put("cnt", 1);
                }
            } else if (data.indexOf("require load") > 0) {

                if (map.get("cnt") != null && (int) map.get("cnt") > 0) {
                    map.put("type", map.get("type") + ", 4");
                    map.put("desc", "여러개");
                    break;
                } else {
                    map.put("type", "4");
                    map.put("cnt", 1);
                }
            } else {
                map.put("type", "없다");
                map.put("cnt", 1);
            }

            if ("no".equals(jsversion)) {
                if (data.indexOf("log.dreamsearch.or.kr/servlet/rf") > 0) {
                    map.put("jsversion", "2.0");

                } else {
                    map.put("jsversion", "없다");
                }
            }

        }
    }
}
