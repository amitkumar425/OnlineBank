package com.cg.onlinebankapp.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.login.AccountNotFoundException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cg.moneymoneybankapp.account.service.MonyMonyServiceLayer;
import com.cg.moneymoneybankapp.factory.MMBankFactory;

/**
 * Servlet implementation class BankController
 */
@WebServlet("*.obs")
public class BankController extends HttpServlet {

	private static int flag =0; 
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		MonyMonyServiceLayer serviceLayer = new MonyMonyServiceLayer();
		MMBankFactory bankFactory = new MMBankFactory();
		
		HttpSession session = request.getSession();
		if(flag==0) {
			session.setAttribute("loginStatus", 0);
			session.setAttribute("createdAccount", 0);
			session.setAttribute("accountFound", 0);
			session.setAttribute("accountList", 0);
			session.setAttribute("accountToBeUpdated", 0);
			session.setAttribute("denomination", 0);
			session.setAttribute("denominationStatus", 0);
			session.setAttribute("deposit", 0);
			session.setAttribute("fundTransfer", 0);
			session.setAttribute("searchedAccount", 0);
			session.setAttribute("depositStatus",0);
			session.setAttribute("fundTransferStatus", 0);
			flag++;
		}
		String action = request.getServletPath();
		
		System.out.println(action);
		
		switch (action) {
			
		case "/depositIntoAccount.obs":
			int accountNumber = Integer.parseInt(request.getParameter("accountNumber"));
			double amountToBeDeposited = Double.parseDouble(request.getParameter("amountToBeDeposited"));
			session.setAttribute("deposit", serviceLayer.depositAmount(accountNumber,amountToBeDeposited));
			session.setAttribute("depositStatus", 1);
 			response.sendRedirect("DepositForm.jsp");
			break;
		
		case "/addNewAccount.obs":
			
			Map<String, Object> accountDetails = new HashMap<>(); 
			
			String accountHolderName = request.getParameter("customerName");
			accountDetails.put("accountHolderName", accountHolderName);
			
			String dateOfBirth = request.getParameter("dateOfBirth");
			DateTimeFormatter formater = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate date = LocalDate.parse(dateOfBirth, formater);
			accountDetails.put("dateOfBirth", date);
			
			String contactNumber = request.getParameter("contactNumber");
			accountDetails.put("contactNumber", contactNumber);
			
			String nationality = request.getParameter("nationality");
			accountDetails.put("nationality", nationality);
			
			String gender = request.getParameter("gender");
			accountDetails.put("gender", gender);
			
			String emailID = request.getParameter("emailID");
			accountDetails.put("emailID", emailID);
			
			String houseNo = request.getParameter("houseNo");
			accountDetails.put("houseNo", houseNo);
			
			String street = request.getParameter("street");
			accountDetails.put("street", street);
			
			String city = request.getParameter("city");
			accountDetails.put("city", city);
			
			String state = request.getParameter("state");
			accountDetails.put("state", state);
			
			String pinCode = request.getParameter("pinCode");
			accountDetails.put("pinCode", pinCode);
			
			String accountType = request.getParameter("accountType");
			accountDetails.put("accountType", accountType);
			
			String accountBalance;
			if(accountType.equals("savingAccount")) {
				String salary = request.getParameter("isSalaried");
				
				if(salary.equals("salaried")) {
					
					accountBalance = request.getParameter("savSalbalance");
					accountDetails.put("accountBalance",accountBalance);
					accountDetails.put("salary", true);
					
				}
				else {
					accountBalance = request.getParameter("savNotbalance");
					accountDetails.put("accountBalance",accountBalance);
					accountDetails.put("salary", false);
				}
				session.setAttribute("createdAccount", serviceLayer.createNewSavingsAccount(bankFactory.createNewSavingsAccount(accountDetails)));
			}
			else {
				accountBalance = request.getParameter("curbalance");
				accountDetails.put("accountBalance",accountBalance);
				
				String odLimit = request.getParameter("odLimit");
				accountDetails.put("odLimit", odLimit);
				session.setAttribute("createdAccount", serviceLayer.createNewCurrentAccount(bankFactory.createNewCurrentAccount(accountDetails)));
			}
			response.sendRedirect("Success.jsp");
			break;
			
		case "/withdraw.obs" :
			accountNumber = Integer.parseInt(request.getParameter("accountNumber"));
			double amountToBeWithdrawn = Double.parseDouble(request.getParameter("amountToBeWithdrawn"));
			session.setAttribute("denomination", serviceLayer.withdrawWithDenomination(accountNumber,amountToBeWithdrawn));
			System.out.println(session.getAttribute("denomination"));
			response.sendRedirect("DenominationDisplay.jsp");
			break;
		
		case "/searchAccount.obs":
			int accountNumberToBeSearched = Integer.parseInt(request.getParameter("accountNumber"));
			session.setAttribute("accountFound", serviceLayer.getAccountByAccountNumber(accountNumberToBeSearched));
			response.sendRedirect("ViewAccount.jsp");
			break;
			
		case "/ViewAllCustomer.obs":
			session.setAttribute("accountList",serviceLayer.getAllAccounts());
			response.sendRedirect("ViewAllCustomers.jsp");
			break;
			
		case "/fundTransfer.obs":
			int receipientAccountNumber = Integer.parseInt(request.getParameter("toAccountNumber"));
			int donerAccountNumber = Integer.parseInt(request.getParameter("fromAccountNumber"));
			double amountToBeTransffered = Double.parseDouble(request.getParameter("amountToTransfer"));
			double amountTransferred = serviceLayer.performFundTransfer(receipientAccountNumber,donerAccountNumber,amountToBeTransffered);
			if(amountTransferred!=-1) {
				session.setAttribute("fundTransferStatus", 1);
				session.setAttribute("receipientAccountNumber", receipientAccountNumber);
				session.setAttribute("donerAccountNumber", donerAccountNumber);
				session.setAttribute("fundTransfer", amountTransferred);
				
			}else {
				session.setAttribute("fundTransferStatus", -1);
				
			}
			response.sendRedirect("FundTransfer.jsp");
			break;
		
		case "/update.obs":
			int accountIdToBeUpdated = Integer.parseInt(request.getParameter("accountNumber"));
			session.setAttribute("accountToBeUpdated", serviceLayer.getAccountByAccountNumber(accountIdToBeUpdated));
			response.sendRedirect("UpdateCustomerForm.jsp");
			break;
			
		case "/writeUpdate.obs":
			Map<String,Object> updatedMap = new HashMap<>();
			
			accountNumber = Integer.parseInt(request.getParameter("accountNumer"));
			updatedMap.put("customerName", request.getParameter("customerName"));
			updatedMap.put("emailId", request.getParameter("email"));
			updatedMap.put("contactNumber", request.getParameter("phoneNumber"));
			
			dateOfBirth = request.getParameter("dob");
			formater = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			date = LocalDate.parse(dateOfBirth, formater);
			updatedMap.put("dateOfBirth", date);
			
			serviceLayer.updateCustomer(accountNumber,updatedMap);
			response.sendRedirect("ViewAllCustomers.jsp");
			break;
			
		default:
			break;
		}
		
		
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
