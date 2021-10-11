package jpabook.jpashop.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;

@Embeddable
@Getter
@AllArgsConstructor // 값 타입은 변경 불가능하게 설계해야 해서, 처음 만들 때 생성자로 값을 정의할 수 있게 해야 합니다.
public class Address {

    private String city;
    private String street;
    private String zipcode;

    protected Address() {} // JPA 기본 스펙 상 클래스 생성할 때 reflection이나 proxy 기술을 써야할 때가 있습니다. 이때, 기본 생성자가 없으면 그걸 못해서 기본 생성자를 만듭니다.
    // 이 생성자를 사람들이 호출하지 않도록 protected로 해서 사람들이 이 생성사 안 쓰는 거겠구나 하도록 설정합니다.
}
