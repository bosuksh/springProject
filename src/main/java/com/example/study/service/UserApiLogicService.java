package com.example.study.service;

import com.example.study.ifs.CrudInterface;
import com.example.study.model.entity.OrderGroup;
import com.example.study.model.entity.User;
import com.example.study.model.enumclass.UserStatus;
import com.example.study.model.network.Header;
import com.example.study.model.network.Pagination;
import com.example.study.model.network.request.UserApiRequest;
import com.example.study.model.network.response.ItemApiResponse;
import com.example.study.model.network.response.OrderGroupApiResponse;
import com.example.study.model.network.response.UserApiResponse;
import com.example.study.model.network.response.UserOrderInfoApiResponse;
import com.example.study.repository.UserRepository;
import net.bytebuddy.asm.Advice;
import org.hibernate.cfg.CreateKeySecondPass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserApiLogicService extends BaseService<UserApiRequest, UserApiResponse,User> {


    // 1. request Data
    // 2. user 생성
    // 3. 생성된 데이터 -> UserApi Return

    @Autowired
    OrderGroupApiLogicService orderGroupApiLogicService;

    @Autowired
    ItemApiLogicService itemApiLogicService;

    @Override
    public Header<UserApiResponse> create(Header<UserApiRequest> request) {

        // 1. request data
        UserApiRequest userApiRequest = request.getData();

        // 2. User 설정
        User user = User.builder()
                .account(userApiRequest.getAccount())
                .email(userApiRequest.getEmail())
                .status(UserStatus.REGISTERED)
                .password(userApiRequest.getPassword())
                .phoneNumber(userApiRequest.getPhoneNumber())
                .registeredAt(LocalDateTime.now())
                .build();

        User newUser = baseRepository.save(user);


        // 3. 생성된 데이터 -> userApiResponse Return
        return Header.OK(response(newUser));
    }

    @Override
    public Header<UserApiResponse> read(Long id) {
        // id-> repository getOne, getById

        Optional<User> findUser = baseRepository.findById(id);

        // user-> userApiResponseReturn

        return findUser.map(user->Header.OK(response(user)))
                .orElseGet(
                        ()->Header.ERROR("No Data")
                );
    }

    @Override
    public Header<UserApiResponse> update(Header<UserApiRequest> request) {

        // 1. data

        UserApiRequest userApiRequest = request.getData();

        // 2. id->user 데이터를 찾고

        Optional<User> optionalUser = baseRepository.findById(userApiRequest.getId());

        return optionalUser.map(user -> {

            user.setAccount(userApiRequest.getAccount())
                    .setPassword(userApiRequest.getPassword())
                    .setEmail(userApiRequest.getEmail())
                    .setStatus(userApiRequest.getStatus())
                    .setPhoneNumber(userApiRequest.getPhoneNumber())
                    .setRegisteredAt(userApiRequest.getRegisteredAt())
                    .setUnregisteredAt(userApiRequest.getUnregisteredAt());
            return user;

        })
                // 3. update
                .map(user->baseRepository.save(user))
                // 4. response
                .map(user->Header.OK(response(user)))
                .orElseGet(()->Header.ERROR("No Data"));

    }

    @Override
    public Header delete(Long id) {


        // 1. id -> repository ->user
        Optional<User> optionalUser = baseRepository.findById(id);

        // 2. repository -> delete
        return optionalUser.map(user -> {
            baseRepository.delete(user);
            return Header.OK();
        })
                // 3.response Data
                .orElseGet(() -> Header.ERROR("No Data"));
       //return ;
    }

    private UserApiResponse response(User user) {
        // user-> userApiResponse

        UserApiResponse userApiResponse = UserApiResponse.builder()
                .id(user.getId())
                .account(user.getAccount())
                .password(user.getPassword())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .status(user.getStatus())
                .registeredAt(user.getRegisteredAt())
                .unregisteredAt(user.getUnregisteredAt())
                .build();

        // Header + data return;

        return userApiResponse;
    }

    public Header<List<UserApiResponse>> search(Pageable pageable) {
        Page<User> users = baseRepository.findAll(pageable);
        List<UserApiResponse> userApiResponseList = users.stream()
                .map(user->response(user))
                .collect(Collectors.toList());
        // List<UserApiResponse>
        //Header<List<UserApiResponse>>
        Pagination pagination = Pagination.builder()
                .totalPages(users.getTotalPages())
                .totalElements(users.getTotalElements())
                .currentPage(users.getNumber())
                .currentElements(users.getNumberOfElements())
                .build();

        return Header.OK(userApiResponseList,pagination);
    }

    public Header<UserOrderInfoApiResponse> orderInfo(Long id) {

        //user
        User user = baseRepository.getOne(id);
        UserApiResponse userApiResponse = response(user);

        //orderGroup
        List<OrderGroup> orderGroupList = user.getOrderGroupList();
        List<OrderGroupApiResponse> orderGroupApiResponseList = orderGroupList.stream()
                .map(orderGroup -> {

                   OrderGroupApiResponse orderGroupApiResponse = orderGroupApiLogicService.response(orderGroup);

                   //item api response
                   List<ItemApiResponse> itemApiResponseList = orderGroup.getOrderDetailList().stream()
                           .map(orderDetail -> orderDetail.getItem())
                           .map(item -> itemApiLogicService.response(item))
                           .collect(Collectors.toList());
                   orderGroupApiResponse.setItemApiResponseList(itemApiResponseList);

                   return orderGroupApiResponse;
                })
                .collect(Collectors.toList());
        userApiResponse.setOrderGroupApiResponseList(orderGroupApiResponseList);

        UserOrderInfoApiResponse userOrderInfoApiResponse = UserOrderInfoApiResponse.builder()
                .userApiResponse(userApiResponse).build();

        return Header.OK(userOrderInfoApiResponse);

    }

}
