package servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import data.DataStorage;
import data.StringConstants;

@WebServlet("/SearchServlet")
public class SearchServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public SearchServlet() {
        super();
    }

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//string that describes which search we are doing
		String search = (String)request.getSession().getAttribute(StringConstants.SEARCH);
		//search parameter provided to us by the user
		String searchParam = request.getParameter(StringConstants.SEARCH_PARAM);

		DataStorage ds = (DataStorage) request.getSession().getAttribute(StringConstants.DATA);
		//boolean that stores whether this is a user or movie search
		Boolean userSearch = false;
		//execute the search base on the search type given
		switch(search){
		
		case StringConstants.GENRE:
			request.setAttribute(StringConstants.RESULTS, ds.searchByGenre(searchParam));
			break;
		case StringConstants.TITLE:
			request.setAttribute(StringConstants.RESULTS, ds.searchByTitle(searchParam));
			break;
		case StringConstants.ACTOR:
			request.setAttribute(StringConstants.RESULTS, ds.searchByActor(searchParam));
			break;
		case "user":
			userSearch = true;
			request.setAttribute(StringConstants.RESULTS, ds.searchForUser(searchParam));
			break;
		}
		//set an attribute to tell us on the front end whether the search was for users or movies
		request.setAttribute(StringConstants.IS_USER_SEARCH, userSearch);
		request.getRequestDispatcher(StringConstants.JSP_EXT+StringConstants.SEARCH_JSP).forward(request, response);
	}
}
