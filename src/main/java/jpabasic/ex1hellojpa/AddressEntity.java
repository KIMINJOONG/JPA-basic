package jpabasic.ex1hellojpa;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class AddressEntity {
    @Id
    @GeneratedValue
    private Long id;

    private Address address;


    public AddressEntity(String city, String street, String zipcode) {
        this.address = new Address(city, street, zipcode);
    }

    public AddressEntity(Long id, Address address) {
        this.id = id;
        this.address = address;
    }
}
