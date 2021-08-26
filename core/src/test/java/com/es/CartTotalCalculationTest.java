package com.es;

import com.es.core.cart.CartService;
import com.es.core.cart.CartTotal;
import com.es.core.cart.HttpSessionCartService;
import com.es.core.model.phone.JdbcPhoneDao;
import com.es.core.model.phone.Phone;
import com.es.core.order.OutOfStockException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CartTotalCalculationTest {

    private static JdbcPhoneDao phoneDao = mock(JdbcPhoneDao.class);

    private CartService cartService = new HttpSessionCartService(phoneDao);


    @BeforeClass
    public static void setup(){
        when(phoneDao.getPhonesById(Arrays.asList(1L,2L))).thenReturn(
                new ArrayList<Phone>(){{
                    add(new Phone(){{
                        setPrice(new BigDecimal(10));
                        setId(1L);
                    }});
                    add(new Phone(){{
                        setPrice(new BigDecimal(20));
                        setId(2L);
                    }});
                }});
        when(phoneDao.getPhonesById(Arrays.asList(1L,2L,3L))).thenReturn(
                new ArrayList<Phone>(){{
                   add(new Phone(){{
                       setPrice(new BigDecimal(10));
                       setId(1L);
                   }});
                    add(new Phone(){{
                        setPrice(new BigDecimal(20));
                        setId(2L);
                    }});
                    add(new Phone(){{
                        setPrice(new BigDecimal(15));
                        setId(3L);
                    }});
                }});
        when(phoneDao.getPhonesById(Arrays.asList(1L,2L,3L,4L))).thenReturn(
                new ArrayList<Phone>(){{
                    add(new Phone(){{
                        setPrice(new BigDecimal(10));
                        setId(1L);
                    }});
                    add(new Phone(){{
                        setPrice(new BigDecimal(20));
                        setId(2L);
                    }});
                    add(new Phone(){{
                        setPrice(new BigDecimal(15));
                        setId(3L);
                    }});
                    add(new Phone(){{
                        setPrice(new BigDecimal(25));
                        setId(4L);
                    }});
                }});
        when(phoneDao.getPhoneStockAmount(1L)).thenReturn(4);
        when(phoneDao.getPhoneStockAmount(2L)).thenReturn(5);
        when(phoneDao.getPhoneStockAmount(3L)).thenReturn(7);
        when(phoneDao.getPhoneStockAmount(4L)).thenReturn(5);
        when(phoneDao.getPhoneStockAmount(5L)).thenReturn(0);
    }

    @Test
    public void testItemsAmount() throws OutOfStockException {
        cartService.addPhone(1L, 3L);
        cartService.addPhone(2L, 2L);
        cartService.addPhone(3L, 1L);

        Map<Long,Long> cartItemsCopy = new HashMap<>(cartService.getCart().getItems());
        CartTotal cartTotal = cartService.getCartTotal(cartItemsCopy);
        long actualItemsAmount = cartTotal.getItemsAmount();
        assertEquals(6, actualItemsAmount);
    }

    @Test
    public void testOverallPrice01() throws OutOfStockException {
        cartService.addPhone(1L, 3L);
        cartService.addPhone(2L, 2L);
        cartService.addPhone(3L, 1L);

        Map<Long,Long> cartItemsCopy = new HashMap<>(cartService.getCart().getItems());
        CartTotal cartTotal = cartService.getCartTotal(cartItemsCopy);
        long actualPrice = cartTotal.getOverallPrice().longValue();
        long expected = 85;
        assertEquals(expected, actualPrice);
    }

    @Test
    public void testOverallPrice02() throws OutOfStockException {
        cartService.addPhone(1L, 3L);
        cartService.addPhone(2L, 4L);

        Map<Long,Long> cartItemsCopy = new HashMap<>(cartService.getCart().getItems());
        CartTotal cartTotal = cartService.getCartTotal(cartItemsCopy);
        long actualPrice = cartTotal.getOverallPrice().longValue();
        long expected = 110;
        assertEquals(expected, actualPrice);
    }

    @Test
    public void testOverallPrice03() throws OutOfStockException {
        cartService.addPhone(1L, 1L);
        cartService.addPhone(2L, 5L);
        cartService.addPhone(3L, 4L);

        Map<Long,Long> cartItemsCopy = new HashMap<>(cartService.getCart().getItems());
        CartTotal cartTotal = cartService.getCartTotal(cartItemsCopy);
        long actualPrice = cartTotal.getOverallPrice().longValue();
        long expected = 170;
        assertEquals(expected, actualPrice);
    }

    @Test
    public void testItemsAmountAfterAdd() throws OutOfStockException {
        cartService.addPhone(1L, 2L);
        cartService.addPhone(2L, 3L);
        cartService.addPhone(3L, 7L);
        cartService.addPhone(4L, 5L);

        long expected = 17;
        Map<Long,Long> cartItemsCopy = new HashMap<>(cartService.getCart().getItems());
        CartTotal cartTotal = cartService.getCartTotal(cartItemsCopy);
        long actual = cartTotal.getItemsAmount();

        assertEquals(expected,actual);
    }

    @Test
    public void testOverallPriceAfterAdd() throws OutOfStockException{
        cartService.addPhone(1L, 2L);
        cartService.addPhone(2L, 3L);
        cartService.addPhone(3L, 7L);
        cartService.addPhone(4L, 5L);

        long expected = 310;
        Map<Long,Long> cartItemsCopy = new HashMap<>(cartService.getCart().getItems());
        CartTotal cartTotal = cartService.getCartTotal(cartItemsCopy);
        long actual = cartTotal.getOverallPrice().longValue();

        assertEquals(expected,actual);
    }

}
