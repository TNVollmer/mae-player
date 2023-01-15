package thkoeln.dungeon.monte.planet.domain;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlanetRepository extends CrudRepository<Planet, UUID> {
    public List<Planet> findAll();
    Optional<Planet> findByPlanetId( UUID planetId );
    public List<Planet> findBySpawnPointEquals( boolean spawnPointFlag );
}
