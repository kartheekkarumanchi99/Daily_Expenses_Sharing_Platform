package com.expensesharing.com.expensesharing.repositories;

import com.expensesharing.com.expensesharing.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
}
