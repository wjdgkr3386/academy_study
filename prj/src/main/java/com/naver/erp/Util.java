package com.naver.erp;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.multipart.MultipartFile;

public class Util {

	//mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm
	public static void pt( Object data){
		System.out.println("------------------------------");
		System.out.println(data);
		System.out.println("------------------------------");
	}
	
	
	//mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm
	// 검색 화면에서 필요한 [페이징 처리 관련 데이터]를 HashMap 객체에 저장해 리턴하는 메소드
	//mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm
	public static Map<String,Integer>   getPagingMap(
		int selectPageNo       // 선택한 페이지 번호
		, int rowCntPerPage    // 페이지 당 보여줄 검색 행의 개수
		, int totCnt           // 검색 결과물 개수 
	){
		Map<String,Integer> map = new HashMap<String,Integer>();
		try {
			//-----------------------------------------------------
			int pageNoCntPerPage = 10;
			if( rowCntPerPage<=0 )     { rowCntPerPage = 10; }
			if( selectPageNo<=0 )      { selectPageNo = 1; }
			//-----------------------------------------------------
			if( totCnt==0 ) {
				//-----------
				map.put("selectPageNo", selectPageNo);
				map.put("rowCntPerPage", rowCntPerPage);
				map.put("totCnt", totCnt);
				map.put("pageNoCntPerPage", pageNoCntPerPage);
				//-----------
				map.put("last_pageNo", 0);
				//-----------
				map.put("begin_rowNo", 0);
				map.put("end_rowNo", 0);
				//-----------
				map.put("serialNo_asc", 0);
				map.put("serialNo_desc", 0 );
				//-----------
				map.put("begin_pageNo", 0);
				map.put("end_pageNo", 0);
				//-----------
				return map;
			}
			//-----------------------------------------------------
			int last_pageNo = totCnt/rowCntPerPage;
					// 아래 처럼도 가능
					// int last_pageNo = totCnt/rowCntPerPage + (totCnt%rowCntPerPage==0?0:1);
					// int last_pageNo = (int)(Math.ceil(totCnt*1.0/rowCntPerPage));
				if( totCnt%rowCntPerPage>0 ) { last_pageNo++; }
				if( last_pageNo<selectPageNo ) { selectPageNo = last_pageNo; }
			//-----------------------------------------------------
			int end_rowNo = selectPageNo * rowCntPerPage;
			int begin_rowNo = end_rowNo-rowCntPerPage+1;
				if( end_rowNo>totCnt ) { end_rowNo = totCnt; }
			//-----------------------------------------------------
			int begin_pageNo = (int)Math.floor(  (selectPageNo-1)/pageNoCntPerPage  )*pageNoCntPerPage + 1;
			int end_pageNo = begin_pageNo + pageNoCntPerPage - 1;
				if( end_pageNo>last_pageNo ) {
					end_pageNo = last_pageNo;
				}
			//-----------------------------------------------------
			map.put("selectPageNo", selectPageNo);
			map.put("rowCntPerPage", rowCntPerPage);
			map.put("totCnt", totCnt);
			map.put("pageNoCntPerPage", pageNoCntPerPage);
			//--------------
			map.put("last_pageNo", last_pageNo);
			//--------------
			map.put("begin_rowNo", begin_rowNo);   
			map.put("end_rowNo" , end_rowNo);       
			//-----------
			map.put("begin_serialNo_asc", begin_rowNo);
			map.put("begin_serialNo_desc", totCnt - begin_rowNo + 1 );
			//-----------
			map.put("begin_pageNo", begin_pageNo);
			map.put("end_pageNo", end_pageNo);
			//-----------------------------------------------------
			return map;
		}catch(Exception ex) {
			return new HashMap<String,Integer>();
		}
	}
	

	//mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm
	// <파일업로드> 
	// MultipartFile 객체가 관리하는 업로드 파일에 
	// 주어질 새 이름을 리턴하는 메소드
	//  <주의> 새이름에는 확장자도 포함함.
	//mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm
	public static String getNewFileName(
			MultipartFile multi
	) throws Exception {
		//---------------------------------------------
		// MultipartFile 객체가 null 이거나 비어 있으면, 즉 업로드된 파일이 없으면 null 리턴하기
		//---------------------------------------------
		if( multi==null || multi.isEmpty() ) { return null; }
		//---------------------------------------------
		// 업로드한 파일의 원래 파일명 얻기. 파일명에는 확장자가 포함한다.
		// 업로드한 파일의  파일확장자 얻기
		// 고유한 새파일명 리턴하기.
		//---------------------------------------------
		String oriFileName = multi.getOriginalFilename();
		System.out.println("파일 이름:"+oriFileName);
		String file_extension = oriFileName.substring(   oriFileName.lastIndexOf(".")+1      );
		
		return UUID.randomUUID() + "." + file_extension;
	}

