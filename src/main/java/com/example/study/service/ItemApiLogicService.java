package com.example.study.service;

import com.example.study.ifs.CrudInterface;
import com.example.study.model.entity.Item;
import com.example.study.model.network.Header;
import com.example.study.model.network.Pagination;
import com.example.study.model.network.request.ItemApiRequest;
import com.example.study.model.network.response.ItemApiResponse;
import com.example.study.repository.ItemRepository;
import com.example.study.repository.PartnerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ItemApiLogicService extends BaseService<ItemApiRequest,ItemApiResponse,Item> {

    @Autowired
    private PartnerRepository partnerRepository;

    @Override
    public Header<ItemApiResponse> create(Header<ItemApiRequest> request) {
        ItemApiRequest itemApiRequest = request.getData();

        Item item = Item.builder()
                .status(itemApiRequest.getStatus())
                .name(itemApiRequest.getName())
                .brandName(itemApiRequest.getBrandName())
                .price(itemApiRequest.getPrice())
                .content(itemApiRequest.getContent())
                .title(itemApiRequest.getTitle())
                .registeredAt(LocalDateTime.now())
                .partner(partnerRepository.getOne(itemApiRequest.getPartnerId()))
                .build();

        Item newItem = baseRepository.save(item);
        log.info("{}",newItem.getId());
        return Header.OK(response(newItem));
    }

    @Override
    public Header<ItemApiResponse> read(Long id) {
        Optional<Item> optionalItem = baseRepository.findById(id);

        return optionalItem.map(item -> Header.OK(response(item)))
                .orElseGet(()->Header.ERROR("No Data"));

    }

    @Override
    public Header<ItemApiResponse> update(Header<ItemApiRequest> request) {
        ItemApiRequest itemApiRequest = request.getData();

        Optional<Item> optionalItem = baseRepository.findById(itemApiRequest.getId());

        return optionalItem.map(item -> {
            item.setName(itemApiRequest.getName())
                    .setRegisteredAt(itemApiRequest.getRegisteredAt())
                    .setTitle(itemApiRequest.getTitle())
                    .setStatus(itemApiRequest.getStatus())
                    .setContent(itemApiRequest.getContent())
                    .setPrice(itemApiRequest.getPrice())
                    .setBrandName(itemApiRequest.getBrandName())
                    .setUnregisteredAt(itemApiRequest.getUnregisteredAt())
                    .setPartner(partnerRepository.getOne(itemApiRequest.getPartnerId()));
            Item newItem = baseRepository.save(item);
            return Header.OK(response(newItem));
        }).orElseGet(()->Header.ERROR("No Data"));
    }

    @Override
    public Header delete(Long id) {

    return baseRepository.findById(id)
                .map(item -> {
                    baseRepository.delete(item);
                    return Header.OK();
                })
                .orElseGet(()->Header.ERROR("No Data"));
    }

    public ItemApiResponse response(Item item) {
        ItemApiResponse body = ItemApiResponse.builder()
                .id(item.getId())
                .status(item.getStatus())
                .name(item.getName())
                .title(item.getTitle())
                .content(item.getContent())
                .brandName(item.getBrandName())
                .registeredAt(item.getRegisteredAt())
                .unregisteredAt(item.getUnregisteredAt())
                .partnerId(item.getPartner().getId())
                .price(item.getPrice())
                .build();

        return body;
    }

    @Override
    public Header<List<ItemApiResponse>> search(Pageable pageable) {
        Page<Item> items = baseRepository.findAll(pageable);
        List<ItemApiResponse> itemApiResponseList = items.stream()
                .map(item -> response(item))
                .collect(Collectors.toList());
        Pagination pagination = Pagination.builder()
                .totalPages(items.getTotalPages())
                .totalElements(items.getTotalElements())
                .currentPage(items.getNumber())
                .currentElements(items.getNumberOfElements())
                .build();
        return Header.OK(itemApiResponseList, pagination);
    }
}
