package thkoeln.dungeon.monte.domainprimitives;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertThrows;

public class MoneyTest {
    private Money m27_1, m27_2, m28;

    @BeforeEach
    public void setUp() {
        m27_1 = Money.fromInteger( 27 );
        m27_2 = Money.fromInteger( 27 );
        m28 = Money.fromInteger( 28 );
    }

    @Test
    public void testTwoMoneyEqualAndUnequal() {
        Assertions.assertEquals( m27_1, m27_2 );
        Assert.assertNotEquals( m27_1, m28 );
    }

    @Test
    public void testValidation() {
        assertThrows( DomainPrimitiveException.class, () -> {
            Money.fromInteger( null );
        });
        assertThrows( DomainPrimitiveException.class, () -> {
            Money.fromInteger( -1 );
        });
    }

}
