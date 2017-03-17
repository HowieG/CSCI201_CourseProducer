package servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import data.DataStorage;
import data.StringConstants;

/**
 * Servlet implementation class ChangeFollowServlet
 */
@WebServlet("/ChangeFollowServlet")
public class ChangeFollowServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String username = request.getParameter(StringConstants.USERNAME);
		Boolean toFollow = Boolean.parseBoolean(request.getParameter(StringConstants.TO_FOLLOW));
		
		DataStorage ds = (DataStorage) request.getSession().getAttribute(StringConstants.DATA);
		//if the logged in user wants to follow this username
		if (toFollow) { ds.addFollowing(username); }
		//else the logged in user wants to unfollow this username
		else { ds.removeFollowing(username); }
	}
}
