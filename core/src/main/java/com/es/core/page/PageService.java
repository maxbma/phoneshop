package com.es.core.page;

public interface PageService {
    int PAGE_LIMIT = 10;
    int getPagesAmount(String search);
    int[] getPagesNumeration(int page, int total);
}
