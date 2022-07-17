package jpabasic.ex1hellojpa;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;

@Embeddable
@Getter
@Setter
public class Address {
    private String city;
    private String address;
    private String zipcode;

    public Address() {
    }

    public Address(String city, String address, String zipcode) {
        this.city = city;
        this.address = address;
        this.zipcode = zipcode;
    }
}
