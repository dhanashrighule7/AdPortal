package com.adPortal.user;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import com.adPortal.Exception.Error;
import com.adPortal.Notifications.Notifications;
import com.adPortal.Notifications.NotificationsRepository;
import com.adPortal.ads.AdsInfoException;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private NotificationsRepository notificationRepository;

	private static Session session = null;

	public ResponseEntity<?> getUserDetails() {
		try {
			List<User> all = userRepository.findAll();
			return ResponseEntity.ok(all);
		} catch (AdsInfoException e) {
			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to find data", System.currentTimeMillis()));
		}
	}

	public ResponseEntity<?> getUser(long id) {
		Optional<User> user = userRepository.findById(id);
		if (user != null) {
			return ResponseEntity.ok(user);
		} else {
			return null;
		}
	}

	public Map<String, Object> getAdvertiser() {
		Map<String, Object> result = new HashMap<>();
		try {
			List<User> users = userRepository.findAll();
			List<User> advertiser = new ArrayList<>();
			for (User userList : users) {
				if (userList.getRole() != null && userList.getRole().equals("Advertiser")) {
					advertiser.add(userList);
				}
			}
			result.put("count", advertiser.size());
			result.put("advertisers", advertiser);
//			List<User> advertisers = users.stream()
//					.filter(user -> user.getRole() != null && user.getRole().equals("Advertiser"))
//					.collect(Collectors.toList());
//			result.put("count", advertisers.size());
//			result.put("advertisers", advertisers);
		} catch (Exception e) {
			result.put("error", e.getMessage());
		}
		return result;
	}

	public Map<String, Object> getViewer() {
		Map<String, Object> result = new HashMap<>();
		try {
			List<User> users = userRepository.findAll();
			List<User> advertisers = users.stream()
					.filter(user -> user.getRole() != null && user.getRole().equals("Viewer"))
					.collect(Collectors.toList());
			result.put("count", advertisers.size());
			result.put("Viewer", advertisers);
		} catch (Exception e) {
			result.put("error", e.getMessage());
		}
		return result;
	}

	public Map<String, Object> getAdmin() {
		Map<String, Object> result = new HashMap<>();
		try {
			List<User> users = userRepository.findAll();
			List<User> admin = users.stream().filter(user -> user.getRole() != null && user.getRole().equals("Admin"))
					.collect(Collectors.toList());
			result.put("Admin count", admin.size());
			result.put("Admin", admin);
		} catch (Exception e) {
			result.put("error", e.getMessage());
		}
		return result;
	}

	public ResponseEntity<?> registerAdminOrManager(User user) {
		System.out.println("Check Point 1 ");
		try {

			if (userRepository.existsByEmail(user.getEmail())) {
				System.out.println("Check Point 2 ");
				throw new UserControllerException(409, "Email already exist");
			}
			System.out.println("Check Point 3 ");
			user.setAction("UnBlock");
			String[] username = user.getEmail().split("@");
			user.setUsername(username[0]);
			User advertiserSignup = userRepository.save(user);
			return ResponseEntity.ok().body(advertiserSignup);
		}

		catch (UserControllerException e) {
			System.out.println("Caught UserControllerException:");
			System.out.println("Exception message: " + e.getMessage());

			return ResponseEntity.status(e.getStatusCode()).body(new Error(e.getStatusCode(), e.getMessage(),
					"Unable to register user", System.currentTimeMillis()));
		}

	}

	public ResponseEntity<?> registerViewer(String walletAddress) {

		try {
			System.out.println("Check Point 1 :::::" + walletAddress);
			List<User> existingUser = userRepository.findByWalletAddress(walletAddress);
			System.out.println("Check Point 2 ::::: " + existingUser);
			System.err.println("Check Point 3 :::::  new walletAddress >>> " + walletAddress);
//		        System.err.println("Check Point 3 ::::: saved walletAddress >> "+existingUser.getWalletAddress());

			String walletAddress2 = null;
			String role = null;
			for (User user : existingUser) {
				role = user.getRole();
				walletAddress2 = user.getWalletAddress();
			}

			if (walletAddress2 == null) {
				System.out.println("Check Point 4 :::::  if null then save >> ");
				User user = new User();
				user.setRole("Viewer");
				user.setAction("UnBlock");
				user.setWalletAddress(walletAddress);
				User newUser = userRepository.save(user);
				return ResponseEntity.ok(newUser);
			} else {
				System.out.println("Check Point 5 ::::: >> ");
				if (role.equals("Viewer")) {
					System.out.println("Check Point 6 ::::: >> ");
					return ResponseEntity.ok(existingUser);
				} else {
					System.out.println("Check Point 7 ::::: >> ");
					throw new UserControllerException(401, "User already exists");
				}
			}
		} catch (UserControllerException e) {
			return ResponseEntity.status(e.getStatusCode()).body(new Error(e.getStatusCode(), e.getMessage(),
					"Unable to find or register user", System.currentTimeMillis()));
		}
	}

	public ResponseEntity<?> updateUserProfile(long id, MultipartFile media) throws IOException {
		try {
			Optional<User> user = userRepository.findById(id);
			if (user != null) {
				byte[] mediaBytes = media.getBytes();
				User user2 = user.get();
				System.out.println("Check Point 2 ");
				String originalFilename = media.getOriginalFilename();
				if (originalFilename != null) {
					String[] split = originalFilename.split("\\.");
					System.out.println("split" + split);
					String extension = split[split.length - 1]; // Get the last part after splitting by dot
					System.out.println("extension" + extension);

					File myFile = File.createTempFile("Myfile", "." + extension);
					String absolutePath = myFile.getAbsolutePath();
				}

				user2.setProfile(mediaBytes);
				userRepository.save(user2);

				return ResponseEntity.ok(user2);
			} else {
				throw new UserControllerException(401, "User not found");
			}

		} catch (UserControllerException e) {
			return ResponseEntity.status(e.getStatusCode()).body(new Error(e.getStatusCode(), e.getMessage(),
					"Unable to find user profile", System.currentTimeMillis()));
		}

	}

	public ResponseEntity<?> updateUser(long id, String response) {
		try {
			Optional<User> user = userRepository.findById(id);

			List<User> adminList = userRepository.findByRole("Admin");
			if (user.isPresent()) {
				User save = user.get();
				save.setAction(response);
				userRepository.save(save);
				String redirectKey = "checkAdApprovalList";

				for (User newAdminList : adminList) {
					System.out.println("Check point 1::");
					if (save.getRole().equalsIgnoreCase("Advertiser")) {
						Notifications notification = new Notifications(false,
								save.getUsername() + " profile is " + response + " by " + newAdminList.getUsername(),
								save.getEmail(), redirectKey, System.currentTimeMillis());
						System.out.println("Check point 2::");

						notificationRepository.save(notification);
					} else if (save.getRole().equalsIgnoreCase("Viewer")) {
						System.out.println("Check point 3::");
						String viewerId = Long.toString(id);
						Notifications notification = new Notifications(false,
								"your profile is " + response + " by " + newAdminList.getUsername(), viewerId,
								redirectKey, System.currentTimeMillis());
						System.out.println("Check point 4::");

						notificationRepository.save(notification);
					}
				}
			}
			return ResponseEntity.ok(user);
		} catch (AdsInfoException e) {
			return ResponseEntity.status(e.getStatusCode())
					.body(new Error(e.getStatusCode(), e.getMessage(), "Unable to update", System.currentTimeMillis()));
		}

	}

