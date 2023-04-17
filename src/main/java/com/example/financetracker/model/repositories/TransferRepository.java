package com.example.financetracker.model.repositories;

import com.example.financetracker.model.entities.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransferRepository extends JpaRepository<Transfer, Integer> {

    List<Transfer> findByAccountSender_Owner_Id(int senderId);

    List<Transfer> findByAccountReceiver_Owner_Id(int receiverId);
}
