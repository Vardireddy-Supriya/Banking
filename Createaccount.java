package com.banking;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet("/Createaccount")
public class Createaccount extends HttpServlet {
	private static final long serialVersionUID = 1L;
	String url ="jdbc:mysql://localhost:3306/javaproject";
	String un = "root";
	String pn ="Suppi123";
	Connection con = null;
	PreparedStatement pstmt = null;
	String query = "insert into createaccount values(?,?,?,?);";
	
	@Override
	public void init() throws ServletException {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			System.out.println("Driver loaded successfully");
			con = DriverManager.getConnection(url, un, pn);
			System.out.println("Connection established between Java and SQL");
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// Validate inputs
		String accountNoStr = req.getParameter("acc");
		String passwordStr = req.getParameter("pw");
		String name = req.getParameter("na");
		String balanceStr = req.getParameter("ib");

		PrintWriter writer = resp.getWriter();
		resp.setContentType("text/html");

		// Check if any parameter is null or empty
		if (accountNoStr == null || passwordStr == null || name == null || balanceStr == null ||
			accountNoStr.isEmpty() || passwordStr.isEmpty() || name.isEmpty() || balanceStr.isEmpty()) {
			writer.println("<h1 style='color:red;display: flex; justify-content: center; align-items: center; height: 100vh;'>Error: All fields are required!</h1>");
			return;
		}

		try {
			// Parse input to integers
			int accountNo = Integer.parseInt(accountNoStr);
			int password = Integer.parseInt(passwordStr);
			int balance = Integer.parseInt(balanceStr);

			// Output the inputs to console
			System.out.println(accountNo);
			System.out.println(password);
			System.out.println(name);
			System.out.println(balance);

			// Prepare SQL statement
			pstmt = con.prepareStatement(query);
			pstmt.setInt(1, accountNo);
			pstmt.setInt(2, password);
			pstmt.setString(3, name);
			pstmt.setInt(4, balance);

			// Execute update
			int res = pstmt.executeUpdate();

			// Check if the insert was successful
			if (res > 0) {
				writer.println("<h1 style='color:blue; display: flex; justify-content: center; align-items: center; height: 100vh;'>Account Created Successfully.</h1>");
			} else {
				writer.println("<h1 style='color:red;display: flex; justify-content: center; align-items: center; height: 100vh;'>Error: Could not create account!</h1>");
			}
		} catch (NumberFormatException e) {
			writer.println("<h1 style='color:red;display: flex; justify-content: center; align-items: center; height: 100vh;'>Error: Invalid input for account number, password, or balance.</h1>");
			e.printStackTrace();
		} catch (SQLException e) {
			writer.println("<h1 style='color:red;display: flex; justify-content: center; align-items: center; height: 100vh;'>Database error occurred!</h1>");
			e.printStackTrace();
		}
	}

	@Override
	public void destroy() {
		// Close connection when servlet is destroyed
		try {
			if (con != null) {
				con.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}


