package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter @Setter
public class Delivery {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_id")
    private Long id;

    @OneToOne(mappedBy = "delivery", fetch = LAZY) // 1:1 관계기 때문에 access가 많이 될 곳이 order를 통한 delivery일 거라서 여기는 조회만 되도록 하고, FK를 order에 넣었습니다.
    private Order order;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING) // enum의 default가 숫자라서 EnumType.STRING을 넣어줘야 합니다
    private DeliveryStatus status; //READY, COMP
}
