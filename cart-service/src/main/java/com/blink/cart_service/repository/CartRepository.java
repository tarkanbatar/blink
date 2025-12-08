package com.blink.cart_service.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.blink.cart_service.model.Cart;

@Repository
public interface CartRepository extends CrudRepository<Cart, String>{
    
}
