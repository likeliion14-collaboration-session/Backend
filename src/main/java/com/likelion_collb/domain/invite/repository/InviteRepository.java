package com.likelion_collb.domain.invite.repository;

import com.likelion_collb.domain.invite.entity.Invite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InviteRepository extends JpaRepository<Invite, Long> {

    @Query("SELECT i FROM Invite i WHERE i.code = :code")
    Optional<Invite> findByCode(@Param("code") String code);
}
