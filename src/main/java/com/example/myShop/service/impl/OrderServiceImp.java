package com.example.myShop.service.impl;

import com.example.myShop.domain.entity.Goods;
import com.example.myShop.domain.entity.Order;
import com.example.myShop.domain.entity.Status;
import com.example.myShop.domain.exception.OrderCheckCountException;
import com.example.myShop.domain.mapper.OrderMapper;
import com.example.myShop.repository.OrderRepository;
import com.example.myShop.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author nafis
 * @since 19.12.2021
 */

@Service
@RequiredArgsConstructor
public class OrderServiceImp implements OrderService{
    private final OrderRepository orderRepository;
    private final GoodsService goodsService;
    private final UserService userService;
    private final ReceivingService receivingService;
    private final PaymentService paymentService;
    private final OrderMapper orderMapper;

    @Override
    public Order get(Integer id) {
        return orderRepository.findById(id).orElse(null);
    }

    public Map<String, Object> getAll(int page, int size){
        Pageable pageable = PageRequest.of(page, size);

        Page orderPage = orderRepository.findAll(pageable);

        Map<String, Object> response = new HashMap<>();

        response.put("orders", orderPage.getContent());
        response.put("currentPage", orderPage.getNumber());
        response.put("totalItems", orderPage.getTotalElements());
        response.put("totalPages", orderPage.getTotalPages());

        return response;
    }

//    private void setUser(Order order){
//        order.setUser(userService.getUserByEmail(
//                        SecurityContextHolder
//                        .getContext()
//                        .getAuthentication()
//                        .getName()));
//    }

    private boolean checkCount(int count, int goodsId){
        Goods goods = goodsService.get(goodsId);
        long availability = goods.getAvailability();

        return count <= availability;
    }

    private void setPrice(Order order, Integer goodsId){
        Goods goods = goodsService.get(goodsId);

        BigDecimal count = new BigDecimal(order.getCount());
        BigDecimal orderPrice = goods.getPrice().multiply(count);

        //Reduce availability for goods
        goods.decAvailability(Long.getLong(count.toString()));
        goodsService.update(goodsId, goods);

        order.setPrice(orderPrice);
    }

    @Override
    public Order create(Order order, Integer goodsId, Integer receiveId, Integer payId, Integer userId) {
        order.setUser(userService.get(userId));

        if(!checkCount(order.getCount(), goodsId)){
            throw new OrderCheckCountException(goodsId);
        }

        setPrice(order, goodsId);
        order.setGoods(goodsService.get(goodsId));
        order.setStatus(Status.STATUS1);
        order.setReceiving(receivingService.get(receiveId));
        order.setPayment(paymentService.get(payId));

        return orderRepository.save(order);
    }

    @Override
    public Order  update(Integer id, Order order) {
        return Optional.of(id)
                .map(this::get)
                .map(current -> orderMapper.merge(current, order))
                .map(orderRepository::save)
                .orElseThrow();
    }

    @Override
    public void delete(Integer id) {
        orderRepository.deleteById(id);
    }

    @Override
    public Page<Order> getOrders() {
        Pageable myPage = PageRequest.ofSize(10);
        return orderRepository.findAll(myPage);
    }
}
