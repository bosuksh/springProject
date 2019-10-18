package com.example.study.service;

import com.example.study.ifs.CrudInterface;
import com.example.study.model.entity.OrderDetail;
import com.example.study.model.entity.OrderGroup;
import com.example.study.model.network.Header;
import com.example.study.model.network.request.OrderDetailApiRequest;
import com.example.study.model.network.response.OrderDetailApiResponse;
import com.example.study.repository.ItemRepository;
import com.example.study.repository.OrderDetailRepository;
import com.example.study.repository.OrderGroupRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderDetailApiLogicService extends BaseService<OrderDetailApiRequest, OrderDetailApiResponse,OrderDetail> {


    @Autowired
    OrderGroupRepository orderGroupRepository;

    @Autowired
    ItemRepository itemRepository;

    @Override
    public Header<OrderDetailApiResponse> create(Header<OrderDetailApiRequest> request) {
        OrderDetailApiRequest body = request.getData();
        OrderDetail orderDetail =
                OrderDetail.builder()
                        .status(body.getStatus())
                        .arrivalDate(body.getArrivalDate())
                        .totalPrice(body.getTotalPrice())
                        .quantity(body.getQuantity())
                        .orderGroup(orderGroupRepository.getOne(body.getOrderGroupId()))
                        .item(itemRepository.getOne(body.getItemId()))
                        .build();
        OrderDetail newOrderDetail = baseRepository.save(orderDetail);

        return Header.OK(response(newOrderDetail));
    }

    @Override
    public Header<OrderDetailApiResponse> read(Long id) {
        return baseRepository
                .findById(id)
                .map(orderDetail -> Header.OK(response(orderDetail)))
                .orElseGet(()->Header.ERROR("No Data"));
    }

    @Override
    public Header<OrderDetailApiResponse> update(Header<OrderDetailApiRequest> request) {
        OrderDetailApiRequest body = request.getData();
        log.info("id = {}",body.getId());
        return baseRepository
                .findById(body.getId())
                .map(orderDetail -> {
                    orderDetail.setStatus(body.getStatus())
                            .setTotalPrice(body.getTotalPrice())
                            .setQuantity(body.getQuantity())
                            .setArrivalDate(body.getArrivalDate())
                            .setItem(itemRepository.getOne(body.getItemId()))
                            .setOrderGroup(orderGroupRepository.getOne(body.getOrderGroupId()));
                    return orderDetail;
                }).map(changeOrderDetail -> baseRepository.save(changeOrderDetail))
                .map(orderDetail -> Header.OK(response(orderDetail)))
                .orElseGet(()->Header.ERROR("No Data"));


    }

    @Override
    public Header delete(Long id) {
        return baseRepository
                .findById(id)
                .map(orderDetail -> {
                    baseRepository.delete(orderDetail);
                    return Header.OK();
                })
                .orElseGet(()->Header.ERROR("No Data"));
    }

    private OrderDetailApiResponse response(OrderDetail orderDetail) {
        OrderDetailApiResponse orderDetailApiResponse =
                OrderDetailApiResponse
                        .builder()
                        .id(orderDetail.getId())
                        .status(orderDetail.getStatus())
                        .arrivalDate(orderDetail.getArrivalDate())
                        .totalPrice(orderDetail.getTotalPrice())
                        .quantity(orderDetail.getQuantity())
                        .orderGroupId(orderDetail.getOrderGroup().getId())
                        .itemId(orderDetail.getItem().getId())
                        .build();
        return orderDetailApiResponse;
    }

    @Override
    public Header<List<OrderDetailApiResponse>> search(Pageable pageable) {
        Page<OrderDetail> orderDetails = baseRepository.findAll(pageable);
        List<OrderDetailApiResponse> orderDetailApiResponses = orderDetails.stream()
                .map(orderDetail -> response(orderDetail))
                .collect(Collectors.toList());

        return Header.OK(orderDetailApiResponses);
    }
}
