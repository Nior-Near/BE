package nior_near.server.domain.store.repository;

import nior_near.server.domain.store.entity.StoreRegion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRegionRepository extends JpaRepository<StoreRegion, Long> {
}
