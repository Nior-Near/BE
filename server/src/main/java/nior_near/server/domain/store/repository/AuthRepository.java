package nior_near.server.domain.store.repository;

import nior_near.server.domain.store.entity.Auth;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthRepository extends JpaRepository<Auth, Long> {
}
