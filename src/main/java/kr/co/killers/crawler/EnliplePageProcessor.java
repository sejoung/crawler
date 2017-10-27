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
import us.codecraft.webmagic.pipeline.JsonFilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

public class EnliplePageProcessor implements PageProcessor {

    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);

    public static final String TEST = "http://www.styletiba.com/m/index.html";

    /**
     * 1) enliple_min.js, enliple_min2.js 없으면 2.0 enliple_min.js 3.0 enliple_min2.js
     * 3.1 2) async="true" 비동기
     * 
     * @return
     */
    private static String getEnlipleMinVersion(Document document) {
        Elements elements = document.select("script[src]");
        String jsVersion = "2.0";
        for (Element element : elements) {
            if (element.attr("src").contains("enliple_min2")) {
                jsVersion = "3.1";
                if (element.hasAttr("async")) {
                    if (element.attr("async").contains("true") || element.attr("async").contains("")) {
                        jsVersion += " 비동기";
                    } else if (element.attr("async").contains("false")) {
                        jsVersion += " 동기";
                    }

                } else {
                    jsVersion += " 동기";
                }
            } else if (element.attr("src").contains("enliple_min")) {
                jsVersion = "3.0";
                if (element.hasAttr("async")) {
                    if (element.attr("async").contains("true") || element.attr("async").contains("")) {
                        jsVersion += " 비동기";
                    } else if (element.attr("async").contains("false")) {
                        jsVersion += " 동기";
                    }

                } else {
                    jsVersion += " 동기";
                }
            }

        }
        return jsVersion;
    }

    @Override
    public void process(Page page) {

        Map<String, Object> map = new HashMap<String, Object>();

        String url = page.getUrl().get();

        if (TEST.equals(url)) {
            page.addTargetRequests(page.getHtml().links().all());
        }

        map.put("scversion", getEnlipleMinVersion(page.getHtml().getDocument()));
        Elements els = page.getHtml().getDocument().select("script");

        map.put("url", url);

        if (page.getHtml().getDocument().body().hasAttr("onload")) {
            map.put("type", "1");
            map.put("cnt", 1);
        }

        if (!TEST.equals(page.getResultItems().get("url"))) {
            // skip this page
            page.setSkip(true);
        }

        for (Iterator<Element> iterator = els.iterator(); iterator.hasNext();) {
            Element element = iterator.next();

            String src = element.attr("src");
            String data = element.data();

            // String data = element.data().replaceAll("/\\*(?:.|[\\n\\r])*?\\*/", "");

            if (data.indexOf("window.attachEvent(\"onload\"") > 0 || data.indexOf("window.onload") > 0 || data.indexOf("window.addEventListener") > 0) {

       
                if (map.get("cnt") != null && (int) map.get("cnt") > 0) {
                    map.put("type", map.get("type")+", 2");
                    map.put("desc", "여러개");
                    
                    break;
                } else {
                    map.put("type", "2");
                    map.put("cnt", 1);
                }

            } else if (data.indexOf("$(window).load") > 0 || data.indexOf("$(window).on") > 0) {
     
                if (map.get("cnt") != null && (int) map.get("cnt") > 0) {
                    map.put("type", map.get("type")+", 3");
                    map.put("desc", "여러개");
                    break;
                } else {
                    map.put("type", "3");
                    map.put("cnt", 1);
                }
            } else if (data.indexOf("equire load") > 0) {
           
                if (map.get("cnt") != null && (int) map.get("cnt") > 0) {
                    map.put("type", map.get("type")+", 4");
                    map.put("desc", "여러개");
                    break;
                } else {
                    map.put("type", "4");
                    map.put("cnt", 1);
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

        Spider.create(new EnliplePageProcessor()).addUrl(TEST).addPipeline(new JsonFilePipeline("E:\\data\\webmagic")).thread(10).run();
    }
}
