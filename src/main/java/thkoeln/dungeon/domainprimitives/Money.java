package thkoeln.dungeon.domainprimitives;

import lombok.*;

import javax.persistence.Embeddable;

@NoArgsConstructor( access = AccessLevel.PROTECTED )
@AllArgsConstructor
@Getter
@EqualsAndHashCode
@Embeddable
@ToString
public class Money {
    private Integer amount = 0;

    public static Money fromInteger(Integer amount ) {
        if ( amount == null ) throw new MonetenException( "Amount cannot be null!" );
        if ( amount < 0 ) throw new MonetenException( "Amount must be >= 0!" );
        return new Money( amount );
    }
}
