package nior_near.server.domain.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nior_near.server.domain.letter.entity.Letter;
import nior_near.server.domain.store.entity.Region;
import nior_near.server.domain.store.entity.Store;
import nior_near.server.global.util.Time;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Member extends Time {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private String social;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 50)
    private String email;

    private String profileImage;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Long point;

//  For Spring Security
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20, columnDefinition = "VARCHAR(20) DEFAULT 'NONMEMBER'")
    private UserAuthorization userAuthorization = UserAuthorization.NONMEMBER;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    private Region region; // 사용자가 선택한 지역

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Letter> senderLetterList = new ArrayList<>();

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Letter> receiverLetterList = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Account> accountList = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LikeStore> likeStoreList = new ArrayList<>();

    public void setUserAuthorization(UserAuthorization userAuthorization) {
        this.userAuthorization = userAuthorization;
    }

    public Member (String userId, String nickname, String profileImage, String email, String phone, String type) {
        this.name = userId;
        this.profileImage = profileImage;
        this.password = "Passw0rd";
        this.social = type;
        this.role = "ROLE_USER";
        this.email = email;
        this.phone = phone;
        this.point = 0L;
        this.nickname = nickname;
    }

    public void setRegion(Region region) {
        this.region = region;
    }
}
