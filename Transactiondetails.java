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

@WebServlet("/Transactiondetails")
public class Transactiondetails extends HttpServlet {
    private static final long serialVersionUID = 1L;

    String url = "jdbc:mysql://localhost:3306/javaproject";
    String username = "root";
    String password = "Suppi123";
    Connection con = null;
    PreparedStatement pstmtDeposit = null;
    PreparedStatement pstmtWithdraw = null;
    PreparedStatement pstmtTransferFrom = null;
    PreparedStatement pstmtTransferTo = null;

    @Override
    public void init() throws ServletException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(url, username, password);
            System.out.println("Connection established successfully.");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new ServletException("Error establishing database connection.", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter writer = resp.getWriter();
        resp.setContentType("text/html");

        String accountNoParam = req.getParameter("accountno");

        if (accountNoParam == null || accountNoParam.isEmpty()) {
            writer.println("<h3 style='color:red;display: flex; justify-content: center; align-items: center; height: 100vh;'>Error: Account number is missing or invalid.</h3>");
            return;  // Exit the method if no account number is provided
        }

        int accountNo = 0;
        try {
            accountNo = Integer.parseInt(accountNoParam);
        } catch (NumberFormatException e) {
            writer.println("<h3 style='color:red;display: flex; justify-content: center; align-items: center; height: 100vh;'>Error: Invalid account number format.</h3>");
            return;
        }

        try {
            // Fetch deposit transactions
            String queryDeposit = "SELECT * FROM depositmoney WHERE accountno = ?";
            pstmtDeposit = con.prepareStatement(queryDeposit);
            pstmtDeposit.setInt(1, accountNo);
            ResultSet rsDeposit = pstmtDeposit.executeQuery();

            writer.println("<h2 style='display: flex; justify-content: center; align-items: center;height:auto;'>Deposit Transactions</h2>");
            if (!rsDeposit.isBeforeFirst()) {
                writer.println("<p style='display: flex; justify-content: center; align-items: center;height:auto; '>No deposits found for account: " + accountNo + "</p>");
            } else {
                writer.println("<table border='1'style='display: flex; justify-content: center; align-items: center; height:auto;'><tr><th>depositid</th><th>Accountno</th><th>Amount</th><th>deposittime</th></tr>");
                while (rsDeposit.next()) {
                    writer.println("<tr><td>" + rsDeposit.getInt("depositid") + "</td>");
                    writer.println("<td>" + rsDeposit.getInt("accountno") + "</td>");
                    writer.println("<td>" + rsDeposit.getInt("amount") + "</td>");
                    writer.println("<td>" + rsDeposit.getDate("deposittime") + "</td></tr>");
                }
                writer.println("</table>");
            }

            // Fetch withdrawal transactions
            String queryWithdraw = "SELECT * FROM withdrawmoney WHERE accountno = ?";
            pstmtWithdraw = con.prepareStatement(queryWithdraw);
            pstmtWithdraw.setInt(1, accountNo);
            ResultSet rsWithdraw = pstmtWithdraw.executeQuery();

            writer.println("<h2 style='display: flex; justify-content: center; align-items: center; height:auto;'>Withdrawal Transactions</h2>");
            if (!rsWithdraw.isBeforeFirst()) {
                writer.println("<p style='display: flex; justify-content: center; align-items: center; height:auto;'>No withdrawals found for account: " + accountNo + "</p>");
            } else {
                writer.println("<table border='1' style='display: flex; justify-content: center; align-items: center; height: auto;'><tr><th>withdrawid</th><th>Accountno</th><th>Amount</th><th>withdrawtime</th></tr>");
                while (rsWithdraw.next()) {
                    writer.println("<tr><td>" + rsWithdraw.getInt("withdrawid") + "</td>");
                    writer.println("<td>" + rsWithdraw.getInt("accountno") + "</td>");
                    writer.println("<td>" + rsWithdraw.getInt("amount") + "</td>");
                    writer.println("<td>" + rsWithdraw.getDate("withdrawtime") + "</td></tr>");
                }
                writer.println("</table>");
            }

            // Fetch money transferred from this account
            String queryTransferFrom = "SELECT * FROM transfermoney WHERE fromaccountno = ?";
            pstmtTransferFrom = con.prepareStatement(queryTransferFrom);
            pstmtTransferFrom.setInt(1, accountNo);
            ResultSet rsTransferFrom = pstmtTransferFrom.executeQuery();

            writer.println("<h2 style='display: flex; justify-content: center; align-items: center; height:auto;'>Money Transferred From Account</h2>");
            if (!rsTransferFrom.isBeforeFirst()) {
                writer.println("<p style='display: flex; justify-content: center; align-items: center; height:auto;'>No money transfers from account: " + accountNo + "</p>");
            } else {
                writer.println("<table border='1' style='display: flex; justify-content: center; align-items: center; height: auto;'><tr><th>Transferid</th><th>Fromaccount</th><th>Toaccount</th><th>transfertime</th><th>Amount</th></tr>");
                while (rsTransferFrom.next()) {
                    writer.println("<tr><td>" + rsTransferFrom.getInt("transferid") + "</td>");
                    writer.println("<td>" + rsTransferFrom.getInt("fromaccountno") + "</td>");
                    writer.println("<td>" + rsTransferFrom.getInt("toaccountno") + "</td>");
                    writer.println("<td>" + rsTransferFrom.getDate("transfertime") + "</td>");
                    writer.println("<td>" + rsTransferFrom.getInt("amount") + "</td></tr>");
                }
                writer.println("</table>");
            }

            // Fetch money transferred to this account
            String queryTransferTo = "SELECT * FROM transfermoney WHERE toaccountno = ?";
            pstmtTransferTo = con.prepareStatement(queryTransferTo);
            pstmtTransferTo.setInt(1, accountNo);
            ResultSet rsTransferTo = pstmtTransferTo.executeQuery();

            writer.println("<h2 style='display: flex; justify-content: center; align-items: center; height:auto;'>Money Transferred To Account</h2>");
            if (!rsTransferTo.isBeforeFirst()) {
                writer.println("<p style='display: flex; justify-content: center; align-items: center; height:auto;'>No money transfers to account: " + accountNo + "</p>");
            } else {
                writer.println("<table border='1' style='display: flex; justify-content: center; align-items: center; height: auto;'><tr><th>Transferid</th><th>Fromaccount</th><th>Toaccount</th><th>transfertime</th><th>Amount</th></tr>");
                while (rsTransferTo.next()) {
                    writer.println("<tr><td>" + rsTransferTo.getInt("transferid") + "</td>");
                    writer.println("<td>" + rsTransferTo.getInt("fromaccountno") + "</td>");
                    writer.println("<td>" + rsTransferTo.getInt("toaccountno") + "</td>");
                    writer.println("<td>" + rsTransferTo.getDate("transfertime") + "</td>");
                    writer.println("<td>" + rsTransferTo.getInt("amount") + "</td></tr>");
                }
                writer.println("</table>");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            writer.println("<h3 style='display: flex; justify-content: center; align-items: center; height: 100vh;'>Error fetching transaction details. Please try again later.</h3>");
        }
    }

    @Override
    public void destroy() {
        try {
            if (con != null) con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


