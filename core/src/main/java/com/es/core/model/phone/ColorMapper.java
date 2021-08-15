package com.es.core.model.phone;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ColorMapper implements RowMapper<Color> {
    @Override
    public Color mapRow(ResultSet rs, int i) throws SQLException {
        long id = rs.getLong("colorId");
        String code = rs.getString("code");

        return new Color(id, code);
    }
}
