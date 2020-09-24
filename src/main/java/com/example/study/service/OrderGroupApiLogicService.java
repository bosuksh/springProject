package com.example.study.service;

import com.example.study.ifs.CrudInterface;
import com.example.study.model.entity.OrderGroup;
import com.example.study.model.entity.User;
import com.example.study.model.network.Header;
import com.example.study.model.network.Pagination;
import com.example.study.model.network.request.OrderGroupApiRequest;
import com.example.study.model.network.response.OrderGroupApiResponse;
import com.example.study.model.network.response.UserApiResponse;
import com.example.study.repository.OrderGroupRepository;
import com.example.study.repository.UserRepository;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderGroupApiLogicService extends BaseService<OrderGroupApiRequest, OrderGroupApiResponse,OrderGroup> {


    @Autowired
    UserRepository userRepository;
    @Override
    public Header<OrderGroupApiResponse> create(Header<OrderGroupApiRequest> request) {
        OrderGroupApiRequest body = request.getData();
        OrderGroup orderGroup = OrderGroup.builder()
                .status(body.getStatus())
                .paymentType(body.getPaymentType())
                .orderType(body.getOrderType())
                .revAddress(body.getRevAddress())
                .revName(body.getRevName())
                .totalPrice(body.getTotalPrice())
                .totalQuantity(body.getTotalQuantity())
                .orderAt(body.getOrderAt())
                .user(userRepository.getOne(body.getUserId()))
                .build();
        OrderGroup newOrderGroup = baseRepository.save(orderGroup);

        return Header.OK(response(newOrderGroup));
    }

    @Override
    public Header<OrderGroupApiResponse> read(Long id) {
        return baseRepository.findById(id)
                .map(orderGroup -> Header.OK(response(orderGroup)))
                .orElseGet(()->Header.ERROR("No Data"));
    }

    @Override
    public Header<OrderGroupApiResponse> update(Header<OrderGroupApiRequest> request) {
        OrderGroupApiRequest body = request.getData();
        return baseRepository.findById(body.getId())
                .map(orderGroup -> {
                    orderGroup
                            .setRevName(body.getRevName())
                            .setStatus(body.getStatus())
                            .setUser(userRepository.getOne(body.getUserId()))
                            .setStatus(body.getStatus())
                            .setOrderType(body.getOrderType())
                            .setRevName(body.getRevName())
                            .setArrivalDate(body.getArrivalDate())
                            .setOrderAt(body.getOrderAt())
                            .setTotalQuantity(body.getTotalQuantity())
                            .setTotalPrice(body.getTotalPrice())
                            .setPaymentType(body.getPaymentType());

                    return orderGroup;
                })
                .map(changeOrderGroup -> baseRepository.save(changeOrderGroup))
                .map(orderGroup -> Header.OK(response(orderGroup)))
                .orElseGet(()->Header.ERROR("No Data"));
    }

    @Override
    public Header delete(Long id) {
        return baseRepository.findById(id)
                .map(orderGroup -> {
                    baseRepository.delete(orderGroup);
                    return Header.OK();
                }).orElseGet(()->Header.ERROR("No Data"));
    }

    public OrderGroupApiResponse response(OrderGroup orderGroup) {
        OrderGroupApiResponse orderGroupApiResponse = OrderGroupApiResponse.builder()
                .id(orderGroup.getId())
                .status(orderGroup.getStatus())
                .orderType(orderGroup.getOrderType())
                .revAddress(orderGroup.getRevAddress())
                .revName(orderGroup.getRevName())
                .paymentType(orderGroup.getPaymentType())
                .totalPrice(orderGroup.getTotalPrice())
                .totalQuantity(orderGroup.getTotalQuantity())
                .orderAt(orderGroup.getOrderAt())
                .arrivalDate(orderGroup.getArrivalDate())
                .userId(orderGroup.getUser().getId())
                .build();
        return orderGroupApiResponse;
    }

    @Override
    public Header<List<OrderGroupApiResponse>> search(Pageable pageable) {
        Page<OrderGroup> orderGroups = baseRepository.findAll(pageable);
        List<OrderGroupApiResponse> orderGroupApiResponses = orderGroups.stream()
                .map(orderGroup -> response(orderGroup))
                .collect(Collectors.toList());
        Pagination pagination = Pagination.builder()
                .totalPages(orderGroups.getTotalPages())
                .totalElements(orderGroups.getTotalElements())
                .currentPage(orderGroups.getNumber())
                .currentElements(orderGroups.getNumberOfElements())
                .build();
        return Header.OK(orderGroupApiResponses,pagination);
    }
}
