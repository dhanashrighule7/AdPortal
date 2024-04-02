package com.adPortal.ads;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.adPortal.Exception.Error;
import com.adPortal.Notifications.Notifications;
import com.adPortal.Notifications.NotificationsRepository;
import com.adPortal.user.User;
import com.adPortal.user.UserRepository;

@RestController
@CrossOrigin(origins = { ("http://localhost:3000") })
@RequestMapping("/ads")
public class AdsInfoController {

	@Autowired
	private AdsInfoRepository infoRepository;

	@Autowired
	private AdsInfoService infoService;

	@Autowired
	private ViewsRepository viewsRepository;

	@Autowired
	private MediaAdRepository mediaAdRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private NotificationsRepository notificationsRepository;

	@CrossOrigin(origins = { "http://localhost:3000" })
	@GetMapping("/myads/{id}")
	public ResponseEntity<?> getMyAds(@PathVariable Long id) {
		try {
			Optional<AdsInfo> myAds = infoService.getMyAds(id);
			if (myAds.isPresent()) {
				AdsInfo adsInfo = myAds.get();
				long currentViews = adsInfo.getViews();
				adsInfo.setViews(currentViews + 1);

				infoRepository.save(adsInfo);
				return ResponseEntity.ok().body(adsInfo);
			} else {
				return ResponseEntity.notFound().build();
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An error occurred while processing your request. Please try again later.");
		}
	}

///////////++++++++ HAVE TWO TABLES LIST ++++++++//////////
//	@CrossOrigin(origins = { ("http://localhost:3000") })
//	@GetMapping("/getList/{category}/{subcategory}")
//	public ResponseEntity<List<AdsInfo>> filterAdsInfosByCategoryAndSubcategory(@PathVariable String category, @PathVariable String subcategory){
//		return infoService.filterAdsInfosByCategoryAndSubcategory(category, subcategory)
//	}

	// get data in url format
	@CrossOrigin(origins = { ("http://localhost:3000") })
	@GetMapping("/mediaData/{userId}")
	public ResponseEntity<?> getMediaAllList(@PathVariable Long userId) throws IOException {
		try {
			ResponseEntity<?> mediaData = infoService.getMediaByUserId(userId);// rahul sir has
			return mediaData;
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error occurred while fetching media data.");
		}

	}

	// get data in url format
	@CrossOrigin(origins = { ("http://localhost:3000") })
	@GetMapping("/mediaNew/{userId}/{id}")
	public ResponseEntity<?> getMediaNew(@PathVariable Long userId, @PathVariable Long id) {

		try {
			ResponseEntity<?> mediaDataByUserIdAndId = infoService.getMediaDataByUserIdAndId(userId, id);

			return mediaDataByUserIdAndId;

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error occurred while fetching media data.");
		}
	}

///////rahul sir has
	@CrossOrigin(origins = { ("http://localhost:3000") })
	@GetMapping("/media/{userId}/{id}")
	public ResponseEntity<?> getMedia(@PathVariable Long userId, @PathVariable Long id) {

		try {
			MediaAd mediaData = mediaAdRepository.findByUserIdAndId(userId, id);
			byte[] imageData = mediaData.getMedia();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.IMAGE_JPEG);
			headers.setContentLength(imageData.length);

			return ResponseEntity.ok().headers(headers).contentLength(imageData.length)
					.contentType(MediaType.IMAGE_JPEG).body(imageData);

//			return ResponseEntity.status(HttpStatus.OK)
//	                .contentType(MediaType.valueOf(IMAGE_PNG_VALUE))
//	                .body(imageData);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error occurred while fetching media data.");
		}
	}

	// get data in url format
	@CrossOrigin(origins = { "http://localhost:3000" })
	@GetMapping("/viewer/all/{latitude}/{longitude}/{pageNumber}")
	public ResponseEntity<?> getAllMediaForViewer(@PathVariable double latitude, @PathVariable double longitude,
			@PathVariable Integer pageNumber) {
		try {
			ResponseEntity<?> response = infoService.getAllAdsForViewers(latitude, longitude, pageNumber);
			return response;
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error occurred while fetching media data.");
		}
	}

