package com.andy.warehouse.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sys_organization")
@Getter
@Setter
public class Organization extends BaseEntity {

    @Column(name = "org_code", unique = true, nullable = false, length = 50)
    private String orgCode;

    @Column(name = "org_name", nullable = false, length = 100)
    private String orgName;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "address", length = 200)
    private String address;

    @Column(name = "contact_person", length = 50)
    private String contactPerson;

    @Column(name = "contact_phone", length = 20)
    private String contactPhone;

    @Column(name = "status")
    private Integer status = 1;

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Department> departments = new ArrayList<>();

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<User> users = new ArrayList<>();
}
