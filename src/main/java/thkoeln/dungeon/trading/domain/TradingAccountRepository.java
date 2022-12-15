package thkoeln.dungeon.trading.domain;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface TradingAccountRepository extends CrudRepository<TradingAccount, UUID> {
    public List<TradingAccount> findAll();
}
