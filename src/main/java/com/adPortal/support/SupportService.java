package com.adPortal.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import com.adPortal.Exception.Error;
import com.adPortal.Notifications.Notifications;
import com.adPortal.Notifications.NotificationsRepository;
import com.adPortal.ads.Status;
import com.adPortal.user.User;
import com.adPortal.user.UserControllerException;
import com.adPortal.user.UserRepository;

@Service
public class SupportService {

	@Autowired
	private SupportRepository repository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private NotificationsRepository notificationsRepository;

	public ResponseEntity<?> generateSupportTicket(Support object) {
		try {
			long userId = object.getUser().getId();
			Optional<User> user = userRepository.findById(userId);
			if (user == null) {
				System.out.println("User not fount ");
				throw new UserControllerException(409, "User not found");
			}
			List<User> byRole = userRepository.findByRole("Admin");
			System.out.println("username::" + user.get().getUsername());
			Notifications notification = null;
			String redirectKey = "checkSupport";

			for (User adminList : byRole) {
				String email = adminList.getEmail();
				if (user.get().getRole().equals("Advertiser")) {
					notification = new Notifications(false, "ticket is raised by " + user.get().getUsername(), email,
							redirectKey, System.currentTimeMillis());
				} else {
					notification = new Notifications(false,
							"ticket is raised by viewer " + user.get().getWalletAddress(), email, redirectKey,
							System.currentTimeMillis());
				}
				notificationsRepository.save(notification);
			}
			return ResponseEntity.ok(repository.save(object));
		} catch (UserControllerException e) {
			System.out.println("In first catch block");
			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), " Unable to find user ", System.currentTimeMillis()));
		} catch (Exception ex) {
			System.out.println("in second catch block");
			return new ResponseEntity<>("An error occurred while processing the request.",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<?> getAllListOfSupportTickets(@PathVariable int pageNumber) {
		try {
			int pageSize = 20;
			Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
			Page<Support> supportPage = repository.findAll(pageable);
			List<Support> all = repository.findAll();
			return ResponseEntity.ok(supportPage.getContent());
		} catch (SupportException e) {
			return ResponseEntity.status(e.getStatusCode()).body(new Error(e.getStatusCode(), e.getMessage(),
					" !!! Unable to Find List !!! ", System.currentTimeMillis()));
		}
	}

//	public ResponseEntity<?> advertiserSupportList(int pageNumber) {
//		try {
//			int pageSize = 20;
//			int startIndex = (pageNumber - 1) * pageSize;
//			int endIndex = pageNumber * pageSize;
//			List<Support> allSupports = repository.findAll();
//			System.out.println("User:: " + allSupports);
//
//			List<Support> advertiserSupportList = new ArrayList<Support>();
//			for (int i = startIndex; i < Math.min(endIndex, allSupports.size()); i++) {
//				Support support = allSupports.get(i);
//				User user = support.getUser();
//				System.out.println("User:: " + user);
//				if (user != null && user.getRole().equals("Advertiser")) {
//					advertiserSupportList.add(support);
//				} else {
//					throw new SupportException(409, "User not found");
//				}
//			}
//			System.out.println("advertiserSupportList:: " + advertiserSupportList);
//			return ResponseEntity.ok(advertiserSupportList);
//		} catch (SupportException e) {
//			return ResponseEntity.status(e.getStatusCode()).body(new Error(e.getStatusCode(), e.getMessage(),
//					"!!! Unable to Find List !!!", System.currentTimeMillis()));
//		}
//	}

	public ResponseEntity<?> advertiserSupportList(int pageNumber) {
		try {
			int pageSize = 20;
			int startIndex = (pageNumber - 1) * pageSize;
			int endIndex = pageNumber * pageSize;

			List<Support> allSupports = repository.findAll();
			List<Support> advertiserSupportList = new ArrayList<>();

			for (Support support : allSupports) {
				User user = support.getUser();
				if (user != null && user.getRole().equals("Advertiser")) {
					advertiserSupportList.add(support);
				}
			}

			List<Support> paginatedSupports = new ArrayList<>();
			int endIndexLimited = Math.min(endIndex, advertiserSupportList.size());
			for (int i = startIndex; i < endIndexLimited; i++) {
				paginatedSupports.add(advertiserSupportList.get(i));
			}

			return ResponseEntity.ok(paginatedSupports);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Error(500, "Internal Server Error",
					"Unable to fetch advertiser list", System.currentTimeMillis()));
		}
	}

	public ResponseEntity<?> viewerSupportList(int pageNumber) {
		try {
			int pageSize = 20;
			int startIndex = (pageNumber - 1) * pageSize;
			int endIndex = pageNumber * pageSize;
			List<Support> allSupports = repository.findAll();
			List<Support> viewerSupportList = new ArrayList<>();

			for (Support support : allSupports) {
				User user = support.getUser();
				if (user != null && user.getRole().equals("Viewer")) {
					viewerSupportList.add(support);
				}
			}
			List<Support> paginatedSupports = new ArrayList<>();
			int endIndexLimited = Math.min(endIndex, viewerSupportList.size());
			for (int i = startIndex; i < endIndexLimited; i++) {
				paginatedSupports.add(viewerSupportList.get(i));
			}
			return ResponseEntity.ok(viewerSupportList);
		} catch (SupportException e) {
			return ResponseEntity.status(e.getStatusCode()).body(new Error(e.getStatusCode(), e.getMessage(),
					"!!! Unable to Find List !!!", System.currentTimeMillis()));
		}
	}

	public Optional<Support> getTicketById(long id) {
		return repository.findById(id);
	}

	public ResponseEntity<?> solveOrDeleteTicket(long id, Long userId, String response) {
		try {
			System.out.println("User Id " + userId);
			System.out.println("Support Id " + id);
			System.out.println("response " + response);
			Optional<User> byId = userRepository.findById(userId);
			User user = byId.get();
			if (byId.get() == null) {
				System.out.println("User not fount ");
				throw new SupportException(409, "User not found");
			}
			String notificationMessage;
			Optional<Support> supportById = repository.findById(id);
			Support support = supportById.get();
			System.err.println(support);
			Notifications notification = null;
			String redirectKey = "checkSupport";

			if (user.getRole().equals("Admin") || user.getRole().equals("Manager")) {
				System.out.println("If role is Admin or Manager ");
				if (response.equals("solve")) {
					System.out.println("if response id solve");
					support.markApproved();
					support.setAction(response);
					notificationMessage = " Your status is changed by " + user.getEmail();
					if (support.getUser().getRole().equals("Advertiser")) {
						System.out.println("if solve then to advertiser");
						notification = new Notifications(false, notificationMessage, support.getUser().getEmail(),
								redirectKey, System.currentTimeMillis());
					} else {
						System.out.println("if solve then to advertiser");
						notificationMessage = " Your status is changed by " + user.getEmail();
						notification = new Notifications(false, notificationMessage,
								Long.toString(support.getUser().getId()), redirectKey, System.currentTimeMillis());
					}
				} else if (response.equals("reject")) {
					System.out.println("if status id rejected");
					support.markRejected();
					support.setAction(response);
					notificationMessage = " Your status is changed by " + user.getEmail();
					if (support.getUser().getRole().equals("Advertiser")) {
						System.out.println("if reject then to advertiser");
						notification = new Notifications(false, notificationMessage, support.getUser().getEmail(),
								redirectKey, System.currentTimeMillis());
					} else {
						System.out.println("if reject then to viewer");
						notificationMessage = " Your status is changed by " + user.getEmail();
						notification = new Notifications(false, notificationMessage,
								Long.toString(support.getUser().getId()), redirectKey, System.currentTimeMillis());
					}
				}
			} else if (support.getStatus() == Status.pending && user.getRole().equals("Viewer")
					|| user.getRole().equals("Advertiser")) {
				System.out.println("if role is advertiser or viewer");
				if (response.equals("delete")) {
					support.markDeleted();
					support.setAction(response);
					if (user.getRole().equals("Viewer")) {
						System.out.println("if delete then to viewer");
						notificationMessage = " You have deleted token. ";
						notification = new Notifications(false, notificationMessage, Long.toString(user.getId()),
								redirectKey, System.currentTimeMillis());
					} else {
						System.out.println("if delete then to advertiser");
						notificationMessage = " You have deleted token. " + user.getEmail();
						notification = new Notifications(false, notificationMessage, user.getEmail(), redirectKey,
								System.currentTimeMillis());
					}
				}
			} else {
				System.out.println("Invalid response");
				throw new SupportException(400, "Invalid response ");
			}

			repository.save(support);
			notificationsRepository.save(notification);
			return ResponseEntity.ok(support);
		} catch (SupportException e) {
			System.out.println("In first catch block");
			return ResponseEntity.status(e.getStatusCode()).body(new Error(e.getStatusCode(), e.getMessage(),
					"Unable to find user , please add valid address", System.currentTimeMillis()));
		} catch (Exception ex) {
			System.out.println("in second catch block");
			return new ResponseEntity<>("An error occurred while processing the request.",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public long deleteTicketById(@PathVariable long id) {
		if (repository.existsById(id)) {
			repository.deleteById(id);
			return id;
		}

		return 0;
	}

	public List<Support> solvedAdsList(String status, int pageNumber) {
		int pageSize = 20;
		Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);

		if (status.equals("solved")) {
			System.out.println("check point 0");
			Page<Support> supportPage = repository.findByStatus(Status.solved, pageable);
			System.out.println("list from db::" + supportPage);
			return supportPage.getContent();
		}

		return Collections.emptyList();
	}
}
