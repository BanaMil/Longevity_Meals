package com.capstone.backend.dto;

import com.capstone.backend.domain.Address;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponse {
    private String addressRoad;
    private String addressJibun;
    private String postCode;
    private String addressDetail;
    private boolean isDefault;

    public static AddressResponse from(Address address) {
        if (address == null) {
            return null;
        }
        
        return new AddressResponse(
            address.getAddressRoad(),
            address.getAddressJibun(),
            address.getPostCode(),
            address.getAddressDetail(),
            address.isDefault()
        );
    }
}
