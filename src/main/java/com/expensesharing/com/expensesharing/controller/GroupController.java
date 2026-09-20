package com.expensesharing.com.expensesharing.controller;

import com.expensesharing.com.expensesharing.dto.AddMemberRequest;
import com.expensesharing.com.expensesharing.dto.Settlement;
import com.expensesharing.com.expensesharing.entity.Expense;
import com.expensesharing.com.expensesharing.entity.Group;
import com.expensesharing.com.expensesharing.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/groups")
@Tag(name = "Groups", description = "Manage expense groups, members, and group-scoped settlements")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @PostMapping
    @Operation(summary = "Create a new group")
    public ResponseEntity<Group> createGroup(@Valid @RequestBody Group group) {
        return new ResponseEntity<>(groupService.createGroup(group), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a group by ID")
    public ResponseEntity<Group> getGroupById(@PathVariable Long id) {
        return new ResponseEntity<>(groupService.getGroupById(id), HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "Get all groups")
    public ResponseEntity<List<Group>> getAllGroups() {
        return new ResponseEntity<>(groupService.getAllGroups(), HttpStatus.OK);
    }

    @PostMapping("/{id}/members")
    @Operation(summary = "Add a member to a group")
    public ResponseEntity<Group> addMember(@PathVariable Long id,
                                           @Valid @RequestBody AddMemberRequest request) {
        return new ResponseEntity<>(groupService.addMember(id, request.getUserId()), HttpStatus.OK);
    }

    @GetMapping("/{id}/expenses")
    @Operation(summary = "Get all expenses recorded for a group")
    public ResponseEntity<List<Expense>> getGroupExpenses(@PathVariable Long id) {
        return new ResponseEntity<>(groupService.getGroupExpenses(id), HttpStatus.OK);
    }

    @GetMapping("/{id}/settlements")
    @Operation(summary = "Get minimal who-pays-whom settlements within a group")
    public ResponseEntity<List<Settlement>> getGroupSettlements(@PathVariable Long id) {
        return new ResponseEntity<>(groupService.getGroupSettlements(id), HttpStatus.OK);
    }
}
