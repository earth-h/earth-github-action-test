package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter @Setter
public class Category {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    private String name;

    @ManyToMany
    @JoinTable(name = "category_item", // 관계형 디비는 양쪽에 컬렉션 관계를 가질 수 없어서, 중간 테이블을 생성해주어야 해서 JoinTable을 지정합니다. (join table에 추가 컬럼을 넣을 수가 없어서 이 방법 실무에서 안 쓰는 것)
            joinColumns = @JoinColumn(name = "cateogory_id"), // 중간테이블의 카테고리 아이디
            inverseJoinColumns = @JoinColumn(name = "item_id")) // 중간테이블의 반대쪽(아이템) 아이디
    private List<Item> items = new ArrayList<>();

    // 카테고리 계층 구조 사용할 때 사용하는 것 (셀프로 양방향 연관관계를 건 것)
    @ManyToOne(fetch = LAZY) // 자식들 여러명이 하나의 부모 가질 수 있음
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent") // 부모입장에서는 여러 자식 가질 수 있음
    private List<Category> child = new ArrayList<>();

    // 연관관계 편의 메소드
    public void addChildCategory(Category child) {
        this.child.add(child);
        child.setParent(this);
    }
}
