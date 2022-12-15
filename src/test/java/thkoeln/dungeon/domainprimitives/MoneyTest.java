package thkoeln.dungeon.domainprimitives;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
        assertEquals( m27_1, m27_2 );
        assertNotEquals( m27_1, m28 );
    }

    @Test
    public void testValidation() {
        assertThrows( MonetenException.class, () -> {
            Money.fromInteger( null );
        });
        assertThrows( MonetenException.class, () -> {
            Money.fromInteger( -1 );
        });
    }

}
