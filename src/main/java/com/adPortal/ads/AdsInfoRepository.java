package com.adPortal.ads;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AdsInfoRepository extends JpaRepository<AdsInfo, Long>{

	List<AdsInfo> findByUserIdOrderByIdAsc(long userId);

	List<AdsInfo> findByUserIdOrderByIdDesc(long userId);

	List<AdsInfo> findByUserId(long id);

	List<AdsInfo> findByCreatedOnBetween(long time, long time2);
	
	@Query("SELECT COUNT(a) FROM AdsInfo a WHERE a.category = 'video'")
	long countVideoAds();

	@Query("SELECT COUNT(a) FROM AdsInfo a WHERE a.category = 'banner'")
	long countBannerAds();

	AdsInfo getByUserId(long userId);

}
