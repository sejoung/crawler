package kr.co.killers.crawler.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class EnlipleScript {

    private String scriptVersion;
    private boolean async;
    private String loadType;
    private int loadTypeCnt;
    
    public void setLoadTypeCnt() {
        setLoadTypeCnt(getLoadTypeCnt() + 1);  
    }
}
