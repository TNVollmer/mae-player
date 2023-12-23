package thkoeln.dungeon.player.core.domainprimitives.purchasing;

import jakarta.persistence.Embeddable;
import lombok.*;
import thkoeln.dungeon.player.core.domainprimitives.DomainPrimitiveException;

@NoArgsConstructor( access = AccessLevel.PROTECTED )
@AllArgsConstructor
@Getter
@Setter( AccessLevel.PROTECTED )
@EqualsAndHashCode
@Embeddable
public class Money {
    private Integer amount = 0;

    public static Money from( Integer amountAsInt ) {
        if ( amountAsInt == null ) throw new DomainPrimitiveException( "Amount cannot be null!" );
        if ( amountAsInt < 0 ) throw new DomainPrimitiveException( "Amount must be >= 0!" );
        Money money = new Money();
        money.amount = amountAsInt;
        return money;
    }


    public static Money zero() {
        return from( 0 );
    }

    public int canBuyThatManyFor( Money price ) {
        if ( amount == null ) throw new DomainPrimitiveException( "price == null" );
        return ( this.amount / price.amount );
    }

    public Money decreaseBy( Money moneyDue ) {
        if ( moneyDue == null ) throw new DomainPrimitiveException( "amountDue == null" );
        if ( moneyDue.greaterThan( this ) ) {
            return zero();
        }
        return Money.from( amount - moneyDue.amount );
    }

    public Money increaseBy( Money additionalMoney ) {
        if ( additionalMoney == null ) throw new DomainPrimitiveException( "additionalAmount == null" );
        return Money.from( amount + additionalMoney.amount);
    }

    public boolean greaterThan( Money otherMoney ) {
        if ( otherMoney == null ) throw new DomainPrimitiveException( "otherMoney == null" );
        return ( amount > otherMoney.amount);
    }

    public boolean greaterEqualThan( Money otherMoney ) {
        if ( otherMoney == null ) throw new DomainPrimitiveException( ">=: otherMoney == null" );
        return ( amount >= otherMoney.amount );
    }

    @Override
    public String toString() {
        return amount + "€";
    }
}
