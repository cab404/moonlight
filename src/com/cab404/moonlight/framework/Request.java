package com.cab404.moonlight.framework;

import com.cab404.moonlight.facility.ResponseFactory;
import com.cab404.moonlight.util.RU;
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
		return cancelled;
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

	protected void fetch(AccessProfile profile) {
		HttpRequestBase request = getRequest(profile);
		HttpResponse response;

		try {
			do {
				response = RU.exec(request, profile, false);
				// Putting after all time consuming operations.
				if (cancelled) return;

				onResponseGain(response);

				if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_MOVED_PERM) {
					onRedirect(response.getFirstHeader("Location").getValue());
					request.setURI(URI.create(response.getFirstHeader("Location").getValue()));
				} else break;

			} while (true);

		} catch (Throwable e) {
			throw new RequestFail(e);
		}

		prepare(profile);
		try {
			ResponseFactory.read(response, this);
		} catch (Throwable e) {
			throw new LoadingFail(e);
		}


	}

}
