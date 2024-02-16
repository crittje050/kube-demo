package com.example.kubedemo.service;

import com.example.kubedemo.model.Name;
import com.example.kubedemo.repository.NameRepository;
import org.springframework.stereotype.Service;

@Service
public class CreateNameService {

    private final NameRepository nameRepository;

    public CreateNameService(NameRepository nameRepository) {
        this.nameRepository = nameRepository;
    }

    public String createName(String nameValue) {
        Name name = nameRepository.save(new Name(nameValue));

        return name.getId();
    }
}
