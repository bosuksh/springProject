package com.example.study.service;

import com.example.study.ifs.CrudInterface;
import com.example.study.model.entity.Category;
import com.example.study.model.network.Header;
import com.example.study.model.network.request.CategoryApiRequest;
import com.example.study.model.network.response.CategoryApiResponse;
import com.example.study.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
        return Header.OK(response(newCategory));
    }

    @Override
    public Header<CategoryApiResponse> read(Long id) {
        return baseRepository.findById(id).map(category -> Header.OK(response(category))).orElseGet(()->Header.ERROR("No Data"));
    }

    @Override
    public Header<CategoryApiResponse> update(Header<CategoryApiRequest> request) {
        CategoryApiRequest body = request.getData();
        return baseRepository.findById(body.getId()).map(category -> {
            category.setTitle(body.getTitle())
                    .setType(body.getType());
            return category;
        }).map(changeCategory-> baseRepository.save(changeCategory))
                .map(category -> Header.OK(response(category)))
                .orElseGet(()->Header.ERROR("No Data"));
    }

    @Override
    public Header delete(Long id) {
        return baseRepository.findById(id).map(category -> {
            baseRepository.delete(category);
            return Header.OK();
        }).orElseGet(()->Header.ERROR("No Data"));
    }

    private CategoryApiResponse response(Category category) {
        CategoryApiResponse categoryApiResponse =
                CategoryApiResponse.builder()
                        .id(category.getId())
                        .type(category.getType())
                        .title(category.getTitle())
                        .build();

        return categoryApiResponse;
    }

    @Override
    public Header<List<CategoryApiResponse>> search(Pageable pageable) {
        Page<Category> categories = baseRepository.findAll(pageable);
        List<CategoryApiResponse> categoryApiResponseList = categories.stream()
                .map(category -> response(category))
                .collect(Collectors.toList());
        return Header.OK(categoryApiResponseList);
    }
}
