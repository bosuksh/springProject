package com.example.study.repository;

import com.example.study.StudyApplicationTests;
import com.example.study.model.entity.Category;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.Optional;
@RunWith(SpringRunner.class)
@DataJpaTest
public class CategoryRepositoryTest  {

    @Autowired
    CategoryRepository categoryRepository;

    @Test
    public void create() {
        String type = "COMPUTER";
        String title = "컴퓨터";
        LocalDateTime createdAt = LocalDateTime.now();
        String createdBy = "AdminServer";

        Category category = new Category();

        category.setTitle(title);
        category.setType(type);
        category.setCreatedBy(createdBy);
        category.setCreatedAt(createdAt);

        Category newCategory = categoryRepository.save(category);

        Assert.assertNotNull(newCategory);
        Assert.assertEquals(newCategory.getType(),type);
        Assert.assertEquals(newCategory.getTitle(),title);
    }

    @Test
    public void read() {
        String type = "COMPUTER";
        Optional<Category> optionalCategory = categoryRepository.findByType(type);

        // select * from category where type = 'Computer'

        optionalCategory.ifPresent(category -> {

            Assert.assertEquals(category.getType(),type);
            System.out.println(category.getTitle());
            System.out.println(category.getId());
            System.out.println(category.getType());

        });


    }

}
