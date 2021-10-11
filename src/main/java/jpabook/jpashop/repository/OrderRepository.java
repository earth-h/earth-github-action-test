package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Order;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    public void save(Order order) {
        em.persist(order);
    }

    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }

    /**
     * 주문 검색 기능 (동적 쿼리) - 무식한 버전 (직접 조립하는 버전)
     */
    public List<Order> findAllByString(OrderSearch orderSearch) {
        String jpql = "select o from Order o join o.member m";
        boolean isFirstCondition = true;

        // 주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            if(isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " o.status = :status";
        }
        // 회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if(isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.name = :name";
        }

        TypedQuery<Order> query = em.createQuery(jpql, Order.class)
                .setMaxResults(1000);

        if (orderSearch.getOrderStatus() != null)
            query = query.setParameter("status", orderSearch.getOrderStatus());
        if (StringUtils.hasText(orderSearch.getMemberName()))
            query = query.setParameter("name", orderSearch.getMemberName());

        return query.getResultList();
    }

    /**
     * 주문 검색 기능 (동적 쿼리) - JPA Criteria (이것 역시 권장 X)
     * 표준 스펙이긴 하지만, 유지보수할 때 이게 뭔지 바로 이해가 안되어서 실무에서 안 씀
     */
    public List<Order> findAllByCriteria(OrderSearch orderSearch) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);

        Root<Order> o = cq.from(Order.class);
        Join<Object, Object> m = o.join("member", JoinType.INNER);
        List<Predicate> criteria = new ArrayList<>();

        // 주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            Predicate status = cb.equal(o.get("status"), orderSearch.getOrderStatus()); // 조건
            criteria.add(status);
        }

        // 회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name = cb.like(m.get("name"), "%" + orderSearch.getMemberName() + "%");
            criteria.add(name);
        }

        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000);
        return query.getResultList();
    }

    /**
     * 주문 검색 기능 (동적 쿼리) - QueryDSL
     */
//    public List<Order> findAll(OrderSearch orderSearch) {
//        QOrder order = QOrder.order;
//        QMember member = QMember.member;
//
//        return query
//                .select(order)
//                .from(order)
//                .join(order.member, member)
//                .where(statusEq(orderSearch.getOrderStatus()),
//                        nameLike(orderSearch.getMemberName()))
//                .limit(1000)
//                .fetch();

        // 아래와 같이 쿼리를 날리려면, status와 memberName 필터가 모두 적용되어야 합니다(예를 들어, OrderStatus.CANCEL인 주문 전체 다 라고 하면 아래 쿼리로 불가)
/*
        return em.createQuery("select o from Order o join o.member m" +
                        " where o.status = :status " +
                        "and m.name = :name", Order.class)
                .setParameter("status", orderSearch.getOrderStatus()) // parameter 값 넣음
                .setParameter("name", orderSearch.getMemberName())  // parameter 값 넣음
                .setFirstResult(1) // paging (예를 들어 여기에 100 적으면 100부터 1000개 가져옴)
                .setMaxResults(1000) // 결과 최대 개수
                .getResultList();// Order와 연관된 Member와 조인해라
*/
//    }
}
