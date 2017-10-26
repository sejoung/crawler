package kr.co.killers.crawler;

import java.util.Iterator;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.JsonFilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

public class EnliplePageProcessor implements PageProcessor {

    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);

    public static final String TEST = "http://www.gumzzi.co.kr";

    @Override
    public void process(Page page) {

        String url = page.getUrl().get();

        if (TEST.equals(url)) {
            page.addTargetRequests(page.getHtml().links().all());
        }

        Elements els = page.getHtml().getDocument().select("script");
        page.putField("url", url);
        System.out.println(page.getHtml().getDocument().body().attr("onload"));
        if (!TEST.equals(page.getResultItems().get("url"))) {
            // skip this page
            page.setSkip(true);
        }

        for (Iterator iterator = els.iterator(); iterator.hasNext();) {
            Element element = (Element) iterator.next();

            String src = element.attr("src");
            String data = element.data();
            boolean urlok = false;
            
            if (data.indexOf("window.attachEvent(\"onload\"") > 0||data.indexOf("$(window).load") > 0 || data.indexOf("window.onload") > 0 || data.indexOf("window.addEventListener") > 0 || data.indexOf("$(window).on") > 0 || data.indexOf("equire load") > 0) {
                System.out.println(element.data());
                urlok = true;
            }

            if (src.indexOf("enliple") > 0) {
                page.putField("src", src);
                System.out.println(src);
                urlok = true;

            }
            
            if(urlok) {
                System.out.println(url);

            }
        }

    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {

        // Spider.create(new
        // GithubRepoPageProcessor()).addUrl("https://github.com/sejoung").thread(5).run();
        Spider.create(new EnliplePageProcessor()).addUrl(TEST).addPipeline(new JsonFilePipeline("E:\\data\\webmagic")).thread(5).run();
    }
}
