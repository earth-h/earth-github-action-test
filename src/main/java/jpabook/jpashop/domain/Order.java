package jpabook.jpashop.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.*;

@Entity
@Getter @Setter
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 생성메소드가 있으므로 new로 entity 생성 막기 위함
public class Order {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    // CascadeType.ALL은 orderItems에 데이터 넣고 저장하면, order에도 저장되는 것입니다.
    // orderItemA, orderItemB, orderItemC를 각각 em.persist()하고, 그 뒤 order도 persist를 해야 합니다.
    // 그런데, Cascade를 두면, order만 persist 해주면 orderItem을 따로 persist하지 않아도 됩니다. => persist를 전파합니다(ALL은 delete할때도 같이 지움).
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = LAZY, cascade = CascadeType.ALL) // order 저장할 때, delivery 객체 변경 사항도 함께 persist한다는 의미
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    private LocalDateTime orderDate; // 따로 어노테이션 없어도 됩니다.(Date와는 다름)

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // 주문상태 [ORDER, CANCEL]

    // 연관관계 편의 메소드
    // 연관관계 편의 메소드를 만드는 곳은 주로 컨트롤하는 엔티티에 넣으면 됩니다(연관관계 주인일 확률이 높을 듯).
    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }

    public void addOrderItem(OrderItem orderItem) {
        this.orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    //==생성 메소드==//
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems) {
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for (OrderItem orderItem: orderItems) {
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    //==비즈니스 로직==//
    /**
     * 주문 취소
     */
    public void cancel() {
        if (delivery.getStatus() == DeliveryStatus.COMP) {
            throw new IllegalStateException("이미 배송 완료된 상품은 취소가 불가능합니다");
        }
        this.setStatus(OrderStatus.CANCEL);
        for (OrderItem orderItem: this.getOrderItems()) {
            orderItem.cancel();
        }
    }

    //==조회 로직==/

    /**
     * 전체 주문 가격 조회
     * @return
     */
    public int getTotalPrice() {
//        int totalPrice = 0;
//        for (OrderItem orderItem: this.orderItems) {
//            totalPrice += orderItem.getTotalPrice();
//        }
//        return totalPrice;
        return this.orderItems.stream()
                .mapToInt(OrderItem::getTotalPrice)
                .sum();
    }
}
