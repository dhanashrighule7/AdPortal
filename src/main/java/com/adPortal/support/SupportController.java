package com.adPortal.support;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.adPortal.Exception.Error;
import com.adPortal.user.User;

@RestController
@CrossOrigin(origins = { ("http://localhost:3000") })
@RequestMapping("/support")
public class SupportController {

	@Autowired
	private SupportService supportService;

	@Autowired
	private SupportRepository supportRepository;

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@GetMapping("/tickets/{pageNumber}")
	public ResponseEntity<?> getAllListOfSupportTickets(@PathVariable int pageNumber) {
		try {
			return supportService.getAllListOfSupportTickets(pageNumber);

		} catch (Exception e) {
			return null;
		}
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@GetMapping("/ticket/{id}")
	public ResponseEntity<?> getTicketById(@PathVariable Long id) {
		try {
			Optional<Support> ticket = supportService.getTicketById(id);
			if (ticket.isPresent()) {
				return ResponseEntity.ok(ticket);
			} else {
				return ResponseEntity.notFound().build();
			}
		} catch (Exception e) {

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@PostMapping("/raiseTicket")
	public ResponseEntity<?> generateSupportTicket(@RequestBody Support support) {
		try {
			return supportService.generateSupportTicket(support);
		} catch (Exception e) {

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

//	@PutMapping("/update/{userId}/{id}")
//	public Optional<Support> solveOrDeleteTicket(@PathVariable Long userId, @PathVariable long id) {
//		return supportService.solveOrDeleteTicket(id, userId);
//	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@PutMapping("/update/{userId}/{id}/{response}")
	public ResponseEntity<?> solveOrDeleteTicket(@PathVariable("userId") Long userId, @PathVariable("id") long id,
			@PathVariable("response") String response) {
		try {
			return supportService.solveOrDeleteTicket(id, userId, response);
		} catch (SupportException ex) {
			return ResponseEntity.status(ex.getStatusCode()).body(new Error(ex.getStatusCode(), ex.getMessage(),
					"Don't have access to process the request", System.currentTimeMillis()));
		}
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@GetMapping("/advertiserList/{pageNumber}")
	public ResponseEntity<?> advertiserSupportList(@PathVariable int pageNumber) {
		try {
			return supportService.advertiserSupportList(pageNumber);

		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@GetMapping("/viewerList/{pageNumber}")
	public ResponseEntity<?> viewerSupportList(@PathVariable int pageNumber) {
		try {
			return supportService.viewerSupportList(pageNumber);
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@GetMapping("/solvedList/{status}/{pageNumber}")
	public List<Support> getApprovedAds(@PathVariable String status, @PathVariable int pageNumber) {
		try {
			return supportService.solvedAdsList(status, pageNumber);

		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteTicket(@PathVariable("id") Long id) {
		try {
			if (supportRepository.existsById(id)) {
				supportService.deleteTicketById(id);
				return ResponseEntity.ok("Ticket deleted successfully");
			} else {
				return ResponseEntity.notFound().build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete history.");
		}

	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@GetMapping("/list/{userId}")
	public List<Support> findByUserId(@PathVariable long userId) {
		try {
			return supportRepository.findByUserId(userId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// to count whole rows for pagination
	@CrossOrigin(origins = { ("http://localhost:3000") })
	@GetMapping("/countForAdvertisersList")
	public int getNumberOfRowsForAdvertiser() {
		try {
			List<Support> sortedDataOfUser = supportRepository.findAll();
			int advertisersCount = 0;
			for (Support advertiserList : sortedDataOfUser) {
				User user = advertiserList.getUser();

				if (user != null && user.getRole().equals("Advertiser")) {
					advertisersCount++;
				}
			}
			return advertisersCount;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@GetMapping("/countForViewersList")
	public int getNumberOfRowsForViewer() {
		try {
			List<Support> sortedDataOfUser = supportRepository.findAll();
			int viewersCount = 0;
			for (Support viewerList : sortedDataOfUser) {
				User user = viewerList.getUser();

				if (user != null && user.getRole().equals("Viewer")) {
					viewersCount++;
				}
			}
			return viewersCount;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

}
