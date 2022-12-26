package thkoeln.dungeon.monte.domainprimitives;

import lombok.*;

import javax.persistence.Embeddable;

@NoArgsConstructor( access = AccessLevel.PROTECTED )
@AllArgsConstructor( access = AccessLevel.PRIVATE )
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
}
