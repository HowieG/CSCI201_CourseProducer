package servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import data.DataStorage;
import data.StringConstants;
import data.User;

/**
 * Servlet implementation class SignUpServlet
 */
@WebServlet("/SignUpServlet")
public class SignUpServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		DataStorage ds = (DataStorage) request.getSession().getAttribute(StringConstants.DATA);
		String fullname = request.getParameter(StringConstants.FULL_NAME);
		String password = request.getParameter(StringConstants.PASSWORD);
		String username = request.getParameter(StringConstants.USERNAME);
		String url = request.getParameter(StringConstants.IMAGE_URL);
		//check to make sure all the fields were filled out
		if (fullname.isEmpty() || fullname == null){
			response.getWriter().write("no name provided");
		}
		else if (password.isEmpty() || password == null){
			response.getWriter().write("no password provided");
		}
		else if (username.isEmpty() || username == null){
			response.getWriter().write("no username provided");
		}
		else if (url.isEmpty() || url == null){
			response.getWriter().write("no image url provided");
		}
		else if (ds.validUsername(username)){
			response.getWriter().write("username has already been chosen");
		}
		else{
			//parse the first and last name
			String [] name = fullname.split("\\s+");
			//error check that we are given only a first and last name
			if (name.length != 2){
				response.getWriter().write("no last name provided");
			}
			else{
				//create a user object
				User user = new User();
				user.setImage(url);
				user.setPassword(password);
				user.setUsername(username);
				user.setFName(name[0]);
				user.setLName(name[1]);
				//add the user to the xml file
				ds.addUser(user);
				//set the loggedin user to be the new user
				ds.setLoggedInUser(user.getUsername());
			}
		}
	}
}
