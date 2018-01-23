package com.cab404.moonlight.framework;

import com.cab404.moonlight.facility.ResponseFactory;
import com.cab404.moonlight.util.exceptions.LoadingFail;
import com.cab404.moonlight.util.exceptions.RequestFail;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;

import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.CharBuffer;

/**
 * Base class for the requests.
 *
 * @author cab404
 */
public abstract class Request implements ResponseFactory.Parser {

	private boolean cancelled = false;

	public void cancel() {
		cancelled = true;
	}

	public boolean isCancelled() {
		return cancelled || Thread.interrupted();
	}

	protected abstract HttpRequestBase getRequest(AccessProfile accessProfile);

	/**
	 * Preparations before receiving data into {@link com.cab404.moonlight.facility.ResponseFactory.Parser#part(java.nio.CharBuffer)}
	 */
	protected void prepare(AccessProfile accessProfile) {}
	/**
	 * It will receive data until we out of it or false is returned.
	 *
	 * @param part
	 */
	@Override public boolean part(CharBuffer part) {return false;}
	/**
	 * Invoked after data loading finished.
	 */
	@Override public abstract void finished();

	/**
	 * Do not process response entity in this method!
	 * ...or throw error at least, so we won't activate parsers on empty stream.
	 */
	protected void onResponseGain(HttpResponse response) {}

	/**
	 * Invoked on redirect response. Redirection will be processed automatically.
	 */
	protected void onRedirect(String to) {

	}

	public void fetch(AccessProfile profile) {
		HttpRequestBase request = getRequest(profile);
		HttpResponse response;

		try {
			do {
				// Putting after all time consuming operations and possible overrides.
				if (isCancelled()) return;
				response = profile.exec(request, false);
				if (isCancelled()) return;

				onResponseGain(response);

				if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_MOVED_PERM) {
					onRedirect(response.getFirstHeader("Location").getValue());
					if (isCancelled()) return;
					URI uri = URI.create(response.getFirstHeader("Location").getValue());
					if(uri.getScheme() == null) {
						uri = new URI(profile.getHost().getSchemeName(),
								uri.getHost(),
								uri.getPath(),
								uri.getFragment());
					}
					request.setURI(uri);
				} else break;

			} while (true);

		} catch (Throwable e) {
			throw new RequestFail(e);
		}

		if (isCancelled()) return;

		prepare(profile);

		if (isCancelled()) return;

		try {
			ResponseFactory.read(response, this);
		} catch (Throwable e) {
			throw new LoadingFail(e);
		}


	}

}
