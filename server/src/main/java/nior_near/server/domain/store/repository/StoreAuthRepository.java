package nior_near.server.domain.store.repository;

import nior_near.server.domain.store.entity.StoreAuth;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreAuthRepository extends JpaRepository<StoreAuth, Long> {
}
