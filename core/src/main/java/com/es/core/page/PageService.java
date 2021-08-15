package com.es.core.page;

public interface PageService {
    int getPagesAmount(String search);
    int[] getPagesNumeration(int page, int total);
}
