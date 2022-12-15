package thkoeln.dungeon.domainprimitives;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MovementDifficultyTest {
    private MovementDifficulty m2_1, m2_2, m2, m3;

    @BeforeEach
    public void setUp() {
        m2_1 = MovementDifficulty.fromInteger( 2 );
        m2_2 = MovementDifficulty.fromInteger( 2 );
        m3 = MovementDifficulty.fromInteger( 3 );
    }

    @Test
    public void testTwogit straMoneyEqualAndUnequal() {
        assertEquals( m2_1, m2_2 );
        assertNotEquals( m2_1, m3 );
    }

    @Test
    public void testValidation() {
        assertThrows( DomainPrimitiveException.class, () -> {
            MovementDifficulty.fromInteger( null );
        });
        assertThrows( DomainPrimitiveException.class, () -> {
            MovementDifficulty.fromInteger( -1 );
        });
        assertThrows( DomainPrimitiveException.class, () -> {
            MovementDifficulty.fromInteger( 0 );
        });
        assertThrows( DomainPrimitiveException.class, () -> {
            MovementDifficulty.fromInteger( 4 );
        });
    }

    @Test
    public void testProperValue() {
        assertEquals( 2, m2_2.getDifficulty() );
        assertEquals( 3, m3.getDifficulty() );
    }

}
