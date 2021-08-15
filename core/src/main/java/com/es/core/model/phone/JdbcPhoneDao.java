package com.es.core.model.phone;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
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
    private static final String SELECT_PHONE_BY_ID = "select * from phones left join phone2color on phones.id = phone2color.phoneId left join colors on phone2color.colorId = colors.id where phones.id = ?";
    private static final String INSERT_PHONE = "insert into phones(id, brand, model, price, displaySizeInches, weightGr, lengthMm, widthMm, heightMm, announced, deviceType, os, displayResolution, pixelDensity, displayTechnology, backCameraMegapixels, frontCameraMegapixels, ramGb, internalStorageGb, batteryCapacityMah, talkTimeHours, standByTimeHours, bluetooth, positioning, imageUrl, description) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
    private static final String INSERT_PHONE2COLOR = "insert into phone2color(phoneId,colorId) values(?,?);";
    private static final String CHECK_IF_PHONE_EXISTS = "select count(*) from phones where id = ?";
    private static final String SELECT_ALL_PHONES_WITH_OFFSET_LIMIT = "select * from phones join stocks on phones.id = stocks.phoneId where stocks.stock > 0 and price is not null order by %s offset ? limit ?";
    private static final String SELECT_PHONE_COLORS = "select colorId, colors.code from colors join phone2color on colors.id = phone2color.colorId where phoneId = ?";
    private static final String SELECT_PHONES_AMOUNT = "select count(*) from phones join stocks on phones.id = stocks.phoneId where stocks.stock > 0 and price is not null";
    private static final String SELECT_SEARCHED_PHONES = "select * from phones join stocks on phones.id=stocks.phoneId where stocks.stock > 0 and price is not null and upper(phones.model) like upper(?) order by %s offset ? limit ?";
    private static final String SELECT_SEARCHED_PHONES_AMOUNT = "select count(*) from phones join stocks on phones.id = stocks.phoneId where stocks.stock > 0 and price is not null and upper(phones.model) like upper(?)";
    private static final String SELECT_PHONE_STOCK = "select stock from stocks where phoneId = ?";
    private static final String SELECT_RESERVED_PHONES = "select reserved from stocks where phoneId = ?";


    private PhoneExtractor phoneExtractor = new PhoneExtractor();
    private ColorMapper colorMapper = new ColorMapper();
    private RowMapper<Phone> phoneMapper= new BeanPropertyRowMapper<>(Phone.class);

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

    public List<Phone> findAll(String order, int offset, int limit) {
        if(offset < 0 ) throw new IllegalArgumentException();
        if(limit < 0) throw new IllegalArgumentException();
        return jdbcTemplate.query(String.format(SELECT_ALL_PHONES_WITH_OFFSET_LIMIT,order),
                new Object[]{offset,limit}, phoneMapper);
    }

    public List<Phone> findSearchedPhones(String search, String order, int offset, int limit){
        if(offset < 0 ) throw new IllegalArgumentException();
        if(limit < 0) throw new IllegalArgumentException();
        String sqlSearch = search + "%%";
        String query = String.format(SELECT_SEARCHED_PHONES, order);
        return jdbcTemplate.query(query, new Object[]{sqlSearch, offset, limit}, phoneMapper);
    }

    @Override
    public List<Color> getPhoneColors(Long id) {
        return jdbcTemplate.query(SELECT_PHONE_COLORS, new Object[]{id}, colorMapper);
    }

    @Override
    public int getPhonesAmount() {
        return jdbcTemplate.queryForObject(SELECT_PHONES_AMOUNT,Integer.class);
    }

    @Override
    public int getPhoneStockAmount(Long phoneId){
        Object[] args = new Object[]{phoneId};
        return jdbcTemplate.queryForObject(SELECT_PHONE_STOCK, args, Integer.class) -
                jdbcTemplate.queryForObject(SELECT_RESERVED_PHONES, args, Integer.class);
    }

    @Override
    public int getSearchedPhonesAmount(String search) {
        String sqlSearch = search + "%%";
        return jdbcTemplate.queryForObject(SELECT_SEARCHED_PHONES_AMOUNT,
                new Object[]{sqlSearch}, Integer.class);
    }
}

