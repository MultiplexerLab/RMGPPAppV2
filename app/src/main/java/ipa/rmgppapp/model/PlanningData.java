package ipa.rmgppapp.model;

public class PlanningData {
    private String id;
    private String buyer;
    private String style;
    private String item;
    private String description;
    private String orderNo;
    private String shipmentData;
    private String plannedQuantity;
    private String status;

    public PlanningData(String buyer, String style, String item, String description, String orderNo, String shipmentData, String plannedQuantity) {
        this.buyer = buyer;
        this.style = style;
        this.item = item;
        this.description = description;
        this.orderNo = orderNo;
        this.shipmentData = shipmentData;
        this.plannedQuantity = plannedQuantity;
    }

    public PlanningData(String id, String buyer, String style, String item, String description, String orderNo, String shipmentData, String plannedQuantity, String status) {
        this.id = id;
        this.buyer = buyer;
        this.style = style;
        this.item = item;
        this.description = description;
        this.orderNo = orderNo;
        this.shipmentData = shipmentData;
        this.plannedQuantity = plannedQuantity;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBuyer() {
        return buyer;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setBuyer(String buyer) {
        this.buyer = buyer;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getShipmentData() {
        return shipmentData;
    }

    public void setShipmentData(String shipmentData) {
        this.shipmentData = shipmentData;
    }

    public String getPlannedQuantity() {
        return plannedQuantity;
    }

    public void setPlannedQuantity(String plannedQuantity) {
        this.plannedQuantity = plannedQuantity;
    }
}
