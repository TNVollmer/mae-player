package thkoeln.dungeon.monte.core.domainprimitives.purchasing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import thkoeln.dungeon.monte.core.domainprimitives.DomainPrimitiveException;
import thkoeln.dungeon.monte.core.domainprimitives.location.MineableResource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static thkoeln.dungeon.monte.core.domainprimitives.location.MineableResource.fromTypeAndAmount;
import static thkoeln.dungeon.monte.core.domainprimitives.location.MineableResourceType.*;

public class MineableResourcesTest {
    private MineableResource gold12_1, gold12_2, platin12;

    @BeforeEach
    public void setUp() {
        gold12_1 = fromTypeAndAmount( GOLD, 12 );
        gold12_2 = fromTypeAndAmount( GOLD, 12 );
        platin12 = fromTypeAndAmount( PLATIN, 12 );
    }

    @Test
    public void testEqualAndUnequal() {
        assertEquals( gold12_1, gold12_2 );
        assertNotEquals( gold12_2, platin12 );
    }

    @Test
    public void testValidation() {
        assertThrows( DomainPrimitiveException.class, () -> {
            fromTypeAndAmount( null, 12 );
        });
        assertThrows( DomainPrimitiveException.class, () -> {
            fromTypeAndAmount( GOLD, 0 );
        });
        assertThrows( DomainPrimitiveException.class, () -> {
            fromTypeAndAmount( GOLD, -1 );
        });
    }

}
