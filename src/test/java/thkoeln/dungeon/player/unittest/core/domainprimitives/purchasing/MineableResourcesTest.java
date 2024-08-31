package thkoeln.dungeon.player.unittest.core.domainprimitives.purchasing;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import thkoeln.dungeon.player.core.domainprimitives.DomainPrimitiveException;
import thkoeln.dungeon.player.core.domainprimitives.location.MineableResource;
import thkoeln.dungeon.player.core.domainprimitives.location.MineableResourceType;


public class MineableResourcesTest {
    private MineableResource gold12_1, gold12_2, platin12;

    @BeforeEach
    public void setUp() {
        gold12_1 = MineableResource.fromTypeAndAmount( MineableResourceType.GOLD, 12 );
        gold12_2 = MineableResource.fromTypeAndAmount( MineableResourceType.GOLD, 12 );
        platin12 = MineableResource.fromTypeAndAmount( MineableResourceType.PLATIN, 12 );
    }

    @Test
    public void testEqualAndUnequal() {
        Assertions.assertEquals( gold12_1, gold12_2 );
        Assertions.assertNotEquals( gold12_2, platin12 );
    }

    @Test
    public void testValidation() {
        Assertions.assertThrows( DomainPrimitiveException.class, () -> {
            MineableResource.fromTypeAndAmount( null, 12 );
        });
        Assertions.assertThrows( DomainPrimitiveException.class, () -> {
            MineableResource.fromTypeAndAmount( MineableResourceType.GOLD, 0 );
        });
        Assertions.assertThrows( DomainPrimitiveException.class, () -> {
            MineableResource.fromTypeAndAmount( MineableResourceType.GOLD, -1 );
        });
    }

    @Test
    public void testAddResources() {
        MineableResource gold_test = gold12_1.add(gold12_2);
        Assertions.assertEquals(MineableResource.fromTypeAndAmount(MineableResourceType.GOLD, 24),gold_test);
    }

}
