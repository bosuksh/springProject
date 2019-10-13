package com.example.study.service;

import com.example.study.ifs.CrudInterface;
import com.example.study.model.entity.User;
import com.example.study.model.network.Header;
import com.example.study.model.network.request.UserApiRequest;
import com.example.study.model.network.response.UserApiResponse;
import com.example.study.repository.UserRepository;
import org.hibernate.cfg.CreateKeySecondPass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserApiLogicService implements CrudInterface<UserApiRequest, UserApiResponse> {


    @Autowired
    private UserRepository userRepository;

    // 1. request Data
    // 2. user 생성
    // 3. 생성된 데이터 -> UserApi Return

    @Override
    public Header<UserApiResponse> create(Header<UserApiRequest> request) {

        // 1. request data
        UserApiRequest userApiRequest = request.getData();

        // 2. User 설정
        User user = User.builder()
                .account(userApiRequest.getAccount())
                .email(userApiRequest.getEmail())
                .status("REGISTERED")
                .password(userApiRequest.getPhoneNumber())
                .phoneNumber(userApiRequest.getPhoneNumber())
                .registeredAt(LocalDateTime.now())
                .build();

        User newUser = userRepository.save(user);


        // 3. 생성된 데이터 -> userApiResponse Return
        return response(newUser);
    }

    @Override
    public Header<UserApiResponse> read(Long id) {
        // id-> repository getOne, getById

        Optional<User> findUser = userRepository.findById(id);

        // user-> userApiResponseReturn

        return findUser.map(user->response(user))
                .orElseGet(
                        ()->Header.ERROR("No Data")
                );
    }

    @Override
    public Header<UserApiResponse> update(Header<UserApiRequest> request) {

        // 1. data

        UserApiRequest userApiRequest = request.getData();

        // 2. id->user 데이터를 찾고

        Optional<User> optionalUser = userRepository.findById(userApiRequest.getId());

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
                .map(user->userRepository.save(user))
                // 4. response
                .map(user->response(user))
                .orElseGet(()->Header.ERROR("No Data"));

    }

    @Override
    public Header delete(Long id) {


        // 1. id -> repository ->user
        Optional<User> optionalUser = userRepository.findById(id);

        // 2. repository -> delete
        return optionalUser.map(user -> {
            userRepository.delete(user);
            return Header.OK();
        })
                // 3.response Data
                .orElseGet(() -> Header.ERROR("No Data"));
       //return ;
    }

    private Header<UserApiResponse> response(User user) {
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

        return Header.OK(userApiResponse);
    }
}
