package com.karigarnow.config;

import com.karigarnow.model.AppService;
import com.karigarnow.repository.AppServiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final AppServiceRepository appServiceRepository;

    @Override
    public void run(String... args) {
        seedServicesIfEmpty();
    }

    private void seedServicesIfEmpty() {
        record ServiceData(String id, String slug, String name, String desc) {}

        List<ServiceData> defaults = List.of(
            new ServiceData("21bf4164-ddd2-417b-86b2-df3b1d7e3876", "plumbing", "Plumbing", "Pipe fitting, leakage repair, tap, drain cleaning"),
            new ServiceData("d4c34d38-ac78-4379-91a1-4bdc78481cd3", "painting", "Wall Painting", "Interior, exterior, texture, waterproofing coats"),
            new ServiceData("0fedf85a-9b79-4d9b-bd5f-25e04038f087", "electrical", "Electrical", "Wiring, switchboard, fan, light fitting, MCB work"),
            new ServiceData("9e24a525-f6d7-471c-ab06-c0975e01c5f5", "putty-plaster", "Putty & Plaster", "Wall putty, ceiling plaster, crack filling"),
            new ServiceData("8ab7844b-ad2a-4116-a3db-8cb2423ce8bd", "tiling", "Tile Installation", "Floor, bathroom, kitchen tiles"),
            new ServiceData("e45a03bf-3536-4d8e-880e-4a9baadb17ea", "carpentry", "Carpentry", "Doors, windows, furniture assembly, shelves"),
            new ServiceData("14d10d49-844f-46bb-9624-87c8fcde7ce8", "waterproofing", "Waterproofing", "Terrace, bathroom, basement waterproofing"),
            new ServiceData("455c53f8-d5f8-4123-9ce3-9fbf5a384955", "cleaning", "Deep Cleaning", "Full home cleaning, sofa, carpet, kitchen")
        );

        for (ServiceData s : defaults) {
            UUID serviceId = UUID.fromString(s.id());
            if (!appServiceRepository.existsById(serviceId)) {
                AppService svc = AppService.builder()
                        .id(serviceId)
                        .slug(s.slug())
                        .name(s.name())
                        .description(s.desc())
                        .isActive(true)
                        .build();
                appServiceRepository.save(svc);
                log.info("Auto-seeded service category: {}", s.name());
            }
        }
    }
}
