package com.expensesharing.com.expensesharing.service;

import com.expensesharing.com.expensesharing.entity.Group;
import com.expensesharing.com.expensesharing.entity.User;
import com.expensesharing.com.expensesharing.repositories.ExpenseRepository;
import com.expensesharing.com.expensesharing.repositories.GroupRepository;
import com.expensesharing.com.expensesharing.repositories.UserRepository;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class GroupServiceTest {

    @InjectMocks
    private GroupService groupService;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private ExpenseService expenseService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createGroup_withValidMembers_saves() {
        Group group = new Group("Trip", new ArrayList<>(Arrays.asList(1L, 2L)));
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User()));
        when(groupRepository.save(any(Group.class))).thenReturn(group);

        Group result = groupService.createGroup(group);

        assertEquals(group, result);
        verify(groupRepository).save(group);
    }

    @Test
    void createGroup_withUnknownMember_throws() {
        Group group = new Group("Trip", new ArrayList<>(Arrays.asList(99L)));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ValidationException.class, () -> groupService.createGroup(group));
        verify(groupRepository, never()).save(any(Group.class));
    }

    @Test
    void getGroupById_notFound_throws() {
        when(groupRepository.findById(1L)).thenReturn(Optional.empty());
        ValidationException ex = assertThrows(ValidationException.class,
                () -> groupService.getGroupById(1L));
        assertEquals("Group not found", ex.getMessage());
    }

    @Test
    void addMember_addsNewMemberOnce() {
        Group group = new Group("Trip", new ArrayList<>(Arrays.asList(1L)));
        when(groupRepository.findById(10L)).thenReturn(Optional.of(group));
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User()));

        Group updated = groupService.addMember(10L, 2L);

        assertTrue(updated.getMemberUserIds().contains(2L));
        assertEquals(2, updated.getMemberUserIds().size());
        verify(groupRepository).save(group);
    }

    @Test
    void addMember_doesNotDuplicateExistingMember() {
        Group group = new Group("Trip", new ArrayList<>(Arrays.asList(1L)));
        when(groupRepository.findById(10L)).thenReturn(Optional.of(group));
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));

        Group updated = groupService.addMember(10L, 1L);

        assertEquals(1, updated.getMemberUserIds().size());
        verify(groupRepository, never()).save(any(Group.class));
    }

    @Test
    void getGroupSettlements_delegatesToExpenseService() {
        Group group = new Group("Trip", new ArrayList<>(Arrays.asList(1L)));
        when(groupRepository.findById(10L)).thenReturn(Optional.of(group));
        when(expenseRepository.findByGroupId(10L)).thenReturn(new ArrayList<>());
        when(expenseService.getSettlementsFor(any())).thenReturn(new ArrayList<>());

        groupService.getGroupSettlements(10L);

        verify(expenseService).getSettlementsFor(any());
    }
}
