package com.capstone.backend.repository;
import com.capstone.backend.domain.Address;
import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.repository.MongoRepository;

@Repository
public interface AddressRepository extends MongoRepository<Address, String> {
    // 추가적인 쿼리 메서드가 필요하면 여기에 정의
    Address findByAddressRoad(String addressRoad);
    Address findByAddressJibun(String addressJibun);
    Address findByPostCode(String postCode);
    Address findByDetailAddress(String detailAddress);
}