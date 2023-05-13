package com.driver;

import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class OrderRepository {
    Map<String, Order> orderMap = new HashMap<>();
    Map<String, DeliveryPartner> partnerMap = new HashMap<>();
    Map<String, String> orderPartnerMap = new HashMap<>();
    Map<String, List<String>> partnerOrderMap = new HashMap<>();
    public void addOrder(Order order) {
        orderMap.put(order.getId(), order);
    }

    public void addPartner(DeliveryPartner partner) {
        partnerMap.put(partner.getId(), partner);
    }

    public Optional<Order> getOrderById(String orderId) {
        if(orderMap.containsKey(orderId)) {
            return Optional.of(orderMap.get(orderId));
        }
        return Optional.empty();
    }

    public Optional<DeliveryPartner> getPartnerById(String partnerId) {
        if(partnerMap.containsKey(partnerId)) {
            return Optional.of(partnerMap.get(partnerId));
        }
        return Optional.empty();
    }

    public void addOrderPartnerPair(String orderId, String partnerId) {
        orderPartnerMap.put(orderId, partnerId);
        List<String> updateOrder = new ArrayList<>();
        if(partnerOrderMap.containsKey(partnerId)) {
            updateOrder = partnerOrderMap.get(partnerId);
        }
        updateOrder.add(orderId);
        partnerOrderMap.put(partnerId, updateOrder);
    }

    public Map<String, String> getAllOrderPartnerMappings() {
        return orderPartnerMap;
    }

    public List<String> getAllOrderForPartner(String partnerId) {
        return partnerOrderMap.get(partnerId);
    }

    public List<String> getAllOrders() {
        return new ArrayList<>(orderMap.keySet());
    }

    public List<String> getAllAssignedOrders() {
        return new ArrayList<>(orderPartnerMap.keySet());
    }

    public void deletePartnerForId(String partnerId) {
        partnerMap.remove(partnerId);
        partnerOrderMap.remove(partnerId);
    }

    public void removeOrderPartnerMappings(String orderId) {
        orderPartnerMap.remove(orderId);
    }

    public void deleteOrderForId(String orderId) {
        orderMap.remove(orderId);
        orderPartnerMap.remove(orderId);
    }

    public String getPartnerForOrder(String orderId) {
        return orderPartnerMap.get(orderId);
    }

    public void removeOrderForPartner(String partnerId, String orderId) {
        List<String> orderIds = partnerOrderMap.get(partnerId);
        orderIds.remove(orderId);
        partnerOrderMap.put(partnerId, orderIds);
    }
}