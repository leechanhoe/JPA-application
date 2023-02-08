package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @NotEmpty
    private String name;

    @Embedded // able 중 둘중 하나만 있어도됨
    private Address address;

    @JsonIgnore // 양방향 연관관계는 한쪽에다 이걸 해줘야함
    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();
}
