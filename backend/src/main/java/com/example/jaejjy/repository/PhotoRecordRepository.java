package com.example.jaejjy.repository;

import com.example.jaejjy.entity.PhotoRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhotoRecordRepository extends JpaRepository<PhotoRecord, Long> {
    List<PhotoRecord> findAllByOrderByCreatedAtDesc();
}

