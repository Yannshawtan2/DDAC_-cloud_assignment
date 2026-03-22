package com.example.eventads.repository;

import com.example.eventads.model.EventAd;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventAdRepository extends JpaRepository<EventAd, Long> {
} 