//	public List<User> getViewersList() {
//		List<User> all = userRepository.findAll();
//		List<User> viewers = new ArrayList<>();
//
//		for (User user : all) {
//			if (user != null && "Viewer".equals(user.getRole())) {
//				viewers.add(user);
//			}
//		}
//		return viewers.isEmpty() ? null : viewers;
//	}
//	
//	public List<User> getAdvertiserList() {
//		List<User> all = userRepository.findAll();
//		List<User> viewers = new ArrayList<>();
//
//		for (User user : all) {
//			if (user != null && "Advertiser".equals(user.getRole())) {
//				viewers.add(user);
//			}
//		}
//		return viewers.isEmpty() ? null : viewers;
//	}

	public ResponseEntity<?> updateViewerProfile(long id, User newUser) {
		Optional<User> user = userRepository.findById(id);
		if (user != null && user.get().getRole().equals("Viewer")) {
			User save = user.get();
			System.out.println("Check Point 1 :::: into if block ");
			save.setId(save.getId()); // id
			save.setEmail(save.getEmail()); // email
			save.setUsername(save.getUsername()); // userName
			save.setCreatedOn(save.getCreatedOn()); // createdOn
			save.setPassword(save.getPassword()); // password
			save.setClaimToken(save.getClaimToken()); // claims
			save.setProfile(save.getProfile()); // profile
			save.setRole(save.getRole()); // role
			save.setTotalAdRan(newUser.getTotalAdRan()); // TotalAdRan
			save.setTotalCredit(save.getTotalCredit()); // TotalCredit
			save.setTypesOfAds(newUser.getTypesOfAds()); // TypesOfAds
			save.setWalletAddress(save.getWalletAddress()); // WalletAddress
			save.setTotalEarnedToken(save.getTotalEarnedToken()); // TotalClaim
			save.setClaimFrequency(save.getClaimFrequency());
			userRepository.save(save);
			System.out.println("Check Point 5 :::: object saved ");
			return ResponseEntity.ok(user);
		} else {
			return ResponseEntity.ok(user);
		}
	}

	public ResponseEntity<?> updateAdvetriserProfile(long id, User newUser) {
		Optional<User> user = userRepository.findById(id);
		if (user != null && user.get().getRole().equals("Advertiser")) {
			User save = user.get();
			System.out.println("Check Point 1 :::: into if block ");
			save.setId(save.getId()); // id
			save.setEmail(save.getEmail()); // email
			save.setUsername(save.getUsername()); // userName
			save.setCreatedOn(save.getCreatedOn()); // createdOn
			save.setPassword(save.getPassword()); // password
			save.setClaimToken(save.getClaimToken());
			save.setProfile(save.getProfile()); // profile
			save.setRole(save.getRole()); // role
			save.setTotalAdRan(save.getTotalAdRan()); // TotalAdRan
			save.setTotalCredit(newUser.getTotalCredit()); // TotalCredit
			save.setTypesOfAds(save.getTypesOfAds()); // TypesOfAds
			save.setWalletAddress(save.getWalletAddress()); // WalletAddress
			save.setTotalEarnedToken(save.getTotalEarnedToken());
			save.setClaimFrequency(save.getClaimFrequency());
			userRepository.save(save);
			System.out.println("Check Point 5 :::: object saved ");
			return ResponseEntity.ok(user);
		} else {
			return ResponseEntity.ok(user);
		}
	}

	public User advertiserSignup(SignUpDTO signUpDto) throws UserControllerException {

		try {
			System.out.println("Check Point 5 ");

			User user = new User();
			user.setEmail(signUpDto.getEmail());
			user.setPassword(signUpDto.getConfirmPassword());
			user.setRole("Advertiser");
			user.setAction("UnBlock");
//			String username = signUpDto.getEmail().substring(signUpDto.getEmail().lastIndexOf("@"));
			String[] username = signUpDto.getEmail().split("@");
			user.setUsername(username[0]);
			User newUser = userRepository.save(user);
			return newUser;
		} catch (Exception e) {
			e.printStackTrace();
			throw new UserControllerException(401, "Unable to register user");
		}
	}

	public void sendMail(String msg, String otp, String email2) {
		try {
			System.out.println(email2);
			User findByEmail = userRepository.findByEmail(email2);
			System.out.println("User:: " + findByEmail);
			String email = findByEmail.getEmail();
			String otp2 = findByEmail.getOtp();
			System.out.println("Email:: " + email);

			String username = findByEmail.getUsername();

			Session mailSession = getMailSession();
			MimeMessage message = new MimeMessage(mailSession);

			message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
			message.setSubject("OTP for Password Reset");

			String content = "<!DOCTYPE html>\r\n" + "<html>\r\n" + "<body>\r\n" + "    <div>\r\n" + "        <p>Dear "
					+ username + " " + ",</p>\r\n" + "        <p>Your OTP for password reset is: " + otp2 + "</p>\r\n"
					+ "        <p>Please use this OTP to reset your password.</p>\r\n" + "        <p>Thank you!</p>\r\n"
					+ "    </div>\r\n" + "</body>\r\n" + "</html>";

			message.setContent(content, "text/html");

			Transport.send(message);
			System.out.println("OTP sent successfully to " + email);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Session getMailSession() {
		Properties properties = System.getProperties();
		System.out.println("Properties :" + properties);
		properties.put("mail.smtp.host", "smtp.gmail.com");
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.socketFactory.port", "465");
		properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		properties.put("mail.smtp.socketFactory.fallback", "false");
		session = Session.getInstance(properties, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("dhanashrighule7@gmail.com", "smlt geic ckxu eedf");
			}
		});
		session.setDebug(true);
		return session;
	}

	public ResponseEntity<?> getUserByWalletAddress(String walletAddress) {
		try {
			System.out.println("Wallet Address0 :: " + walletAddress);
			User userByWalletAddress2 = userRepository.getUserByWalletAddress(walletAddress);
			if (!userByWalletAddress2.getWalletAddress().equals(walletAddress)) {
				throw new UserControllerException(409, "User wallet address is not found");
			} else {
				System.out.println("Wallet Address1 :: " + walletAddress);
				User userByWalletAddress = userRepository.getUserByWalletAddress(walletAddress);
				return ResponseEntity.ok(userByWalletAddress);
			}
		} catch (UserControllerException e) {
			return ResponseEntity.status(e.getStatusCode()).body(new Error(e.getStatusCode(), e.getMessage(),
					"Unable to find user , please add valid address", System.currentTimeMillis()));
		}
	}

	public ResponseEntity<?> getTotalTokens() {
		List<User> allList = userRepository.findAll();
		long totalTokenss = 0;
		for (User newList : allList) {
			String totalEarnedToken = newList.getTotalEarnedToken();
			if (totalEarnedToken != null) {
				totalTokenss += Long.parseLong(totalEarnedToken);
			}
		}
		return ResponseEntity.ok(totalTokenss);
	}

	public ResponseEntity<?> authenticateUser(User user) {
		try {
			System.out.println("Check Point 1 :::: in login ");
			User byEmail = userRepository.findByEmail(user.getEmail());
			if (byEmail == null) {
				throw new UserControllerException(409, "User profile not found");
			}
			System.err.println("Check Point 2 :::: user " + byEmail.toString());

			String password = user.getPassword();
			System.out.println("Check Point 3 :::: password " + password);

			String password2 = byEmail.getPassword();
			System.out.println("Check Point 4 :::: password2 " + password2);

			if (user != null && password.equals(password2) && byEmail.getAction().equalsIgnoreCase("UnBlock")) {
				System.out.println("check point 5");
				return ResponseEntity.ok(byEmail);
			} else {
				if (byEmail.getAction().equalsIgnoreCase("Block")) {
					throw new UserControllerException(401, "User profile is Blocked");
				}
				throw new UserControllerException(409, "Invalid email and password");
			}
		} catch (UserControllerException e) {
			System.out.println("Caught UserControllerException:");
			System.out.println("Exception message: " + e.getMessage());

			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to login user", System.currentTimeMillis()));
		} catch (Exception ex) {
			throw new UserControllerException(409, "Invalid Credentials ");
		}
	}

	public ResponseEntity<?> getTotalWatchedAds(@PathVariable long id) {
		try {
			User user = userRepository.findById(id).orElse(null);
			if (user != null) {
				return ResponseEntity.ok(user.getTotalAdRan());
			} else {
				throw new UserControllerException(400, "User profile not found");
			}
		} catch (UserControllerException e) {
			System.out.println("Caught UserControllerException:");
			System.out.println("Exception message: " + e.getMessage());

			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to find details", System.currentTimeMillis()));
		}
	}

	public ResponseEntity<?> getTotalClaimToken(@PathVariable long id) {
		try {
			User user = userRepository.findById(id).orElse(null);
			if (user != null) {
				return ResponseEntity.ok(user.getClaimToken());
			} else {
				throw new UserControllerException(400, "User profile not found");
			}
		} catch (UserControllerException e) {
			System.out.println("Caught UserControllerException:");
			System.out.println("Exception message: " + e.getMessage());

			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to find details", System.currentTimeMillis()));
		}
	}

	public ResponseEntity<?> getTotalEarnedToken(long id) {
		try {
			User user = userRepository.findById(id).orElse(null);
			if (user != null) {
				return ResponseEntity.ok(user.getClaimToken());
			} else {
				throw new UserControllerException(400, "User profile not found");
			}
		} catch (UserControllerException e) {
			System.out.println("Caught UserControllerException:");
			System.out.println("Exception message: " + e.getMessage());

			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to find details", System.currentTimeMillis()));
		}
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
					throw new UserControllerException(404, "User profile not found");
				}
			} else {
				throw new UserControllerException(400, "User not found");
			}
		} catch (UserControllerException e) {
			System.out.println("Caught UserControllerException:");
			System.out.println("Exception message: " + e.getMessage());

			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to find details", System.currentTimeMillis()));
		}
	}

}
