package com.adPortal.payments;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adPortal.Notifications.Notifications;
import com.adPortal.Notifications.NotificationsRepository;
import com.adPortal.ads.AdsInfo;
import com.adPortal.ads.AdsInfoRepository;
import com.adPortal.history.History;
import com.adPortal.history.HistoryRepository;
import com.adPortal.user.User;
import com.adPortal.user.UserRepository;

@Service
public class PaymentsService {

	@Autowired
	private PaymentRepository repository;

	@Autowired
	private HistoryRepository historyRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AdsInfoRepository infoRepository;

	@Autowired
	private NotificationsRepository notificationsRepository;

	public PaymentsData paymentsReceivesFromAdvertiser(PaymentsData data, long userId, long infoId) {
		User userRole = userRepository.getUserRoleById(userId);
		Optional<AdsInfo> byId = infoRepository.findById(infoId);

		if (byId.isPresent() && userRole.getRole().contentEquals("Advertiser")) {
			AdsInfo adsInfo = byId.get();
			String status = adsInfo.getStatus().toString();
			long feesPaid = adsInfo.getFeesPaid();
			History history = new History(adsInfo.getAdTitle(), data.getTransactionId(), status, userId, infoId,
					adsInfo.getAdsDuration(), Long.toString(feesPaid));
			historyRepository.save(history);
			String adType = adsInfo.getCategory();
			String notificationMessage = "Payment for " + adType + " ad(" + adsInfo.getFileName() + ") is successful.";

			List<User> adminList = userRepository.findByRole("Admin");
			String redirectKey = "checkHistory";
			for (User admin : adminList) {
				Notifications notification = new Notifications(false, notificationMessage, admin.getEmail(),
						redirectKey, System.currentTimeMillis());
				notificationsRepository.save(notification);
			}
			Notifications advertiserNotification = new Notifications(false, notificationMessage, userRole.getEmail(),
					redirectKey, System.currentTimeMillis());
			notificationsRepository.save(advertiserNotification);
		}

		PaymentsData paymentsData = new PaymentsData();
		paymentsData.setTransactionId(data.getTransactionId());
		paymentsData.setReceiver(data.getStatus());
		paymentsData.setSender(data.getSender());
		paymentsData.setStatus(data.getStatus());
		paymentsData.setValue(data.getValue());
		paymentsData.setUserId(userId);
		paymentsData.setInfoId(infoId);
		return repository.save(paymentsData);
	}

	public PaymentsData paymentsDoneToViewer(PaymentsData data, long userId) {
		User userRole = userRepository.getUserRoleById(userId);

		PaymentsData paymentsData = new PaymentsData();
		paymentsData.setTransactionId(data.getTransactionId());
		paymentsData.setReceiver(data.getStatus());
		paymentsData.setSender(data.getSender());
		paymentsData.setStatus(data.getStatus());
		paymentsData.setValue(data.getValue());
		paymentsData.setUserId(userId);
		long totalClaim = Long.parseLong(data.getValue());

		if (userRole.getRole().equals("Viewer")) {
			String status = data.getStatus();
			History history = new History(userRole.getTotalCredit(), data.getTransactionId(), status, userId,
					totalClaim);
			long totalCredit = userRole.getTotalCredit();
			long remained = totalCredit - totalClaim;
			userRole.setClaimToken(Long.toString(remained));
			String user = Long.toString(userId);
			String redirectKey = "checkPayment";
			Notifications notification = new Notifications(false, paymentsData.getValue() + " is received", user,
					redirectKey, System.currentTimeMillis());
			notificationsRepository.save(notification);
			historyRepository.save(history);
		}

		userRepository.save(userRole);
		return repository.save(paymentsData);
	}

	public List<PaymentsData> getAllPayments() {
		return repository.findAll();
	}

	public List<PaymentsData> getPaymentsByUserIdAndInfoId(long userId, long infoId) {
		return repository.getPaymentsByUserIdAndInfoId(userId, infoId);

	}

	public List<PaymentsData> getPaymentsByUserId(long userId) {
		List<PaymentsData> byUserId = repository.findByUserId(userId);
		return byUserId;
	}

	public Optional<PaymentsData> getPaymentById(long id) {
		return repository.findById(id);
	}
}
