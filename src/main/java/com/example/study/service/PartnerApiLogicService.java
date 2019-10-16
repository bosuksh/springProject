package com.example.study.service;

import com.example.study.ifs.CrudInterface;
import com.example.study.model.entity.Category;
import com.example.study.model.entity.Partner;
import com.example.study.model.network.Header;
import com.example.study.model.network.request.PartnerApiRequest;
import com.example.study.model.network.response.PartnerApiResponse;
import com.example.study.repository.CategoryRepository;
import com.example.study.repository.PartnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PartnerApiLogicService implements CrudInterface<PartnerApiRequest, PartnerApiResponse> {

    @Autowired
    PartnerRepository partnerRepository;
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
        Partner newPartner = partnerRepository.save(partner);
        return response(newPartner);
    }

    @Override
    public Header<PartnerApiResponse> read(Long id) {
        return partnerRepository.findById(id).map(this::response).orElseGet(()->Header.ERROR("No Data"));
    }

    @Override
    public Header<PartnerApiResponse> update(Header<PartnerApiRequest> request) {
        PartnerApiRequest body = request.getData();
        return partnerRepository.findById(body.getId()).map(partner -> {
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
                }).map(changePartner->partnerRepository.save(changePartner))
                .map(this::response)
                .orElseGet(()->Header.ERROR("No Data"));
    }

    @Override
    public Header delete(Long id) {
        return partnerRepository.findById(id)
                .map(partner -> {
                    partnerRepository.delete(partner);
                    return Header.OK();
                }).orElseGet(()->Header.ERROR("No Data"));
    }

    private Header<PartnerApiResponse> response(Partner partner) {
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


        return Header.OK(partnerApiResponse);
    }

}
