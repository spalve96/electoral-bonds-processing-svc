package com.techworm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techworm.entity.FundReceiver;

/**
 * @author spalve
 */
@Repository
public interface ReceiverRepository extends JpaRepository<FundReceiver, Long> {

}
