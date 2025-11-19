package com.phucchinh.dogomynghe.repository;

import com.phucchinh.dogomynghe.entity.Image;
import com.phucchinh.dogomynghe.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    Optional<Image> findOneByUserAndTypeAndLatest(User user, String imageType, Boolean latest);

}
