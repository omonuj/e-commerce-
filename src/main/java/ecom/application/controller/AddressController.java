package ecom.application.controller;


import ecom.application.model.User;
import ecom.application.payload.AddressRequest;
import ecom.application.repositories.AddressRepository;
import ecom.application.security.services.UserDetailsServiceImpl;
import ecom.application.service.AddressServiceImpl;
import ecom.application.util.AuthUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AddressController {

    @Autowired
    private AddressServiceImpl addressService;

    @Autowired
    private AuthUtil authUtil;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @PostMapping("/addresses")
    public ResponseEntity<AddressRequest> createAddress(@Valid @RequestBody AddressRequest addressRequest) {

        User user = authUtil.loggedInUser();
        AddressRequest createdAddressRequest = addressService.createAddress(addressRequest, user);

        return new ResponseEntity<>(createdAddressRequest, HttpStatus.CREATED);
    }

    @GetMapping("/addresses")
    public ResponseEntity<List<AddressRequest>> getAllAddresses() {
        List<AddressRequest> addressRequests = addressService.getAllAddresses();
        if (addressRequests.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<List<AddressRequest>>(addressRequests, HttpStatus.OK);
        }
    }

    @GetMapping("/addresses/{addressId}")
    public ResponseEntity<AddressRequest> getAddressById(@PathVariable Long addressId) {
        AddressRequest addressRequest = addressService.getAddressById(addressId);
        if (addressRequest == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(addressRequest, HttpStatus.OK);
        }
    }


    @GetMapping("/users/addresses")
    public ResponseEntity<List<AddressRequest>> getAddressByUsers() {
        User user = authUtil.loggedInUser();
        List<AddressRequest> addressRequest = addressService.getUserAddresses(user);
        if (addressRequest == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(addressRequest, HttpStatus.OK);
        }
    }


    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<AddressRequest> updateAddress(@PathVariable Long addressId, @Valid @RequestBody AddressRequest addressRequest) {
        AddressRequest addressUpdatedRequest = addressService.getAddressById(addressId);
        if (addressRequest == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            addressService.updateAddress(addressId, addressRequest);
            return new ResponseEntity<>(addressRequest, HttpStatus.OK);
        }
    }

    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<String> deleteAddress(@PathVariable Long addressId) {
        String status = addressService.deleteAddress(addressId);
        return new ResponseEntity<>(status, HttpStatus.OK );
    }
}
