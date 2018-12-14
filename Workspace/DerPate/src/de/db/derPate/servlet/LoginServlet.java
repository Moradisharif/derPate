package de.db.derPate.servlet;

import java.io.IOException;
import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.eclipse.jdt.annotation.Nullable;

import de.db.derPate.Constants;
import de.db.derPate.Constants.Ui.Inputs;
import de.db.derPate.Userform;
import de.db.derPate.manager.LoggingManager;
import de.db.derPate.manager.LoginManager;
import de.db.derPate.model.Admin;
import de.db.derPate.model.Godfather;
import de.db.derPate.model.Trainee;
import de.db.derPate.persistence.AdminDao;
import de.db.derPate.persistence.GodfatherDao;
import de.db.derPate.persistence.TraineeDao;
import de.db.derPate.util.InputVerifyUtil;

/**
 * This servlet get's called by the client, when the user tries to log on. It
 * checks the given credentials and redirects to the start page for logged in
 * users.<br>
 * Allowed http methods: <code>POST</code>
 *
 * @author MichelBlank
 *
 */
public class LoginServlet extends CSRFCheckServlet {
	private static final long serialVersionUID = 1L;
	/**
	 * Http status code used to indicate a successful login
	 */
	public static final int SC_LOGIN_SUCCESS = HttpServletResponse.SC_NO_CONTENT;
	/**
	 * Http status code used to indicate a bad login
	 */
	public static final int SC_LOGIN_ERROR = HttpServletResponse.SC_FORBIDDEN;
	/**
	 * Http status code used to indicate that not all required fields have been
	 * sent.
	 */
	public static final int SC_LOGIN_INCOMPLETE = HttpServletResponse.SC_BAD_REQUEST;

	/**
	 * Constructor
	 */
	public LoginServlet() {
		super(Userform.LOGIN);
	}

	/**
	 * This method serves the http post-method and receives the credentials provided
	 * by the client, checks them and redirects to the next page (or sends an http
	 * status code, to let the client know, that the login was not successful).
	 */
	@Override
	protected void doPost(@Nullable HttpServletRequest request, @Nullable HttpServletResponse response)
			throws IOException {
		if (request == null || response == null) {
			return;
		}
		if (!this.handleCSRFToken(request, response)) {
			return;
		}

		String email = request.getParameter(Inputs.LOGIN_EMAIL.toString());
		String password = request.getParameter(Inputs.LOGIN_PASSWORD.toString());
		String token = request.getParameter(Inputs.LOGIN_TOKEN.toString());

		if (email != null && InputVerifyUtil.isNotBlank(email) && InputVerifyUtil.isNotBlank(password)) {

			// find admin user
			Admin admin = AdminDao.getInstance().byEmail(email);
			if (admin != null) {
				// try to login as admin
				String dbPassword = admin.getPassword();
				if (Constants.Login.hashUtil.isEqual(password, dbPassword, Constants.Login.hashPepper,
						Constants.Login.hashSeperator)) {
					LoginManager.getInstance().login(request, admin);
					response.setStatus(SC_LOGIN_SUCCESS);
					return;
				}
			} else {
				// find godfather
				Godfather godfather = GodfatherDao.getInstance().byEmail(email);
				if (godfather != null) {
					// try to login as godfather
					String dbPassword = godfather.getPassword();
					if (Constants.Login.hashUtil.isEqual(password, dbPassword, Constants.Login.hashPepper,
							Constants.Login.hashSeperator)) {
						LoginManager.getInstance().login(request, godfather);
						response.setStatus(SC_LOGIN_SUCCESS);
						return;
					}
				}
			}
		} else if (InputVerifyUtil.isNotBlank(token)) {
			// find token
			Trainee trainee = TraineeDao.getInstance().byToken(token);
			if (trainee != null) {
				// no password needed
				LoginManager.getInstance().login(request, trainee);
				response.setStatus(SC_LOGIN_SUCCESS);
				return;
			}
		} else {
			// invalid login attempt
			LoggingManager.log(Level.INFO,
					"Client with IP " + request.getRemoteAddr() + " tried to login with no credentials"); //$NON-NLS-1$ //$NON-NLS-2$
			response.sendError(SC_LOGIN_INCOMPLETE);
		}

		// no valid login, but valid token. So request can be trusted again
		HttpSession session = request.getSession();
		if (session != null) {
			this.respondWithNewToken(session, response);
		}

		// send error code
		response.setStatus(SC_LOGIN_ERROR);
		return;
	}

}
