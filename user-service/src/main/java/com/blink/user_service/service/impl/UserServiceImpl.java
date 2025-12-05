package com.blink.user_service.service.impl;

import java.util.List;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.blink.user_service.dto.request.AddressRequest;
import com.blink.user_service.dto.request.LoginRequest;
import com.blink.user_service.dto.request.RegisterRequest;
import com.blink.user_service.dto.request.UpdateProfileRequest;
import com.blink.user_service.dto.response.AuthResponse;
import com.blink.user_service.dto.response.UserResponse;
import com.blink.user_service.exception.EmailAlreadyExistsException;
import com.blink.user_service.exception.InvalidCredentialsException;
import com.blink.user_service.exception.UserNotFoundException;
import com.blink.user_service.model.Address;
import com.blink.user_service.model.User;
import com.blink.user_service.repository.UserRepository;
import com.blink.user_service.security.JwtService;
import com.blink.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    

    // General flow for registration: Email Check -> Password Hashing -> Create & Save User -> Generate JWT Token -> Return AuthResponse
    
    @Override
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());

        if(userRepository.existsByEmail(request.getEmail())) {
            log.warn("Email already exists: {}", request.getEmail());
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        User user = User.builder()
                    .email(request.getEmail().toLowerCase().trim())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .firstName(request.getFirstName().trim())
                    .lastName(request.getLastName().trim())
                    .phone(request.getPhone())
                    .build();

        //TODO
        User savedUser = userRepository.save(user);
        log.info("User registered successfully with id: {}", savedUser.getId());

        //create jwt token
        String token = jwtService.generateToken(org.springframework.security.core.userdetails.User.builder()
                                    .username(savedUser.getEmail())
                                    .password(savedUser.getPassword())
                                    .authorities("ROLE_USER")
                                    .build()
        );

        //return response
        return AuthResponse.builder().token(token).expiresIn(jwtService.getExpirationTime()).user(UserResponse.fromUser(savedUser)).build();
    }

    // User Login Flow: Validate Credentials with AuthenticationManager -> Bring User if it is valid -> Generate JWT Token -> Return AuthResponse
    @Override
    public AuthResponse login(LoginRequest request){
        log.info("Login attempt for email: {}", request.getEmail());
        
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail().toLowerCase().trim(), request.getPassword()));
        } catch (AuthenticationException e) {
            log.warn("Authentication failed for email: {}", request.getEmail());
            throw new InvalidCredentialsException();
        }

        User user = userRepository.findByEmail(request.getEmail().toLowerCase().trim()).orElseThrow(() -> new UserNotFoundException("email", request.getEmail()));
        
        String token = jwtService.generateToken(org.springframework.security.core.userdetails.User.builder()
                                    .username(user.getEmail())
                                    .password(user.getPassword())
                                    .authorities(user.getRoles().toArray(new String[0]))
                                    .build()
        );

        log.info("User logged in successfully with id: {}", user.getEmail());

        return AuthResponse.builder().token(token).expiresIn(jwtService.getExpirationTime()).user(UserResponse.fromUser(user)).build();
    }

    @Override
    @Transactional(readOnly = true) // for performance optimization
    public UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("email", email));
        return UserResponse.fromUser(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(String id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("id", id));
        return UserResponse.fromUser(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(UserResponse::fromUser).toList();
    }

    // Profile update - it updates only sent fields
    @Override
    public UserResponse updateProfile(String email, UpdateProfileRequest request) {
        log.info("Updating profile for user with email: {}", email);

        User user = findByEmail(email);

        if (request.getFirstName() != null && !request.getFirstName().isBlank()) {
            user.setFirstName(request.getFirstName().trim());
        }
        if (request.getLastName() != null && !request.getLastName().isBlank()) {
            user.setLastName(request.getLastName().trim());
        }
        if (request.getPhone() != null && !request.getPhone().isBlank()) {
            user.setPhone(request.getPhone().trim());
        }

        User updatedUser = userRepository.save(user);
        log.info("Profile updated for: {}", email);

        return UserResponse.fromUser(updatedUser);
    }

    @Override
    public UserResponse addAddress(String email, AddressRequest request) {
        log.info("Adding new address for user with email: {}", email);
        
        User user = findByEmail(email);

        Address newAddress = Address.builder()
                .title(request.getTitle())
                .addressLine1(request.getAddressLine1())
                .addressLine2(request.getAddressLine2())
                .city(request.getCity())
                .state(request.getState())
                .country(request.getCountry())
                .zipCode(request. getZipCode())
                .isDefault(request.isDefault())
                .build();

        if(request.isDefault()) {
            user.getAddresses().forEach(addr -> addr.setDefault(false));
        }

        if(user.getAddresses().isEmpty()) {
            newAddress.setDefault(true);
        }

        user.getAddresses().add(newAddress);
        User updatedUser = userRepository.save(user);

        log.info("New addresses: {} \n added for user with email: {}", newAddress, email);

        return UserResponse.fromUser(updatedUser);
    }

    @Override
    public UserResponse deleteAddress(String email, String addressId){
        log.info("Deleting address with id: {} for user with email: {}", addressId, email);

        User user = findByEmail(email);

        Address addressToDelete = user.getAddresses().stream()
                .filter(addr -> addr.getId().equals(addressId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Address not found with id: " + addressId));
        
        user.getAddresses().remove(addressToDelete);

        if(addressToDelete.isDefault() && !user.getAddresses().isEmpty()) {
            user.getAddresses().get(0).setDefault(true);
        }        

        User updatedUser = userRepository.save(user);
        log.info("Address with id: {} deleted for user with email: {}", addressId, email);

        return UserResponse.fromUser(updatedUser);
    }

    @Override
    public UserResponse setDefaultAddress(String email, String addressId) {
        log.info("Setting default address with id: {} for user with email: {}", addressId, email);

        User user = findByEmail(email);

        boolean addressFound = false;

        for (Address addr : user.getAddresses()) {
            if (addr.getId().equals(addressId)) {
                addr.setDefault(true);
                addressFound = true;
            } else {
                addr.setDefault(false);
            }
        }

        if(!addressFound) {
            throw new RuntimeException("Address not found with id: " + addressId);
        }

        User updatedUser = userRepository.save(user);
        log.info("Default address set to id: {} for user with email: {}", addressId, email);

        return UserResponse.fromUser(updatedUser);
    }

    //for internal usage
    @Override
    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepository.findByEmail(email.toLowerCase().trim()).orElseThrow(() -> new UserNotFoundException("email", email));
    }
    
}
