package nior_near.server.domain.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import nior_near.server.domain.letter.entity.Letter;
import nior_near.server.domain.store.entity.Region;
import nior_near.server.domain.store.entity.Store;
import nior_near.server.global.util.Time;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Member extends Time {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private String social;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = false, length = 50)
    private String email;

    private String profileImage;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Long point;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20, columnDefinition = "VARCHAR(20) DEFAULT 'NONMEMBER'")
    private UserAuthorization userAuthorization = UserAuthorization.NONMEMBER;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    private Region region; // 사용자가 선택한 지역

    @OneToOne(mappedBy = "member")
    private Store store;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Letter> letterList = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Account> accountList = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LikeStore> likeStoreList = new ArrayList<>();
}
