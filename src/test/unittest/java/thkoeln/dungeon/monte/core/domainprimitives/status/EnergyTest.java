package thkoeln.dungeon.monte.core.domainprimitives.status;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import thkoeln.dungeon.monte.core.domainprimitives.DomainPrimitiveException;

import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.*;

public class EnergyTest {
    private Energy e0, e3, e3b, e17, e20;

    @BeforeEach
    public void setUp() {
        e0 = Energy.from( 0 );
        e3 = Energy.from( 3 );
        e3b = Energy.from( 3 );
        e17 = Energy.from( 17 );
        e20 = Energy.from( 20 );
    }

    @Test
    public void testEqualAndUnequal() {
        assertEquals( e3, e3b );
        assertNotEquals( e3, e20 );
    }


    @Test
    public void testValidation() {
        Assert.assertThrows( DomainPrimitiveException.class, () -> {
            Energy.from( null );
        });
        Assert.assertThrows( DomainPrimitiveException.class, () -> {
            Energy.from( -1 );
        });
    }



    @Test
    public void testGreater() {
        assertTrue( e3.greaterEqualThan( e3b ) );
        assertFalse( e3.greaterThan( e3 ) );
        assertTrue( e3.greaterEqualThan( e0 ) );
        assertFalse( e0.greaterThan( e3 ) );
        Assert.assertThrows( DomainPrimitiveException.class, () -> {
            e3.greaterEqualThan( null );
        });
    }


    @Test
    public void testDecrease() {
        assertEquals( e3.decreaseBy( e0 ), e3b );
        assertEquals( e3.decreaseBy( e3b ), e0 );
        assertEquals( e20.decreaseBy( e3 ), e17 );
        Assert.assertThrows( DomainPrimitiveException.class, () -> {
            e3.decreaseBy( null );
        });
        Assert.assertThrows( DomainPrimitiveException.class, () -> {
            e3.decreaseBy( e17 );
        });
    }



    @Test
    public void testIncrease() {
        assertEquals( e3.increaseBy( e0 ), e3b );
        assertEquals( e17.increaseBy( e3 ), e20 );
        Assert.assertThrows( DomainPrimitiveException.class, () -> {
            e3.increaseBy( null );
        });
    }

}
