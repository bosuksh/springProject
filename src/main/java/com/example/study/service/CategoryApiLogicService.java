package com.example.study.service;

import com.example.study.ifs.CrudInterface;
import com.example.study.model.entity.Category;
import com.example.study.model.network.Header;
import com.example.study.model.network.request.CategoryApiRequest;
import com.example.study.model.network.response.CategoryApiResponse;
import com.example.study.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryApiLogicService extends BaseService<CategoryApiRequest, CategoryApiResponse,Category> {

    @Override
    public Header<CategoryApiResponse> create(Header<CategoryApiRequest> request) {
        CategoryApiRequest body = request.getData();
        Category category =
                Category.builder()
                        .title(body.getTitle())
                        .type(body.getType())
                        .build();
        Category newCategory = baseRepository.save(category);
        return response(newCategory);
    }

    @Override
    public Header<CategoryApiResponse> read(Long id) {
        return baseRepository.findById(id).map(this::response).orElseGet(()->Header.ERROR("No Data"));
    }

    @Override
    public Header<CategoryApiResponse> update(Header<CategoryApiRequest> request) {
        CategoryApiRequest body = request.getData();
        return baseRepository.findById(body.getId()).map(category -> {
            category.setTitle(body.getTitle())
                    .setType(body.getType());
            return category;
        }).map(changeCategory-> baseRepository.save(changeCategory))
                .map(this::response)
                .orElseGet(()->Header.ERROR("No Data"));
    }

    @Override
    public Header delete(Long id) {
        return baseRepository.findById(id).map(category -> {
            baseRepository.delete(category);
            return Header.OK();
        }).orElseGet(()->Header.ERROR("No Data"));
    }

    private Header<CategoryApiResponse> response(Category category) {
        CategoryApiResponse categoryApiResponse =
                CategoryApiResponse.builder()
                        .id(category.getId())
                        .type(category.getType())
                        .title(category.getTitle())
                        .build();

        return Header.OK(categoryApiResponse);
    }
}
