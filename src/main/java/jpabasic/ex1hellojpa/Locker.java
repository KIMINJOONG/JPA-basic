package jpabasic.ex1hellojpa;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class Locker {
    @Id
    @GeneratedValue

    private Long id;

    @Column
    private String name;

    @OneToOne(mappedBy = "locker")
    private Member member;
}
