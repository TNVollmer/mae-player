package thkoeln.dungeon.player.core.domainprimitives.status;

import jakarta.persistence.Embeddable;
import lombok.*;
import thkoeln.dungeon.player.core.domainprimitives.DomainPrimitiveException;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
@Embeddable
public class Health {
    private Integer healthAmount;

    public static Health from(Integer amount) {
        if (amount == null) throw new DomainPrimitiveException("Amount cannot be null!");
        if (amount < 0) throw new DomainPrimitiveException("Amount must be >= 0!");
        return new Health(amount);
    }

    public static Health zero() {
        return from(0);
    }

    public static Health defaultMovementDifficulty() {
        return new Health(1);
    }

    public static Health initialRobotHealth() {
        return new Health(20);
    }

    public Health decreaseBy(Health health) {
        if (health == null) throw new DomainPrimitiveException("decrease by null");
        if (health.greaterThan(this)) throw new DomainPrimitiveException("negative health not allowed");
        return Health.from(healthAmount - health.healthAmount);
    }


    public Health increaseBy(Health health) {
        if (health == null) throw new DomainPrimitiveException("increase by null");
        return Health.from(healthAmount + health.healthAmount);
    }

    public boolean greaterThan(Health health) {
        if (health == null) throw new DomainPrimitiveException("> null not defined");
        return (healthAmount > health.healthAmount);
    }


    public boolean greaterEqualThan(Health health) {
        if (health == null) throw new DomainPrimitiveException(">= null not defined");
        return (healthAmount >= health.healthAmount);
    }


    public boolean lowerThanPercentage(int percentage, Health comparisonHealth) {
        if (percentage < 0 || percentage > 100 || comparisonHealth == null)
            throw new DomainPrimitiveException("percentage < 0 || percentage > 100 || comparisonHealth == null");
        float fraction = (float) comparisonHealth.healthAmount * (float) percentage / 100f;
        return ((float) healthAmount < fraction);
    }

    @Override
    public String toString() {
        return healthAmount + "LP";
    }
}
