package com.adPortal;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RoundRobinAds {
	private List<String> ads;
	private int currentAdIndex;

	public RoundRobinAds(List<String> ads) {
		this.ads = ads;
		Collections.shuffle(this.ads);
		this.currentAdIndex = 0;
	}

	public String getNextAd() {
		String ad = ads.get(currentAdIndex);
		currentAdIndex = (currentAdIndex + 1) % ads.size();
		return ad;
	}

	public static void main(String[] args) {
		List<String> adList = Arrays.asList("Ad 1", "Ad 2", "Ad 3", "Ad 4", "Ad 5"

		);

		RoundRobinAds roundRobinAds = new RoundRobinAds(adList);

		for (int i = 0; i < 10; i++) {
			System.out.println(roundRobinAds.getNextAd());

		}
	}
}