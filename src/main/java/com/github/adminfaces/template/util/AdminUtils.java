package com.github.adminfaces.template.util;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;

public class AdminUtils {

	/**
	 * 
	 * @param url
	 * @param paramValues
	 */
	public static void redirect(String url, Object... paramValues) {
		redirect(FacesContext.getCurrentInstance(), url, paramValues);
	}

	/**
	 * Copied from OmniFaces to avoid version conflicts (see https://github.com/adminfaces/admin-template/issues/177)
	 * 
	 * @param context
	 * @param url
	 * @param paramValues
	 */
	private static void redirect(FacesContext context, String url, Object... paramValues) {
		ExternalContext externalContext = context.getExternalContext();
		externalContext.getFlash().setRedirect(true); // MyFaces also requires this for a redirect in current request (which is incorrect).
		try {
			externalContext.redirect(prepareRedirectURL(getRequest(context), url, paramValues));
		} catch (IOException e) {
			throw new RuntimeException("Could not redirect to url: "+url, e);
		}
	}

	public static HttpServletRequest getRequest(FacesContext context) {
		return (HttpServletRequest) context.getExternalContext().getRequest();
	}

	/**
	 * Copied from OmniFaces to avoid version conflicts
	 * 
	 * @param request
	 * @param url
	 * @param paramValues
	 * @return
	 */
	private static String prepareRedirectURL(HttpServletRequest request, String url, Object... paramValues) {
		String redirectURL = url;

		if (!Assert.startsWithOneOf(url, "http://", "https://", "/")) {
			redirectURL = request.getContextPath() + "/" + url;
		}

		if (!Assert.has(paramValues)) {
			return redirectURL;
		}

		Object[] encodedParams = new Object[paramValues.length];

		for (int i = 0; i < paramValues.length; i++) {
			Object paramValue = paramValues[i];
			encodedParams[i] = (paramValue instanceof String) ? encodeURL((String) paramValue) : paramValue;
		}

		return format(redirectURL, encodedParams);
	}

	/**
	 * Copied from OmniFaces to avoid version conflicts URL-encode the given string
	 * using UTF-8.
	 * 
	 * @param string The string to be URL-encoded using UTF-8.
	 * @return The given string, URL-encoded using UTF-8, or <code>null</code> if
	 *         <code>null</code> was given.
	 * @throws UnsupportedOperationException When this platform does not support
	 *                                       UTF-8.
	 */
	public static String encodeURL(String string) {
		if (string == null) {
			return null;
		}

		try {
			return URLEncoder.encode(string, UTF_8.name());
		} catch (UnsupportedEncodingException e) {
			throw new UnsupportedOperationException("UTF-8 is apparently not supported on this platform.", e);
		}
	}

}
