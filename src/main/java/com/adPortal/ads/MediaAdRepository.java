package com.adPortal.ads;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MediaAdRepository extends JpaRepository<MediaAd, Long> {

	MediaAd findByUserIdAndId(Long userId, Long id);

	List<MediaAd> findByUserId(Long userId);

	List<MediaAd> findByAdInfoId(long id);

	List<MediaAd> findAllByUserId(Long userId);

}
