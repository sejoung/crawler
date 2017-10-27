package kr.co.killers.crawler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

public class EnlipleMobilePageProcessor implements PageProcessor {

    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000).setUserAgent("Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1");

    public static final String TEST = "http://www.momnuri.com/?ref=mobion";

    /**
     * 1) enliple_min.js, enliple_min2.js 없으면 2.0 enliple_min.js 3.0 enliple_min2.js
     * 3.1 2) async="true" 비동기
     * 
     * @return
     */
    private static Map<String, String> getEnlipleMinVersion(Document document) {
        Elements elements = document.select("script[src]");

        Map<String, String> data = new HashMap<String, String>();
        data.put("jsversion", "no");
        for (Element element : elements) {
            if (element.attr("src").contains("enliple_min2")) {
                data.put("jsversion", "3.1");
                
                if (element.hasAttr("async")) {
                    if (element.attr("async").contains("true") || element.attr("async").contains("")) {
                        data.put("syncText", "비동기");
                    } else if (element.attr("async").contains("false")) {
                        data.put("syncText", "동기");
                    }

                } else {
                    data.put("syncText", "동기");
                }
            } else if (element.attr("src").contains("enliple_min")) {
                data.put("jsversion", "3.0");
                
                if (element.hasAttr("async")) {
                    if (element.attr("async").contains("true") || element.attr("async").contains("") || element.attr("async").contains("async")) {
                        data.put("syncText", "비동기");
                    } else if (element.attr("async").contains("false")) {
                        data.put("syncText", "동기");
                    }

                } else {
                    data.put("syncText", "동기");
                }
            }

        }
        return data;
    }

    @Override
    public void process(Page page) {

        Map<String, Object> map = new HashMap<String, Object>();

        String url = page.getUrl().get();

        if (TEST.equals(url)) {
            page.addTargetRequests(page.getHtml().links().all());
        }

        Document root = page.getHtml().getDocument();

        map.putAll(getEnlipleMinVersion(root));
        String jsversion = (String) map.get("jsversion");

        Elements els = root.select("script").not("src");
        map.put("url", url);

        if (root.body().hasAttr("onload")) {
            map.put("type", "1");
            map.put("cnt", 1);
        }

        if (!TEST.equals(page.getResultItems().get("url"))) {
            // skip this page
            page.setSkip(true);
        }

        for (Iterator<Element> iterator = els.iterator(); iterator.hasNext();) {
            Element element = iterator.next();

             String data = element.data();
            //String data = element.data().replaceAll("(?:/\\*(?:[^*]|(?:\\*+[^*/]))*\\*+/)|(?://.*)", "");
            if (data.indexOf("window.attachEvent(\"onload\"") > 0 || data.indexOf("window.onload") > 0 || data.indexOf("window.addEventListener(\"onload\"") > 0 || data.indexOf("window.attachEvent('onload'") > 0  || data.indexOf("window.addEventListener('onload'") > 0) {

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
            }

            if ("no".equals(jsversion)) {
                if (data.indexOf("log.dreamsearch.or.kr/servlet/rf") > 0) {
                    map.put("jsversion", "2.0");

                } else {
                    map.put("jsversion", "없다");
                }
            }

        }
         System.out.println(map);
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {

        Spider.create(new EnlipleMobilePageProcessor()).addUrl(TEST).thread(10).run();

    }
}
