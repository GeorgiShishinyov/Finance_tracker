package com.example.financetracker.model.repositories;

import com.example.financetracker.model.entities.LoginLocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginLocationRepository extends JpaRepository<LoginLocation, Integer> {


    void deleteAllByUserId(int id);

    boolean existsByIpAndUser_Id(String ip, int id);

}
