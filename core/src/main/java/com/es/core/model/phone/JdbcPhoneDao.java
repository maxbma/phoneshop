package com.es.core.model.phone;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class JdbcPhoneDao implements PhoneDao{
    private static final String SELECT_ALL_PHONES_WITH_OFFSET_LIMIT = "select * from ( select * from phones offset ? limit ?) a left join phone2color on a.id = phone2color.phoneId left join colors on phone2color.colorId = colors.id";
    private static final String SELECT_PHONE_BY_ID = "select * from phones left join phone2color on phones.id = phone2color.phoneId left join colors on phone2color.colorId = colors.id where phones.id = ?";
    private static final String INSERT_PHONE = "insert into phones(id, brand, model, price, displaySizeInches, weightGr, lengthMm, widthMm, heightMm, announced, deviceType, os, displayResolution, pixelDensity, displayTechnology, backCameraMegapixels, frontCameraMegapixels, ramGb, internalStorageGb, batteryCapacityMah, talkTimeHours, standByTimeHours, bluetooth, positioning, imageUrl, description) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
    private static final String INSERT_PHONE2COLOR = "insert into phone2color(phoneId,colorId) values(?,?);";
    private static final String CHECK_IF_PHONE_EXISTS = "select count(*) from phones where id = ?";

    private PhoneExtractor phoneExtractor = new PhoneExtractor();
    private SimpleJdbcInsert jdbcInsert;
    @Resource
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    private void initJdbcInsert(){
        jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName("phones")
                .usingGeneratedKeyColumns("id")
                .compile();
    }
    @Override
    public boolean exists(Long id) {
        Long result = jdbcTemplate.queryForObject(CHECK_IF_PHONE_EXISTS, new Object[]{id}, Long.class);
        return result.longValue() != 0;
    }

    public Optional<Phone> get(final Long key) {
        List<Phone> phones = jdbcTemplate.query(SELECT_PHONE_BY_ID, new Object[] {key}, phoneExtractor);
        if(phones.isEmpty()) return Optional.empty();
        return Optional.of(phones.get(0));
    }

    @Transactional
    public void save(final Phone phone) {

        Object[] args = new Object[]{
                phone.getId(), phone.getBrand(), phone.getModel(), phone.getPrice(), phone.getDisplaySizeInches(),
                phone.getWeightGr(), phone.getLengthMm(), phone.getWidthMm(), phone.getHeightMm(),
                phone.getAnnounced(), phone.getDeviceType(), phone.getOs(), phone.getDisplayResolution(),
                phone.getPixelDensity(), phone.getDisplayTechnology(), phone.getBackCameraMegapixels(),
                phone.getFrontCameraMegapixels(), phone.getRamGb(), phone.getInternalStorageGb(),
                phone.getBatteryCapacityMah(), phone.getTalkTimeHours(), phone.getStandByTimeHours(),
                phone.getBluetooth(), phone.getPositioning(), phone.getImageUrl(), phone.getDescription()
        };

        if(phone.getId() == null){
            Long id = (jdbcInsert.executeAndReturnKey(new BeanPropertySqlParameterSource(phone))).longValue();
            phone.setId(id);
        } else {
            if(exists(phone.getId())) throw new IllegalArgumentException("Phone with such id already exists");
            jdbcTemplate.update(INSERT_PHONE,args);
        }

        long phoneId = phone.getId();
        List<Object[]> batchArgs = new ArrayList<>();
        for(Color color: phone.getColors()){
            Object[] values = new Object[]{
                    phoneId, color.getId()
            };
            batchArgs.add(values);
        }
        jdbcTemplate.batchUpdate(INSERT_PHONE2COLOR, batchArgs);
    }

    public List<Phone> findAll(int offset, int limit) {
        if(offset < 0 ) throw new IllegalArgumentException();
        if(limit < 0) throw new IllegalArgumentException();
        return jdbcTemplate.query(SELECT_ALL_PHONES_WITH_OFFSET_LIMIT,
                new Object[] {offset, limit}, phoneExtractor);
    }
}
