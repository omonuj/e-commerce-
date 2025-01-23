package ecom.application.service;

import ecom.application.exceptions.ResourceNotFoundException;
import ecom.application.model.Address;
import ecom.application.model.User;
import ecom.application.payload.AddressRequest;
import ecom.application.repositories.AddressRepository;
import ecom.application.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    private final ModelMapper modelMapper;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public AddressServiceImpl(ModelMapper modelMapper, AddressRepository addressRepository, UserRepository userRepository) {
        this.modelMapper = modelMapper;
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    @Override
    public AddressRequest createAddress(AddressRequest addressRequest, User user) {

        Address address = modelMapper.map(addressRequest, Address.class);

        List<Address> addressList = user.getAddresses();
        addressList.add(address);
        user.setAddresses(addressList);

        address.setUser(user);
        Address addressSaved = addressRepository.save(address);

        return modelMapper.map(addressSaved, AddressRequest.class);
    }

    @Override
    public List<AddressRequest> getAllAddresses() {
        List<Address> addressList = addressRepository.findAll();

        List<AddressRequest> addressRequests = addressList.stream()
                .map(address -> modelMapper.map(address, AddressRequest.class)).toList();

        return addressRequests;
    }

    @Override
    public AddressRequest getAddressById(Long addressId) {
        Address address = addressRepository.findById(addressId).orElseThrow(
                () -> new ResourceNotFoundException("Address", "id", addressId));

        return modelMapper.map(address, AddressRequest.class);
    }

    @Override
    public List<AddressRequest> getUserAddresses(User user) {

        List<Address> addresses =user.getAddresses();
        return addresses.stream()
                .map(address -> modelMapper.map(address, AddressRequest.class))
                .toList();
    }

    @Override
    public AddressRequest updateAddress(Long addressId, AddressRequest addressRequest) {

        Address addressFromDB = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));

        addressFromDB.setBuildingName(addressRequest.getBuildingName());
        addressFromDB.setStreet(addressRequest.getStreet());
        addressFromDB.setCity(addressRequest.getCity());
        addressFromDB.setState(addressRequest.getState());
        addressFromDB.setZipCode(addressRequest.getZipCode());
        addressFromDB.setCountry(addressRequest.getCountry());

        Address addressUpdated = addressRepository.save(addressFromDB);

        User user = addressFromDB.getUser();
        user.getAddresses().removeIf(address -> address.getAddressId().equals(addressId));
        user.getAddresses().add(addressUpdated);
        userRepository.save(user);

        return modelMapper.map(addressUpdated, AddressRequest.class);


    }

    @Override
    public String deleteAddress(Long addressId) {
        Address addressFromDB = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));

        User user = addressFromDB.getUser();
        user.getAddresses().removeIf(address -> address.getAddressId().equals(addressId));
        userRepository.save(user);

        addressRepository.delete(addressFromDB);

        return "Address successfully deleted with addressId: " + addressId;
    }
}
