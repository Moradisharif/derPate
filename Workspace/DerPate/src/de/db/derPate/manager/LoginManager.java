package de.db.derPate.manager;

import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import de.db.derPate.Constants;
import de.db.derPate.model.LoginUser;

/**
 * This class is used to read and write login data (see {@link LoginUser}) to
 * the client's {@link HttpSession}.<br>
 * Pattern: Singleton
 *
 * @author MichelBlank
 * @see LoginUser
 */
public class LoginManager {
	private static LoginManager instance = null;
	private final String userKey;

	/**
	 * Default constructor used for initializing attributes.
	 */
	private LoginManager() {
		this.userKey = "user";
	}

	/**
	 * Returns instance of {@link LoginManager}
	 *
	 * @return {@link LoginManager}
	 */
	@NonNull
	public static LoginManager getInstance() {
		if (instance == null) {
			instance = new LoginManager();
		}
		return instance;
	}

	/**
	 * Returns {@link LoginUser} that is connected with this session. May return
	 * <b>null</b>, if user was not logged in.
	 *
	 * @param session Session of which the {@link LoginUser} should be read from
	 * @return {@link LoginUser} or <code>null</code>, if given {@link HttpSession}
	 *         was null or user was not properly logged in
	 */
	@Nullable
	public LoginUser getUserBySession(@Nullable HttpSession session) {
		LoginUser user = null;
		if (session != null) {
			try {
				user = (LoginUser) session.getAttribute(this.userKey);
			} catch (IllegalStateException | ClassCastException e) {
				LoggingManager.log(Level.INFO, "Could not get user due to an session error");
			}
		}
		return user;
	}

	/**
	 * Connects {@link HttpSession} with {@link LoginUser}
	 *
	 * @param request {@link HttpServletRequest}
	 * @param user    {@link LoginUser}
	 * @return <code>true</code>, if successful; <code>false</code> if an error
	 *         occurred or given {@link HttpServletRequest} was null
	 */
	public boolean login(@Nullable HttpServletRequest request, @NonNull LoginUser user) {
		boolean success = false;

		if (request != null) {
			try {
				HttpSession session = this.newSession(request); // creates new session to prevent session hijacking

				user.removeSecret(); // remove password from user to prevent unwanted use
				session.setAttribute(this.userKey, user);
				session.setMaxInactiveInterval(Constants.Login.MAX_INACTIVE_SECONDS);

				success = true;
			} catch (IllegalStateException e) {
				LoggingManager.log(Level.INFO, "Could not login user due to an session error");
			}
		}
		return success;
	}

	/**
	 * Returns current login status
	 *
	 * @param session {@link HttpSession}
	 * @return <code>true</code>, if user is logged in; <code>false</code>, if user
	 *         is not or given {@link HttpSession} was null
	 */
	public boolean isLoggedIn(@Nullable HttpSession session) {
		boolean success = false;
		if (session != null) {
			try {
				success = (session.getAttribute(this.userKey) != null); // user is logged in, when session contains an
																		// user
																		// object
			} catch (IllegalStateException e) {
				LoggingManager.log(Level.INFO, "Could not read login status out of session");
			}
		}
		return success;
	}

	/**
	 * Removes connection between {@link HttpSession} and {@link LoginUser}
	 *
	 * @param session HttpSession
	 * @return <code>true</code>, if successful; <code>false</code>, if an error
	 *         occurred or given {@link HttpSession} was null
	 */
	public boolean logout(@Nullable HttpSession session) {
		boolean success = false;
		if (session != null) {
			try {
				session.removeAttribute(this.userKey); // remove user
				session.invalidate(); // destroy session
				success = true;
			} catch (IllegalStateException e) {
				LoggingManager.log(Level.INFO, "Could not logout user due to an session error");
			}
		}
		return success;
	}

	/**
	 * Destroys old session and creates a new one to prevent session hijacking.<br>
	 * Should be called before every login and after every logout.<br>
	 * Caution: All session attributed will be gone after this method was executed!
	 *
	 * @param request {@link HttpServletRequest}
	 * @return new {@link HttpSession} or <code>null</code>, if given
	 *         {@link HttpServletRequest} was null
	 */
	@Nullable
	private HttpSession newSession(@Nullable HttpServletRequest request) {
		HttpSession newSession = null;
		if (request != null) {
			HttpSession session = request.getSession(true); // true means, that a new session should be initiated if
															// none was existent
			session.invalidate(); // destroy old session
			newSession = request.getSession(true); // create new session to prevent session hijacking
		}
		return newSession;
	}
}
