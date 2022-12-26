package thkoeln.dungeon.monte.domainprimitives;

import lombok.*;

import javax.persistence.Embeddable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor( access = AccessLevel.PROTECTED )
@Getter
@Setter( AccessLevel.PROTECTED )
@EqualsAndHashCode
@Embeddable
public class Capability {
    private CapabilityType type;
    private Integer level;
    public static final Integer MIN_LEVEL = 0;
    public static final Integer MAX_LEVEL = 5;


    /**
     * @param type
     * @return Base capability for the given type
     */
    public static Capability baseForType( CapabilityType type ) {
        return forTypeAndLevel( type, MIN_LEVEL );
    }

    /**
     * @param type
     * @param level
     * @return Capability for given type and level
     */
    public static Capability forTypeAndLevel( CapabilityType type, Integer level ) {
        if ( type == null || level == null ) throw new DomainPrimitiveException( "type == null || level == null" );
        if ( level < MIN_LEVEL || level > MAX_LEVEL ) throw new DomainPrimitiveException( "level < MIN_LEVEL || level > MAX_LEVEL" );
        Capability capability = new Capability();
        capability.setLevel( level );
        capability.setType( type );
        return capability;
    }


    /**
     * @return Complete list of all base capabities a robot can have
     */
    public static List<Capability> allBaseCapabilities() {
        List<Capability> allBaseCapabilities = new ArrayList<>();
        for ( CapabilityType capabilityType : CapabilityType.values() ) {
            allBaseCapabilities.add( Capability.baseForType( capabilityType) );
        }
        return allBaseCapabilities;
    }


    /**
     * @return Same capability, one level higher, in case the max is not yet reached. Otherwise null is returned.
     */
    public Capability nextLevel() {
        if ( level < MAX_LEVEL ) return forTypeAndLevel( this.type, this.level + 1 );
        else return null;
    }


    public boolean isMinimumLevel() {
        return level == MIN_LEVEL;
    }


    public boolean isMaximumLevel() {
        return level == MAX_LEVEL;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Capability)) return false;
        Capability that = (Capability) o;
        return type == that.type && level.equals(that.level);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, level);
    }
}
