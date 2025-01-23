package ecom.application.service;

import ecom.application.model.User;
import ecom.application.payload.AddressRequest;
import jakarta.validation.Valid;

import java.util.List;

public interface AddressService {
    AddressRequest createAddress(AddressRequest addressRequest, User user);

    List<AddressRequest> getAllAddresses();

    AddressRequest getAddressById(Long addressId);

    List<AddressRequest> getUserAddresses(User user);

    AddressRequest updateAddress(Long addressId, @Valid AddressRequest addressRequest);

    String deleteAddress(Long addressId);
}
