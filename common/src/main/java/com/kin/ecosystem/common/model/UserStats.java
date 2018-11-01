package com.kin.ecosystem.common.model;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;

/**
 * Created by yohaybarski on 25/10/2018.
 */

public class UserStats {

	private int earnCount = 0;
	private int spendCount = 0;
	private String lastEarnDate = null;
	private String lastSpendDate = null;

	public int getEarnCount() {
		return earnCount;
	}

	public void setEarnCount(int earnCount) {
		this.earnCount = earnCount;
	}

	public int getSpendCount() {
		return spendCount;
	}

	public void setSpendCount(int spendCount) {
		this.spendCount = spendCount;
	}

	public String getLastEarnDate() {
		return lastEarnDate;
	}

	public void setLastEarnDate(String lastEarnDate) {
		this.lastEarnDate = lastEarnDate;
	}

	public String getLastSpendDate() {
		return lastSpendDate;
	}

	public void setLastSpendDate(String lastSpendDate) {
		this.lastSpendDate = lastSpendDate;
	}

}
