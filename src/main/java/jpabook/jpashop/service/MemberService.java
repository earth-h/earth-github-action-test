package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true) // 조회할 때 readOnly 하면 최적화함(dirty check 안하는 등의 이점 있음) // LAZY Loading 하려면 transaction안에서 동작해야 합니다! (데이터 변경은 반드시 transaction이어야 함)
// @javax.transaction.Transactional 도 존재하긴 하지만, spring에 디펜던시가 많이 걸려 있어서 쓸 수 있는 옵션이 많아서 org.springframework.transaction.annotation.Transactional 쓰는 것 권장함
//@AllArgsConstructor // 필드 전체를 가진 생성자 만드는 것
@RequiredArgsConstructor // final으로 선언된 필드만 가지고 생성자를 만드는 것(AllArgsConstructor 사용하는거보다 더 나은 방법)
public class MemberService {

    // field injection
    //@Autowired
    private final MemberRepository memberRepository; // final로 하는 이유는 처음 만든 이후로 변경할 일이 없기 때문입니다.
    // final로 하게 되면, MemberService 생성자에 memberRepository 생성하는 코드 없으면 빨간줄 뜹니다(그때빼곤 생성이 불가하므로)


    // Repository를 환경마다 다르게 사용할 수 있도록 아래와 같이 setter injection을 통해 @Autowired가 하는 경우도 있습니다.
    // 장점: 테스트코드 짤 때 단위 테스트 Mock(목)을 직접 주입가능해서 좋습니다.
    // 단점: 보통 runtime 돌아가는 시점에 memberRepository를 바꿀 일이 없습니다(이미 조립한 이후에 repository를 바꾸는 과정은 일반적으로 없음) => 굳이 setter가 필요없는 것
    // setter injection
    //@Autowired
    //public void setMemberRepository(MemberRepository memberRepository) {
    //    this.memberRepository = memberRepository;
    //}

    // 그래서 요즘 잘 쓰는 건 constructor injection (생성자 인젝션) ----> 이것은 @AllArgsConstructor를 사용하므로써 모든 필드를 가진 생성자를 만들어줍니다.
    // 한 번 생성할 때만 설정하고 그 뒤에 set하는 일 없음
    // 그리고 좋은 점은 테스트 코드 작성할 때 memberService를 생성할 때 파라미터 안 넣으면 빨간줄 뜨니까 놓치지 않고 생성 가능합니다.
    //@Autowired // 생성자 하나인 경우, @Autowired 안 써도 알아서 해당 생성자로 주입해줍니다!
    //public MemberService(MemberRepository memberRepository) {
    //    this.memberRepository = memberRepository;
    //}

    /**
     * 회원 가입 (읽기 전용이 아니므로 @Transactional(readOnly = true) 넣으면 안됨!
     */
    @Transactional // 조회 로직이 많은 관계로 서비스 전체에 readOnly = true로 걸었으나, 이 로직은 읽기 전용 아님(그래서, 여기엔 Transactional 어노테이션 따로 걸어서 이게 먹히도록 함 - readOnly = false가 default)
    public Long join(Member member) {
        validateDuplicateMember(member); // 중복 회원 로직
        memberRepository.save(member); // 이 순간 em.persist()를 통해 영속성 컨텍스트의 캐시에 객체 올라가면서 id가 저장됨
        return member.getId(); // save에 의해 영속성 컨텍스트의 캐시에 해당 객체가 올라가 있어서 아직 db에 갔다오지 않아도 id를 꺼낼 수 있습니다.
    }

    private void validateDuplicateMember(Member member) {
        // EXCEPTION 던지기 (예제라 이렇게 적었을 뿐, 실제로는 보통 리스트 길이가 0보다 크다로 비교 함)
        // 사실, 아래와 같이 로직 던지고 WAS가 여러 대이면, 동일 이름을 동시에 여러 인스턴스로 추가 진행하면 이슈 생깁니다.
        // 따라서, 실무에서는 최후에 한 번 더 방어를 해야합니다. => DB에서 "name 값에 unique 제약조건"을 잡습니다.(여기서 이름이 중복되면 안되므로 unique 쓰라는 것)
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    /**
     * 회원 전체 조회
      */
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    /**
     * 회원 한명 조회(단건)
      */
    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }
}
