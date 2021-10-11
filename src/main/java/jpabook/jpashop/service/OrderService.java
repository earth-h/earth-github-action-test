package jpabook.jpashop.service;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    /**
     * 주문
     */
    @Transactional
    public Long order(Long memberId, Long itemId, int count) {

        // 엔티티 조회
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        // 배송정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress()); // 실제로는 따로 배송 정보 받아야하지만, 쉽게 하기 위해서 회원 정보에 있는 배송 정보 넣음

        // 주문상품 생성 (생성 메소드 이용)
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        // 주문 생성
        Order order = Order.createOrder(member, delivery, orderItem);

        // 주문 저장
        orderRepository.save(order); // Order entity를 보면 cascade 설정이 있어서 deliveryt랑 orderItems을 다 persist해주기 떄문에 persist를 한 번만 하면 됩니다.

        // cascade 범위는 아래와 같이 잡으면 좋습니다.
        // order가 delivery, orderItem 관리하는 이런 그림에서만 써야 합니다 => 주인이 private owner인경우만 써야함(다른 쪽에서 참조하지 않고 order만 참조하는 경우)
        // 이리저리 참조하는 경우에는 cascade로 사용하면 안됩니다.
        // persist의 lifecycle이 동일할 때 사용하면 좋습니다.

        return order.getId();
    }


    // 취소
    @Transactional
    public void cancelOrder(Long orderId) {
        // 주문 엔티티 조회
        Order order = orderRepository.findOne(orderId);
        // 주문 취소(entity의 비즈니스 메소드 사용)
        order.cancel();
        // JPA를 사용하므로써 dirty check가 일어나서 order와 orderItem에 대해 바뀐 부분이 있어 알아서 update 쿼리가 나갑니다(mybatis같은거 쓰면 일일이 update 쿼리 날려야했음)
    }
    // 검색
/*    public List<Order> findOrders(OrderSearch orderSearch) {
        return orderRepository.findAll(orderSearch);
    }*/
}
