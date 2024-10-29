package nior_near.server.domain.store.repository;

import nior_near.server.domain.store.entity.Store;
import nior_near.server.domain.user.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {
    List<Store> findByRegion_Id(Long regionId);
    List<Store> findAll();
    @Query("SELECT s FROM Store s JOIN s.menuList m JOIN s.region r WHERE s.name LIKE %:keyword% OR m.name LIKE %:keyword% OR r.name LIKE %:keyword%")
    List<Store> searchStoresByKeyword(@Param("keyword") String keyword);
    Optional<Store> findByMember(Member member);
}
