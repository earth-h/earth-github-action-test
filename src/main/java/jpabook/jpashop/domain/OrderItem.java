package jpabook.jpashop.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.aspectj.weaver.ast.Or;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 생성메소드가 있기 때문에, new로 생성해서 setter로 값 넣는것 방지(유지보수할 때 힘들기때문)
public class OrderItem {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice; //주문 가격

    private int count; //주문 수량

    //==생성 메소드==/
    public static OrderItem createOrderItem(Item item, int orderPrice, int count) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderPrice(orderPrice); // 쿠폰같은 할인이 있을 수 있어서 따로 가져갑니다.
        orderItem.setItem(item);
        orderItem.setCount(count);

        item.removeStock(count); // 재고 삭제
        return orderItem;
    }

    //==비즈니스 로직==/
    public void cancel() {
        this.getItem().addStock(this.count); // 재고 롤백
    }

    public int getTotalPrice() {
        return this.getOrderPrice() * this.getCount();
    }
}
