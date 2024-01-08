package thkoeln.dungeon.player.core.domainprimitives.purchasing;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import thkoeln.dungeon.player.core.domainprimitives.DomainPrimitiveException;

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
        Assertions.assertNotEquals( m27_1, m28 );
    }

    @Test
    public void testValidation() {
        Assertions.assertThrows( DomainPrimitiveException.class, () -> {
            Money.from( null );
        });
        Assertions.assertThrows( DomainPrimitiveException.class, () -> {
            Money.from( -1 );
        });
    }

    @Test
    public void testGreater() {
        Assertions.assertTrue( m3.greaterEqualThan(m3b) );
        Assertions.assertFalse( m3.greaterThan(m3) );
        Assertions.assertTrue( m3.greaterEqualThan(m0) );
        Assertions.assertFalse( m0.greaterThan(m3) );
        Assertions.assertThrows( DomainPrimitiveException.class, () -> {
            m3.greaterEqualThan( null );
        });
    }


    @Test
    public void testDecrease() {
        Assertions.assertEquals( m3.decreaseBy(m0), m3b );
        Assertions.assertEquals( m3.decreaseBy(m17), m0 );
        Assertions.assertEquals( m3.decreaseBy(m3b), m0 );
        Assertions.assertEquals( m20.decreaseBy(m3), m17 );
        Assertions.assertThrows( DomainPrimitiveException.class, () -> {
            m3.decreaseBy( null );
        });
    }



    @Test
    public void testIncrease() {
        Assertions.assertEquals( m3.increaseBy(m0), m3b );
        Assertions.assertEquals( m17.increaseBy(m3), m20 );
        Assertions.assertThrows( DomainPrimitiveException.class, () -> {
            m3.increaseBy( null );
        });
    }

}
