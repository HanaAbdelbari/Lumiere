package com.marketplace.lumiere.order;

import jakarta.persistence.*;

@Entity
@Table(name = "customer_info")
public class CustomerInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // One-to-one with the order.
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(nullable = false, length = 50)
    private String governorate;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String address;

    // Optional — e.g. ring size, delivery notes.
    @Column(columnDefinition = "TEXT")
    private String notes;

    protected CustomerInfo() {
    }

    public CustomerInfo(String fullName, String phone, String governorate,
                        String address, String notes) {
        this.fullName = fullName;
        this.phone = phone;
        this.governorate = governorate;
        this.address = address;
        this.notes = notes;
    }

    public Long getId() {
        return id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhone() {
        return phone;
    }

    public String getGovernorate() {
        return governorate;
    }

    public String getAddress() {
        return address;
    }

    public String getNotes() {
        return notes;
    }
}