package com.adPortal.history;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.adPortal.user.UserControllerException;

@RestController
@CrossOrigin(origins = { ("http://localhost:3000") })
@RequestMapping("/history")
public class HistoryController {

	@Autowired
	private HistoryService historyService;

	@Autowired
	private HistoryRepository historyRepo;

//    @GetMapping
//    public List<History> getAllHistory() throws InterruptedException {
//        return historyService.getAllHistory();
//    }

//    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<StreamingResponseBody> getAllHistoryWithDelay() {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.setContentDispositionFormData("filename", "history.json");
//
//        StreamingResponseBody responseBody = outputStream -> {
//            ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
//            List<History> historyList = historyService.getAllHistory();
//            for (History history : historyList) {
//                executor.schedule(() -> {
//                    try {
//                        outputStream.write(history.toString().getBytes());
//                        outputStream.write("\n".getBytes());
//                        outputStream.flush();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }, 3, TimeUnit.SECONDS);
//            }
//            executor.shutdown();
//            try {
//                executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//                System.err.println("Thread interrupted");
//            }
//        };
//
//        return new ResponseEntity<>(responseBody, headers, HttpStatus.OK);
//    }

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@GetMapping("/{id}")
	public History getHistoryById(@PathVariable long id) {
		try {
			return historyService.getHistoryById(id);

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@PostMapping("/save")
	public History createHistory(@RequestBody History history) {
		try {
			return historyService.createHistory(history);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@PutMapping("/{id}")
	public History updateHistory(@PathVariable long id, @RequestBody History historyDetails) {
		try {
			return historyService.updateHistory(id, historyDetails);

		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}

	@CrossOrigin(origins = { "http://localhost:3000" })
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteHistory(@PathVariable long id) {
		try {
			if (historyRepo.existsById(id)) {
				historyService.deleteHistory(id);
				return ResponseEntity.ok("History deleted successfully.");
			} else {
				return ResponseEntity.notFound().build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete history.");
		}
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@GetMapping("/advertiserDTO/{userId}")
	public ResponseEntity<?> getHistoriesByUserIdConvertedToDTO(@PathVariable long userId) {

		try {
			List<AdvertiserDTO> dtos = historyService.getHistoriesByUserIdConvertedToDTO(userId);
			return ResponseEntity.ok(dtos);
		} catch (UserControllerException e) {
			return ResponseEntity.status(e.getStatusCode())
					.body(new Error(e.getStatusCode(), e.getMessage(), "User not found ", System.currentTimeMillis()));

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid user details");
		}

	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@GetMapping("/viewerDTO/{userId}")
	public ResponseEntity<?> getHistoriesByUserNameConvertedToDTO(@PathVariable long userId) {
		try {
			List<ViewerDTO> dtos = historyService.getHistoriesByUserNameConvertedToDTO(userId);
			return ResponseEntity.ok(dtos);
		} catch (UserControllerException e) {
			return ResponseEntity.status(e.getStatusCode())
					.body(new Error(e.getStatusCode(), e.getMessage(), "User not found ", System.currentTimeMillis()));

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid user details");
		}
	}

}
