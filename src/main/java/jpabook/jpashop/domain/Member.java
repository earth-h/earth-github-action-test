package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private String name;

    @Embedded // 내장 타입 (@Embeddable이랑 @Embedded 하나만 써도 되는데, 이렇게 양쪽에 넣으면 안 헷갈림)
    private Address address;

    @OneToMany(mappedBy = "member") // orders 테이블에 있는 member와 매핑된 것이다는 의미
    private List<Order> orders = new ArrayList<>();
}
