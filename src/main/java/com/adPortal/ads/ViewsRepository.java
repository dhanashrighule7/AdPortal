package com.adPortal.ads;

import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ViewsRepository extends JpaRepository<Views, Long> {

	@Query("SELECT SUM(v.views) " + "FROM Views v " + "WHERE v.userId = :userId AND v.date = :date AND v.infoId IN "
			+ "(SELECT a.id FROM AdsInfo a WHERE a.category = :category)")
	Long getTotalViewsByUserIdAndDateAndCategory(long userId, LocalDate date, String category);

	Views findByUserIdAndInfoId(long userId, long infoId);
}
