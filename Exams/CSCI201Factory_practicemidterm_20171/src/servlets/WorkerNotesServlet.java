package servlets;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import client.Factory;
import client.FactoryProduct;
import client.FactoryWorker;
import server.WebSocketEndpoint;

/**
 * Servlet implementation class WorkerNotesServlet
 */
@WebServlet("/WorkerNotesServlet")
public class WorkerNotesServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String workerIndex = request.getParameter("workerIndex");
		String socketSessionId = request.getParameter("socketSessionId");
		System.out.println(socketSessionId);
		
		Factory factory = WebSocketEndpoint.getFactory(socketSessionId);
		FactoryWorker worker = factory.getWorker(Integer.parseInt(workerIndex));
		
		String workerName = worker.name;
		int productsSize = factory.getProducts().size();
		String[] productsNames = new String[productsSize], 
				productsStarted = new String[productsSize], 
				productsCompleted = new String[productsSize];
		int completedTotal = 0;
		
		for (int i = 0; i < productsSize; i+= 1) {
			FactoryProduct product = factory.getProducts().get(i);
			productsNames[i] = product.name;
			
			// set productsStarted
			if (worker.getProductToMake() != null && worker.getProductToMake().name.equals(product.name)) {
				productsStarted[i] = "1";
			} else {
				productsStarted[i] = "0";
			}
			
			// set productsCompleted
			if (worker.getProductsCompleted().containsKey(product.name)) {
				Integer productCompleted = worker.getProductsCompleted().get(product.name);
				productsCompleted[i] = productCompleted.toString();
				completedTotal += productCompleted.intValue();
			} else {
				productsCompleted[i] = "0";
			}
		}

		// set attributes
		request.setAttribute("workerName", workerName);
		request.setAttribute("productsNames", productsNames);
		request.setAttribute("productsStarted", productsStarted);
		request.setAttribute("productsCompleted", productsCompleted);
		request.setAttribute("completedTotal", new Integer(completedTotal).toString());
		
		// generate jsp results
		RequestDispatcher rd = request.getRequestDispatcher("jsp/WorkerNotes.jsp");
		rd.forward(request, response);
		
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
