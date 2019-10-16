package com.example.study.service;

import com.example.study.ifs.CrudInterface;
import com.example.study.model.entity.AdminUser;
import com.example.study.model.network.Header;
import com.example.study.model.network.request.AdminUserApiRequest;
import com.example.study.model.network.response.AdminUserApiResponse;
import com.example.study.repository.AdminUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminUserApiLogicService implements CrudInterface<AdminUserApiRequest, AdminUserApiResponse> {

    @Autowired
    AdminUserRepository adminUserRepository;

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

        AdminUser newAdminUser = adminUserRepository.save(adminUser);

        return response(adminUser);
    }

    @Override
    public Header<AdminUserApiResponse> read(Long id) {
        return adminUserRepository.findById(id).map(this::response).orElseGet(()->Header.ERROR("No Data"));
    }

    @Override
    public Header<AdminUserApiResponse> update(Header<AdminUserApiRequest> request) {
        AdminUserApiRequest body = request.getData();
        return adminUserRepository.findById(body.getId()).map(adminUser -> {
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
                .map(changeAdminUser -> adminUserRepository.save(changeAdminUser))
                .map(this::response)
                .orElseGet(()->Header.ERROR("No Data"));

    }

    @Override
    public Header delete(Long id) {
        return adminUserRepository
                .findById(id)
                .map(adminUser -> {
                    adminUserRepository.delete(adminUser);
                    return Header.OK();
                })
                .orElseGet(()->Header.ERROR("No Data"));
    }

    private Header<AdminUserApiResponse> response(AdminUser adminUser) {
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

        return Header.OK(adminUserApiResponse);
    }
}
