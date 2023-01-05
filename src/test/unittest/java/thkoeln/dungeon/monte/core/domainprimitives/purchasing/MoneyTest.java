package thkoeln.dungeon.monte.core.domainprimitives.purchasing;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import thkoeln.dungeon.monte.core.domainprimitives.DomainPrimitiveException;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MoneyTest {
    private Money m27_1, m27_2, m28, m0, m3, m3b, m17, m20;

    @BeforeEach
    public void setUp() {
        m27_1 = Money.from( 27 );
        m27_2 = Money.from( 27 );
        m28 = Money.from( 28 );
        m0 = Money.from( 0 );
        m3 = Money.from( 3 );
        m3b = Money.from( 3 );
        m17 = Money.from( 17 );
        m20 = Money.from( 20 );
    }

    @Test
    public void testTwoMoneyEqualAndUnequal() {
        Assertions.assertEquals( m27_1, m27_2 );
        Assert.assertNotEquals( m27_1, m28 );
    }

    @Test
    public void testValidation() {
        assertThrows( DomainPrimitiveException.class, () -> {
            Money.from( null );
        });
        assertThrows( DomainPrimitiveException.class, () -> {
            Money.from( -1 );
        });
    }

    @Test
    public void testGreater() {
        assertTrue( m3.greaterEqualThan(m3b) );
        assertFalse( m3.greaterThan(m3) );
        assertTrue( m3.greaterEqualThan(m0) );
        assertFalse( m0.greaterThan(m3) );
        Assert.assertThrows( DomainPrimitiveException.class, () -> {
            m3.greaterEqualThan( null );
        });
    }


    @Test
    public void testDecrease() {
        assertEquals( m3.decreaseBy(m0), m3b );
        assertEquals( m3.decreaseBy(m17), m0 );
        assertEquals( m3.decreaseBy(m3b), m0 );
        assertEquals( m20.decreaseBy(m3), m17 );
        Assert.assertThrows( DomainPrimitiveException.class, () -> {
            m3.decreaseBy( null );
        });
    }



    @Test
    public void testIncrease() {
        assertEquals( m3.increaseBy(m0), m3b );
        assertEquals( m17.increaseBy(m3), m20 );
        Assert.assertThrows( DomainPrimitiveException.class, () -> {
            m3.increaseBy( null );
        });
    }

}
