package thkoeln.dungeon.monte.core.domainprimitives.purchasing;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import thkoeln.dungeon.monte.core.domainprimitives.DomainPrimitiveException;
import thkoeln.dungeon.monte.core.domainprimitives.location.MineableResource;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
    public void testTwoMoneyEqualAndUnequal() {
        assertEquals( gold12_1, gold12_2 );
        assertNotEquals( gold12_2, platin12 );
    }

    @Test
    public void testValidation() {
        Assert.assertThrows( DomainPrimitiveException.class, () -> {
            fromTypeAndAmount( null, 12 );
        });
        Assert.assertThrows( DomainPrimitiveException.class, () -> {
            fromTypeAndAmount( GOLD, 0 );
        });
        Assert.assertThrows( DomainPrimitiveException.class, () -> {
            fromTypeAndAmount( GOLD, -1 );
        });
    }

    @Test
    public void testToString() {
        // given
        MineableResource c9 = MineableResource.fromTypeAndAmount( COAL, 9 );
        MineableResource g22 = MineableResource.fromTypeAndAmount( GOLD, 22 );
        MineableResource p103 = MineableResource.fromTypeAndAmount( PLATIN, 103 );
        MineableResource j752 = MineableResource.fromTypeAndAmount( GEM, 752 );
        MineableResource i1203 = MineableResource.fromTypeAndAmount( IRON, 1203 );
        MineableResource c8921 = MineableResource.fromTypeAndAmount( COAL, 8921 );
        MineableResource g12655 = MineableResource.fromTypeAndAmount( GOLD, 12655 );
        MineableResource i562899 = MineableResource.fromTypeAndAmount( IRON, 562899 );
        MineableResource c2716623 = MineableResource.fromTypeAndAmount( COAL, 2716623 );

        // when
        // then
        assertEquals( "C.01", c9.toString() );
        assertEquals( "G.02", g22.toString() );
        assertEquals( "P.10", p103.toString() );
        assertEquals( "J.75", j752.toString() );
        assertEquals( "I1.2", i1203.toString() );
        assertEquals( "C8.9", c8921.toString() );
        assertEquals( "G 12", g12655.toString() );
        assertEquals( "I562", i562899.toString() );
        assertEquals( "C999", c2716623.toString() );
    }

}
