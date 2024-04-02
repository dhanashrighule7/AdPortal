package com.adPortal.ads;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import com.adPortal.Exception.Error;
import com.adPortal.Notifications.Notifications;
import com.adPortal.Notifications.NotificationsRepository;
import com.adPortal.user.User;
import com.adPortal.user.UserControllerException;
import com.adPortal.user.UserRepository;

@Service
public class AdsInfoService {

	@Autowired
	private AdsInfoRepository infoRepository;

	@Autowired
	private MediaAdRepository mediaAdRepository;

	@Autowired
	private ViewsRepository viewsRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private NotificationsRepository notificationsRepository;

	public Optional<AdsInfo> getMyAds(long id) {
		return infoRepository.findById(id);
	}

	//// get all ads list for admin
	public List<AdsInfo> getAllAds() {
		return infoRepository.findAll();
	}

////approve or rejects ads
	public ResponseEntity<?> updateAdsInfo(long id, String response) throws Exception {
		try {
			List<User> adminList = userRepository.findByRole("Admin");
			Optional<AdsInfo> adsInfo = infoRepository.findById(id);

			String msg = null;
			if (adsInfo != null) {
				AdsInfo info = adsInfo.get();
				if (!response.isEmpty()) {

					if ("yes".equalsIgnoreCase(response)) {
						info.markApproved();
						info.setUpdatedOn(System.currentTimeMillis());
						infoRepository.save(info);
						msg = "Your ad is approved";
					} else if ("no".equalsIgnoreCase(response)) {
						info.markRejected();
						info.setUpdatedOn(System.currentTimeMillis());
						infoRepository.save(info);
						msg = "Your ad is rejectd ";
					} else {
						throw new AdsInfoException(409, "Invalid response. Use 'yes' or 'no'.");
					}
				}
				String redirectKey = "checkStatus";
				for (User newAdminList : adminList) {
					Notifications notification = new Notifications(false, msg + " by " + newAdminList.getUsername(),
							adsInfo.get().getUser().getEmail(), redirectKey, System.currentTimeMillis());
					notificationsRepository.save(notification);
				}
				return ResponseEntity.ok(info);
			} else {
				throw new AdsInfoException(400, "Data not found.");
			}
		} catch (UserControllerException e) {
			System.out.println("In first catch block");
			return ResponseEntity.status(e.getStatusCode()).body(new Error(e.getStatusCode(), e.getMessage(),
					" Unable to load , please provide valid details ", System.currentTimeMillis()));
		} catch (Exception ex) {
			System.out.println("in second catch block");
			return new ResponseEntity<>("An error occurred while processing the request.",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

//	public ResponseEntity<?> saveAdsInfo(AdsInfo adsInfo, MultipartFile media) throws IOException {
//
//		String originalFilename = media.getOriginalFilename();
//		byte[] bytes = media.getBytes();
//		String category = adsInfo.getCategory();
//		long id = adsInfo.getUser().getId();
//
//		long createdOn = adsInfo.getCreatedOn();
//		String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
//		AdsInfo save;
//		if(category.equals("Video") && extension.equals(".mp4")) {
//			 save = infoRepository.save(adsInfo);
//			 MediaAd mm = new MediaAd(id, save.getId(), bytes, extension, category, createdOn, originalFilename,link);
//				mediaAdRepository.save(mm);
//				adsInfo.setFileName(originalFilename);
//			return new ResponseEntity<>("Video saved sucessfully", HttpStatus.OK);
//		}
//		else if(category.equals("Banner") && extension.equals(".jpg") || extension.equals(".png")) {
//			 save = infoRepository.save(adsInfo);
//			 MediaAd mm = new MediaAd(id, save.getId(), bytes, extension, category, createdOn, originalFilename);
//				mediaAdRepository.save(mm);
//				adsInfo.setFileName(originalFilename);
//			return new ResponseEntity<>("Banner saved sucessfully", HttpStatus.OK);
//		}
//		else {
//			return new ResponseEntity<>("Please upload Proper format file", HttpStatus.BAD_REQUEST);
//		}

//	public AdsInfo saveAdsInfo(AdsInfo adsInfo, MultipartFile videoORbanner) throws IOException {
//
//		User user = adsInfo.getUser();
//		Optional<User> userById = userRepository.findById(user.getId());
//
//		userById.ifPresent(adsInfo::setUser);
//
//		try {
//			if (adsInfo.getCategory().equals("Video")) {
//				byte[] videoData = videoORbanner.getBytes();
//				
//				adsInfo.setVideo(videoData);
//			} else if (adsInfo.getCategory().equals("Banner")) {
//				byte[] banner = videoORbanner.getBytes();
//				adsInfo.setBanner(banner);
//			}
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		return infoRepository.save(adsInfo);
//	}

	//// count of approved ads for advertiser
	public Long getCountOfApprovedAds(long id) {
		List<AdsInfo> byId = infoRepository.findByUserId(id);
		long count = 0;
		for (AdsInfo adsInfo : byId) {
			String role = adsInfo.getUser().getRole();
			if ("Advertiser".equals(role) && adsInfo.getStatus() == Status.approved) {
				count++;
			}
		}
		return count;
	}

	public long getTotalViewsCountToAdvertiser(long userId) {
		List<AdsInfo> adsInfoList = infoRepository.findByUserId(userId);
		if (!adsInfoList.isEmpty()) {
			AdsInfo adsInfo = adsInfoList.get(0);
			long userId2 = adsInfo.getUser().getId();
			List<User> list = userRepository.findByRole("Advertiser");
			long views = getTotalViewsCountByUserId(userId2);
			LocalDate currentDate = LocalDate.now();
			long endDateMillis = adsInfo.getEndDate();

			// Convert milliseconds to LocalDate
			Instant instant = Instant.ofEpochMilli(endDateMillis);
			LocalDate endDate = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate();
			String redirectKey = "Status";
			if (endDate.equals(currentDate)) {
				for (User advertiserListNew : list) {
					Notifications notification = new Notifications(false,
							adsInfo.getUser().getUsername() + " ads show time is expired today ",
							advertiserListNew.getEmail(), redirectKey, System.currentTimeMillis());
					notificationsRepository.save(notification);
				}
			}

			return views;
		} else {
			return -1;
		}
	}
//
//	//// get total views
//	public long getTotalViewsCount() {
//		List<AdsInfo> adsInfoList = infoRepository.findAll();
//		long totalCount = 0L;
//		for (AdsInfo adsInfo : adsInfoList) {
//			totalCount += adsInfo.getViews();
//		}
//		return totalCount;
//	}

//////get total views
//	public ResponseEntity<?> getTotalViewsCount() {
//	    try {
//	        List<AdsInfo> adsInfoList = infoRepository.findAll();
//	        long totalCount = 0;
//	        for (AdsInfo adsInfo : adsInfoList) {
//	            totalCount += adsInfo.getViews();
//	        }
//	        if (totalCount == 0) {
//	            throw new AdsInfoException(404, "No views found for any ad.");
//	        }
//	        return ResponseEntity.ok().body(totalCount);
//	    } catch (AdsInfoException e) {
//	        return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to get total views count.");
//	    }//// get total views

	public ResponseEntity<?> getTotalViewsCount() {
		try {
			List<AdsInfo> adsInfoList = infoRepository.findAll();
			long totalCount = 0;
			for (AdsInfo adsInfo : adsInfoList) {
				totalCount += adsInfo.getViews();
			}
			if (totalCount == 0) {
				throw new AdsInfoException(404, "No views found for any ad.");
			}
			return ResponseEntity.ok().body(totalCount);
		} catch (AdsInfoException e) {
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to get total views count.");
		}
	}

	//// get total views count for advertiser for his all ads
	public long getTotalViewsCountByUserId(long userId) {
		List<AdsInfo> adsInfoList = infoRepository.findByUserId(userId);
		long totalCount = 0L;
		for (AdsInfo adsInfo : adsInfoList) {
			totalCount += adsInfo.getViews();
		}
		return totalCount;
	}

	//// total amount spent count for advertiser
	public long getTotalAmountSpentByUserId(long userId) {
		List<AdsInfo> adsInfoList = infoRepository.findByUserId(userId);
		long feesPaid = 0;
		for (AdsInfo adsInfo : adsInfoList) {
			long feesPaid2 = adsInfo.getFeesPaid();
			System.out.println("Total Fees :::: " + feesPaid2);
			if (feesPaid2 != 0) {
				try {
					feesPaid += adsInfo.getFeesPaid();
				} catch (NumberFormatException e) {
					System.err.println("Invalid feesPaid value for adsInfo id " + adsInfo.getId() + ": " + feesPaid2);
				}
			}
		}
		return feesPaid;
	}

	//// total amount spend by advertiser for his all ads for admin dash board
	public long getTotalAmountSpent() {
		List<AdsInfo> adsInfoList = infoRepository.findAll();
		long totalFeesPaid = 0;
		for (AdsInfo adsInfo : adsInfoList) {
			long feesPaid2 = adsInfo.getFeesPaid();
			if (feesPaid2 != 0) {
				try {
//					totalFeesPaid += Long.parseLong(feesPaidStr);
					totalFeesPaid += feesPaid2;
				} catch (NumberFormatException e) {
				}
			}
		}
		return totalFeesPaid;
	}

	//// total ads count for admin
	public long getTotalAds() {
		List<AdsInfo> adsInfoList = infoRepository.findAll();
		return adsInfoList.size();
	}

	//// total ads by any advertiser
	public long getTotalAdsByUserId(long userId) {
		List<AdsInfo> adsInfoList = infoRepository.findByUserId(userId);
		return adsInfoList.size();
	}

	//// to get count of viewed user in specified dates===== not working
	public List<AdsInfo> getViewsByDateRange(Date startDate, Date endDate) {
		return infoRepository.findByCreatedOnBetween(startDate.getTime(), endDate.getTime());
	}

	// convert long to date
	public static LocalDate longToDate(long time) {
		LocalDate ofEpochDay = LocalDate.ofEpochDay(TimeUnit.MILLISECONDS.toDays(time));
		System.out.println("Saved dates :::: " + ofEpochDay);
		return ofEpochDay;
	}

	private int count;

	//// find oldest End date to check available slot
	public Map<LocalDate, Integer> findOldestEndDate() {
		List<AdsInfo> adsInfos = infoRepository.findAll();
		LocalDate oldestEndDate = null;
		for (AdsInfo adsInfo : adsInfos) {
			System.out.println("End Dates  :::: " + adsInfo.getEndDate());
			LocalDate endDate = longToDate(adsInfo.getEndDate());
//	        endDate==System.currentTimeMillis();
			System.out.println("Found End Date ::>>>> " + endDate);
			if (oldestEndDate == null || endDate.isBefore(oldestEndDate)) {
				oldestEndDate = endDate;
				count = 1; // Reset the count when a new oldest end date is found
				System.out.println("Received oldest date :::: " + oldestEndDate);
			} else if (endDate.equals(oldestEndDate)) {
				count++; // Increment the count if the current end date is the same as the oldest
			}
		}
		System.err.println("Received oldest date :::: " + oldestEndDate);

		System.out.println("Count of oldest end date: " + count);
		LocalDate plusDays = oldestEndDate.plusDays(1);
		Map<LocalDate, Integer> mm = new HashMap<>();
		mm.put(plusDays, count);
		return mm;
	}

//	  public Map<LocalDate,Integer> availableDate() {
//		  List<AdsInfo> adsInfos = infoRepository.findAll();
//		  
//		  for (AdsInfo adsInfo : adsInfos) {
//			  LocalDate startDate = longToDate(adsInfo.getStartDate());
//			  LocalDate endDate = longToDate(adsInfo.getEndDate());
//			  int size = 101;
//			  System.out.println("Count::"+size);
//			  int limit=100;
//			  int remained;
//			  if(size<=limit) {
//				  long timeMillis = System.currentTimeMillis();
//				  LocalDate longToDate = longToDate(timeMillis);
//				  remained= limit-size;
//				  System.out.println("Remained :: "+remained);
//				  Map<LocalDate, Integer> mm = new HashMap<>();
//				    mm.put(longToDate, remained);
//				  return mm;
//			  }
//			  else {
//				   Map<LocalDate, Integer> mm = findOldestEndDate();
//				   LocalDate date=null;
//				   int count=0;
//				   for(Map.Entry<LocalDate, Integer> map:mm.entrySet())	{
//					    date = map.getKey();
//					    count = map.getValue();
//				   }
//				  System.out.println("Check count::"+count);
//				  
//				int total=  size-count;
//				System.out.println("total :::::::::::::::::::::::::: "+total);
//				  remained = limit-total;
//				  System.out.println("Remained :: "+remained);
//				  limit= remained+size-count;
//				  System.err.println("limit :: "+limit);
////				  Map<LocalDate, Integer> mm = new HashMap<>();
//				    mm.put(date, remained);
//				  System.out.println("slot is not avaliable");
//				  return mm;
//			  }
//			
//		}
//		return null; 
//	  }

	//// to get total count of video ads
	public long countVideoAds() {
		return infoRepository.countVideoAds();
	}

	////// to get total count of banner ads
	public long countBannerAds() {
		return infoRepository.countBannerAds();
	}

	/// to find latest End date
	public Map<LocalDate, Integer> findLatestEndDate() {
		List<AdsInfo> adsInfos = infoRepository.findAll();
		LocalDate latestEndDate = null;
		for (AdsInfo adsInfo : adsInfos) {
			LocalDate endDate = longToDate(adsInfo.getEndDate());
			if (latestEndDate == null || endDate.isAfter(latestEndDate)) {
				latestEndDate = endDate;
				count = 1; // Reset the count when a new latest end date is found
			} else if (endDate.equals(latestEndDate)) {
				count++; // Increment the count if the current end date is the same as the latest
			}
		}

		Map<LocalDate, Integer> result = new HashMap<>();
		if (latestEndDate != null) {
			LocalDate nextDay = latestEndDate.plusDays(1);
			result.put(nextDay, count);
		}
		return result;
	}

	//////////////////////// find available date //////////////////////////////
//	public Map<LocalDate, Integer> availableDate() {
//		List<AdsInfo> adsInfos = infoRepository.findAll();
//		LocalDate currentDate = LocalDate.now();
//
//		for (AdsInfo adsInfo : adsInfos) {
//			LocalDate startDate = longToDate(adsInfo.getStartDate());
//			LocalDate endDate = longToDate(adsInfo.getEndDate());
//			System.out.println("Check Point :::: 11 ");
//			int size = adsInfos.size();
//			System.out.println("Count::" + size);
//			int limit = 100;
//			int remained;
//			if (size <= limit) {
//				System.out.println("Check Point :::: 12 ");
//				long timeMillis = System.currentTimeMillis();
//				LocalDate longToDate = longToDate(timeMillis);
//				remained = limit - size;
//				System.out.println("Remained :: " + remained);
//				Map<LocalDate, Integer> mm = new HashMap<>();
//				mm.put(longToDate, remained);
//				return mm;
//			}
//
//			Map<LocalDate, Integer> latestEndDateMap = findLatestEndDate();
//			if (!latestEndDateMap.isEmpty()) {
//				LocalDate latestEndDate = latestEndDateMap.keySet().iterator().next();
//				if (currentDate.isAfter(latestEndDate)) {
//					Map<LocalDate, Integer> result = new HashMap<>();
//					result.put(currentDate, latestEndDateMap.get(latestEndDate));
//					System.err.println("Check Point  :::::            44444");
//					return result;
//				} else if (currentDate.isEqual(latestEndDate)) {
//					Map<LocalDate, Integer> result = new HashMap<>();
//					result.put(currentDate, latestEndDateMap.get(latestEndDate));
//					System.err.println("Check Point  :::::            55555");
//					return result;
//				} else {
//					Map<LocalDate, Integer> result = new HashMap<>();
//					result.put(latestEndDate, latestEndDateMap.get(latestEndDate));
//					System.err.println("Check Point  :::::            66666");
//					return result;
//				}
//			}
//
//		}
//		return null;
//	}
////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	public List<Map<LocalDate, Integer>> availableDate() {
//		List<AdsInfo> adsInfos = infoRepository.findAll();
//		LocalDate currentDate = LocalDate.now();
//		List<Map<LocalDate, Integer>> result = new ArrayList<>();
//		int limit = 100;
//		int size = adsInfos.size();
//		if (size < limit) {
//			// If the size is less than the limit, add a map with current date and the
//			// remaining slots
//			int remainingSlots = limit - size;
//			// Increase remaining slots for records where currentDate.isAfter(endDate) is
//			// true
//			for (AdsInfo adsInfo : adsInfos) {
//				LocalDate endDate = longToDate(adsInfo.getEndDate());
//				if (currentDate.isAfter(endDate)) {
//					remainingSlots++;
//				}
//			}
//			Map<LocalDate, Integer> map = new HashMap<>();
//			map.put(currentDate, remainingSlots);
//			result.add(map);
//		} else {
//			// If the size exceeds the limit, find the oldest end date and adjust the count
//			LocalDate oldestEndDate = findOldestEndDate(adsInfos);
//			int freeSlots = getFreeSlots(adsInfos, oldestEndDate);
//			// Increase free slots for records where currentDate.isAfter(endDate) is true
//			for (AdsInfo adsInfo : adsInfos) {
//				LocalDate endDate = longToDate(adsInfo.getEndDate());
//				if (currentDate.isAfter(endDate)) {
//					freeSlots++;
//				}
//			}
//			Map<LocalDate, Integer> map = new HashMap<>();
//			map.put(oldestEndDate, freeSlots);
//			result.add(map);
//		}
//		return result;
//	}

	public ResponseEntity<?> availableDate(String category) {
		List<AdsInfo> adsInfos = infoRepository.findAll();
		System.out.println("Category ::::: " + category);
		int bannerLimit = 100;
		int videoLimit = 50;
		int bannerSize = 0;
		int videoSize = 0;
		LocalDate currentDate = LocalDate.now();
		List<Map<LocalDate, Integer>> result = new ArrayList<>();

		List<AdsInfo> banner = adsInfos.stream()
				.filter(adsInfo -> adsInfo.getSubCategory() != null && adsInfo.getCategory().equals("Banner"))
				.collect(Collectors.toList());
		bannerSize = banner.size();
		System.out.println("Size of banner ::::: " + bannerSize);

		List<AdsInfo> video = adsInfos.stream()
				.filter(adsInfo -> adsInfo.getSubCategory() != null && adsInfo.getCategory().equals("Video"))
				.collect(Collectors.toList());
		videoSize = video.size();
		System.out.println("Size of video ::::: " + videoSize);

		System.out.println("continue logic ::::: ");
		if (category.equals("Banner") && bannerSize < bannerLimit) {
			// If the size is less than the banner limit, add a map with current date and
			// the remaining slots
			int remainingSlots = bannerLimit - bannerSize;
			for (AdsInfo adsInfo : banner) {
				LocalDate endDate = longToDate(adsInfo.getEndDate());
				if (currentDate.isAfter(endDate)) {
					remainingSlots++;
				}
			}
			Map<LocalDate, Integer> map = new HashMap<>();
			map.put(currentDate, remainingSlots);
			result.add(map);
		} else if (category.equals("Video") && videoSize < videoLimit) {
			// If the size is less than the video limit, add a map with current date and the
			// remaining slots
			int remainingSlots = videoLimit - videoSize;
			for (AdsInfo adsInfo : video) {
				LocalDate endDate = longToDate(adsInfo.getEndDate());
				if (currentDate.isAfter(endDate)) {
					remainingSlots++;
				}
			}
			Map<LocalDate, Integer> map = new HashMap<>();
			map.put(currentDate, remainingSlots);
			result.add(map);
		} else {
			// If the size exceeds the limit, find the oldest end date and adjust the count
			LocalDate oldestEndDate = findOldestEndDate(category.equals("Banner") ? banner : video);
			int freeSlots = getFreeSlots(category.equals("Banner") ? banner : video, oldestEndDate);
			for (AdsInfo adsInfo : category.equals("Banner") ? banner : video) {
				LocalDate endDate = longToDate(adsInfo.getEndDate());
				if (currentDate.isAfter(endDate)) {
					freeSlots++;
				}
			}
			Map<LocalDate, Integer> map = new HashMap<>();
			map.put(oldestEndDate, freeSlots);
			result.add(map);
		}
		return ResponseEntity.ok(result);
	}

	private LocalDate findOldestEndDate(List<AdsInfo> adsInfos) {
		LocalDate oldestEndDate = null;
		for (AdsInfo adsInfo : adsInfos) {
			LocalDate endDate = longToDate(adsInfo.getEndDate());
			if (oldestEndDate == null || endDate.isBefore(oldestEndDate)) {
				oldestEndDate = endDate;
			}
		}
		return oldestEndDate;
	}

	private int getFreeSlots(List<AdsInfo> adsInfos, LocalDate endDate) {
		int freeSlots = 0;
		for (AdsInfo adsInfo : adsInfos) {
			LocalDate adsEndDate = longToDate(adsInfo.getEndDate());
			System.out.println("Check Point ::: 1 ::: " + adsEndDate);
			if (adsEndDate.equals(endDate)) {
				freeSlots++;
				System.out.println("Check Point ::: 1 ::: " + freeSlots);
			}
		}
		System.out.println("Slots :::3:: " + freeSlots);
		return freeSlots;
	}

/////////////////////////////////////////////////////////////////////////////////////////////////////
	// to save payments data
//	public void savePaymentData(AdsInfo adsInfo) {
//		PaymentsData paymentsData = new PaymentsData("transactionId", "receiver", "sender", "StatusOFPayment",
//				adsInfo.getFeesPaid(), adsInfo.getUser().getId(), adsInfo.getId());
//		System.err.println("Payments Data :::::: " + paymentsData);
//		paymentRepository.save(paymentsData);
//	}

	///// to get approved overall count
	public Long getCountOfApprovedAds() {
		long count = 0;
		System.out.println("Check Point 1 :::: ");
		List<AdsInfo> all = infoRepository.findAll();
		for (AdsInfo adsInfo : all) {
			if (adsInfo.getStatus() == Status.approved) {

				System.out.println("Check Point 3 :::: " + adsInfo.getStatus());
				count++;
				System.out.println("Check Point 4 :::: Count :: " + count);
			}
		}
		return count;
	}

	public List<AdsInfo> getUserAds(String category, String subcategory) {
		List<AdsInfo> filteredAdsInfos = filterAdsInfosByCategoryAndSubcategory(category, subcategory);
		return filteredAdsInfos;
	}

	public List<AdsInfo> getUserBannerAds(String category, String subcategory) {
		List<AdsInfo> filteredAdsInfos = filterAdsInfosByCategoryAndSubcategory(category, subcategory);
		for (AdsInfo adsInfo : filteredAdsInfos) {
			if (!(adsInfo.getEndDate() == System.currentTimeMillis())) {
				return filteredAdsInfos;
			}
		}
		return null;
	}

	public List<AdsInfo> getDescendingData(int pageNumber, String order, long userId) {
		try {
			System.out.println("Check Point 2 :::: ");
			List<AdsInfo> sortedAdsInfo;
			if ("asc".equalsIgnoreCase(order)) {
				System.out.println("Check Point 3 :::: ");
				sortedAdsInfo = infoRepository.findByUserIdOrderByIdAsc(userId);
			} else if ("desc".equalsIgnoreCase(order)) {
				System.out.println("Check Point 4 :::: ");
				sortedAdsInfo = infoRepository.findByUserIdOrderByIdDesc(userId);
			} else {
				System.out.println("Check Point 5 :::: ");
				throw new UserControllerException(403, "invalid input");
			}
			System.out.println("Check Point 6 :::: ");
			int startIndex = (pageNumber - 1) * 20;
			int endIndex = Math.min(startIndex + 20, sortedAdsInfo.size());
			System.out.println("/" + startIndex + "/" + endIndex + "/" + sortedAdsInfo.size());
			sortedAdsInfo = new ArrayList<>(sortedAdsInfo.subList(startIndex, endIndex));
			System.out.println("Sorted List : : " + sortedAdsInfo);
			return sortedAdsInfo;
		} catch (UserControllerException e) {
			e.printStackTrace();
			throw e;
		}
	}
//	private List<AdsInfo> filterAdsInfosByCategoryAndSubcategory(String category, String subcategory) {
//		List<AdsInfo> adsInfos = infoRepository.findAll();
//		return adsInfos.stream().filter(adsInfo -> category == null || adsInfo.getCategory().equalsIgnoreCase(category))
//				.filter(adsInfo -> subcategory == null || adsInfo.getSubCategory().equalsIgnoreCase(subcategory))
//				.collect(Collectors.toList());
//	}

	private List<AdsInfo> filterAdsInfosByCategoryAndSubcategory(String category, String subcategory) {
		LocalDate currentDate = LocalDate.now();
		List<AdsInfo> adsInfos = infoRepository.findAll();
		return adsInfos
				.stream().filter(
						adsInfo -> (category == null || adsInfo.getCategory().equalsIgnoreCase(category))
								&& (subcategory == null || adsInfo.getSubCategory().equalsIgnoreCase(subcategory))
								&& (longToDate(adsInfo.getEndDate()) == null
										|| longToDate(adsInfo.getEndDate()).isAfter(currentDate)))
				.collect(Collectors.toList());
	}

	/////////// ++++++++ HAVE TWO TABLES LIST ++++++++//////////
	public ResponseEntity<?> adsForViewersToView(String category, String subcategory) {
		LocalDate currentDate = LocalDate.now();
		List<AdsInfo> adsInfos = infoRepository.findAll();
		Map<AdsInfo, List<MediaAd>> adsMap = new HashMap<>();

		adsInfos.stream().filter(adsInfo -> (category == null || adsInfo.getCategory().equalsIgnoreCase(category))
				&& (subcategory == null || adsInfo.getSubCategory().equalsIgnoreCase(subcategory))
				&& (longToDate(adsInfo.getEndDate()) == null || longToDate(adsInfo.getEndDate()).isAfter(currentDate)))
				.forEach(adsInfo -> {
					List<MediaAd> mediaAds = mediaAdRepository.findByAdInfoId(adsInfo.getId());
					adsMap.put(adsInfo, mediaAds);
				});

		return ResponseEntity.ok(adsMap);
	}

	public ResponseEntity<?> adsForViewersToView(String category, String subcategory, String longitude,
			String lattitude) {
		LocalDate currentDate = LocalDate.now();
		List<AdsInfo> adsInfos = infoRepository.findAll();
		Map<AdsInfo, List<MediaAd>> adsMap = new HashMap<>();

		adsInfos.stream()
				.filter(adsInfo -> (category == null || adsInfo.getCategory().equalsIgnoreCase(category))
						&& (subcategory == null || adsInfo.getSubCategory().equalsIgnoreCase(subcategory))
						&& (longitude == null)
						|| adsInfo.getLongitude().equalsIgnoreCase(longitude) && (lattitude == null)
						|| adsInfo.getLattitude().equalsIgnoreCase(lattitude)
								&& (longToDate(adsInfo.getEndDate()) == null
										|| longToDate(adsInfo.getEndDate()).isAfter(currentDate)))
				.forEach(adsInfo -> {
					List<MediaAd> mediaAds = mediaAdRepository.findByAdInfoId(adsInfo.getId());
					adsMap.put(adsInfo, mediaAds);
				});

		return ResponseEntity.ok(adsMap);
	}

	public ResponseEntity<?> allAdsByUserId(long userId) {
		try {
			if (userId == 0) {

				throw new UserControllerException(409, "Not Found");
			} else {
				List<AdsInfo> byUserId = infoRepository.findByUserId(userId);
				return ResponseEntity.ok(byUserId);
			}
		} catch (UserControllerException e) {
			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to find details", System.currentTimeMillis()));
		}
	}

//	private ResponseEntity<?> updateUserToken(long userId, int time, long infoId){
//		
//	
//		User user = userRepository.findById(userId)
//				.orElseThrow(() -> new UserControllerException(401, "!!!!! User not found !!!!!"));
//		List<AdsInfo> filteredAdsInfos = filterAdsInfosByCategoryAndSubcategory(category, subcategory);
//
//		for (AdsInfo adsInfo : filteredAdsInfos) {
//			if (!(adsInfo.getEndDate() == System.currentTimeMillis())) {
//				
//				Long tokenCount = filteredAdsInfos.stream().filter(Objects::nonNull).map(AdsInfo::getToken)
//						.filter(token -> token != null).mapToLong(Long::longValue).sum();
//
//				user.setTotalCredit(Long.toString(tokenCount));
//				userRepository.save(user);
//
//			}
//	}
//   }

//	 public String getMediaById(long id) throws IOException {
//		 System.out.println("Checkpoint 11");
//	        Optional<MediaAd> optionalMediaAd = mediaAdRepository.findById(id);
//	        System.out.println("Checkpoint 12");
//	        List<String> fileLinks = new ArrayList<>();
//	        if (optionalMediaAd.isPresent()) {
//	            MediaAd mediaAd = optionalMediaAd.get();
//	            byte[] mediaBytes = mediaAd.getMedia();
//	            System.out.println("Checkpoint 1");
//	            File myFile = new File("D:\\myFile"+mediaAd.getExtension());
//	            String filePath = "D:\\21 Feb 2024\\Ad-Portal\\src\\main\\resources\\data\\Myfile" + mediaAd.getExtension();
//	            File myFile = new File(filePath);
//	            File myFile = File.createTempFile("Myfile", mediaAd.getExtension());
//	            String fileName = "Myfile" + mediaAd.getExtension();
//	            File dataFolder = new File("D:\\21 Feb 2024\\Ad-Portal\\src\\main\\resources\\data\\");
	// Create the file in the folder
//	            File myFile = new File(dataFolder, fileName);
//	            try {
//	                if (myFile.createNewFile()) {
//	                    System.out.println("File created successfully.");
//	                } else {
//	                    System.out.println("File already exists.");
//	                }
//	            } catch (IOException e) {
//	                System.out.println("Failed to create the file.");
//	                e.printStackTrace();
//	            }

//	            String fileLink = myFile.getAbsolutePath();
//
//	            System.out.println("Checkpoint 2 ::::: File link "+fileLink);
//	            InputStream is = new ByteArrayInputStream(mediaBytes);
//	            Files.copy(is, myFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
//	            
//	            URL url = myFile.toURI().toURL();
//	            String string = url.toString();
//	            System.out.println("url :::: "+url);
//	            String extension = mediaAd.getExtension().toLowerCase();
//	            if (extension.endsWith("png") || extension.endsWith("jpg") || extension.endsWith("jpeg") || extension.endsWith("gif")) {
//	                ByteArrayInputStream bis = new ByteArrayInputStream(mediaBytes);
//	                BufferedImage image = ImageIO.read(bis);
//	                return Optional.ofNullable(fileLink);
//	            fileLinks.add(fileLink);
//	            System.out.println(fileLinks);
//	           
//	                    return "File Link: " + fileLink + "\n" + "Media Ad: " + mediaAd.toString();
//	            		
//	        }
//	            } else {
//	            	 System.out.println("Checkpoint 1");
//	                return Optional.of(mediaBytes); 
//	            }
//	        }
//	        System.out.println("Checkpoint 21");
//	        return null;
//	    }

	public List<MediaAd> getMediaById(long userId) throws IOException {
		List<MediaAd> byUserId = mediaAdRepository.findByUserId(userId);
		for (MediaAd mediaAd : byUserId) {
			System.out.println("Check Point 1 ");
			byte[] mediaBytes = mediaAd.getMedia();
			System.out.println("Check Point 2 ");
			File myFile = File.createTempFile("Myfile", mediaAd.getExtension());
			myFile.getAbsolutePath();
			System.out.println("Check Point 3 ");
			try (InputStream is = new ByteArrayInputStream(mediaBytes)) {
				System.out.println("Check Point 4 ");
				long copy = Files.copy(is, myFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			}
			String fileLink = myFile.getAbsolutePath();
			System.out.println("Check Point 6 ");
			mediaAd.setAccessLink(fileLink);
			MediaAd save = mediaAdRepository.save(mediaAd);
			String link = fileLink;

		}
		return byUserId;
	}

//	 public ResponseEntity<?> getMediaById(long id) throws IOException {
//	        List<String> fileLinks = new ArrayList<>();
//
//	        Optional<MediaAd> optionalMediaAd = mediaAdRepository.findById(id);
//	        if (optionalMediaAd.isPresent()) {
//	            MediaAd mediaAd = optionalMediaAd.get();
//	            byte[] mediaBytes = mediaAd.getMedia();
//	            String fileName = "Myfile" + mediaAd.getExtension();
//	            File dataFolder = new File("D:\\21 Feb 2024\\Ad-Portal\\src\\main\\resources\\data\\");
//	            File myFile = new File(dataFolder, fileName);
//	            InputStream is = new ByteArrayInputStream(mediaBytes);
//	            Files.copy(is, myFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
//	            URL url = myFile.toURI().toURL();
//	            String fileLink = myFile.getAbsolutePath();
////	            String fileLink = url.toString();
//	            fileLinks.add(fileLink);
//	        }
//
//	        return ResponseEntity.ok(fileLinks);
//	    }

	public ResponseEntity<?> addViews(long userId, long infoId) {
		try {
			Views existingViews = viewsRepository.findByUserIdAndInfoId(userId, infoId);

			if (existingViews == null) {
				int viewsCount = 1;
				System.out.println("viewsCount Before:: " + viewsCount);
				Views views = new Views(userId, infoId, viewsCount);
				viewsRepository.save(views);
				System.out.println("views after:: " + views);
				AdsInfo ad = infoRepository.findById(infoId).orElse(null);
				if (ad != null) {
					ad.setViews(viewsCount);
					infoRepository.save(ad);
				}
			} else {
				long viewsCount = existingViews.getViews() + 1;
				System.out.println("Views count after:: " + viewsCount);
				existingViews.setViews(viewsCount);
				viewsRepository.save(existingViews);

				AdsInfo ad = infoRepository.findById(infoId).orElse(null);
				if (ad != null) {
					ad.setViews(viewsCount);
					infoRepository.save(ad);
				}
			}
		} catch (UserControllerException e) {
			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to find data", System.currentTimeMillis()));
		}
		return null;
	}

//	public ResponseEntity<?> getMediaByUserId(long userId) throws IOException {
//		try {
//			List<MediaAd> byUserId = mediaAdRepository.findByUserId(userId);
//			List<AdsInfo> ads = infoRepository.findByUserId(userId);
//
//			for (AdsInfo ad : ads) {
//				addViews(userId, ad.getId());
//			}
//
//			for (MediaAd mediaAd : byUserId) {
//				byte[] mediaBytes = mediaAd.getMedia();
//				File myFile = File.createTempFile("Myfile", mediaAd.getExtension());
//				myFile.getAbsolutePath();
//
//				try (InputStream is = new ByteArrayInputStream(mediaBytes)) {
//					long copy = Files.copy(is, myFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
//				}
//				String fileLink = myFile.getAbsolutePath();
//				mediaAd.setAccessLink(fileLink);
//				MediaAd save = mediaAdRepository.save(mediaAd);
//				String link = fileLink;
//			}
//
//			return ResponseEntity.ok(byUserId);
//		} catch (UserControllerException e) {
//			return ResponseEntity.status(e.getStatusCode()).body(
//					new Error(e.getStatusCode(), e.getMessage(), "Unable to find data", System.currentTimeMillis()));
//		}
//	}
	public ResponseEntity<?> getMediaByUserId(long userId) throws IOException {
		try {
			List<MediaAd> byUserId = mediaAdRepository.findByUserId(userId);
			List<String> mediaUrls = new ArrayList<>();
			List<AdsInfo> ads = infoRepository.findByUserId(userId);

			for (AdsInfo ad : ads) {
				addViews(userId, ad.getId());
			}

			for (MediaAd mediaAd : byUserId) {
				String mediaUrl = "http://localhost:8080/ads/media/allData?id=" + mediaAd.getId(); // Modify as per your
				// implementation
				mediaUrls.add(mediaUrl);

				mediaAd.setAccessLink(mediaUrl);
			}

			return ResponseEntity.ok(mediaUrls);

		} catch (UserControllerException e) {
			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to find data", System.currentTimeMillis()));
		}
	}

	public ResponseEntity<?> getMediaDataByUserIdAndId(long userId, long id) {
		try {
			MediaAd mediaAd = mediaAdRepository.findByUserIdAndId(userId, id);
			if (mediaAd != null) {
				String mediaAdUrl = "http://localhost:8080/ads/media/allData?id=" + mediaAd.getId();
				return ResponseEntity.ok(mediaAdUrl);
			} else {
				throw new UserControllerException(404, "Ad not found");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Error(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), "Unable to find data",
							System.currentTimeMillis()));
		}
	}

	private static final int SMALL_VIDEO_TOKENS = 5;
	private static final int LARGE_VIDEO_TOKENS = 10;

	public ResponseEntity<?> watchVideoAndGetTokens(String userId, String subcategory, Integer time) {
		try {
			User user = userRepository.findById(Long.parseLong(userId)).orElse(null);
			if (user == null) {
				throw new AdsInfoException(400, "User not found");
			}
			int tokensEarned = 0;
			if (time != null && time >= 10) {
				if (subcategory.equals("small")) {
					tokensEarned = SMALL_VIDEO_TOKENS;
				} else if (subcategory.equals("large")) {
					tokensEarned = LARGE_VIDEO_TOKENS;
				}
			}
			// Add tokens to user's balance and reset video start time
			user.setTotalCredit(user.getTotalCredit() + tokensEarned);
			user.setTotalAdRan(user.getTotalAdRan() + 1);
			userRepository.save(user);

			return ResponseEntity.ok(tokensEarned + " Token Credited..");
		} catch (UserControllerException e) {
			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to find details", System.currentTimeMillis()));
		}
	}

	public ResponseEntity<String> calculateViewersStatistics() {
		List<User> viewers = userRepository.findByRole("viewer"); // Assuming userRepository provides access to all
																	// users with role "viewer"

		int totalViewers = viewers.size();
		int totalViewedVideos = 0;

		for (User viewer : viewers) {
			long tokensCredited = viewer.getTotalCredit();
			if (tokensCredited >= SMALL_VIDEO_TOKENS || tokensCredited >= LARGE_VIDEO_TOKENS) {
				totalViewedVideos++;
			}
		}

		double percentage = totalViewers == 0 ? 0.0 : ((double) totalViewedVideos / totalViewers) * 100;

		DecimalFormat df = new DecimalFormat("#.##");
		String formattedPercentage = df.format(percentage) + "%";

		String result = "Total Viewers: " + totalViewers + ", Percentage Viewed Videos: " + formattedPercentage;
		return ResponseEntity.ok().body(result);
	}

	public ResponseEntity<?> getMedia(@PathVariable Long userId) {
		try {
			Optional<User> userOptional = userRepository.findById(userId);

			if (userOptional.isPresent()) {
				User user = userOptional.get();
				byte[] imageData = user.getProfile();

				if (imageData != null && imageData.length > 0) {
					HttpHeaders headers = new HttpHeaders();
					headers.setContentType(MediaType.IMAGE_JPEG);
					headers.setContentLength(imageData.length);

					return ResponseEntity.ok().headers(headers).contentLength(imageData.length)
							.contentType(MediaType.IMAGE_JPEG).body(imageData);
				} else {
					return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User profile picture not found");
				}
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with ID: " + userId);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An error occurred while retrieving user profile picture");
		}
	}

	public ResponseEntity<?> getMediaForViewers(Long id, String category, String subcategory, String lattitude,
			String longitude) {
		try {
			MediaAd mediaData = mediaAdRepository.findById(id).orElse(null);
			if (mediaData != null) {
				Optional<AdsInfo> byId = infoRepository.findById(mediaData.getAdInfoId());
				AdsInfo adsInfo = byId.get();
				LocalDate currentDate = LocalDate.now();
				if (longToDate(adsInfo.getEndDate()).isAfter(currentDate)) {
					if (category.equals(adsInfo.getCategory()) && subcategory.equals(adsInfo.getSubCategory())
							&& longitude.equals(adsInfo.getLongitude()) && lattitude.equals(adsInfo.getLattitude())) {
						byte[] imageData = mediaData.getMedia();
						HttpHeaders headers = new HttpHeaders();
						headers.setContentType(MediaType.IMAGE_JPEG);
						headers.setContentLength(imageData.length);

						return ResponseEntity.ok().headers(headers).contentLength(imageData.length)
								.contentType(MediaType.IMAGE_JPEG).body(imageData);
					} else {
						return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unsupported media type");
					}
				} else {
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unsupported media type");
				}
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Media not found");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
		}
	}

	public ResponseEntity<?> getAllAdsForViewers(double latitude, double longitude, Integer pageNumber) {
		try {
			int pageSize = 11;
			int offset = (pageNumber - 1) * pageSize;

			// Retrieve data with pagination
			Page<MediaAd> mediaAdsPage = mediaAdRepository.findAll(PageRequest.of(offset, pageSize));

			List<String> imageUrls = new ArrayList<>();
			LocalDate currentDate = LocalDate.now();
			int videoAdsCount = 5;
			int bannerAdsCount = 6;
			int largeVideoAdsCount = 1;
			int smallVideoAdsCount = 4;
			boolean foundActualData = false;

			// Iterate through retrieved data
			for (MediaAd mediaAd : mediaAdsPage.getContent()) {
				Optional<AdsInfo> optionalAdsInfo = infoRepository.findById(mediaAd.getAdInfoId());
				if (!optionalAdsInfo.isPresent()) {
					continue;
				}
				AdsInfo adsInfo = optionalAdsInfo.get();

				double adLat = Double.parseDouble(adsInfo.getLattitude());
				double adLon = Double.parseDouble(adsInfo.getLongitude());

				if (longToDate(adsInfo.getEndDate()).isAfter(currentDate)
						&& calculateDistance(latitude, longitude, adLat, adLon) <= 20) {
					foundActualData = true;
					String imageUrl = "http://localhost:8080/ads/media/allData?id=" + mediaAd.getId();
					imageUrls.add(imageUrl);

					if (mediaAd.getCategory().equalsIgnoreCase("Video")) {
						if (videoAdsCount > 0) {
							if (mediaAd.getSubCategory().equalsIgnoreCase("large") && largeVideoAdsCount > 0) {
								largeVideoAdsCount--;
								videoAdsCount--;
							} else if (mediaAd.getSubCategory().equalsIgnoreCase("small") && smallVideoAdsCount > 0) {
								smallVideoAdsCount--;
								videoAdsCount--;
							}
						}
					} else if (mediaAd.getCategory().equalsIgnoreCase("Banner") && bannerAdsCount > 0) {
						bannerAdsCount--;
					}
				}
			}

			// Check if no actual data is found
			if (!foundActualData) {
				if (pageNumber == 1) {
					return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No ads found for the given location");
				} else {
					return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Page not found");
				}
			}

			// Add dummy video ads if not enough actual data found
			while (videoAdsCount > 0) {
				if (largeVideoAdsCount > 0) {
					String imageUrl = "http://localhost:8080/ads/media/allData?id=" + 15;
					imageUrls.add(imageUrl);
					largeVideoAdsCount--;
					videoAdsCount--;
				} else if (smallVideoAdsCount > 0) {
					String imageUrl = "http://localhost:8080/ads/media/allData?id=" + 15;
					imageUrls.add(imageUrl);
					smallVideoAdsCount--;
					videoAdsCount--;
				} else {
					break;
				}
			}

			// Add dummy banner ads if not enough actual data found and no data found at all
			while (bannerAdsCount > 0) {
				String imageUrl = "http://localhost:8080/ads/media/allData?id=" + 13;
				imageUrls.add(imageUrl);
				bannerAdsCount--;
			}

			return ResponseEntity.ok().body(imageUrls);

		} catch (UserControllerException e) {
			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to find details", System.currentTimeMillis()));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
		}
	}

	private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
		double R = 6371; // Radius of Earth in km
		double dLat = Math.toRadians(lat2 - lat1);
		double dLon = Math.toRadians(lon2 - lon1);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double distance = R * c; // Distance in km
		return distance;
	}

	private boolean isImage(String extension) {
		return Arrays.asList("jpg", "jpeg", "png", "gif").contains(extension.toLowerCase());
	}

	private boolean isVideo(String extension) {
		return Arrays.asList("mp4", "avi", "mov").contains(extension.toLowerCase());
	}

	public Map<String, String> getTotalAdsByTitlePercentage() {
		List<AdsInfo> allAds = infoRepository.findAll();
		Map<String, Long> adsByTitleMap = new HashMap<>();
		Map<String, String> adsByTitlePercentageMap = new HashMap<>();

		Set<String> specificAdTitles = new HashSet<>();
		specificAdTitles.add("Crypto");
		specificAdTitles.add("Token");
		specificAdTitles.add("Defi");
		specificAdTitles.add("NFT");
		specificAdTitles.add("Metaverse");
		specificAdTitles.add("P2E");

		long totalAds = allAds.size();

		long totalSpecificAds = 0;
		for (AdsInfo ad : allAds) {
			String adTitle = ad.getAdTitle();
			if (adTitle != null && specificAdTitles.contains(adTitle)) {
				adsByTitleMap.put(adTitle, adsByTitleMap.getOrDefault(adTitle, 0L) + 1);
				totalSpecificAds++;
			}
		}

		DecimalFormat df = new DecimalFormat("0.00");
		for (String adTitle : specificAdTitles) {
			long count = adsByTitleMap.getOrDefault(adTitle, 0L);
			double percentage = (count * 100.0) / totalAds;
			String formattedPercentage = df.format(percentage) + "%";
			adsByTitlePercentageMap.put(adTitle, formattedPercentage);
		}

		long otherAdsCount = totalAds - totalSpecificAds;
		double otherPercentage = (otherAdsCount * 100.0) / totalAds;
		String formattedOtherPercentage = df.format(otherPercentage) + "%";
		adsByTitlePercentageMap.put("Other", formattedOtherPercentage);

		return adsByTitlePercentageMap;
	}

}