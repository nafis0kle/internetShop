package com.example.myShop.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author nafis
 * @since 18.02.2022
 */
@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
public class PaymentRequiredException extends RuntimeException{
    public PaymentRequiredException(){
        super("If PaymentType: ONLINE, it should be completed before OrderStatus.PENDING");
    }
}
