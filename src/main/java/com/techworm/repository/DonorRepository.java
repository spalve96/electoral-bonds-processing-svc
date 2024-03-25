package com.techworm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techworm.entity.FundDonor;

/**
 * @author spalve
 */
@Repository
public interface DonorRepository extends JpaRepository<FundDonor, Long> {

}
