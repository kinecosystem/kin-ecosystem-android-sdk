package com.kin.ecosystem.core.data.auth;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.kin.ecosystem.common.Callback;
import com.kin.ecosystem.core.network.ApiCallback;
import com.kin.ecosystem.core.network.ApiException;
import com.kin.ecosystem.core.network.api.AuthApi;
import com.kin.ecosystem.core.network.model.AccountInfo;
import com.kin.ecosystem.core.network.model.JWT;
import com.kin.ecosystem.core.network.model.UserProfile;
import com.kin.ecosystem.core.network.model.UserProperties;
import com.kin.ecosystem.core.util.ExecutorsUtil;
import java.util.List;
import java.util.Map;

public class AuthRemoteData implements AuthDataSource.Remote {

	/**
	 * This is new api client to be different from oder apis without access token interceptor.
	 */
	private static volatile AuthRemoteData instance;

	private final AuthApi authApi;
	private final ExecutorsUtil executorsUtil;

	private AuthRemoteData(@NonNull ExecutorsUtil executorsUtil) {
		this.authApi = new AuthApi();
		this.executorsUtil = executorsUtil;
	}


	public static AuthRemoteData getInstance(@NonNull ExecutorsUtil executorsUtil) {
		if (instance == null) {
			synchronized (AuthRemoteData.class) {
				if (instance == null) {
					instance = new AuthRemoteData(executorsUtil);
				}
			}
		}
		return instance;
	}

	@Override
	public void getAccountInfo(@NonNull JWT jwt, @NonNull final Callback<AccountInfo, ApiException> callback) {
		try {
			authApi.signInAsync(jwt, "", new ApiCallback<AccountInfo>() {
				@Override
				public void onFailure(final ApiException e, int statusCode, Map<String, List<String>> responseHeaders) {
					executorsUtil.mainThread().execute(new Runnable() {
						@Override
						public void run() {
							callback.onFailure(e);
						}
					});
				}

				@Override
				public void onSuccess(final AccountInfo result, int statusCode, Map<String, List<String>> responseHeaders) {
					executorsUtil.mainThread().execute(new Runnable() {
						@Override
						public void run() {
							callback.onResponse(result);
						}
					});
				}
			});
		} catch (final ApiException e) {
			executorsUtil.mainThread().execute(new Runnable() {
				@Override
				public void run() {
					callback.onFailure(e);
				}
			});
		}
	}

	@Override
	@Nullable
	public AccountInfo getAccountInfoSync(@NonNull JWT jwt) {
		try {
			return authApi.signIn(jwt, "");
		} catch (ApiException e) {
			return null;
		}
	}

	@Override
	public void hasAccount(@NonNull String userId, @NonNull final Callback<Boolean, ApiException> callback) {
		try {
			authApi.hasAccountAsync(userId, "", new ApiCallback<Boolean>() {
				@Override
				public void onFailure(final ApiException e, int statusCode, Map<String, List<String>> responseHeaders) {
					executorsUtil.mainThread().execute(new Runnable() {
						@Override
						public void run() {
							callback.onFailure(e);
						}
					});
				}

				@Override
				public void onSuccess(final Boolean result, int statusCode, Map<String, List<String>> responseHeaders) {
					executorsUtil.mainThread().execute(new Runnable() {
						@Override
						public void run() {
							callback.onResponse(result);
						}
					});
				}
			});
		} catch (final ApiException e) {
			executorsUtil.mainThread().execute(new Runnable() {
				@Override
				public void run() {
					callback.onFailure(e);
				}
			});
		}
	}

	@Override
	public void userProfile(@NonNull final Callback<UserProfile, ApiException> callback) {
		try {
			authApi.userProfileAsync("", new ApiCallback<UserProfile>() {
				@Override
				public void onFailure(final ApiException e, int statusCode, Map<String, List<String>> responseHeaders) {
					executorsUtil.mainThread().execute(new Runnable() {
						@Override
						public void run() {
							callback.onFailure(e);
						}
					});
				}

				@Override
				public void onSuccess(final UserProfile result, int statusCode, Map<String, List<String>> responseHeaders) {
					executorsUtil.mainThread().execute(new Runnable() {
						@Override
						public void run() {
							callback.onResponse(result);
						}
					});

				}

			});
		} catch (final ApiException e){
			executorsUtil.mainThread().execute(new Runnable() {
				@Override
				public void run() {
					callback.onFailure(e);
				}
			});
		}
	}


	@Override
	public void updateWalletAddress(@NonNull UserProperties userProperties, @NonNull final Callback<Void, ApiException> callback) {
		try {
			authApi.updateUserAsync(userProperties, new ApiCallback<Void>() {
				@Override
				public void onFailure(final ApiException e, int statusCode, Map<String, List<String>> responseHeaders) {
					executorsUtil.mainThread().execute(new Runnable() {
						@Override
						public void run() {
							callback.onFailure(e);
						}
					});
				}

				@Override
				public void onSuccess(final Void result, int statusCode, Map<String, List<String>> responseHeaders) {
					executorsUtil.mainThread().execute(new Runnable() {
						@Override
						public void run() {
							callback.onResponse(result);
						}
					});
				}
			});
		} catch (final ApiException e) {
			executorsUtil.mainThread().execute(new Runnable() {
				@Override
				public void run() {
					callback.onFailure(e);
				}
			});
		}
	}

	@Override
	public void logout(@Nullable final Callback<Void, ApiException> callback) {
		try {
			authApi.logoutAsync(new ApiCallback<Void>() {
				@Override
				public void onFailure(final ApiException e, int statusCode, Map<String, List<String>> responseHeaders) {
					if(callback != null) {
						executorsUtil.mainThread().execute(new Runnable() {
							@Override
							public void run() {
								callback.onFailure(e);
							}
						});
					}
				}

				@Override
				public void onSuccess(final Void result, int statusCode, Map<String, List<String>> responseHeaders) {
					if(callback != null) {
						executorsUtil.mainThread().execute(new Runnable() {
							@Override
							public void run() {
								callback.onResponse(result);
							}
						});
					}
				}
			});
		} catch (final ApiException e) {
			if(callback != null) {
				executorsUtil.mainThread().execute(new Runnable() {
					@Override
					public void run() {
						callback.onFailure(e);
					}
				});
			}
		}
	}
}
