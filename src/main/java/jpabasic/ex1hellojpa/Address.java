package jpabasic.ex1hellojpa;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address1 = (Address) o;
        return Objects.equals(city, address1.city) && Objects.equals(address, address1.address) && Objects.equals(zipcode, address1.zipcode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(city, address, zipcode);
    }
}
