package servlets;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import data.CinemateException;
import data.DataStorage;
import data.StringConstants;
import data.User;

@WebServlet("/FileChooserServlet")
public class FileChooserServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//try to parse file, if a CinemateException is caught, we know there was an error
		try{
			DataStorage ds = new DataStorage(request.getParameter(StringConstants.INFILE));
			request.getSession().setAttribute(StringConstants.DATA, ds);
		}
		catch(CinemateException e){
			response.getWriter().write(e.getMessage());
//			response.getWriter().flush();
		}
	}
}
 