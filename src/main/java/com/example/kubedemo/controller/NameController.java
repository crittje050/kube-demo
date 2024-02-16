package com.example.kubedemo.controller;

import com.example.kubedemo.service.CreateNameService;
import com.example.kubedemo.service.GetNameService;
import org.springframework.web.bind.annotation.*;

@RestController
public class NameController {

    private final CreateNameService createNameService;
    private final GetNameService getNameService;

    public NameController(CreateNameService createNameService, GetNameService getNameService) {
        this.createNameService = createNameService;
        this.getNameService = getNameService;
    }

    @GetMapping("/{id}")
    public String getNameById(@PathVariable(name = "id") final String id) {
        return getNameService.getById(id);
    }

    @PostMapping()
    public String createUser(@RequestParam(name = "name") String name) {
        return createNameService.createName(name);
    }
}
