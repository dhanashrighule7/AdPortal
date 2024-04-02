
package com.adPortal.support;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.adPortal.ads.Status;

@Repository
public interface SupportRepository extends JpaRepository<Support, Long> {

	Page<Support> findByStatus(Status solved, Pageable pageable);

	List<Support> findByUserId(long userId);

}
