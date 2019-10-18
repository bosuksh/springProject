package com.example.study.service;


import com.example.study.model.entity.Partner;
import com.example.study.model.network.Header;
import com.example.study.model.network.request.PartnerApiRequest;
import com.example.study.model.network.response.PartnerApiResponse;
import com.example.study.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PartnerApiLogicService extends BaseService<PartnerApiRequest, PartnerApiResponse,Partner> {


    @Autowired
    CategoryRepository categoryRepository;

    @Override
    public Header<PartnerApiResponse> create(Header<PartnerApiRequest> request) {
        PartnerApiRequest body = request.getData();
        Partner partner =
                Partner
                        .builder()
                        .id(body.getId())
                        .name(body.getName())
                        .status(body.getStatus())
                        .ceoName(body.getCeoName())
                        .businessNumber(body.getBusinessNumber())
                        .address(body.getAddress())
                        .partnerNumber(body.getPartnerNumber())
                        .callCenter(body.getCallCenter())
                        .registeredAt(body.getRegisteredAt())
                        .unregisteredAt(body.getUnregisteredAt())
                        .category(categoryRepository.getOne(body.getCategoryId()))
                        .build();
        Partner newPartner = baseRepository.save(partner);
        return Header.OK(response(newPartner));
    }

    @Override
    public Header<PartnerApiResponse> read(Long id) {
        return baseRepository.findById(id).map(partner -> Header.OK(response(partner))).orElseGet(()->Header.ERROR("No Data"));
    }

    @Override
    public Header<PartnerApiResponse> update(Header<PartnerApiRequest> request) {
        PartnerApiRequest body = request.getData();
        return baseRepository.findById(body.getId()).map(partner -> {
                    partner.setStatus(body.getStatus())
                            .setAddress(body.getAddress())
                            .setCeoName(body.getCeoName())
                            .setBusinessNumber(body.getBusinessNumber())
                            .setPartnerNumber(body.getPartnerNumber())
                            .setCallCenter(body.getCallCenter())
                            .setName(body.getName())
                            .setCategory(categoryRepository.getOne(body.getCategoryId()))
                            .setRegisteredAt(body.getRegisteredAt())
                            .setUnregisteredAt(body.getUnregisteredAt());
                    return partner;
                }).map(changePartner->baseRepository.save(changePartner))
                .map(partner -> Header.OK(response(partner)))
                .orElseGet(()->Header.ERROR("No Data"));
    }

    @Override
    public Header delete(Long id) {
        return baseRepository.findById(id)
                .map(partner -> {
                    baseRepository.delete(partner);
                    return Header.OK();
                }).orElseGet(()->Header.ERROR("No Data"));
    }

    private PartnerApiResponse response(Partner partner) {
        PartnerApiResponse partnerApiResponse =
                PartnerApiResponse
                        .builder()
                        .id(partner.getId())
                        .name(partner.getName())
                        .status(partner.getStatus())
                        .ceoName(partner.getCeoName())
                        .businessNumber(partner.getBusinessNumber())
                        .address(partner.getAddress())
                        .partnerNumber(partner.getPartnerNumber())
                        .callCenter(partner.getCallCenter())
                        .registeredAt(partner.getRegisteredAt())
                        .unregisteredAt(partner.getUnregisteredAt())
                        .categoryId(partner.getCategory().getId())
                        .build();


        return partnerApiResponse;
    }

    @Override
    public Header<List<PartnerApiResponse>> search(Pageable pageable) {
        Page<Partner> partners = baseRepository.findAll(pageable);
        List<PartnerApiResponse> partnerApiResponses = partners.stream()
                .map(partner -> response(partner))
                .collect(Collectors.toList());
        return Header.OK(partnerApiResponses);
    }
}
