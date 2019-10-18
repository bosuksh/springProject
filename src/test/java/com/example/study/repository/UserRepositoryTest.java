package com.example.study.repository;

import com.example.study.StudyApplicationTests;
import com.example.study.model.entity.Item;
import com.example.study.model.entity.User;
import com.example.study.model.enumclass.UserStatus;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

public class UserRepositoryTest extends StudyApplicationTests {


    //Dependency Injection(DI)
    @Autowired
    private UserRepository userRepository;

    @Test
    public void create(){

        String account = "Test04";
        String password = "Test04";
        UserStatus status = UserStatus.REGISTERED;
        String email = "Test04@gmail.com";
        String phoneNumber = "010-1111-4444";
        LocalDateTime registeredAt = LocalDateTime.now();
     //   LocalDateTime createdAt = LocalDateTime.now();
       // String createdBy = "AdminServer";

//        User user = new User();
//        user.setAccount(account);
//        user.setPassword(password);
//        user.setStatus(status);
//        user.setEmail(email);
//        user.setPhoneNumber(phoneNumber);
//       // user.setCreatedBy(createdBy);
//       // user.setCreatedAt(createdAt);
//        user.setRegisteredAt(registeredAt);

        User u = User.builder()
                .account(account)
                .email(email)
                .status(status)
                .password(password)
                .phoneNumber(phoneNumber)
                .registeredAt(registeredAt)
                .build();

        User newUser = userRepository.save(u);
        Assert.assertNotNull(newUser);

    }

    @Test
    @Transactional
    public void read() {

        User user = userRepository.findFirstByPhoneNumberOrderByIdDesc("010-1111-2222");

        user.getOrderGroupList().stream().forEach(orderGroup -> {

            System.out.println("---------------주문묶음-----------------");
            System.out.println("수령인 : " + orderGroup.getRevName());
            System.out.println("수령지 : " + orderGroup.getRevAddress());
            System.out.println("총 금액 : " + orderGroup.getTotalPrice());
            System.out.println("총 수량 : " + orderGroup.getTotalQuantity());
            System.out.println("---------------주문상세-----------------");

            orderGroup.getOrderDetailList().forEach(orderDetail -> {
                System.out.println("카테고리 : " + orderDetail.getItem().getPartner().getCategory().getTitle());
                System.out.println("파트너사 이름: " + orderDetail.getItem().getPartner().getName());
                System.out.println("주문 상품 : " +orderDetail.getItem().getName());
                System.out.println("고객 센터 번호 : " + orderDetail.getItem().getPartner().getCallCenter());
                System.out.println("주문의 상태 : "+ orderDetail.getStatus());
                System.out.println("도착 예정 일자 : "+ orderDetail.getArrivalDate());


            });


        });
        Assert.assertNotNull(user);
    }

    @Test
    public void update() {
        Optional<User> user01 = userRepository.findById(2L);

        user01.ifPresent(selectUser -> {
            selectUser.setAccount("ppppp");
            selectUser.setUpdatedAt(LocalDateTime.now());
            selectUser.setCreatedBy("update method()");

            userRepository.save(selectUser);
        });


    }

    @Test
    @Transactional
    public void delete() {
        Optional<User> user01 = userRepository.findById(3L);

        Assert.assertTrue(user01.isPresent());

        user01.ifPresent(selectUser -> {
            userRepository.delete(selectUser);
        });

        Optional<User> deleteUser = userRepository.findById(2L);

        Assert.assertFalse(deleteUser.isPresent());
    }
}
