package thkoeln.dungeon.planet.domain;

import org.springframework.data.repository.CrudRepository;
import thkoeln.dungeon.planet.domain.Planet;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlanetRepository extends CrudRepository<Planet, UUID> {
    public List<Planet> findAll();
    Optional<Planet> findByCoordinate_XAndCoordinate_Y( Integer x, Integer y );
    Optional<Planet> findByPlanetId( UUID planetId );
}
