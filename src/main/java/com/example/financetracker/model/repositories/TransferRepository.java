package com.example.financetracker.model.repositories;

import com.example.financetracker.model.entities.Transfer;
import com.example.financetracker.model.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TransferRepository extends JpaRepository<Transfer, Integer> {

    List<Transfer> findByAccountSender_Owner_Id(int senderId);

    List<Transfer> findByAccountReceiver_Owner_Id(int receiverId);
}
