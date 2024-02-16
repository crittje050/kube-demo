package com.example.kubedemo.service;

import com.example.kubedemo.model.Name;
import com.example.kubedemo.repository.NameRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GetNameService {

    private final NameRepository nameRepository;

    public GetNameService(NameRepository nameRepository) {
        this.nameRepository = nameRepository;
    }

    public String getById(final String id) {

        Optional<Name> nameOptional = nameRepository.findById(id);

        if (nameOptional.isPresent()) {
            return nameOptional.get().getName();
        }

        return "NAME NOT FOUND!";
    }
}
