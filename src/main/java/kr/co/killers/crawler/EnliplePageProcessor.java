package kr.co.killers.crawler;

import java.util.HashMap;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import kr.co.killers.crawler.util.CrawlerUtil;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

public class EnliplePageProcessor implements PageProcessor {

    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000).setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");

    public static final String TEST = "http://www.cocovill.com";

    @Override
    public void process(Page page) {

        Map<String, Object> map = new HashMap<String, Object>();

        String url = page.getUrl().get();
        page.putField("url", url);
        if (TEST.equals(url)) {
            page.addTargetRequests(page.getHtml().links().all());
        }

        Document root = page.getHtml().getDocument();

        map.putAll(CrawlerUtil.getEnlipleMinVersion(root));
        String jsversion = (String) map.get("jsversion");

        Elements els = root.select("script").not("src");
        map.put("url", url);

        if (root.body().hasAttr("onload")) {
            map.put("type", "1");
            map.put("scriptOnloadCnt", 1);
        }

        if (!TEST.equals(page.getResultItems().get("url"))) {
            // skip this page
            page.setSkip(true);
        }

        CrawlerUtil.getEnlipleScriptType(map, els, jsversion);

        System.out.println(map);
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {

        Spider.create(new EnliplePageProcessor()).addUrl(TEST).thread(10).run();

    }
}
