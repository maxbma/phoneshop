package com.es.core.model.phone;

import java.util.List;
import java.util.Optional;

public interface PhoneDao {
    boolean exists(Long id);
    Optional<Phone> get(Long key);
    void save(Phone phone);
    List<Phone> findAll(String order, int offset, int limit);
    List<Phone> getPhonesById(List<Long> idList);
    int getPhonesAmount();
    int getSearchedPhonesAmount(String search);
    List<Phone> findSearchedPhones(String search, String order, int offset, int limit);
}
