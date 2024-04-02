package com.adPortal.user;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.text.RandomStringGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.adPortal.Exception.Error;
import com.adPortal.ads.AdsInfoException;
import com.adPortal.ads.MediaAd;

import jakarta.transaction.Transactional;

@RestController
@CrossOrigin(origins = { ("http://localhost:3000") })
@RequestMapping("/users")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepository;

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@GetMapping("/getUserByWalletAddress/{walletAddress}")
	public ResponseEntity<?> getUserByWalletAddress(@PathVariable String walletAddress) {
		try {
			return userService.getUserByWalletAddress(walletAddress);
		} catch (AdsInfoException e) {
			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to find data", System.currentTimeMillis()));
		}
	}

	// get total tokens for admin
	@CrossOrigin(origins = { ("http://localhost:3000") })
	@GetMapping("/getTotalTokens")
	public ResponseEntity<?> getTotalTokens() {
		try {
			return userService.getTotalTokens();
		} catch (AdsInfoException e) {
			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to find data", System.currentTimeMillis()));
		}
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@PostMapping("/login")
	public ResponseEntity<?> authenticateUser(@RequestBody User user) {
		try {
			System.out.println("Check Point 0 :::: in Controller login ");
			return userService.authenticateUser(user);
		} catch (UserControllerException e) {
			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to login user", System.currentTimeMillis()));
		}
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@GetMapping("/getAll")
	public ResponseEntity<?> getUserDetails() {
		try {
			return userService.getUserDetails();
		} catch (AdsInfoException e) {
			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to find data", System.currentTimeMillis()));
		}
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@GetMapping("/getById/{id}")
	public ResponseEntity<?> getUser(@PathVariable long id) {
		try {
			return userService.getUser(id);
		} catch (AdsInfoException e) {
			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to find data", System.currentTimeMillis()));
		}
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@GetMapping("/getAdvertiser")
	public ResponseEntity<?> getAdvertiser() {
		try {
			Map<String, Object> advertiser = userService.getAdvertiser();
			return ResponseEntity.ok(advertiser);
		} catch (AdsInfoException e) {
			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to find data", System.currentTimeMillis()));
		}
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@GetMapping("/getViewer")
	public ResponseEntity<?> getViewer() {
		try {
			Map<String, Object> viewer = userService.getViewer();
			return ResponseEntity.ok(viewer);
		} catch (AdsInfoException e) {
			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to find data", System.currentTimeMillis()));
		}
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@GetMapping("/getAdmin")
	public ResponseEntity<?> getAdmin() {
		try {
			Map<String, Object> admin = userService.getAdmin();
			return ResponseEntity.ok(admin);
		} catch (AdsInfoException e) {
			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to find data", System.currentTimeMillis()));
		}
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@PostMapping("/register")
	public ResponseEntity<?> registerAdminOrManager(@RequestBody User user) {
		try {
			return userService.registerAdminOrManager(user);
		} catch (AdsInfoException e) {
			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to find data", System.currentTimeMillis()));
		}
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@PostMapping("/registerViewer/{walletAddress}")
	public ResponseEntity<?> registerViewer(@PathVariable String walletAddress) {
		try {
			return userService.registerViewer(walletAddress);
		} catch (AdsInfoException e) {
			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to find data", System.currentTimeMillis()));
		}
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@PutMapping("/update/{id}/{response}")
	public ResponseEntity<?> updateUser(@PathVariable long id, @PathVariable String response) {
		try {
			return userService.updateUser(id, response);
		} catch (AdsInfoException e) {
			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to find data", System.currentTimeMillis()));
		}
	}

//	@GetMapping("/listOfViewers")
//	public List<User> getViewersList() {
//		return userService.getViewersList();
//	}
//
//	@GetMapping("/listOfAdvertiser")
//	public List<User> getAdvertiserList() {
//		return userService.getAdvertiserList();
//	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@PutMapping("/updateViewer/{id}") //// no
	public ResponseEntity<?> updateViewerProfile(@PathVariable long id, @RequestBody User user) {
		try {
			return userService.updateViewerProfile(id, user);

		} catch (AdsInfoException e) {
			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to find data", System.currentTimeMillis()));
		}
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@PutMapping("/updateAdvertiser/{id}") //// no
	public ResponseEntity<?> updateAdvertiserProfile(@PathVariable long id, @RequestBody User user) {
		try {
			return userService.updateAdvetriserProfile(id, user);

		} catch (AdsInfoException e) {
			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to find data", System.currentTimeMillis()));
		}
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@PostMapping("/advertiserSignup")
	public ResponseEntity<?> advertiserSignup(@RequestBody SignUpDTO signUpDto) throws UserControllerException {
		System.out.println("Check Point 1 ");
		try {

			if (userRepository.existsByEmail(signUpDto.getEmail())) {
				System.out.println("Check Point 2 ");
				throw new UserControllerException(409, "Email already exist");
			}

			System.out.println("Check Point 3 ");
			if (!signUpDto.getPassword().equals(signUpDto.getConfirmPassword())) {
				System.out.println("Check Point 4 ");
				throw new UserControllerException(401, "Password and confirm password is not same");
			}

			User advertiserSignup = userService.advertiserSignup(signUpDto);
			return ResponseEntity.ok().body(advertiserSignup);
		}

		catch (UserControllerException e) {
			System.out.println("Caught UserControllerException:");
			System.out.println("Exception message: " + e.getMessage());

			return ResponseEntity.status(e.getStatusCode()).body(new Error(e.getStatusCode(), e.getMessage(),
					"Unable to register user", System.currentTimeMillis()));
		}

		catch (Exception e) {
			System.out.println("Caught exception details:");
			System.out.println("Exception message: " + e.getMessage());
			throw new UserControllerException(401, "Unable to register user");
		}

	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@PostMapping("/sendOtp")
	@Transactional
	public ResponseEntity<?> sendEmail(@RequestParam String email) {
		try {
			User user = userRepository.findByEmail(email);
			if (user == null) {
				throw new UserControllerException(409, "User does not exist");
			}
			System.err.println("User Found:::::::");
			System.out.println(user.toString());
			RandomStringGenerator generator = new RandomStringGenerator.Builder().withinRange('0', '9').build();
			String generate = generator.generate(6);
			String subject = "Welcome to AdPortal";
			String msg = "Dear " + user.getUsername() + ",\nYour OTP is: " + generate;

			user.setOtp(generate);
			System.out.println("OTP::" + generate);
			userService.sendMail(msg, subject, email);
			userRepository.save(user);
			return ResponseEntity.ok("OTP sent successfully....!!!!");
		} catch (UserControllerException e) {
			System.out.println("Caught UserControllerException:");
			System.out.println("Exception message: " + e.getMessage());

			return ResponseEntity.status(e.getStatusCode()).body(new Error(e.getStatusCode(), e.getMessage(),
					"Error occurred while sending otp", System.currentTimeMillis()));
		} catch (Exception e) {

			System.out.println("Caught exception details:");
			System.out.println("Exception message: " + e.getMessage());
			throw new UserControllerException(401, "Unable to send OTP ");
		}
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@PostMapping("/verifyOtp")
	public ResponseEntity<?> toVerify(@RequestBody User user) {
		try {
			String email = user.getEmail();
			String otp = user.getOtp();
			User dataFromDb = userRepository.findByEmail(email);
			if (dataFromDb != null) {
				String otpFromDb = dataFromDb.getOtp();
				if (otpFromDb != null && otpFromDb.equals(otp)) {
					return ResponseEntity.ok("OTP verified successfully....!!!");
				} else {
					throw new UserControllerException(409, "Invalid OTP");
				}
			} else {
				throw new UserControllerException(409, "User not found");
			}
		} catch (UserControllerException e) {
			System.out.println("Caught UserControllerException:");
			System.out.println("Exception message: " + e.getMessage());
			return ResponseEntity.status(e.getStatusCode())
					.body(new Error(e.getStatusCode(), e.getMessage(), "Invalid otp", System.currentTimeMillis()));
		} catch (Exception e) {
			System.out.println("Caught exception details:");
			System.out.println("Exception message: " + e.getMessage());
			throw new UserControllerException(401, "Unable to verify otp ");
		}
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@PutMapping("/forgotpassword")
	public ResponseEntity<?> forgotPassword(@RequestBody User user) {
		try {
			String email = user.getEmail();
			String password = user.getPassword();
			User user1 = userRepository.findByEmail(email);
			if (user1 != null) {
				user1.setPassword(password);
				userRepository.save(user1);
				return ResponseEntity.ok("Paasword updated successfully...!!!");
			} else {
				throw new UserControllerException(409, "User not found");
			}

		} catch (UserControllerException e) {
			System.out.println("Caught UserControllerException:");
			System.out.println("Exception message: " + e.getMessage());
			return ResponseEntity.status(e.getStatusCode())
					.body(new Error(e.getStatusCode(), e.getMessage(), "Invalid email", System.currentTimeMillis()));
		} catch (Exception e) {
			System.out.println("Caught exception details:");
			System.out.println("Exception message: " + e.getMessage());
			throw new UserControllerException(401, "Unable to forgot password ");
		}

	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@PutMapping("/updateProfilePic/{id}")
	public ResponseEntity<?> updateUserProfile(@PathVariable long id, @RequestParam MultipartFile media)
			throws IOException {
		System.out.println("Check Point 11 :::: ");
		Optional<User> byId = userRepository.findById(id);
		System.out.println("Check Point 12 :::: User Found >> " + byId.toString());
		User user = byId.get();
		if (user == null) {
			System.out.println("Check Point 13 :::: User Null ");
			return ResponseEntity.badRequest().body("User not found");
		}
		try {
			System.out.println("Check Point 14 :::: ");
			userService.updateUserProfile(id, media);
			System.out.println("Check Point 15 :::: Update query fired ");
			return ResponseEntity.ok("Profile updated successfully...!!!");
		} catch (UserControllerException e) {
			System.out.println("Caught UserControllerException:");
			System.out.println("Exception message: " + e.getMessage());

			return ResponseEntity.status(e.getStatusCode()).body(new Error(e.getStatusCode(), e.getMessage(),
					"Unable ro read file format", System.currentTimeMillis()));

		} catch (Exception e) {
			System.out.println("Caught UserControllerException:");
			System.out.println("Exception message: " + e.getMessage());

			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("please upload profile pic");
		}
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@PutMapping("/updateEmail/{id}/{email}")
	public ResponseEntity<?> updateUserEmail(@PathVariable long id, @PathVariable String email) {
		try {
			Optional<User> byId = userRepository.findById(id);
			User user = byId.get();
			if (user != null) {
				user.setEmail(email);
				userRepository.save(user);
				return ResponseEntity.ok(user);
			} else {
				throw new UserControllerException(409, "User not found");
			}
		} catch (UserControllerException e) {
			System.out.println("Caught UserControllerException:");
			System.out.println("Exception message: " + e.getMessage());

			return ResponseEntity.status(e.getStatusCode()).body(new Error(e.getStatusCode(), e.getMessage(),
					"Unable to find details", System.currentTimeMillis()));
		}
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@GetMapping("/getTotalWatchedAds/{id}")
	public ResponseEntity<?> getTotalWatchedAds(@PathVariable long id) {
		try {
			return userService.getTotalWatchedAds(id);
		} catch (AdsInfoException e) {
			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to find data", System.currentTimeMillis()));
		}
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@GetMapping("/getTotalClaimToken/{id}")
	public ResponseEntity<?> getTotalClaimToken(@PathVariable long id) {
		try {
			return userService.getTotalClaimToken(id);
		} catch (AdsInfoException e) {
			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to find data", System.currentTimeMillis()));
		}
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@GetMapping("/getTotalEarnedToken/{id}")
	public ResponseEntity<?> getTotalEarnedToken(@PathVariable long id) {
		try {
			return userService.getTotalEarnedToken(id);

		} catch (UserControllerException e) {
			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to find data", System.currentTimeMillis()));
		}
	}

//	@GetMapping("/image/{id}")
//	public ResponseEntity<?> getImage(@PathVariable long id) {
//		Optional<User> byId = userRepository.findById(id);
//	    byte[] imageData = byId.get().profile;
//	    if (imageData != null) {
//			
//		}
//	    // Create a ByteArrayResource from the byte[] array
//	    ByteArrayResource resource = new ByteArrayResource(imageData);
//
//	    // Set the appropriate headers
//	    HttpHeaders headers = new HttpHeaders();
//	    headers.setContentType(MediaType.IMAGE_JPEG); // Assuming the image is a JPEG
//
//	    // Return the ResponseEntity with the resource and headers
//	    return ResponseEntity.ok()
//	            .headers(headers)
//	            .body(resource);
//	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@GetMapping("/getProfileOfAdvertiser/{userId}")
	public ResponseEntity<?> getMedia(@PathVariable Long userId) {
		try {
			return userService.getMedia(userId);

		} catch (UserControllerException e) {
			return ResponseEntity.status(e.getStatusCode()).body(new Error(e.getStatusCode(), e.getMessage(),
					"An error occurred while retrieving user profile picture", System.currentTimeMillis()));
		}
	}

}
