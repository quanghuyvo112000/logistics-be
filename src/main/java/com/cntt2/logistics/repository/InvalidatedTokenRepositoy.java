package com.cntt2.logistics.repository;

import com.cntt2.logistics.entity.InvalidatedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvalidatedTokenRepositoy extends JpaRepository<InvalidatedToken, String> {
}
