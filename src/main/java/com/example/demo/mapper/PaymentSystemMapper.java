package com.example.demo.mapper;


import com.example.demo.entity.PaymentSystem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentSystemMapper {

    String toString(PaymentSystem paymentSystem);

    PaymentSystem toPaymentSystem(String paymentSystem);
}
