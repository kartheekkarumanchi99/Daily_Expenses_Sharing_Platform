package com.expensesharing.com.expensesharing.service;

import com.expensesharing.com.expensesharing.dto.Settlement;
import com.expensesharing.com.expensesharing.entity.Expense;
import com.expensesharing.com.expensesharing.entity.Group;
import com.expensesharing.com.expensesharing.repositories.ExpenseRepository;
import com.expensesharing.com.expensesharing.repositories.GroupRepository;
import com.expensesharing.com.expensesharing.repositories.UserRepository;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private ExpenseService expenseService;

    public Group createGroup(Group group) {
        validateMembers(group.getMemberUserIds());
        return groupRepository.save(group);
    }

    public Group getGroupById(Long id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Group not found"));
    }

    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    public Group addMember(Long groupId, Long userId) {
        Group group = getGroupById(groupId);
        userRepository.findById(userId)
                .orElseThrow(() -> new ValidationException("User not found"));
        if (!group.getMemberUserIds().contains(userId)) {
            group.getMemberUserIds().add(userId);
            groupRepository.save(group);
        }
        return group;
    }

    public List<Expense> getGroupExpenses(Long groupId) {
        getGroupById(groupId);
        return expenseRepository.findByGroupId(groupId);
    }

    public List<Settlement> getGroupSettlements(Long groupId) {
        List<Expense> groupExpenses = getGroupExpenses(groupId);
        return expenseService.getSettlementsFor(groupExpenses);
    }

    private void validateMembers(List<Long> memberUserIds) {
        if (memberUserIds == null) {
            return;
        }
        for (Long userId : memberUserIds) {
            userRepository.findById(userId)
                    .orElseThrow(() -> new ValidationException("User not found: " + userId));
        }
    }
}
