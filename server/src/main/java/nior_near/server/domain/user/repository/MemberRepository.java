package nior_near.server.domain.user.repository;

import nior_near.server.domain.user.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
