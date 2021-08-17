package com.es.core.page;


import com.es.core.model.phone.JdbcPhoneDao;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class PageServiceImpl implements PageService{
    private final static int PAGES_DISPLAYED = 10;
    private final static int PAGES_BEFORE_CURRENT = PAGES_DISPLAYED /2 - 1;

    @Resource
    private JdbcPhoneDao phoneDao;

    public int getPagesAmount(String search){
        int phonesAmount;
        if(search == null){
            phonesAmount = phoneDao.getPhonesAmount();
        } else{
            phonesAmount = phoneDao.getSearchedPhonesAmount(search);
        }

        return phonesAmount/PAGE_LIMIT  + ((phonesAmount%10 == 0)?0:1);
    }

    public int[] getPagesNumeration(int page, int total){
        int[] result;
        if(total < PAGES_DISPLAYED) result = new int[total];
        else result = new int[PAGES_DISPLAYED];
        if(page <= PAGES_DISPLAYED/2) {
            int limit = result.length;
            for (int i = 0; i < limit; i++) {
                result[i] = i + 1;
            }
        }
        else if(page>total-PAGES_DISPLAYED){
            for(int i=0,k=PAGES_DISPLAYED-1; i<PAGES_DISPLAYED;i++,k--){
                result[i] = total - k;
            }
        } else {
            for(int i = 0, k = PAGES_BEFORE_CURRENT; i < PAGES_DISPLAYED; i++, k--){
                result[i] = page - k;
            }
        }
        return result;
    }
}