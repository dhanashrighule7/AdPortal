package com.adPortal.Notifications;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationsRepository extends JpaRepository<Notifications, Long> {

	// Notifications findByEmail(String email);

	List<Notifications> findByEmail(String emmail);

	boolean existsById(long id);

	Notifications deleteById(long id);

	// Optional<Notifications> existsByEmailAndId(String email, long id);

	Optional<Notifications> findByEmailAndId(String email, long id);

	Optional<Notifications> existsByEmailAndId(String email, long id);

}