	// to access http url for all ads to show advertiser or viewer
	@GetMapping("media/allData")
	public ResponseEntity<byte[]> getImageDataById(@RequestParam("id") long id) {
		try {
			MediaAd mediaAd = mediaAdRepository.findById(id).orElse(null);
			if (mediaAd == null) {
				return ResponseEntity.notFound().build();
			}

			byte[] imageData = mediaAd.getMedia();

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.IMAGE_JPEG);
			headers.setContentLength(imageData.length);

			return ResponseEntity.ok().headers(headers).body(imageData);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	/// ?????????????FOR VIEWERS (SHOW MEDIA) ?????????????/////
	@CrossOrigin(origins = { ("http://localhost:3000") })
	@GetMapping("/viewer")
	public ResponseEntity<?> getAllMediaData() {
		try {
			List<MediaAd> all = mediaAdRepository.findAll();

			return ResponseEntity.ok(all);

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error occurred while fetching media data.");
		}
	}

//	@CrossOrigin(origins = { ("http://localhost:3000") })
//	@GetMapping("/viewer/{id}")
//	public ResponseEntity<?> getMediaForViewers(@PathVariable Long id) {
//		try {
//			return infoService.getMediaForViewers(id);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while fetching  data.");
//		}
//	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@GetMapping("/viewer/{id}/{category}/{subcategory}/{lattitude}/{longitude}")
	public ResponseEntity<?> getMediaForViewers(@PathVariable Long id, @PathVariable String category,
			@PathVariable String subcategory, @PathVariable String lattitude, @PathVariable String longitude) {
		try {
			return infoService.getMediaForViewers(id, category, subcategory, lattitude, longitude);
		} catch (AdsInfoException e) {
			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to find data", System.currentTimeMillis()));
		}
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@GetMapping("/allAds")
	public List<AdsInfo> getAllAds() {
		try {
			return infoService.getAllAds();
		} catch (Exception e) {
			return Collections.emptyList();
		}
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@GetMapping("/allAdsByUserId/{userId}")
	public ResponseEntity<?> allAdsByUserId(@PathVariable long userId) {
		try {
			return infoService.allAdsByUserId(userId);
		} catch (Exception e) {
			return null;
		}
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@PutMapping("/approve/{id}/{response}")
	public ResponseEntity<?> updateAdsInfo(@PathVariable Long id, @PathVariable String response) throws Exception {
		try {
			return infoService.updateAdsInfo(id, response);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while updating data.");
		}
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@GetMapping("/totalViewsCountToAdvertiser/{userId}")
	public long getTotalViewsCountToAdvertiser(@PathVariable long userId) {
		try {
			return infoService.getTotalViewsCountToAdvertiser(userId);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@PostMapping("/adsSave")
	public ResponseEntity<?> saveAdsInfo(@RequestParam(name = "file", required = false) MultipartFile media,
			String category, String subCategory, String language, String link, String adsDuration, String adPosition,
			long startDate, long endDate, long feesPaid, String totalFees, String address, String state, String city,
			String longitude, String latitude, String AdTitle, String description, long userId) {
		try {
			System.out.println("Check point 111");
			String originalFilename = null;
			String extension = null;
			byte[] bytes = null;
			AdsInfo adsInfo = new AdsInfo();
			System.out.println("Check point::::::");
			Optional<User> findById = userRepository.findById(userId);
			User user = findById.get();
			long id = user.getId();
			System.out.println("Check point2::::::");

			adsInfo.setAdTitle(AdTitle);
			adsInfo.setDescription(description);
			adsInfo.setAdPosition(adPosition);
			adsInfo.setCategory(category);
			adsInfo.setSubCategory(subCategory);
			adsInfo.setLanguage(language);
			adsInfo.setLink(link);
			adsInfo.setAdsDuration(adsDuration);
			adsInfo.setStartDate(startDate);
			adsInfo.setEndDate(endDate);
			adsInfo.setFeesPaid(feesPaid);
			adsInfo.setTotalFees(totalFees);
			adsInfo.setLongitude(longitude);
			adsInfo.setLattitude(latitude);
			adsInfo.setAddress(address);
			adsInfo.setCity(city);
			adsInfo.setState(state);
			adsInfo.setUser(user);

			List<User> adminList = userRepository.findByRole("Admin");
			long createdOn = adsInfo.getCreatedOn();
			AdsInfo save;

			if (media != null) {
				System.out.println("Check media:::");
				originalFilename = media.getOriginalFilename();
				bytes = media.getBytes();
				extension = originalFilename.substring(originalFilename.lastIndexOf("."));
				System.out.println("Check extension;:" + extension);
				if (category.equals("Video") && extension.equals(".mp4")) {
					System.out.println("check 112:::");
					save = infoRepository.save(adsInfo);
					MediaAd mm = new MediaAd(id, save.getId(), bytes, extension, category, createdOn, originalFilename,
							subCategory);
					mediaAdRepository.save(mm);
					Views views = new Views(userId, save.getId(), save.getViews());
					viewsRepository.save(views);
					adsInfo.setFileName(originalFilename);
					String redirectKey = "checkAd";
					for (User adminListt : adminList) {
						Notifications notification = new Notifications(false,
								"New video ad posted by " + findById.get().getUsername(), adminListt.getEmail(),
								redirectKey, System.currentTimeMillis());
						notificationsRepository.save(notification);

					}
					return ResponseEntity.ok().body(mm);
				} else if (category.equals("Banner") && extension.equals(".jpg") || extension.equals(".png")) {
					System.out.println("check 114:::");
					save = infoRepository.save(adsInfo);
					MediaAd mm = new MediaAd(id, save.getId(), bytes, extension, category, createdOn, originalFilename,
							subCategory);
					mediaAdRepository.save(mm);
					Views views = new Views(userId, save.getId(), save.getViews());
					viewsRepository.save(views);
					adsInfo.setFileName(originalFilename);
					String redirectKey = "checkAd";
					for (User adminListNew : adminList) {
						Notifications notification = new Notifications(false,
								"New banner ad posted by " + findById.get().getUsername(), adminListNew.getEmail(),
								redirectKey, System.currentTimeMillis());
						notificationsRepository.save(notification);
					}
					return ResponseEntity.ok().body(mm);
				} else {
					System.out.println("check 115:::");
					throw new AdsInfoException(401, "Please upload Proper format file");
				}
			}
			System.out.println("Check adInfo Link::" + adsInfo.getLink());

			if (category.equals("Video") && media == null && adsInfo.getLink() != null) {
				System.out.println("check 113:::");
				save = infoRepository.save(adsInfo);
				MediaAd mm = new MediaAd(id, save.getId(), category, createdOn, adsInfo.getLink(), subCategory);
				mediaAdRepository.save(mm);
				Views views = new Views(userId, save.getId(), save.getViews());
				viewsRepository.save(views);
				String redirectKey = "checkAd";

				for (User adminListNew : adminList) {
					Notifications notification = new Notifications(false,
							"New link for video posted by " + findById.get().getUsername(), adminListNew.getEmail(),
							redirectKey, System.currentTimeMillis());
					notificationsRepository.save(notification);

				}

				return ResponseEntity.ok().body(save);
			} else if (adsInfo.getLink() == null) {
				throw new AdsInfoException(401, "please upload video,banner file or link");
			} else {
				System.out.println("check 115:::");
				throw new AdsInfoException(401, "Please upload Proper format file");
			}
		}

		catch (AdsInfoException e) {
			System.out.println("Caught UserControllerException:");
			System.out.println("Exception message: " + e.getMessage());

			System.out.println("check 116:::");
			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to save file ", System.currentTimeMillis()));
		} catch (Exception e2) {

			return ResponseEntity.badRequest().build();
		}
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@GetMapping("/sort/{userId}/{order}/{pageNumber}")
	public ResponseEntity<List<AdsInfo>> getDescendingData(@PathVariable int pageNumber, @PathVariable String order,
			@PathVariable long userId) {
		try {
			System.out.println("Check Point 1 :::: ");
			List<AdsInfo> descendingData = infoService.getDescendingData(pageNumber, order, userId);
			if (descendingData != null && !descendingData.isEmpty()) {
				System.out.println("Check Point 7 :::: ");
				return ResponseEntity.ok().body(descendingData);
			} else {
				System.out.println("Check Point 8 :::: ");
				return ResponseEntity.notFound().build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@GetMapping("/getApprovedCountForAdvertiser/{id}")
	public Long getCountOfApprovedAds(@PathVariable Long id) {
		try {
			return infoService.getCountOfApprovedAds(id);
		} catch (Exception e) {
			// TODO: handle exception
			return 0L;
		}
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@GetMapping("/getApprovedCountForAdmin")
	public Long getCountOfApprovedAdsForAdmin() {
		try {
			return infoService.getCountOfApprovedAds();
		} catch (Exception e) {
			// TODO: handle exception
			return 0L;
		}
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@GetMapping("/totalViewsCount")
	public ResponseEntity<?> getTotalViewsCount() {
		try {
			return infoService.getTotalViewsCount();

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error occurred while getting views count.");
		}
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@GetMapping("/totalViewsCount/{userId}")
	public long getTotalViewsCountByUserId(@PathVariable long userId) {
		try {
			return infoService.getTotalViewsCountByUserId(userId);

		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@GetMapping("/totalSpent/{userId}")
	public long getTotalAmountSpentCountByUserId(@PathVariable long userId) {
		try {
			return infoService.getTotalAmountSpentByUserId(userId);
		} catch (Exception e) {
			return 0;
		}
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@GetMapping("/totalSpent")
	public long getAmountSpentCount() {
		try {
			return infoService.getTotalAmountSpent();
		} catch (Exception e) {
			return 0;
		}
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@GetMapping("/totalAds/{userId}")
	public long getTotalAdsByUserId(@PathVariable long userId) {
		try {
			return infoService.getTotalAdsByUserId(userId);

		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@GetMapping("/totalAds")
	public long getTotalAds() {
		try {
			return infoService.getTotalAds();

		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}
	}

	@GetMapping("/oldest-end-date")
	public ResponseEntity<?> getOldestEndDate() {
		try {
			Map<LocalDate, Integer> oldestEndDate = infoService.findOldestEndDate();
			return ResponseEntity.ok(oldestEndDate);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error occurred while fetching the oldest end date.");
		}
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@GetMapping("/avalibleDate/{category}")
	public ResponseEntity<?> getavalibleDate(@PathVariable String category) {
		try {
			return infoService.availableDate(category);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error occurred while fetching the available date.");
		}

	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@GetMapping("/ads/count/video")
	public long countVideoAds() {
		try {
			return infoService.countVideoAds();
		} catch (Exception e) {
			return 0;
		}
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@GetMapping("/ads/count/banner")
	public long countBannerAds() {
		try {
			return infoService.countBannerAds();
		} catch (Exception e) {
			return 0;
		}
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@GetMapping("/latest-end-date")
	public ResponseEntity<?> findLatestEndDate() {
		try {
			Map<LocalDate, Integer> oldestEndDate = infoService.findLatestEndDate();
			return ResponseEntity.ok(oldestEndDate);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error occurred while fetching the available date.");
		}
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@GetMapping("/user-ads")
	public ResponseEntity<?> getUserAds(@RequestParam(required = false) String category,
			@RequestParam(required = false) String subcategory) {
		try {
			List<AdsInfo> adsInfos = infoService.getUserAds(category, subcategory);
			return ResponseEntity.ok(adsInfos);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error occurred while fetching the data.");
		}

	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@GetMapping("/user-ads/banner")
	public ResponseEntity<?> getUserBannerAds(@RequestParam(required = false) String category,
			@RequestParam(required = false) String subcategory) {
		try {
			List<AdsInfo> adsInfos = infoService.getUserBannerAds(category, subcategory);
			return ResponseEntity.ok(adsInfos);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error occurred while fetching the data.");
		}

	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@GetMapping("/getList/{category}/{subcategory}/{lattitude}/{longitude}")
	public ResponseEntity<?> adsForViewersToView(@PathVariable String category, @PathVariable String subcategory,
			@PathVariable String longitude, @PathVariable String lattitude) {
		try {
			System.out.println("Check Point 1 ::::: Category " + category + " SubCategory ::::: " + subcategory
					+ " longitude ::::: " + longitude + " lattitude ::::: " + lattitude);
			return infoService.adsForViewersToView(category, subcategory, longitude, lattitude);

		} catch (Exception e) {
			// TODO: handle exception
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error occurred while fetching the data.");
		}
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@GetMapping("/tokensToUser/{userId}/{subcategory}/{time}")
	public ResponseEntity<?> watchVideoAndGetTokens(@PathVariable String userId, @PathVariable String subcategory,
			@PathVariable int time) {
		try {
			return infoService.watchVideoAndGetTokens(userId, subcategory, time);

		} catch (Exception e) {
			// TODO: handle exception
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error occurred while fetching the data.");
		}
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@GetMapping("/getTotalViews/{userId}/{date}/{category}")
	public Long getTotalViewsByUserIdAndDateAndCategory(@PathVariable long userId,
			@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date, @PathVariable String category) {
		try {
			Long totalViews = viewsRepository.getTotalViewsByUserIdAndDateAndCategory(userId, date, category);
			if (totalViews != null) {

				return totalViews;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return 0L;

	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@GetMapping("/viewers/statistics")
	public ResponseEntity<?> getViewersStatistics() {
		try {
			return infoService.calculateViewersStatistics();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
							"Error while calculating viewers statistics", e.getMessage(), System.currentTimeMillis()));
		}
	}

}