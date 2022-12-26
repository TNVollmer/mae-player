package thkoeln.dungeon.monte.domainprimitives;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.*;

public class CapabilityTest {
    private Capability d0, d1, dmax, h1;

    @BeforeEach
    public void setUp() {
        d0 = Capability.baseForType( CapabilityType.HEALTH );
        d1 = Capability.forTypeAndLevel( CapabilityType.HEALTH, 1 );
        dmax = Capability.forTypeAndLevel( CapabilityType.HEALTH, Capability.MAX_LEVEL );
        h1 = Capability.forTypeAndLevel( CapabilityType.DAMAGE, 1 );
    }

    @Test
    public void testValidation() {
        assertThrows( DomainPrimitiveException.class, () -> {
            Capability.baseForType( null );
        });
        assertThrows( DomainPrimitiveException.class, () -> {
            Capability.forTypeAndLevel( CapabilityType.DAMAGE, null );
        });
        assertThrows( DomainPrimitiveException.class, () -> {
            Capability.forTypeAndLevel( CapabilityType.DAMAGE, Capability.MIN_LEVEL-1 );
        });
        assertThrows( DomainPrimitiveException.class, () -> {
            Capability.forTypeAndLevel( CapabilityType.DAMAGE, Capability.MAX_LEVEL+1 );
        });
    }

    @Test
    public void testNextLevel() {
        // given
        // when
        Capability newD2 = d0.nextLevel();
        Capability newDmaxPlus = dmax.nextLevel();

        // then
        assertEquals(d1, newD2 );
        assertNull( newDmaxPlus );
    }

    @Test
    public void testEquals() {
        // given
        // when
        Capability newD1 = Capability.forTypeAndLevel( CapabilityType.HEALTH, 1 );

        // then
        assertEquals(d1, newD1 );
        assertNotEquals(d1, h1);
        assertNotEquals(d0, d1);
    }

}
