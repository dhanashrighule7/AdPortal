package com.adPortal.history;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class HistoryService {

	@Autowired
	private HistoryRepository historyRepository;

	public List<History> getAllHistory() {
		return historyRepository.findAll();
	}

	public void render(History history) {
		System.out.println(history.toString());
	}

	public History getHistoryById(long id) {
		return historyRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "History not found"));
	}

	public List<History> getHistory(History history) {
		return historyRepository.findAll();

	}

	public History createHistory(History history) {
		return historyRepository.save(history);
	}

	public History updateHistory(long id, History historyDetails) {
		History history = historyRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "History not found"));

		history.setAdName(historyDetails.getAdName());
		history.setTransactionId(historyDetails.getTransactionId());
		history.setStatus(historyDetails.getStatus());
		history.setTotalToken(historyDetails.getTotalToken());
		history.setUserId(historyDetails.getUserId());

		return historyRepository.save(history);
	}

	public void deleteHistory(long id) {
		History history = historyRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "History not found"));
		historyRepository.delete(history);
	}

	public List<AdvertiserDTO> getHistoriesByUserIdConvertedToDTO(long userId) {
		List<History> histories = historyRepository.findAllByUserId(userId);

		return histories.stream().map(this::convertToAdvertiserDTO).collect(Collectors.toList());
	}

	private AdvertiserDTO convertToAdvertiserDTO(History history) {
		AdvertiserDTO dto = new AdvertiserDTO();
		dto.setId(history.getId());
		dto.setAdName(history.getAdName());
		dto.setAdsDuration(history.getAdsDuration());
		dto.setFeesPaid(history.getFeesPaid());
		dto.setCreatedOn(history.getCreatedOn());
		dto.setTransactionId(history.getTransactionId());
		dto.setStatus(history.getStatus());
		dto.setUserId(history.getUserId());
		System.out.println("ads name " + history.getAdName());
		return dto;
	}

	public List<ViewerDTO> getHistoriesByUserNameConvertedToDTO(long userId) {
		List<History> histories = historyRepository.findAllByUserId(userId);
		return histories.stream().map(this::convertToViewerDTO).collect(Collectors.toList());
	}

	private ViewerDTO convertToViewerDTO(History history) {
		ViewerDTO dto = new ViewerDTO();
		dto.setId(history.getId());
		dto.setTotalToken(history.getTotalToken());
		dto.setTransactionId(history.getTransactionId());
		dto.setStatus(history.getStatus());
		dto.setCreatedOn(history.getCreatedOn());
		dto.setAmount(history.getAmount());
		return dto;
	}
}
