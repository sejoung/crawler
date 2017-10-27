package kr.co.killers.crawler.pipeline;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.utils.FilePersistentBase;

public class ExcelPipeline extends FilePersistentBase implements Pipeline {
    
    public ExcelPipeline() {
        setPath("/data/webmagic");
    }
    
    public ExcelPipeline(String path) {
        setPath(path);
    }

    @Override
    public void process(ResultItems resultItems, Task task) {
        // TODO Auto-generated method stub
        
    }

}
