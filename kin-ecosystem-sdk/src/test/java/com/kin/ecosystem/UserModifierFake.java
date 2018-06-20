package com.kin.ecosystem;

import com.kin.ecosystem.bi.EventsStore.DynamicValue;
import com.kin.ecosystem.bi.EventsStore.UserModifier;
import com.kin.ecosystem.bi.events.UserProxy;

public class UserModifierFake implements UserModifier {

	private String digitalServiceUserId;
	private double balance;
	private int earnCount;
	private double totalKinSpent;
	private String digitalServiceId;
	private int transactionCount;
	private String entryPointParam;
	private int spendCount;
	private double totalKinEarned;

	public UserModifierFake() {
		this.digitalServiceUserId = "test_digital_service_user_id";
		this.balance = 20;
		this.earnCount = 2;
		this.totalKinSpent = 0;
		this.digitalServiceId = "test_digital_service_id";
		this.transactionCount = 0;
		this.entryPointParam = "test_entry_point";
		this.spendCount = 0;
		this.totalKinEarned = 20;
	}

	@Override
	public void modify(UserProxy mutable) {
		mutable.setDigitalServiceUserId(new DynamicValue<String>() {
			@Override
			public String get() {
				return digitalServiceUserId;
			}
		});
		mutable.setBalance(new DynamicValue<Double>() {
			@Override
			public Double get() {
				return balance;
			}
		});
		mutable.setEarnCount(new DynamicValue<Integer>() {
			@Override
			public Integer get() {
				return earnCount;
			}
		});
		mutable.setTotalKinSpent(new DynamicValue<Double>() {
			@Override
			public Double get() {
				return totalKinSpent;
			}
		});
		mutable.setDigitalServiceId(new DynamicValue<String>() {
			@Override
			public String get() {
				return digitalServiceId;
			}
		});
		mutable.setTransactionCount(new DynamicValue<Integer>() {
			@Override
			public Integer get() {
				return transactionCount;
			}
		});
		mutable.setEntryPointParam(new DynamicValue<String>() {
			@Override
			public String get() {
				return entryPointParam;
			}
		});
		mutable.setSpendCount(new DynamicValue<Integer>() {
			@Override
			public Integer get() {
				return spendCount;
			}
		});
		mutable.setTotalKinEarned(new DynamicValue<Double>() {
			@Override
			public Double get() {
				return totalKinEarned;
			}
		});
	}

	public void setDigitalServiceUserId(String digitalServiceUserId) {
		this.digitalServiceUserId = digitalServiceUserId;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public void setEarnCount(int earnCount) {
		this.earnCount = earnCount;
	}

	public void setTotalKinSpent(double totalKinSpent) {
		this.totalKinSpent = totalKinSpent;
	}

	public void setDigitalServiceId(String digitalServiceId) {
		this.digitalServiceId = digitalServiceId;
	}

	public void setTransactionCount(int transactionCount) {
		this.transactionCount = transactionCount;
	}

	public void setEntryPointParam(String entryPointParam) {
		this.entryPointParam = entryPointParam;
	}

	public void setSpendCount(int spendCount) {
		this.spendCount = spendCount;
	}

	public void setTotalKinEarned(double totalKinEarned) {
		this.totalKinEarned = totalKinEarned;
	}
}
