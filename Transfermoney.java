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
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/Transfermoney")
public class Transfermoney extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

    String url = "jdbc:mysql://localhost:3306/javaproject";
    String username = "root";
    String password = "Suppi123";
    Connection con = null;
    PreparedStatement pstmt = null;
    PreparedStatement pstmt1 = null;
    PreparedStatement pstmt2 = null;
    PreparedStatement pstmt3 = null;
    PreparedStatement pstmt4 = null;
    PreparedStatement pstmt5 = null;

    @Override
    public void init() throws ServletException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver loaded successfully");
            con = DriverManager.getConnection(url, username, password);
            System.out.println("Connection between Java and SQL established");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter writer = resp.getWriter();
        
        // Retrieve and validate input parameters
        String fromAccountNoStr = req.getParameter("fa");
        String toAccountNoStr = req.getParameter("ta");
        String amountStr = req.getParameter("amo");

        if (fromAccountNoStr == null || toAccountNoStr == null || amountStr == null || 
            fromAccountNoStr.isEmpty() || toAccountNoStr.isEmpty() || amountStr.isEmpty()) {
            writer.println("<h1 style='color:red display: flex; justify-content: center; align-items: center; height: 100vh;'>Invalid input parameters. Please provide valid 'fa', 'ta', and 'amo'.</h1>");
            return;
        }

        int fromAccountNo = 0;
        int toAccountNo = 0;
        int amount = 0;

        try {
            fromAccountNo = Integer.parseInt(fromAccountNoStr);
            toAccountNo = Integer.parseInt(toAccountNoStr);
            amount = Integer.parseInt(amountStr);
        } catch (NumberFormatException e) {
            writer.println("<h1 style='color:red; display: flex; justify-content: center; align-items: center; height: 100vh;'>Invalid input: Please enter valid numbers for 'fa', 'ta', and 'amo'.</h1>");
            return;
        }

        try {
            con.setAutoCommit(false); // Begin transaction

            // Check if fromAccountNo exists and has sufficient balance
            String queryFromAccount = "SELECT balance FROM createaccount WHERE accountno = ?";
            pstmt = con.prepareStatement(queryFromAccount);
            pstmt.setInt(1, fromAccountNo);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int fromBalance = rs.getInt(1);

                if (fromBalance < amount) {
                    writer.println("<h1 style='color:red; display: flex; justify-content: center; align-items: center; height: 100vh;'>Insufficient Balance in the From Account</h1>");
                    return;
                }
            } else {
                writer.println("<h1 style='color:red; display: flex; justify-content: center; align-items: center; height: 100vh;'>From Account Not Found</h1>");
                return;
            }

            // Check if toAccountNo exists
            String queryToAccount = "SELECT accountno FROM createaccount WHERE accountno = ?";
            pstmt1 = con.prepareStatement(queryToAccount);
            pstmt1.setInt(1, toAccountNo);
            ResultSet rs1 = pstmt1.executeQuery();

            if (!rs1.next()) {
                writer.println("<h1 style='color:red; display: flex; justify-content: center; align-items: center; height: 100vh;'>To Account Not Found</h1>");
                return;
            }

            // Update the balance of fromAccountNo (deduct amount)
            String queryUpdateFrom = "UPDATE createaccount SET balance = balance - ? WHERE accountno = ?";
            pstmt4 = con.prepareStatement(queryUpdateFrom);
            pstmt4.setInt(1, amount);
            pstmt4.setInt(2, fromAccountNo);
            pstmt4.executeUpdate();

            // Update the balance of toAccountNo (add amount)
            String queryUpdateTo = "UPDATE createaccount SET balance = balance + ? WHERE accountno = ?";
            pstmt5 = con.prepareStatement(queryUpdateTo);
            pstmt5.setInt(1, amount);
            pstmt5.setInt(2, toAccountNo);
            pstmt5.executeUpdate();

            // Log the transfer in the transfermoney table
            String queryLogTransfer = "INSERT INTO transfermoney(fromaccountno, toaccountno, amount) VALUES (?, ?, ?)";
            pstmt3 = con.prepareStatement(queryLogTransfer);
            pstmt3.setInt(1, fromAccountNo);
            pstmt3.setInt(2, toAccountNo);
            pstmt3.setInt(3, amount);
            pstmt3.executeUpdate();

            con.commit(); // Commit the transaction

            writer.println("<h1 style='color:blue; display: flex; justify-content: center; align-items: center; height: 100vh;'>Transfer Successful</h1>");

        } catch (SQLException e) {
            try {
                con.rollback(); // Rollback the transaction in case of an error
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            writer.println("<h1 style='color:red; display: flex; justify-content: center; align-items: center; height: 100vh;'>Transfer Failed. Please Try Again</h1>");
        } finally {
            try {
                // Close all resources
                if (pstmt != null) pstmt.close();
                if (pstmt1 != null) pstmt1.close();
                if (pstmt2 != null) pstmt2.close();
                if (pstmt3 != null) pstmt3.close();
                if (pstmt4 != null) pstmt4.close();
                if (pstmt5 != null) pstmt5.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void destroy() {
        try {
            if (con != null) con.close(); // Close connection when servlet is destroyed
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


