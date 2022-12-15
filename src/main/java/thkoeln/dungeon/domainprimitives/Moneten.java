package thkoeln.dungeon.domainprimitives;

import lombok.*;

import javax.persistence.Embeddable;

@NoArgsConstructor( access = AccessLevel.PROTECTED )
@AllArgsConstructor
@Getter
@EqualsAndHashCode
@Embeddable
@ToString
public class Moneten {
    private Integer amount = 0;

    public static Moneten fromInteger( Integer amount ) {
        if ( amount == null ) throw new DomainPrimitiveException( "Amount cannot be null!" );
        if ( amount < 0 ) throw new DomainPrimitiveException( "Amount must be >= 0!" );
        return new Moneten( amount );
    }

    public int canBuyThatManyFor( Moneten price ) {
        if ( amount == null ) throw new DomainPrimitiveException( "price == null" );
        return ( this.amount / price.amount );
    }
}
