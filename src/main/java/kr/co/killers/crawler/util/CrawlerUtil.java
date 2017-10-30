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
        data.put("jsversion", "no");
        for (Element element : elements) {
            if (element.attr("src").contains("enliple_min2")) {
                data.put("jsversion", "3.1");

                if (element.hasAttr("async")) {
                    if (element.attr("async").contains("true") || element.attr("async").contains("")) {
                        data.put("syncText", "비동기");

                        if (data.get("asyncCnt") != null) {
                            data.put("asyncCnt", (int) data.get("asyncCnt") + 1);
                        } else {
                            data.put("asyncCnt", 1);
                        }

                    } else if (element.attr("async").contains("false")) {
                        data.put("syncText", "동기");

                        if (data.get("syncCnt") != null) {
                            data.put("syncCnt", (int) data.get("syncCnt") + 1);
                        } else {
                            data.put("syncCnt", 1);
                        }

                    }

                } else {
                    data.put("syncText", "동기");

                    if (data.get("syncCnt") != null) {
                        data.put("syncCnt", (int) data.get("syncCnt") + 1);
                    } else {
                        data.put("syncCnt", 1);
                    }

                }
            } else if (element.attr("src").contains("enliple_min")) {
                data.put("jsversion", "3.0");

                if (element.hasAttr("async")) {
                    if (element.attr("async").contains("true") || element.attr("async").contains("") || element.attr("async").contains("async")) {
                        data.put("syncText", "비동기");
                        if (data.get("asyncCnt") != null) {
                            data.put("asyncCnt", (int) data.get("asyncCnt") + 1);
                        } else {
                            data.put("asyncCnt", 1);
                        }
                    } else if (element.attr("async").contains("false")) {
                        data.put("syncText", "동기");

                        if (data.get("syncCnt") != null) {
                            data.put("syncCnt", (int) data.get("syncCnt") + 1);
                        } else {
                            data.put("syncCnt", 1);
                        }

                    }

                } else {
                    data.put("syncText", "동기");

                    if (data.get("syncCnt") != null) {
                        data.put("syncCnt", (int) data.get("syncCnt") + 1);
                    } else {
                        data.put("syncCnt", 1);
                    }

                }
            }

        }
        return data;
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
