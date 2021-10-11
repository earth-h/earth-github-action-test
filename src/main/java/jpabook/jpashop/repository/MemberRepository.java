package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor // Spring-data-JPA가 EntityManager를 @PersistenceContext 대신 Autowired를 사용할 수 있게 해줘서 가능 (일관되게 가능)
public class MemberRepository {

    // Spring boot의 Spring-data-JPA를 사용하면 @PersistenceContext 대신 @Autowired를 써도 인젝션이 가능합니다 > 즉, final 필드로 작성하고 @RequiredArgsConstructor로 어노테이션 가능합니다.
    //@Autowired
    //@PersistenceContext
    private final EntityManager em; // @RequiredArgsConstructor를 통해 생성자 인젝션(constructor injection)

    //public MemberRepository(EntityManager em) {
    //    this.em = em;
    //}

    public void save(Member member) {
        em.persist(member);
    }

    public Member findOne(Long id) {
        return em.find(Member.class, id);
    }

    public List<Member> findAll() {
        // entity에 대해 쿼리를 날리는 것
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    public List<Member> findByName(String name) {
        return em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();
    }
}
