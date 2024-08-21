package unittest.thkoeln.dungeon.player.core.domainprimitives.robot;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import thkoeln.dungeon.player.core.domainprimitives.DomainPrimitiveException;
import thkoeln.dungeon.player.core.domainprimitives.location.MineableResource;
import thkoeln.dungeon.player.core.domainprimitives.location.MineableResourceType;
import thkoeln.dungeon.player.core.domainprimitives.robot.Inventory;

import java.util.ArrayList;
import java.util.List;

public class InventoryTest {

    private Inventory inv1, inv2, inv3;

    @BeforeEach
    public void setUp() {
        List<MineableResource> resources1 = new ArrayList<>();
        resources1.add(MineableResource.fromTypeAndAmount(MineableResourceType.COAL, 6));
        resources1.add(MineableResource.fromTypeAndAmount(MineableResourceType.IRON, 4));
        inv1 = Inventory.fromCapacityAndResources(20, resources1);
        inv2 = Inventory.fromCapacityAndResources(20, resources1);

        List<MineableResource> resources2 = new ArrayList<>();
        resources2.add(MineableResource.fromTypeAndAmount(MineableResourceType.COAL, 4));
        inv3 = Inventory.fromCapacityAndResources(20, resources2);
    }

    @Test
    public void testEqualAndUnequal() {
        Assertions.assertEquals( inv1, inv2 );
        Assertions.assertNotEquals( inv1, inv3 );
    }

    @Test
    public void testValidation() {
        Assertions.assertThrows( DomainPrimitiveException.class, () -> Inventory.fromCapacity(null));
        Assertions.assertThrows( DomainPrimitiveException.class, () -> Inventory.fromCapacity(-1));
        Assertions.assertThrows( DomainPrimitiveException.class, () -> Inventory.fromCapacity(0));
        Assertions.assertThrows( DomainPrimitiveException.class, () -> Inventory.fromCapacityAndResources(10, null));
    }

    @Test
    public void testUsedCapacity() {
        Assertions.assertEquals(10, inv1.getUsedCapacity());

        Inventory fullInv = inv2.setMineableResource(MineableResource.fromTypeAndAmount(MineableResourceType.GOLD, 10));
        Assertions.assertEquals(20, fullInv.getUsedCapacity());
        Assertions.assertTrue(fullInv.isFull());
    }
}
