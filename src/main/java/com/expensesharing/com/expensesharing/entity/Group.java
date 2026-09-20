package com.expensesharing.com.expensesharing.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "expense_group")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Group name is mandatory")
    private String name;

    @ElementCollection
    @CollectionTable(name = "group_members", joinColumns = @JoinColumn(name = "group_id"))
    @Column(name = "user_id")
    private List<Long> memberUserIds = new ArrayList<>();

    public Group() {
    }

    public Group(String name, List<Long> memberUserIds) {
        this.name = name;
        this.memberUserIds = memberUserIds;
    }

    public Long getId() {
        return id;
    }

    public @NotBlank(message = "Group name is mandatory") String getName() {
        return name;
    }

    public void setName(@NotBlank(message = "Group name is mandatory") String name) {
        this.name = name;
    }

    public List<Long> getMemberUserIds() {
        return memberUserIds;
    }

    public void setMemberUserIds(List<Long> memberUserIds) {
        this.memberUserIds = memberUserIds;
    }
}
