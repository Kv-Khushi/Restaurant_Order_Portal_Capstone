package com.users.constant;

/**
 * A utility class that holds constant messages and values used throughout the application.
 * <p>
 * This class is not intended to be instantiated.
 * </p>
 */
public final class ConstantMessage {

 /**
  * The sender email address used in the system.
  */
 public static final String SENDER = "vyaskhushi2407@gmail.com";

 /**
  * Private constructor to prevent instantiation.
  */
 private ConstantMessage() {
  // Prevent instantiation
 }

 /**
  * The message to be used when a user is not found.
  */
 public static final String NOT_FOUND = "User not found";

 /**
  * The message to be used when an email already exists in the system.
  */
 public static final String ALREADY_EXISTS = "Email already exists";

 /**
  * The default wallet amount used in the system.
  */
 public static final Double  WALLET_AMOUNT = 1000.0;


 /**
  * The message to be used when the user successfully added.
  */
 public static final String USER_ADD_SUCCESS = "User added successfully";


 /**
  * The message to be used when an email is successfully sent.
  */
 public static final String MAIL_SENT_SUCCESSFULLY = "Email sent successfully";


 /**
  * The message to be used when an address is successfully added.
  */
 public static final String ADDRESS_ADD_SUCCESS = "Address added successfully";


 public static final String ADDRESS_ALREADY_EXISTS = "Address already exists";

}

