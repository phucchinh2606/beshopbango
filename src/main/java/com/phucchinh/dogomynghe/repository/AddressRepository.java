// AddressRepository.java
package com.phucchinh.dogomynghe.repository;
import com.phucchinh.dogomynghe.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {}