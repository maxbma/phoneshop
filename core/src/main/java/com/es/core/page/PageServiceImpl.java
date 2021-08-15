package com.es.core.page;


import com.es.core.model.phone.JdbcPhoneDao;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class PageServiceImpl implements PageService{
    private final static int PAGE_CAPACITY = 10;
    @Resource
    private JdbcPhoneDao phoneDao;

    public int getPagesAmount(String search){
        int phonesAmount;
        if(search == null){
            phonesAmount = phoneDao.getPhonesAmount();
        } else{
            phonesAmount = phoneDao.getSearchedPhonesAmount(search);
        }

        return phonesAmount/PAGE_CAPACITY + ((phonesAmount%10 == 0)?0:1);
    }

    public int[] getPagesNumeration(int page, int total){
        int[] result;
        if(total < PAGE_CAPACITY) result = new int[total];
        else result = new int[PAGE_CAPACITY];
        if(page <= PAGE_CAPACITY/2) {
            int limit = result.length;
            for (int i = 0; i < limit; i++) {
                result[i] = i + 1;
            }
        }
        else if(page>total-PAGE_CAPACITY){
            for(int i=0,k=PAGE_CAPACITY-1; i<PAGE_CAPACITY;i++,k--){
                result[i] = total - k;
            }
        } else {
            for(int i = 0, k = -4; i < PAGE_CAPACITY; i++, k++){
                result[i] = page + k;
            }
        }
        return result;
    }
}