	//mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm
	// 게시판에서 이미지 파일 업로드할 폴더명 리턴하기
	//mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm
	public static String uploadDirForBoard( ){
		return System.getProperty("user.dir") +"\\src\\main\\resources\\static\\img\\";
	}

	//mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm
	// <파일업로드> 업로드 파일의 크기, 확장자  체크하는 메소드 선언
	//mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm
	public static int checkUploadFile(
			MultipartFile multi     // 업로드되는 파일을 관리하는 MultipartFile 객체
			, int fileSize          // 업로드 파일의 최대 크기
			, String[] extensions   // 업로드 파일의 확장자
	) {
		int no = 0;
		//---------------------------------------------
		// 만약에 업로드된 파일이 있으면
		//---------------------------------------------
		if( multi!=null && multi.isEmpty()==false ) {
			//---------------------------------------------------------
			// 만약에 업로드된 파일의 크기가 매개변수 fileSize 보다 크면
			// no 변수에 -11 누적하기
			//---------------------------------------------------------
			if( multi.getSize()>fileSize ) {
				no = -11;
			}
			//---------------------------------------------------------
			// 만약에 업로드된 파일의 크기가 매개변수 fileSize 보다 같거나 작으면
			//---------------------------------------------------------
			else {
				//--------------------------
				//업로드된 파일의 원래 이름을 얻어서 변수 originalFilename 에 저장하기
				//--------------------------
				String originalFilename = multi.getOriginalFilename();
				originalFilename = originalFilename.toUpperCase();
				int cnt = 0;
				//--------------------------
				// 배열 안의 확장자 문자열이 
				// originalFilename 변수 안의 문자열에서
				// 마지막으로 끝난다면 cnt 변수에 1증가하기
				//--------------------------
				for( int i=0 ; i<extensions.length ; i++ ){
					if( originalFilename.endsWith("."+ extensions[i].toUpperCase() )  ) {
						cnt++;
					}
				}
				//--------------------------
				// 만약에 변수 cnt 가 0이면, 
				// 즉 업로드된 파일이 배열안의 들어 있는 확장자로 끝나지 않으면
				//--------------------------
				if( cnt==0 ) {
					no = -12;
				}
			}
		}
		
		return no;
	}
	//mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm
	// <파일업로드> 업로드 파일의 크기, 확장자  체크하는 메소드 선언
	//mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm
	public static int checkUploadFileForBoard(
			MultipartFile multi     // 업로드되는 파일을 관리하는 MultipartFile 객체
	) {
		return checkUploadFile(
				multi                             // 업로드되는 파일을 관리하는 MultipartFile 객체
				, 1000000                         // 업로드 파일의 최대 크기
				, new String[]{"jpg","png","gif"} // 업로드 파일의 확장자
		);
	}
	

	//mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm
	// 유효성 체크 시 발생하는 에러 메시지를 관리하는
	// BindingResult 객체로 부터 에러 메시지 얻는 메소드 선언. 
	//mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm
	public static String getErrorMsgFromBindingResult( 
		BindingResult bindingResult 
	) {
		//----------------------------------------------
		// 에러 메시지를 저장할 변수 errorMsg 선언하기
		//----------------------------------------------
		String errorMsg = "";

		//----------------------------------------------
		// 만약에 BindingResult 객체가 에러 메시지를 가지고 있다면
		//----------------------------------------------
		if(bindingResult.hasErrors()){  
			// N 개의 ObjectError 객체가 저장된 ArrayList 객체를 얻기
			// 각각의 ObjectError 객체는 한개의 에러 메시지를 소유하고 있다.
			List<ObjectError> list =  bindingResult.getAllErrors();

			errorMsg = errorMsg + list.get(0).getDefaultMessage();
			
			/*
			for( int i = 0; i<list.size() ; i++) {
				errorMsg = errorMsg +" " + list.get(i).getDefaultMessage();
				break;
			}
			*/
			
		}
		//----------------------------------------------
		// 변수 errorMsg 안의 문자 리턴하기
		//----------------------------------------------
		return errorMsg;
	}
	
	//mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm
	// 매개변수로 들어온 String 형 데이터가 
	// null 이거나, 공백으로만 구성되어 있거나, 길이가 없는 문자라면
	// true 리턴하는 메소드 선언
	//mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm
	public static boolean isEmpty( String str ) {
		if(str==null) { return true; }
		str = str.trim();
		return str.length()==0;
	}
	
	

	public static void delBoardImg(String fileName) {
		if( fileName!=null && fileName.length()>0 ) {
			new File(  uploadDirForBoard()  + fileName ).delete();
		}
	}
	
}
	
	
	
	
	
	
	


