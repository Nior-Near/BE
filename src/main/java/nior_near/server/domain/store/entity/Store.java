package nior_near.server.domain.store.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nior_near.server.domain.order.entity.Order;
import nior_near.server.domain.order.entity.Place;
import nior_near.server.domain.user.entity.Member;
import nior_near.server.global.util.Time;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
public class Store extends Time {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false, length = 20)
    private String name;

    @ColumnDefault("'기본 이미지 링크'")
    private String profileImage;

    @ColumnDefault("'기본 음식 이미지 링크'")
    private String thumbnail;

    @Column(nullable = false, length = 50)
    private String title;

    private String introduction;

    @ColumnDefault("36.5")
    private double temperature = 36.5;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private String letter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", nullable = false)
    private Region region;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StoreAuth> storeAuthList = new ArrayList<>();

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StoreImage> storeImageList = new ArrayList<>();

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Menu> menuList = new ArrayList<>();

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orderList = new ArrayList<>(); // 요리사 입장에서 주문 리스트를 조회할 수 있으므로

    @Builder
    public Store(String name, String profileImage, String title, String introduction, String message, Place place, Region region ,Member member, String letter) {
        this.name = name;
        this.profileImage = profileImage;
        this.title = title;
        this.introduction = introduction;
        this.message = message;
        this.place = place;
        this.region = region;
        this.member = member;
        this.letter = letter;
    }

    public void updateThumbnail(String imageLink) {
        this.thumbnail = imageLink;
    }
}
