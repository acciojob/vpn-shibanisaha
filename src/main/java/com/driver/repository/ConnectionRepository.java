package com.driver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.driver.model.Connection;

@Repository
public interface ConnectionRepository extends JpaRepository<Connection, Integer> {
}
