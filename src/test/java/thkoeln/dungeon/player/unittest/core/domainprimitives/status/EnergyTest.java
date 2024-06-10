package thkoeln.dungeon.player.unittest.core.domainprimitives.status;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import thkoeln.dungeon.player.core.domainprimitives.DomainPrimitiveException;
import thkoeln.dungeon.player.core.domainprimitives.status.Energy;


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
        Assertions.assertEquals( e3, e3b );
        Assertions.assertNotEquals( e3, e20 );
        Assertions.assertEquals( Energy.zero(), e0 );
    }


    @Test
    public void testValidation() {
        Assertions.assertThrows( DomainPrimitiveException.class, () -> {
            Energy.from( null );
        });
        Assertions.assertThrows( DomainPrimitiveException.class, () -> {
            Energy.from( -1 );
        });
    }



    @Test
    public void testGreater() {
        Assertions.assertTrue( e3.greaterEqualThan( e3b ) );
        Assertions.assertFalse( e3.greaterThan( e3 ) );
        Assertions.assertTrue( e3.greaterEqualThan( e0 ) );
        Assertions.assertFalse( e0.greaterThan( e3 ) );
        Assertions.assertThrows( DomainPrimitiveException.class, () -> {
            e3.greaterEqualThan( null );
        });
    }


    @Test
    public void testDecrease() {
        Assertions.assertEquals( e3.decreaseBy( e0 ), e3b );
        Assertions.assertEquals( e3.decreaseBy( e3b ), e0 );
        Assertions.assertEquals( e20.decreaseBy( e3 ), e17 );
        Assertions.assertThrows( DomainPrimitiveException.class, () -> {
            e3.decreaseBy( null );
        });
        Assertions.assertThrows( DomainPrimitiveException.class, () -> {
            e3.decreaseBy( e17 );
        });
    }



    @Test
    public void testIncrease() {
        Assertions.assertEquals( e3.increaseBy( e0 ), e3b );
        Assertions.assertEquals( e17.increaseBy( e3 ), e20 );
        Assertions.assertThrows( DomainPrimitiveException.class, () -> {
            e3.increaseBy( null );
        });
    }




    @Test
    public void testLowerThanPercentage() {
        Assertions.assertTrue( e3.lowerThanPercentage( 16, e20 ) );
        Assertions.assertFalse( e3.lowerThanPercentage( 15, e20 ) );
        Assertions.assertThrows( DomainPrimitiveException.class, () -> {
            e3.lowerThanPercentage( -1, e20 );
        });
        Assertions.assertThrows( DomainPrimitiveException.class, () -> {
            e3.lowerThanPercentage( 101, e20 );
        });
        Assertions.assertThrows( DomainPrimitiveException.class, () -> {
            e3.lowerThanPercentage( 50, null );
        });
    }
}
