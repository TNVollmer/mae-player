package thkoeln.dungeon.planet.domain;

import org.springframework.data.repository.CrudRepository;
import thkoeln.dungeon.planet.domain.Planet;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlanetRepository extends CrudRepository<Planet, UUID> {
    public List<Planet> findAll();
    public Optional<Planet> findByName( String name );
}
