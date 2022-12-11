package thkoeln.dungeon.domainprimitives;

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
        if ( mineableResourceType == null ) throw new MineableResourceException( "MineableResourceType cannot be null!" );
        if ( amount == null ) throw new MineableResourceException( "Amount cannot be null!" );
        if ( amount <= 0 ) throw new MineableResourceException( "Amount must be > 0!" );
        return new MineableResource( mineableResourceType, amount );
    }
}
