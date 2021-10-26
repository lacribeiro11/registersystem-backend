package com.example.registersystembackend.data.access.layer.bill;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BillRepository extends MongoRepository<Bill, UUID> {
}
