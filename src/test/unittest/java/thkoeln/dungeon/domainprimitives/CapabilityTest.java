package thkoeln.dungeon.domainprimitives;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.*;

public class CapabilityTest {
    private Capability d1, d2, dmax, h2;

    @BeforeEach
    public void setUp() {
        d1 = Capability.baseForType( CapabilityType.HEALTH );
        d2 = Capability.forTypeAndLevel( CapabilityType.HEALTH, 2 );
        dmax = Capability.forTypeAndLevel( CapabilityType.HEALTH, Capability.MAX_LEVEL );
        h2 = Capability.forTypeAndLevel( CapabilityType.DAMAGE, 2 );
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
            Capability.forTypeAndLevel( CapabilityType.DAMAGE, 0 );
        });
        assertThrows( DomainPrimitiveException.class, () -> {
            Capability.forTypeAndLevel( CapabilityType.DAMAGE, Capability.MAX_LEVEL+1 );
        });
    }

    @Test
    public void testNextLevel() {
        // given
        // when
        Capability newD2 = d1.nextLevel();
        Capability newDmaxPlus = dmax.nextLevel();

        // then
        assertEquals( d2, newD2 );
        assertNull( newDmaxPlus );
    }

    @Test
    public void testEquals() {
        // given
        // when
        Capability newD2 = Capability.forTypeAndLevel( CapabilityType.HEALTH, 2 );

        // then
        assertEquals( d2, newD2 );
        assertNotEquals( d2, h2 );
        assertNotEquals( d1, d2 );
    }

}
