package parser.services.cache;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class PageUserCache {
    Map<Long,Integer> mapPage = new HashMap<>();
    AtomicInteger page;
    public void saveNumberPage(Long userId,int page){
        mapPage.put(userId,page);
    }

    public void deleteNumberPage(Long userId){
        mapPage.remove(userId);
    }

    public int getNumberPage(Long userId){
        if (mapPage.get(userId) == null){
            return 0;
        }
        return mapPage.get(userId);
    }

    public void saveCountPage(int pageCount){
        page = new AtomicInteger(pageCount);
    }

    public int getCountPage(){
        return  page.get();
    }
}
