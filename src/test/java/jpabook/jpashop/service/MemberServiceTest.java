package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class) // springboot 올려서 테스트(db 찔러서 jpa 테스트하기 위함)
@SpringBootTest // RunWith과 함께 넣어야 springboot 올려서 테스트 가능
@Transactional // 롤백 위해서 작성
public class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;

    @Test
    public void 회원가입() throws Exception {
        // given (이렇게 주어지고)
        Member member = new Member();
        member.setName("hwang");
        // when (이게 실행되면)
        Long saveId = memberService.join(member);
        // then (이렇게 되어야 해)
        assertEquals(member, memberRepository.findOne(saveId));
    }

    @Test(expected = IllegalStateException.class) // 이 예외가 터지면 성공이다.
    public void 중복_회원_가입() throws Exception {
        // given
        Member member1 = new Member();
        member1.setName("hwang");
        Member member2 = new Member();
        member2.setName("hwang");
        // when
        memberService.join(member1);
        memberService.join(member2);

/*        try {
            memberService.join(member2); // 예외 발생
        } catch (IllegalStateException e) {
            return;
        }
        // then
        fail("예외가 발생해야 합니다."); // assert가 제공하는 것
        */
    }
}