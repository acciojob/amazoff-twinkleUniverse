package com.driver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class OrderService {
    @Autowired
    OrderRepository orderRepository;

    public void addOrder(Order order) {
        orderRepository.addOrder(order);
    }

    public void addPartner(String partnerId) {
        DeliveryPartner partner = new DeliveryPartner(partnerId);
        orderRepository.addPartner(partner);
    }

    public void addOrderPartnerPair(String orderId, String partnerId) {
        Optional<Order> orderOpt =  orderRepository.getOrderById(orderId);
        Optional<DeliveryPartner> partnerOpt = orderRepository.getPartnerById(partnerId);

        if(orderOpt.isPresent() && partnerOpt.isPresent()) {
            DeliveryPartner partner = partnerOpt.get();
            Integer iniOrderCount = partner.getNumberOfOrders();
            iniOrderCount++;
            partner.setNumberOfOrders(iniOrderCount);
            orderRepository.addPartner(partner);
            orderRepository.addOrderPartnerPair(orderId, partnerId);
        }
    }

    public Order getOrderById(String orderId) throws RuntimeException {
        Optional<Order> order = orderRepository.getOrderById(orderId);
        if(order.isPresent()) {
            return order.get();
        }
        throw new RuntimeException("Order not found");
    }
    public DeliveryPartner getPartnerForId(String partnerId) {
        Optional<DeliveryPartner> partner = orderRepository.getPartnerById(partnerId);
        if(partner.isPresent()) {
            return partner.get();
        }
        throw new RuntimeException("Partner not found");
    }

    public Integer getOrderCountByPartnerId(String partnerId) {
        Optional<DeliveryPartner> partner = orderRepository.getPartnerById(partnerId);
        if(partner.isPresent()) {
            return partner.get().getNumberOfOrders();
        }
        return 0;
    }

    public List<String> getOrdersByPartnerId(String partnerId) {
        Map<String, String> orderPartnerMap = orderRepository.getAllOrderPartnerMappings();
        List<String> orderIds = new ArrayList<>();
        for(var entry : orderPartnerMap.entrySet()) {
            if(entry.getValue().equals(partnerId)) {
                orderIds.add(entry.getKey());
            }
        }
        return orderIds;
    }

    public List<String> getAllOrders() {
        return orderRepository.getAllOrders();
    }

    public Integer getCountOfUnassignedOrders() {
        return orderRepository.getAllOrders().size() - orderRepository.getAllAssignedOrders().size();
    }

    public Integer getOrdersLeftAfterGivenTimeByPartnerId(String time, String partnerId) {
        List<String> orderIds = orderRepository.getAllOrderForPartner(partnerId);
        int currTime = Order.convertDeliveryTime(time);
        int orderLefts = 0;

        for(String orderId : orderIds) {
            int deliveryTime = orderRepository.getOrderById(orderId).get().getDeliveryTime();
            if(deliveryTime > currTime) orderLefts++;
        }
        return orderLefts;
    }

    public String getLastDeliveryTimeForPartnerId(String partnerId) {
        List<String> orderIds = orderRepository.getAllOrderForPartner(partnerId);
        int max = 0;
        for(String orderId : orderIds) {
            int deliveryTime = orderRepository.getOrderById(orderId).get().getDeliveryTime();
            max = Math.max(max, deliveryTime);
        }
        return Order.convertDeliveryTime(max);
    }

    public void deletePartner(String partnerId) {
        List<String> orders = orderRepository.getAllOrderForPartner(partnerId);
        orderRepository.deletePartnerForId(partnerId);

        for(String orderId : orders) {
            orderRepository.removeOrderPartnerMappings(orderId);
        }
    }

    public void deleteOrder(String orderId) {
        String partnerId = orderRepository.getPartnerForOrder(orderId);
        if(Objects.nonNull(partnerId)) {
            DeliveryPartner partner = orderRepository.getPartnerById(partnerId).get();
            Integer iniOrderCount = partner.getNumberOfOrders();
            iniOrderCount--;
            partner.setNumberOfOrders(iniOrderCount);
            orderRepository.addPartner(partner);
            orderRepository.removeOrderForPartner(partnerId, orderId);
        }
    }
}