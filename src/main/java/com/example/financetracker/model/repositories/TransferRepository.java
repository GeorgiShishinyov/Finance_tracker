package com.example.financetracker.model.repositories;

import com.example.financetracker.model.entities.Transfer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferRepository extends JpaRepository<Transfer, Integer> {

    Page<Transfer> findAllByAccountSender_Owner_Id(int ownerId, Pageable pageable);

}
