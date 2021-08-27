package com.es.core.model.phone;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class PhoneExtractor implements ResultSetExtractor<List<Phone>> {

    private RowMapper<Phone> phoneMapper = new BeanPropertyRowMapper<>(Phone.class);
    private ColorMapper colorMapper = new ColorMapper();

    @Override
    public List<Phone> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Long, Phone> phoneMap = new LinkedHashMap<>();
        while(rs.next()){
            long id = rs.getLong("id");
            Phone phone = phoneMap.get(id);
            if(phone == null){
                phone = phoneMapper.mapRow(rs, rs.getRow());
                phone.setId(id);
                phoneMap.put(id, phone);
            }

            String colorCode = rs.getString("code");
            long colorId = rs.getLong("colorId");

            if(colorId != 0 && colorCode != null) {
                Color color = colorMapper.mapRow(rs, rs.getRow());
                phone.getColors().add(color);
            }
        }
        return new ArrayList<>(phoneMap.values());
    }
}
