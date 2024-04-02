//package com.adPortal.Exception;
//
//import java.util.Date;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//
//@ControllerAdvice
//public class GlobalExceptionHandler  {
//
//	@ExceptionHandler(GlobalException.class)
//	public Error handleGlobalException(GlobalException e) {
//		Error error = new Error();
//		error.setDate(new Date());
//		if(e instanceof GlobalException) {
//		GlobalException globalException = (GlobalException) e;
//		System.out.println("Getting code"+globalException.getErrorCode());
//		error.setErrorCode(globalException.getErrorCode());
//		System.out.println("Getting developer msg"+globalException.getDevelopermsg());
//		error.setDevelopermsg(globalException.getDevelopermsg());
//		System.out.println("Getting exception msg"+globalException.getMessage());
//		error.setMsg(globalException.getMessage());
//		return error;
//		}else {
//			error.setErrorCode(500);
//			error.setDevelopermsg("Unknown exception :: " + e.getMessage());
//			error.setMsg("unknown error");
//			System.out.println("check point 1");
//			return error;
//		}
//		
//	}
//
////	@ExceptionHandler(Exception.class)
////	public ResponseEntity<Error> handleOtherExceptions(Exception e, WebRequest request) {
////		Error error = new Error();
////		error.setDate(new Date());
////		error.setErrorCode(500);
////		error.setDevelopermsg("Unknown exception :: " + e.getMessage());
////		error.setMsg("unknown error");
////		HttpStatus httpStatus = getHttpStatus(500);
////		return new ResponseEntity<>(error, httpStatus);
////	}
//
//	private HttpStatus getHttpStatus(int errorCode) {
//		switch (errorCode) {
//		case 200:
//			return HttpStatus.OK;
//		case 201:
//			return HttpStatus.CREATED;
//		case 400:
//			return HttpStatus.BAD_REQUEST;
//		case 406:
//			return HttpStatus.NOT_ACCEPTABLE;
//		default:
//			return HttpStatus.INTERNAL_SERVER_ERROR;
//		}
//	}
//}
