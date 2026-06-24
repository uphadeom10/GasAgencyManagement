package com.agency.management.inventory.repository;

import com.agency.management.inventory.entity.NewConnection;
import com.agency.management.inventory.entity.NewConnectionDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewConnectionDetailsRepository extends JpaRepository<NewConnectionDetails, Long> {

    List<NewConnectionDetails> findByNewConnectionId(NewConnection newConnection);

    List<NewConnectionDetails> findByNewConnectionId_Id(Long id);
}
