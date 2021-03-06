package com.example.myShop.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author nafis
 * @since 14.02.2022
 */

@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
public class UserDeleteException extends RuntimeException{
    public UserDeleteException(){
        super("User has an active order. Deleting is forbidden");
    }
}
