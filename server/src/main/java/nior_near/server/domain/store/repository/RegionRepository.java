package nior_near.server.domain.store.repository;

import nior_near.server.domain.store.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegionRepository extends JpaRepository<Region, Long> {
}
