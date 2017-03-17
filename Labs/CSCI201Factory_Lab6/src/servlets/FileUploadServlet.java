//package servlets;
//
//import java.io.IOException;
//import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.File;
//import java.util.List;
//import org.apache.commons.fileupload.FileItem;
//import org.apache.commons.fileupload.disk.DiskFileItemFactory;
//import org.apache.commons.fileupload.servlet.ServletFileUpload;
//
///**
// * Servlet implementation class FileUploadServlet
// */
//@WebServlet("/FileUploadServlet")
//public class FileUploadServlet extends HttpServlet {
//	private static final long serialVersionUID = 1L;
//	
//	private final String UPLOAD_DIRECTORY = "C:/uploads";
//       
//    /**
//     * @see HttpServlet#HttpServlet()
//     */
//    public FileUploadServlet() {
//        super();
//        // TODO Auto-generated constructor stub
//    }
//
//	/**
//	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
//	 */
//	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		// TODO Auto-generated method stub
//		response.getWriter().append("Served at: ").append(request.getContextPath());
//	}
//
//	/**
//	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
//	 */
//	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//      //process only if its multipart content
//      if(ServletFileUpload.isMultipartContent(request)){
//          try {
//              List<FileItem> multiparts = new ServletFileUpload(
//                                       new DiskFileItemFactory()).parseRequest(request);
//            
//              for(FileItem item : multiparts){
//                  if(!item.isFormField()){
//                      String name = new File(item.getName()).getName();
//                      item.write( new File(UPLOAD_DIRECTORY + File.separator + name));
//                  }
//              }
//         
//             //File uploaded successfully
//             request.setAttribute("message", "File Uploaded Successfully");
//          } catch (Exception ex) {
//             request.setAttribute("message", "File Upload Failed due to " + ex);
//          }          
//       
//      }else{
//          request.setAttribute("message",
//                               "Sorry this Servlet only handles file upload request");
//      }
//  
//      request.getRequestDispatcher("/result.jsp").forward(request, response);
//	}
//
//}
