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

public class EnlipleMobilePageProcessor implements PageProcessor {

    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000).setUserAgent("Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1");

    public static final String TEST = "http://www.momnuri.com/?ref=mobion";

    @Override
    public void process(Page page) {

        Map<String, Object> map = new HashMap<String, Object>();

        String url = page.getUrl().get();

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
            map.put("cnt", 1);
        }

        if (!TEST.equals(page.getResultItems().get("url"))) {
            // skip this page
            page.setSkip(true);
        }

        CrawlerUtil.getEnlipleScriptType(map,els,jsversion);
        
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
