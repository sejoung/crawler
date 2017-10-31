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
            if (element.attr("src").indexOf("enliple") > 0) {

                if (element.hasAttr("defer")) {
                    data.put("jsversion", "3.1");

                    if (data.get("3.1") != null) {
                        data.put("3.1", (int) data.get("3.1") + 1);
                    } else {
                        data.put("3.1", 1);
                    }
                } else {
                    data.put("jsversion", "3.0");

                    if (data.get("3.0") != null) {
                        data.put("3.0", (int) data.get("3.0") + 1);
                    } else {
                        data.put("3.0", 1);
                    }
                }

                getEnlipleAsyncCheck(element, scriptData);

            }

        }
        if (!scriptData.isEmpty()) {
            data.put("scriptData", scriptData);
        }

        int enlipleScriptCnt = 0;
        if (scriptData.get("동기") != null) {
            enlipleScriptCnt += (int) scriptData.get("동기");
        }
        if (scriptData.get("비동기") != null) {
            enlipleScriptCnt += (int) scriptData.get("비동기");
        }
        if (enlipleScriptCnt > 0) {
            data.put("enlipleScriptCnt", enlipleScriptCnt);

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

                if (map.get("scriptOnloadCnt") != null && (int) map.get("scriptOnloadCnt") > 0) {
                    map.put("type", map.get("type") + ", 2");
                    map.put("desc", "여러개");
                    break;
                } else {
                    map.put("type", "2");
                    map.put("scriptOnloadCnt", 1);
                }

            } else if (data.indexOf("$(window).load") > 0 || data.indexOf("$(window).on") > 0) {

                if (map.get("scriptOnloadCnt") != null && (int) map.get("scriptOnloadCnt") > 0) {
                    map.put("type", map.get("type") + ", 3");
                    map.put("desc", "여러개");
                    break;
                } else {
                    map.put("type", "3");
                    map.put("scriptOnloadCnt", 1);
                }
            } else if (data.indexOf("require load") > 0) {

                if (map.get("scriptOnloadCnt") != null && (int) map.get("scriptOnloadCnt") > 0) {
                    map.put("type", map.get("type") + ", 4");
                    map.put("desc", "여러개");
                    break;
                } else {
                    map.put("type", "4");
                    map.put("scriptOnloadCnt", 1);
                }
            } else if (map.get("type") == null || "".equals(map.get("type"))) {
                map.put("type", "없다");
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
