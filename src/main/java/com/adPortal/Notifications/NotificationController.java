package com.adPortal.Notifications;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.adPortal.Exception.Error;
import com.adPortal.ads.AdsInfo;
import com.adPortal.ads.AdsInfoRepository;
import com.adPortal.user.UserRepository;

@RestController
@CrossOrigin(origins = { ("http://localhost:3000") })
@RequestMapping("/notifications")
public class NotificationController {

	@Autowired
	private NotificationsRepository repository;

	@Autowired
	private AdsInfoRepository infoRepository;

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@GetMapping("/getAll")
	public ResponseEntity<?> getAllNotifications() {
		try {
			List<Notifications> notificationList = repository.findAll();

			if (notificationList.isEmpty()) {
				throw new NotificationException(404, "No notifications found...!!!");
			}

			for (Notifications notification : notificationList) {
				notification.setIsSeen(true);
				repository.save(notification);
			}

			return ResponseEntity.ok(notificationList);
		} catch (NotificationException ex) {
			return ResponseEntity.status(ex.getStatusCode()).body(new Error(ex.getStatusCode(), ex.getMessage(),
					"Don't have any notifications ", System.currentTimeMillis()));

		} catch (Exception ex) {
			return new ResponseEntity<>("An error occurred while processing the request.",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@GetMapping("/getNotifi/{email}")
	public ResponseEntity<?> getNotification(@PathVariable String email) {
		try {
			List<Notifications> notificationList = repository.findByEmail(email);
			if (notificationList.isEmpty()) {
				throw new NotificationException(404, "No user found with email: " + email);
			}

			List<AdsInfo> findAll = infoRepository.findAll();
			System.out.println("List from db:: " + findAll);
			String username = null;
			long count = 0;
			String redirectKey = "checkNotification";

			for (AdsInfo adsInfo : findAll) {
				LocalDate currentDate = LocalDate.now();
				long endDateMillis = adsInfo.getEndDate();

				Instant instant = Instant.ofEpochMilli(endDateMillis);
				LocalDate endDate = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate();
				if (endDate.equals(currentDate)) {
					username = adsInfo.getUser().getUsername();
					System.out.println("username:: " + username);
					count = adsInfo.getViews();
				}
				Notifications nn = new Notifications(false,
						username + " your ads show time will expire today " + count + " visitors have seen your ad ",
						email, redirectKey, System.currentTimeMillis());
				repository.save(nn);
			}

			for (Notifications notifications : notificationList) {
				notifications.setIsSeen(true);
				repository.save(notifications);
			}
			return ResponseEntity.ok(notificationList);
		} catch (NotificationException ex) {
			return ResponseEntity.status(ex.getStatusCode()).body(new Error(ex.getStatusCode(), ex.getMessage(),
					"Don't have any notifications ", System.currentTimeMillis()));
		} catch (Exception ex) {
			return new ResponseEntity<>("An error occurred while processing the request.",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/unseen/{email}")
	public ResponseEntity<?> getUnseenCountByUserId(@PathVariable String email) {
		try {
			List<Notifications> notificationList = repository.findByEmail(email);
			if (notificationList.isEmpty()) {
				throw new NotificationException(404, "No user found with email: " + email);
			}

			// Count unseen notifications
			long unseenCount = notificationList.stream().filter(notification -> !notification.getIsSeen()).count();

			return ResponseEntity.ok(unseenCount);
		} catch (NotificationException e) {
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to fetch unseen count: " + e.getMessage());
		}
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@GetMapping("/{email}")
	public ResponseEntity<?> getNotificationByRole(@PathVariable String email) {
		try {
			List<Notifications> notificationList = repository.findByEmail(email);
			if (notificationList.isEmpty()) {
				throw new NotificationException(404, "No user found with email: " + email);
			}

			for (Notifications notifications : notificationList) {
				notifications.setIsSeen(true);
				repository.save(notifications);
			}
			return ResponseEntity.ok(notificationList);
		} catch (NotificationException ex) {
			return ResponseEntity.status(ex.getStatusCode()).body(new Error(ex.getStatusCode(), ex.getMessage(),
					"Don't have any notifications ", System.currentTimeMillis()));
		} catch (Exception ex) {
			return new ResponseEntity<>("An error occurred while processing the request.",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

//	to clear all notification (admin)
	@CrossOrigin(origins = { ("http://localhost:3000") })
	@DeleteMapping("delete")
	public void deleteAllNotification() {
		repository.deleteAll();
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@GetMapping("/getNotification/{id}")
	public ResponseEntity<?> getNotificationById(@PathVariable long id) {
		try {
			Optional<Notifications> findById = repository.findById(id);
			if (findById.isPresent()) {

				return ResponseEntity.ok(findById);

			} else {
				return ResponseEntity.ok(0);
			}
		} catch (NotificationException e) {
			return ResponseEntity.status(e.getStatusCode()).body(new Error(e.getStatusCode(), e.getMessage(),
					"Don't have any notification", System.currentTimeMillis()));
		} catch (Exception ex) {
			return new ResponseEntity<>("An error occurred while processing the request.",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@GetMapping("/getNotification/{email}/{id}")
	public ResponseEntity<?> getNotificationById(@PathVariable String email, @PathVariable long id) {
		try {
			Optional<Notifications> findById = repository.findByEmailAndId(email, id);
			if (findById.isPresent()) {
				return ResponseEntity.ok(findById);

			} else {
				return ResponseEntity.ok(0);
			}
		} catch (NotificationException e) {
			return ResponseEntity.status(e.getStatusCode()).body(new Error(e.getStatusCode(), e.getMessage(),
					"Don't have any notification", System.currentTimeMillis()));
		} catch (Exception ex) {
			return new ResponseEntity<>("An error occurred while processing the request.",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@DeleteMapping("/deleteNotificationById/{id}")
	public ResponseEntity<?> clearNotification(@PathVariable long id) {
		try {
			if (repository.existsById(id)) {
				repository.deleteById(id);
				return ResponseEntity.ok(id);
			} else {
				return ResponseEntity.ok(0);
			}

		} catch (NotificationException e) {
			return ResponseEntity.status(e.getStatusCode()).body(new Error(e.getStatusCode(), e.getMessage(),
					"Don't have any notification to delete", System.currentTimeMillis()));
		} catch (Exception ex) {
			return new ResponseEntity<>("An error occurred while processing the request.",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@DeleteMapping("/{email}/{id}")
	public ResponseEntity<?> clearNotification(@PathVariable String email, @PathVariable long id) {
		try {

			Optional<Notifications> notificationOptional = repository.existsByEmailAndId(email, id);
			if (notificationOptional.isPresent()) {
				repository.deleteById(id);
				return ResponseEntity.ok("Notificaion with id:: " + id + " deleted successfully...!!");
			} else {
				return ResponseEntity.ok("Notification with id:: " + 0 + "does not found..!!");
			}

		} catch (NotificationException e) {
			return ResponseEntity.status(e.getStatusCode()).body(new Error(e.getStatusCode(), e.getMessage(),
					"Don't have any notification to delete", System.currentTimeMillis()));
		} catch (Exception ex) {
			return new ResponseEntity<>("An error occurred while processing the request.",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

//	@GetMapping("/getNotiToAdvertiser/{email}")
//	public void getNotificationToAdvertiser(@PathVariable String email) {
//		List<User> adminList = userRepo.findByRole("Advertiser");
//
//		List<Notifications> notificationsList = repository.findByEmail(email);
//		if (!notificationsList.isEmpty()) {
//			Notifications notifications = notificationsList.get(0);
//			String userEmail = notifications.getEmail();
//			AdsInfo adsInfo = infoRepository.findByUserEmail(userEmail);
//
//			if (adsInfo != null) {
//				LocalDate currentDate = LocalDate.now();
//				LocalDate endDate = LocalDate.ofEpochDay(adsInfo.getEndDate());
//
//				if (endDate.equals(currentDate)) {
//					for (User advertiserListNew : adminList) {
//						Notifications notification = new Notifications(false,
//								adsInfo.getUser().getName() + " ads show time is expired today ",
//								advertiserListNew.getEmail(), System.currentTimeMillis());
//
//						repository.save(notification);
//					}
//				}
//
//			}
//		}
//	}
//	
//	

	//// not in use
	@CrossOrigin(origins = { ("http://localhost:3000") })
	@GetMapping("/getNotiToAdvertiser")
	public ResponseEntity<?> notificationOnEndDate() {
		try {
			List<AdsInfo> findAll = infoRepository.findAll();
			System.out.println("List from db:: " + findAll);
			String email = null;
			String username = null;
			long count = 0;
			for (AdsInfo adsInfo : findAll) {
				System.out.println("Check point 1:: ");
				System.out.println("endDate is:: " + adsInfo.getEndDate());
				LocalDate currentDate = LocalDate.now();
				long endDateMillis = adsInfo.getEndDate();
				String redirectKey = "checkNotification";

				Instant instant = Instant.ofEpochMilli(endDateMillis);
				LocalDate endDate = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate();
				if (endDate.equals(currentDate)) {
					System.out.println("Check point 2:: ");
					email = adsInfo.getUser().getEmail();
					System.out.println("email:: " + email);
					username = adsInfo.getUser().getUsername();
					System.out.println("username:: " + username);

					count = adsInfo.getViews();
				}
				Notifications nn = new Notifications(false,
						username + " your ads show time will expire today " + count + " visitors have seen your ad ",
						email, redirectKey, System.currentTimeMillis());
				return ResponseEntity.ok(nn);
			}
			return null;
		} catch (NotificationException e) {
			return ResponseEntity.status(e.getStatusCode()).body(new Error(e.getStatusCode(), e.getMessage(),
					"Don't have any notification ", System.currentTimeMillis()));
		} catch (Exception ex) {
			return new ResponseEntity<>("An error occurred while processing the request.",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
