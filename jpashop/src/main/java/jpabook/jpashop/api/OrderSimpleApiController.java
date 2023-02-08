package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.*;

// x to One 성능최적화
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for(Order order : all){
            order.getMember().getName(); // Lazy 강제 초기화
        }
        return all;
    }

    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2(){
        return orderRepository.findAllByString(new OrderSearch()).stream()
                .map(SimpleOrderDto::new) // 루프마다 쿼리가 2방씩나가는 문제가 있음
                .collect(toList()); // N + 1 = 1 + 회원 N + 배송 N

//        List<Order> orders = orderRepository.findAllByString(new OrderSearch()).stream() 위와 같은 코드
//                .map(o -> new SimpleOrderDto(orders)) map은 a를 b로 바꾸는함수
//                .collect(Collectors.toList());
    }

    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3(){
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(toList());

        return result;
    }

    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> ordersV4() {
        return orderSimpleQueryRepository.findOrderDtos();
    }



    @Data
    private class SimpleOrderDto { // dto는 entity를 파라미터로 받아도 괜찮다
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        
        public SimpleOrderDto(Order order){
            orderId = order.getId();
            name = order.getMember().getName(); // LAZY 초기화 - 영속성 컨테스트에 찾아보고 없으면 쿼리날림
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress(); // LAZY 초기화
        }
    }
}
