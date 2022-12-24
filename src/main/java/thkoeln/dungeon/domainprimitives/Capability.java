package thkoeln.dungeon.domainprimitives;

import lombok.*;

import javax.persistence.Embeddable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@NoArgsConstructor( access = AccessLevel.PROTECTED )
@Getter
@Setter( AccessLevel.PROTECTED )
@EqualsAndHashCode
@Embeddable
public class Capability {
    private CapabilityType type;
    private Integer level;
    public static final Integer MIN_LEVEL = 1;
    public static final Integer MAX_LEVEL = 5;

    public static Capability baseForType( CapabilityType type ) {
        if ( type == null ) throw new DomainPrimitiveException( "type == null" );
        Capability capability = new Capability();
        capability.setLevel( MIN_LEVEL );
        capability.setType( type );
        return capability;
    }

    public static List<Capability> allBaseCapabilities() {
        List<Capability> allBaseCapabilities = new ArrayList<>();
        for ( CapabilityType capabilityType : CapabilityType.values() ) {
            allBaseCapabilities.add( Capability.baseForType( capabilityType) );
        }
        return allBaseCapabilities;
    }
}
