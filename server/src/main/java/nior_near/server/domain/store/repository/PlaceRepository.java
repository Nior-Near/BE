package nior_near.server.domain.store.repository;

import nior_near.server.domain.order.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceRepository extends JpaRepository<Place, Long> {
}
