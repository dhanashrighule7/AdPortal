package com.adPortal.payments;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.adPortal.Exception.Error;

@RestController
@CrossOrigin(origins = { ("http://localhost:3000") })
@RequestMapping("/pay")
public class PaymentsController {

	@Autowired
	private PaymentsService service;

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@PostMapping("/savePayment/{userId}/{infoId}")
	public ResponseEntity<?> paymentsReceivesFromAdvertiser(@RequestBody PaymentsData data,
			@PathVariable("userId") Long userId, @PathVariable("infoId") Long infoId) {
		System.out.println("Check Point 1 :::: ");
		try {
			System.out.println("Check Point 2 :::: ");
			if (userId == null) {
				System.out.println("Check Point 3 :::: ");
				throw new PaymentsControllerException(HttpStatus.BAD_REQUEST.value(), "User ID is required");
			}

			if (infoId == null) {
				System.out.println("Check Point 4 :::: ");
				throw new PaymentsControllerException(HttpStatus.BAD_REQUEST.value(), "Info ID is required");
			}
			System.out.println("Check Point 5 :::: ");

			PaymentsData paymentsReceives = service.paymentsReceivesFromAdvertiser(data, userId, infoId);
			return ResponseEntity.ok().body(paymentsReceives);
		} catch (PaymentsControllerException e) {
			return ResponseEntity.status(e.getStatusCode()).body(new Error(e.getStatusCode(), e.getMessage(),
					"Unable to save payments data", System.currentTimeMillis()));
		}
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@PostMapping("/savePaymentToViewer/{userId}")
	public ResponseEntity<?> paymentsDoneToViewer(@RequestBody PaymentsData data, @PathVariable("userId") Long userId) {
		System.out.println("Check Point 1 :::: ");
		try {
			System.out.println("Check Point 2 :::: ");
			if (userId == null) {
				System.out.println("Check Point 3 :::: ");
				throw new PaymentsControllerException(HttpStatus.BAD_REQUEST.value(), "User ID is required");
			}
			System.out.println("Check Point 5 :::: ");

			PaymentsData paymentsReceives = service.paymentsDoneToViewer(data, userId);
			return ResponseEntity.ok().body(paymentsReceives);
		} catch (PaymentsControllerException e) {
			return ResponseEntity.status(e.getStatusCode()).body(new Error(e.getStatusCode(), e.getMessage(),
					"Unable to save payments data", System.currentTimeMillis()));
		}
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@GetMapping("/all")
	public ResponseEntity<?> getAllPayments() {
		try {
			List<PaymentsData> paymentsDataList = service.getAllPayments();
			return ResponseEntity.ok(paymentsDataList);
		} catch (PaymentsControllerException e) {

			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to load data", System.currentTimeMillis()));
		}
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@GetMapping("/{id}")
	public ResponseEntity<?> getPaymentById(@PathVariable long id) {
		try {
			Optional<PaymentsData> paymentData = service.getPaymentById(id);
			return ResponseEntity.ok(paymentData);
		} catch (PaymentsControllerException e) {
			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to load data", System.currentTimeMillis()));
		}
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@GetMapping("/user/{userId}")
	public ResponseEntity<?> getPaymentsByUserId(@PathVariable long userId) {
		try {
			List<PaymentsData> paymentsDataList = service.getPaymentsByUserId(userId);
			return ResponseEntity.ok(paymentsDataList);
		} catch (PaymentsControllerException e) {
			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to load data", System.currentTimeMillis()));
		}
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@GetMapping("/user/{userId}/info/{infoId}")
	public ResponseEntity<?> getPaymentsByUserIdAndInfoId(@PathVariable long userId, @PathVariable long infoId) {
		try {
			List<PaymentsData> paymentsDataList = service.getPaymentsByUserIdAndInfoId(userId, infoId);
			return ResponseEntity.ok(paymentsDataList);
		} catch (PaymentsControllerException e) {
			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to load data", System.currentTimeMillis()));
		}
	}
}