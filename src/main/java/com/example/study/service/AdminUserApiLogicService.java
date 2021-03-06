package com.example.study.service;

import com.example.study.ifs.CrudInterface;
import com.example.study.model.entity.AdminUser;
import com.example.study.model.entity.OrderGroup;
import com.example.study.model.network.Header;
import com.example.study.model.network.request.AdminUserApiRequest;
import com.example.study.model.network.response.AdminUserApiResponse;
import com.example.study.repository.AdminUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminUserApiLogicService extends BaseService<AdminUserApiRequest, AdminUserApiResponse, AdminUser> {


    @Override
    public Header<AdminUserApiResponse> create(Header<AdminUserApiRequest> request) {
        AdminUserApiRequest body = request.getData();
        AdminUser adminUser = AdminUser.builder()
                .account(body.getAccount())
                .password(body.getPassword())
                .lastLoginAt(body.getLastLoginAt())
                .loginFailCount(body.getLoginFailCount())
                .registeredAt(body.getRegisteredAt())
                .unregisteredAt(body.getUnregisteredAt())
                .role(body.getRole())
                .status(body.getStatus())
                .passwordUpdatedAt(body.getPasswordUpdatedAt())
                .build();

        AdminUser newAdminUser = baseRepository.save(adminUser);

        return Header.OK(response(adminUser));
    }

    @Override
    public Header<AdminUserApiResponse> read(Long id) {
        return baseRepository.findById(id).map(user->Header.OK(response(user))).orElseGet(()->Header.ERROR("No Data"));
    }

    @Override
    public Header<AdminUserApiResponse> update(Header<AdminUserApiRequest> request) {
        AdminUserApiRequest body = request.getData();
        return baseRepository.findById(body.getId()).map(adminUser -> {
            adminUser.setAccount(body.getAccount())
                    .setRole(body.getRole())
                    .setStatus(body.getStatus())
                    .setPassword(body.getPassword())
                    .setLastLoginAt(body.getLastLoginAt())
                    .setLoginFailCount(body.getLoginFailCount())
                    .setRegisteredAt(body.getRegisteredAt())
                    .setPasswordUpdatedAt(body.getPasswordUpdatedAt())
                    .setUnregisteredAt(body.getUnregisteredAt());
            return adminUser;
        })
                .map(changeAdminUser -> baseRepository.save(changeAdminUser))
                .map(newUser->Header.OK(response(newUser)))
                .orElseGet(()->Header.ERROR("No Data"));

    }

    @Override
    public Header delete(Long id) {
        return baseRepository
                .findById(id)
                .map(adminUser -> {
                    baseRepository.delete(adminUser);
                    return Header.OK();
                })
                .orElseGet(()->Header.ERROR("No Data"));
    }

    private AdminUserApiResponse response(AdminUser adminUser) {
        AdminUserApiResponse adminUserApiResponse =
                AdminUserApiResponse.builder()
                        .id(adminUser.getId())
                        .account(adminUser.getAccount())
                        .password(adminUser.getPassword())
                        .loginFailCount(adminUser.getLoginFailCount())
                        .passwordUpdatedAt(adminUser.getPasswordUpdatedAt())
                        .status(adminUser.getStatus())
                        .role(adminUser.getRole())
                        .lastLoginAt(adminUser.getLastLoginAt())
                        .registeredAt(adminUser.getRegisteredAt())
                        .unregisteredAt(adminUser.getUnregisteredAt())
                        .build();

        return adminUserApiResponse;
    }

    @Override
    public Header<List<AdminUserApiResponse>> search(Pageable pageable) {
        Page<AdminUser> adminUsers = baseRepository.findAll(pageable);
        List<AdminUserApiResponse> adminUserApiResponseList = adminUsers.stream()
                .map(adminUser -> response(adminUser))
                .collect(Collectors.toList());
        return Header.OK(adminUserApiResponseList);
    }
}
