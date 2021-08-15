package com.es;

import com.es.core.model.phone.Color;
import com.es.core.model.phone.JdbcPhoneDao;
import com.es.core.model.phone.Phone;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@RunWith(SpringRunner.class)
@ContextConfiguration("classpath:context/testContext.xml")
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class JdbcProductDaoIntTest {

    private final static int PHONES_AMOUNT_IN_DB = 5;
    private final static String ORDER="id asc";

    @Autowired
    private JdbcPhoneDao phoneDao;

    @Test
    public void testPhonesAmount(){
        assertEquals(PHONES_AMOUNT_IN_DB,phoneDao.findAll(ORDER,0,Integer.MAX_VALUE).size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindWithNegativeOffset(){
        phoneDao.findAll(ORDER,-1, 5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindWithNegativeLimit(){
        phoneDao.findAll(ORDER,0, -5);
    }

    @Test
    public void testSavePhoneWithId(){
        Phone phone = new Phone();
        phone.setId(1005L);
        phone.setBrand("Xiaomi");
        phone.setModel("Redmi Note 7");
        phone.getColors().add(new Color(1002L, "Blue"));

        phoneDao.save(phone);
        Optional<Phone> actual = phoneDao.get(1005L);
        assertTrue(actual.isPresent());
        assertPhonesEqual(phone, actual.get());
    }

    @Test
    public void testSavePhoneWithoutId(){
        Phone phone = new Phone();
        phone.setBrand("Xiaomi");
        phone.setModel("Redmi Note 7");
        phone.getColors().add(new Color(1002L, "Blue"));
        phone.getColors().add(new Color(1003L, "Green"));

        phoneDao.save(phone);
        phone.setId(1005L);
        Optional<Phone> actual = phoneDao.get(1005L);
        assertTrue(actual.isPresent());
        assertPhonesEqual(phone, actual.get());
    }

    @Test
    public void testCheckExistingPhone(){
        assertTrue(phoneDao.exists(1002L));
    }

    @Test
    public void testCheckNonExistingPhone(){
        assertFalse(phoneDao.exists(1010L));
    }



    @Test(expected = IllegalArgumentException.class)
    public void testPhoneWithExistingId(){
        Phone phone = new Phone();
        phone.setId(1002L);
        phone.setBrand("Xiaomi");
        phone.setModel("Redmi Note 7");
        phone.getColors().add(new Color(1002L, "Blue"));

        phoneDao.save(phone);
    }

    @Test
    public void testGetExistingPhone(){
        Optional<Phone> actual = phoneDao.get(1002L);
        Phone expected = expectedPhone();
        assertTrue(actual.isPresent());
        assertPhonesEqual(expected, actual.get());
    }

    @Test
    public void testGetNonExistingPhone(){
        assertFalse(phoneDao.get(-28L).isPresent());
    }

    private void assertPhonesEqual(Phone phone1, Phone phone2){
        assertEquals(phone1.getId(), phone2.getId());
        assertEquals(phone1.getBrand(), phone2.getBrand());
        assertEquals(phone1.getModel(), phone2.getModel());
        assertEquals(phone1.getPrice(), phone2.getPrice());
        assertEquals(phone1.getDisplaySizeInches(), phone2.getDisplaySizeInches());
        assertEquals(phone1.getWeightGr(), phone2.getWeightGr());
        assertEquals(phone1.getLengthMm(), phone2.getLengthMm());
        assertEquals(phone1.getWidthMm(), phone2.getWidthMm());
        assertEquals(phone1.getHeightMm(), phone2.getHeightMm());
        assertEquals(phone1.getAnnounced(), phone2.getAnnounced());
        assertEquals(phone1.getDeviceType(), phone2.getDeviceType());
        assertEquals(phone1.getOs(), phone2.getOs());
        assertEquals(phone1.getColors(), phone2.getColors());
        assertEquals(phone1.getDisplayResolution(), phone2.getDisplayResolution());
        assertEquals(phone1.getPixelDensity(), phone2.getPixelDensity());
        assertEquals(phone1.getDisplayTechnology(), phone2.getDisplayTechnology());
        assertEquals(phone1.getBackCameraMegapixels(), phone2.getBackCameraMegapixels());
        assertEquals(phone1.getFrontCameraMegapixels(), phone2.getFrontCameraMegapixels());
        assertEquals(phone1.getRamGb(), phone2.getRamGb());
        assertEquals(phone1.getInternalStorageGb(), phone2.getInternalStorageGb());
        assertEquals(phone1.getBatteryCapacityMah(), phone2.getBatteryCapacityMah());
        assertEquals(phone1.getTalkTimeHours(), phone2.getTalkTimeHours());
        assertEquals(phone1.getStandByTimeHours(), phone2.getStandByTimeHours());
        assertEquals(phone1.getBluetooth(), phone2.getBluetooth());
        assertEquals(phone1.getPositioning(), phone2.getPositioning());
        assertEquals(phone1.getImageUrl(), phone2.getImageUrl());
        assertEquals(phone1.getDescription(), phone2.getDescription());
    }

    private Phone expectedPhone(){
        Phone phone = new Phone();
        phone.setId(1002L);
        phone.setBrand("ARCHOS");
        phone.setModel("ARCHOS 101 Internet Tablet");
        phone.setPrice(new BigDecimal("199.0"));
        phone.setDisplaySizeInches(new BigDecimal("10.1"));
        phone.setWeightGr(482);
        phone.setLengthMm(null);
        phone.setWidthMm(null);
        phone.setHeightMm(null);
        phone.setAnnounced(null);
        phone.setDeviceType("Tablet");
        phone.setOs("Android (2.2)");
        phone.setDisplayResolution("1024 x  600");
        phone.setPixelDensity(118);
        phone.setDisplayTechnology("TFT");
        phone.setBackCameraMegapixels(null);
        phone.setFrontCameraMegapixels(new BigDecimal("0.3"));
        phone.setRamGb(null);
        phone.setInternalStorageGb(new BigDecimal("8.0"));
        phone.setBatteryCapacityMah(null);
        phone.setTalkTimeHours(null);
        phone.setStandByTimeHours(null);
        phone.setBluetooth("2.1, EDR");
        phone.setPositioning(null);
        phone.setImageUrl("manufacturer/ARCHOS/ARCHOS 101 Internet Tablet.jpg");
        phone.setDescription("Archos 101 Internet Tablet is 10.1\" Android running slate with Wi-Fi connectivity. It has 1GHz processor, 0.3MP front camera, HDMI port, 8GB internal memory and supports up to 32GB microSD memory cards.");

        Set<Color> colors = new HashSet<Color>();
        colors.add(new Color(1000L,"Black"));
        colors.add(new Color(1003L,"Green"));
        phone.setColors(colors);
        return phone;
    }
}
