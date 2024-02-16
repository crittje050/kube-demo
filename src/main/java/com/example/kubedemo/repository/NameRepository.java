package com.example.kubedemo.repository;

import com.example.kubedemo.model.Name;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NameRepository extends MongoRepository<Name, String> {

}
