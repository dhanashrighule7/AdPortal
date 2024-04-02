package com.adPortal.payments;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentsData, Long>{

	List<PaymentsData> getPaymentsByUserIdAndInfoId(long userId, long infoId);

	List<PaymentsData> findByUserId(long userId);

}
