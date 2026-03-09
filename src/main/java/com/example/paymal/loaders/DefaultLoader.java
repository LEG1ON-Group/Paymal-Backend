package com.example.paymal.loaders;

import com.example.paymal.model.entity.Role;
import com.example.paymal.model.entity.User;
import com.example.paymal.model.enums.RoleEnum;
import com.example.paymal.repositories.RoleRepository;
import com.example.paymal.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DefaultLoader implements CommandLineRunner {
    private final UserRepository usersRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public void run(String... args) throws Exception {
        String adminName = "+998905110064";
        if (
                roleRepository.findByRoleName(RoleEnum.ROLE_ADMIN.name()) == null
                        && usersRepository.findByPhone(adminName) == null
        ) {
            setUsersData(adminName);
        }
    }

    private void setUsersData(String adminName) {
        Role adminRole = roleRepository.save(
                Role.builder()
                        .roleName(RoleEnum.ROLE_ADMIN.name())
                        .build()
        );
        roleRepository.save(
                Role.builder()
                        .roleName(RoleEnum.ROLE_USER.name())
                        .build()
        );

        //        Role adminRole = roleRepository.findByRoleName(RoleEnum.ROLE_ADMIN.name());

        User admin = usersRepository.save(
                new User(
                        adminName,
                        "LEG1ON",
                        "Dev",
                        passwordEncoder.encode("nosirov123"),
                        adminRole
                )
        );
    }
}