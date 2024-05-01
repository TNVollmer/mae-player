package thkoeln.dungeon.player.core.domainprimitives.location;

import jakarta.persistence.Embeddable;
import lombok.*;
import thkoeln.dungeon.player.core.domainprimitives.DomainPrimitiveException;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
@Embeddable
public class MineableResource {
    private MineableResourceType type;
    private Integer amount;


    public static MineableResource fromTypeAndAmount( MineableResourceType mineableResourceType, Integer amount ) {
        if ( mineableResourceType == null ) throw new DomainPrimitiveException( "MineableResourceType cannot be null!" );
        if ( amount == null ) throw new DomainPrimitiveException( "Amount cannot be null!" );
        if ( amount <= 0 ) throw new DomainPrimitiveException( "Amount must be > 0!" );
        return new MineableResource( mineableResourceType, amount );
    }


    public MineableResource add( MineableResource additionalResource ) {
        if ( additionalResource == null ) throw new DomainPrimitiveException( "additionalResource cannot be null!" );
        if ( additionalResource.isEmpty() ) return this;
        if ( this.isEmpty() ) return additionalResource;
        if ( this.type != additionalResource.type )
            throw new DomainPrimitiveException( "Cannot add resources of different types!" );
        return new MineableResource( this.type, this.amount + additionalResource.amount );
    }

    public static MineableResource empty( MineableResourceType type ) {
        return new MineableResource( type, 0 );
    }


    public boolean isEmpty() {
        return ( amount == 0 );
    }


    public MineableResource subtract( MineableResource removedResource ) {
        if ( removedResource == null ) throw new DomainPrimitiveException( "removedResource cannot be null!" );
        if ( removedResource.isEmpty() ) return this;
        //if ( this. ) throw new DomainPrimitiveException("cannot remove from nothing");
        if ( this.type != removedResource.type )
            throw new DomainPrimitiveException( "Cannot remove resources of different types!" );
        return new MineableResource(this.type, this.amount - removedResource.amount);
    }

    @Override
    public String toString() {
        return amount + " " + type.toString();
    }
}
