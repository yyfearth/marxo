package test;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.WriteResult;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class Servlet extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ServletOutputStream servletOutputStream = response.getOutputStream();
		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(servletOutputStream);
		outputStreamWriter.write("doPost");

		System.out.println("doPost");
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		String addParameter = request.getParameter("add");
		String cleanParameter = request.getParameter("clean");

		try {
			ServletOutputStream servletOutputStream = response.getOutputStream();
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(servletOutputStream);
			outputStreamWriter.write("doGet");

			MongoConnector mongoConnector = MongoConnector.getConnectedConnector();

			DBCollection dbCollection = mongoConnector.getDb().getCollection("test");

			if (addParameter != null) {
				long numCollections = dbCollection.count();
				WriteResult writeResult = dbCollection.insert(new BasicDBObject("Count", numCollections + 1));
				System.out.println("writeResult.getError() = " + writeResult.getError());
			} else if (cleanParameter != null) {
				dbCollection.drop();
			}

			response.sendRedirect("/");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}