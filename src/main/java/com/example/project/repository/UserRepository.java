package com.example.project.repository;

import com.example.project.entity.AppUser;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByEmail(String email);

    @Transactional
    @Modifying
    @Query("UPDATE AppUser a SET a.enabled = TRUE WHERE a.id = ?1")
    public int updateEnabled(Long id);

    @Query("Update AppUser a Set a.password = ?2 WHERE a.email = ?1")
    @Transactional
    @Modifying
    public int changePassword(String email, String password);
}
