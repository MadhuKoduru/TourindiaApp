/* Application Name = Tour India
   Version = 1.0
   Release Date = October 01, 2015
   Copyright = ©2015 SRISYS Inc
   Developed by = Srisys Inc, 7908 Cincinnati Dayton Rd, Suite C, West Chester, OH 45069 USA
   web: www.srisys.com */

package com.srisys.tourindia.controller;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.text.WordUtils;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.srisys.tourindia.model.Tia12Advertisements;
import com.srisys.tourindia.model.Tia12AuditTrail;
import com.srisys.tourindia.model.Tia12Enterprises;
import com.srisys.tourindia.model.Tia12LookupCodes;
import com.srisys.tourindia.model.Tia12RoleRights;
import com.srisys.tourindia.model.Tia12Users;
import com.srisys.tourindia.service.AdvertisementsService;
import com.srisys.tourindia.service.CommonService;
import com.srisys.tourindia.service.LoginService;
import com.srisys.tourindia.service.LookupCodesService;

@Controller
public class AdvertisementsController {

    @Autowired
    private AdvertisementsService advertisementService;
    
    
    @Autowired
    private CommonService commonservice;
    
    @Autowired
	private LoginService loginService;
    
    @Autowired
	private LookupCodesService lookupCodesService;
    
    @Autowired
    private MessageSource messages;
    List<Tia12Advertisements> advertiselistview = null;
    private Integer loginUserId(HttpServletRequest req) {
		HttpSession session = req.getSession(true);
		Integer loginID = 0;
		if (session.getAttribute("logInUserRoleId") != null) {
			loginID = Integer.parseInt(session.getAttribute("logInUserRoleId").toString());
		}
		return loginID;
	}
    
    @RequestMapping(value = "/listAdvertisements", method = RequestMethod.GET)
    public ModelAndView listadvertisement(Locale locale, HttpServletRequest req) {
System.out.println("My FIrst Commint ");
          HttpSession session = req.getSession(true);
          Integer loginUserId = loginUserId(req);
		  Tia12Users loginUserRole1 = (Tia12Users) session.getAttribute("logInUserObject");
		  String loginUserRoleName = (String) session.getAttribute("logInUserRoleName");
		  Integer logInUserCompanyId =loginUserRole1.getTia12Enterprises().getEnterpriseId();
		  //start role rights
		  List<Tia12RoleRights> LoginRights = loginService.getLoginRights(loginUserId, "Advertisements");
		  ModelAndView modelList = new ModelAndView("advertisement/AdvertisementListing");
		  modelList.addObject("AddRights",LoginRights.get(0).getTia12LookupCodesByAddRights().getLookupCode());
		  modelList.addObject("DeleteRights",LoginRights.get(0).getTia12LookupCodesByDeleteRights().getLookupCode()); 
		  modelList.addObject("ExportRights",LoginRights.get(0).getTia12LookupCodesByExportRights().getLookupCode());
		  modelList.addObject("ImportRights",LoginRights.get(0).getTia12LookupCodesByImportRights().getLookupCode());
		  modelList.addObject("ViewRights",LoginRights.get(0).getTia12LookupCodesByViewRights().getLookupCode());
		  modelList.addObject("EditRights",LoginRights.get(0).getTia12LookupCodesByEditRights().getLookupCode());
		  List<Tia12RoleRights> LoginRight = loginService.getLoginRights(loginUserId, "Enterprises");
		  modelList.addObject("DetailRights",LoginRight.get(0).getTia12LookupCodesByListRights().getLookupCode());
		  //end role rights 
    	 /* List<Tia12Advertisements> list=advertisementService.listadvertisement(logInUserCompanyId, loginUserRoleName, null, null);
    	  modelList.addObject("advertisementList", list);
    	  String logInUserDateFormat = (String) session.getAttribute("logInUserDateFormat");
          String[] fromDate = new String[list.size()];
          String[] toDate = new String[list.size()];
          for (int i=0; i<list.size(); i++) {
              SimpleDateFormat formatter = new SimpleDateFormat(logInUserDateFormat+" HH:mm:ss");
              fromDate[i] = formatter.format(list.get(i).getAdvertisementStartDate());
              toDate[i] = formatter.format(list.get(i).getAdvertisementEndDate());
          }
          modelList.addObject("fromDate", fromDate);
          modelList.addObject("toDate", toDate);*/
          
//          String[] hqlDateFormat = commonservice.loginUsrDateFormatByHQL(req);
  		
  		List<String> listingHeaderPrompts = advertisementService.getListingHeaderPrompts(messages, locale, null);
  	    List<String> listingHeaderTooltips = advertisementService.getListingHeaderTooltips(messages, locale, null);
  	    List<String> listDataClass = advertisementService.getListingHeaderClass(messages, locale, null, listingHeaderPrompts.size());
  	    
  	    String sortOrder = "tia12Enterprises.enterpriseName , advertisementName asc";
  	    List<Tia12Advertisements> list = advertisementService.listadvertisement(null, null, "listingData", sortOrder, 0, 10, null, logInUserCompanyId, loginUserRoleName);
  	    double listCount = advertisementService.listadvertisement(null, null, "listCount", sortOrder, 0, 10, null, logInUserCompanyId, loginUserRoleName).size();
  	    List<List<String>> listingData = advertisementService.getListingData(list, null, null, messages, locale, req);

  	    //Show hide columns display start code
  	    List<String> showHideArray = advertisementService.getShowHideFields(messages, locale);
  	    //Show hide columns display end code

  	    modelList.addObject("showHideArray", showHideArray);
  	    modelList.addObject("listingHeaderPrompts", listingHeaderPrompts);
  	    modelList.addObject("listingHeaderTooltips", listingHeaderTooltips);

  	    modelList.addObject("listDataClass", listDataClass);
  	    modelList.addObject("listFieldsCount", listingHeaderPrompts.size());

  	    modelList.addObject("selectedrow","selectedrow");
  	    modelList.addObject("searchValue", "");
  	    modelList.addObject("currentPageNumber", 0);
  	    modelList.addObject("entries", 10);
  	    modelList.addObject("isPage1", false);
  	    modelList.addObject("page1", 1);
  	    modelList.addObject("isPage2", false);
  	    modelList.addObject("page2", 2);
  	    modelList.addObject("isPage3", false);
  	    modelList.addObject("page3", 3);
  	    if(listCount > 0) {
  	    	modelList.addObject("currentPageNumber", 1);
  	    	modelList.addObject("isPage1", true);
  	        if(listCount > 10) {
  	        	modelList.addObject("isPage2", true);
  	            if(listCount > 20)
  	            	modelList.addObject("isPage3", true);
  	        }
  	    }

  	    modelList.addObject("sortId", "null");
  	    modelList.addObject("sortClass", "ui-icon-carat-2-n-s");
  	    modelList.addObject("listingData", listingData);
  	    modelList.addObject("totalCount", listCount);
    	
        return modelList;
    }

    @RequestMapping(value = "/listAdvertisements{page}/{id}", method = RequestMethod.GET)
    public ModelAndView listadvertisement(Locale locale, @PathVariable("page") String page, @PathVariable("id") Integer id,HttpServletRequest req) {

    	HttpSession session = req.getSession(true);
        Integer loginUserId = loginUserId(req);
		  Tia12Users loginUserRole1 = (Tia12Users) session.getAttribute("logInUserObject");
		  String loginUserRoleName = (String) session.getAttribute("logInUserRoleName");
		  Integer logInUserCompanyId =loginUserRole1.getTia12Enterprises().getEnterpriseId();
		  //start role rights
		  List<Tia12RoleRights> LoginRights = loginService.getLoginRights(loginUserId, "Advertisements");
		  ModelAndView modelList = new ModelAndView("advertisement/AdvertisementListing");
		  modelList.addObject("AddRights",LoginRights.get(0).getTia12LookupCodesByAddRights().getLookupCode());
		  modelList.addObject("DeleteRights",LoginRights.get(0).getTia12LookupCodesByDeleteRights().getLookupCode()); 
		  modelList.addObject("ExportRights",LoginRights.get(0).getTia12LookupCodesByExportRights().getLookupCode());
		  modelList.addObject("ImportRights",LoginRights.get(0).getTia12LookupCodesByImportRights().getLookupCode());
		  modelList.addObject("ViewRights",LoginRights.get(0).getTia12LookupCodesByViewRights().getLookupCode());
		  modelList.addObject("EditRights",LoginRights.get(0).getTia12LookupCodesByEditRights().getLookupCode());
		  List<Tia12RoleRights> LoginRight = loginService.getLoginRights(loginUserId, "Enterprises");
		  modelList.addObject("DetailRights",LoginRight.get(0).getTia12LookupCodesByListRights().getLookupCode());
		  //end role rights 
  	 /* List<Tia12Advertisements> list=advertisementService.listadvertisement(logInUserCompanyId, loginUserRoleName, page, id);
  	  modelList.addObject("advertisementList", list);
  	  String logInUserDateFormat = (String) session.getAttribute("logInUserDateFormat");
        String[] fromDate = new String[list.size()];
        String[] toDate = new String[list.size()];
        for (int i=0; i<list.size(); i++) {
            SimpleDateFormat formatter = new SimpleDateFormat(logInUserDateFormat+" HH:mm:ss");
            fromDate[i] = formatter.format(list.get(i).getAdvertisementStartDate());
            toDate[i] = formatter.format(list.get(i).getAdvertisementEndDate());
        }
        modelList.addObject("fromDate", fromDate);
        modelList.addObject("toDate", toDate);*/
        
//        String[] hqlDateFormat = commonservice.loginUsrDateFormatByHQL(req);
		
		List<String> listingHeaderPrompts = advertisementService.getListingHeaderPrompts(messages, locale, null);
	    List<String> listingHeaderTooltips = advertisementService.getListingHeaderTooltips(messages, locale, null);
	    List<String> listDataClass = advertisementService.getListingHeaderClass(messages, locale, null, listingHeaderPrompts.size());
	    
	    String sortOrder = "tia12Enterprises.enterpriseName , advertisementName asc";
	    List<Tia12Advertisements> list = advertisementService.listadvertisement(id, page, "listingData", sortOrder, 0, 10, null, logInUserCompanyId, loginUserRoleName);
	    double listCount = advertisementService.listadvertisement(id, page, "listCount", sortOrder, 0, 10, null, logInUserCompanyId, loginUserRoleName).size();
	    List<List<String>> listingData = advertisementService.getListingData(list, null, null, messages, locale, req);

	    //Show hide columns display start code
	    List<String> showHideArray = advertisementService.getShowHideFields(messages, locale);
	    //Show hide columns display end code

	    modelList.addObject("showHideArray", showHideArray);
	    modelList.addObject("listingHeaderPrompts", listingHeaderPrompts);
	    modelList.addObject("listingHeaderTooltips", listingHeaderTooltips);

	    modelList.addObject("listDataClass", listDataClass);
	    modelList.addObject("listFieldsCount", listingHeaderPrompts.size());

	    modelList.addObject("selectedrow","selectedrow");
	    modelList.addObject("searchValue", "");
	    modelList.addObject("currentPageNumber", 0);
	    modelList.addObject("entries", 10);
	    modelList.addObject("isPage1", false);
	    modelList.addObject("page1", 1);
	    modelList.addObject("isPage2", false);
	    modelList.addObject("page2", 2);
	    modelList.addObject("isPage3", false);
	    modelList.addObject("page3", 3);
	    if(listCount > 0) {
	    	modelList.addObject("currentPageNumber", 1);
	    	modelList.addObject("isPage1", true);
	        if(listCount > 10) {
	        	modelList.addObject("isPage2", true);
	            if(listCount > 20)
	            	modelList.addObject("isPage3", true);
	        }
	    }
	    modelList.addObject("var_type", page);
	    modelList.addObject("condition_type", id);
	    modelList.addObject("sortId", "null");
	    modelList.addObject("sortClass", "ui-icon-carat-2-n-s");
	    modelList.addObject("listingData", listingData);
	    modelList.addObject("totalCount", listCount);


        return modelList;
    }
    
	@RequestMapping(value = "/listAdvertisements{page}", method = RequestMethod.POST)
    public ModelAndView listRightsDetailsi(Locale locale, @PathVariable("page") String page, HttpServletRequest req) {
		HttpSession session = req.getSession(true);
        Integer loginUserId = loginUserId(req);
		  Tia12Users loginUserRole1 = (Tia12Users) session.getAttribute("logInUserObject");
		  String loginUserRoleName = (String) session.getAttribute("logInUserRoleName");
		  Integer logInUserCompanyId =loginUserRole1.getTia12Enterprises().getEnterpriseId();
		  //start role rights
		  List<Tia12RoleRights> LoginRights = loginService.getLoginRights(loginUserId, "Advertisements");
		  ModelAndView modelList = new ModelAndView("advertisement/AdvertisementListing");
		  modelList.addObject("AddRights",LoginRights.get(0).getTia12LookupCodesByAddRights().getLookupCode());
		  modelList.addObject("DeleteRights",LoginRights.get(0).getTia12LookupCodesByDeleteRights().getLookupCode()); 
		  modelList.addObject("ExportRights",LoginRights.get(0).getTia12LookupCodesByExportRights().getLookupCode());
		  modelList.addObject("ImportRights",LoginRights.get(0).getTia12LookupCodesByImportRights().getLookupCode());
		  modelList.addObject("ViewRights",LoginRights.get(0).getTia12LookupCodesByViewRights().getLookupCode());
		  modelList.addObject("EditRights",LoginRights.get(0).getTia12LookupCodesByEditRights().getLookupCode());
		  List<Tia12RoleRights> LoginRight = loginService.getLoginRights(loginUserId, "Enterprises");
		  modelList.addObject("DetailRights",LoginRight.get(0).getTia12LookupCodesByListRights().getLookupCode());
		  //end role rights 
  	  /*List<Tia12Advertisements> list=advertisementService.listadvertisement(logInUserCompanyId, loginUserRoleName, null, null);
  	  modelList.addObject("advertisementList", list);
  	  String logInUserDateFormat = (String) session.getAttribute("logInUserDateFormat");
        String[] fromDate = new String[list.size()];
        String[] toDate = new String[list.size()];
        for (int i=0; i<list.size(); i++) {
            SimpleDateFormat formatter = new SimpleDateFormat(logInUserDateFormat+" HH:mm:ss");
            fromDate[i] = formatter.format(list.get(i).getAdvertisementStartDate());
            toDate[i] = formatter.format(list.get(i).getAdvertisementEndDate());
        }
        modelList.addObject("fromDate", fromDate);
        modelList.addObject("toDate", toDate);*/
		  
		  List<String> listingHeaderPrompts = advertisementService.getListingHeaderPrompts(messages, locale, null);
		    List<String> listingHeaderTooltips = advertisementService.getListingHeaderTooltips(messages, locale, null);
		    List<String> listDataClass = advertisementService.getListingHeaderClass(messages, locale, null, listingHeaderPrompts.size());
		    
		    String sortOrder = "tia12Enterprises.enterpriseName , advertisementName asc";
		    List<Tia12Advertisements> list = advertisementService.listadvertisement(null, null, "listingData", sortOrder, 0, 10, null, logInUserCompanyId, loginUserRoleName);
		    double listCount = advertisementService.listadvertisement(null, null, "listCount", sortOrder, 0, 10, null, logInUserCompanyId, loginUserRoleName).size();
		    List<List<String>> listingData = advertisementService.getListingData(list, null, null, messages, locale, req);

		    //Show hide columns display start code
		    List<String> showHideArray = advertisementService.getShowHideFields(messages, locale);
		    //Show hide columns display end code

		    modelList.addObject("showHideArray", showHideArray);
		    modelList.addObject("listingHeaderPrompts", listingHeaderPrompts);
		    modelList.addObject("listingHeaderTooltips", listingHeaderTooltips);

		    modelList.addObject("listDataClass", listDataClass);
		    modelList.addObject("listFieldsCount", listingHeaderPrompts.size());

		    modelList.addObject("selectedrow","selectedrow");
		    modelList.addObject("searchValue", "");
		    modelList.addObject("currentPageNumber", 0);
		    modelList.addObject("entries", 10);
		    modelList.addObject("isPage1", false);
		    modelList.addObject("page1", 1);
		    modelList.addObject("isPage2", false);
		    modelList.addObject("page2", 2);
		    modelList.addObject("isPage3", false);
		    modelList.addObject("page3", 3);
		    if(listCount > 0) {
		    	modelList.addObject("currentPageNumber", 1);
		    	modelList.addObject("isPage1", true);
		        if(listCount > 10) {
		        	modelList.addObject("isPage2", true);
		            if(listCount > 20)
		            	modelList.addObject("isPage3", true);
		        }
		    }
            
		    modelList.addObject("sortId", "null");
		    modelList.addObject("sortClass", "ui-icon-carat-2-n-s");
		    modelList.addObject("listingData", listingData);
		    modelList.addObject("totalCount", listCount);
		return modelList;
    }
    
	@RequestMapping(value = "/ajaxAdvertisementsListing", method = RequestMethod.GET)
	   public String ajaxAdvertisementsListing(HttpServletRequest req, Locale locale, Model model,
	        @RequestParam("postedValue1") int entries, @RequestParam("postedValue2") String pagiNation,
	        @RequestParam("postedValue3") int currentPageNumber, @RequestParam("postedValue4") String sortColumnId,
	        @RequestParam("postedValue5") String sortClass, @RequestParam("postedValue6") String hideColumns,
	        @RequestParam("postedValue7") String var_type, @RequestParam("postedValue8") String condition_type,
	        @RequestParam("postedValue9") String term) {

	        HttpSession session = req.getSession(true);
	        Tia12Users loginUserRole1 = (Tia12Users) session.getAttribute("logInUserObject");
			  String loginUserRoleName = (String) session.getAttribute("logInUserRoleName");
			  Integer logInUserCompanyId =loginUserRole1.getTia12Enterprises().getEnterpriseId();
		
	        Integer selectedRecordId = null;
		List<String> listingHeaderPrompts = advertisementService.getListingHeaderPrompts(messages, locale, hideColumns);
	    List<String> listingHeaderTooltips = advertisementService.getListingHeaderTooltips(messages, locale, hideColumns);
	    List<String> listDataClass = advertisementService.getListingHeaderClass(messages, locale, hideColumns, listingHeaderPrompts.size());
	    
	    String sortOrder = advertisementService.getSortOrder(sortColumnId, sortClass, hideColumns, messages, locale);        

	    term = commonservice.escapeSearchString(term);

	    double listCount = 0 ;
	    
	    
	if(!condition_type.equals(""))
		selectedRecordId = Integer.parseInt(condition_type);
	     listCount = advertisementService.listadvertisement(selectedRecordId, var_type, "listCount", sortOrder, 0, 10, term, logInUserCompanyId, loginUserRoleName).size();


	    
	    List<String> arrayOfPaginationData = commonservice.getArrayOfPaginationData(listCount, entries, pagiNation, currentPageNumber);
	    int recordsStart = Integer.parseInt(arrayOfPaginationData.get(7));
	    
	    List<Tia12Advertisements> list = null;
	    
	    list = advertisementService.listadvertisement(selectedRecordId, var_type, "listingData", sortOrder, recordsStart, entries, term, logInUserCompanyId, loginUserRoleName);

	    
	    List<List<String>> listingData = advertisementService.getListingData(list, null, hideColumns, messages, locale, req);

	    model.addAttribute("listingHeaderPrompts", listingHeaderPrompts);
	    model.addAttribute("listingHeaderTooltips", listingHeaderTooltips);

	    model.addAttribute("listDataClass", listDataClass);
	    model.addAttribute("listFieldsCount", listingHeaderPrompts.size());
	    model.addAttribute("selectedrow","selectedrow");

	    term = commonservice.originalSearchString(term);
	    term = term.replace("\"", "&quot;");
	    model.addAttribute("searchValue", term);
	    model.addAttribute("currentPageNumber", Integer.parseInt(arrayOfPaginationData.get(0)));
	    model.addAttribute("entries", entries);
	    model.addAttribute("isPage1", Boolean.parseBoolean(arrayOfPaginationData.get(1)));
	    model.addAttribute("page1", Integer.parseInt(arrayOfPaginationData.get(4)));
	    model.addAttribute("isPage2", Boolean.parseBoolean(arrayOfPaginationData.get(2)));
	    model.addAttribute("page2", Integer.parseInt(arrayOfPaginationData.get(5)));
	    model.addAttribute("isPage3", Boolean.parseBoolean(arrayOfPaginationData.get(3)));
	    model.addAttribute("page3", Integer.parseInt(arrayOfPaginationData.get(6)));

	    model.addAttribute("var_type", var_type);
	    model.addAttribute("condition_type", condition_type);
	    model.addAttribute("sortId", sortColumnId);
	    model.addAttribute("sortClass", sortClass);
	    model.addAttribute("listingData", listingData);
	    model.addAttribute("totalCount", listCount);

	    return "common/CommonListing";
	}

	
    @RequestMapping(value = "/addAdvertisement", method = RequestMethod.GET)
    public ModelAndView addadvertisement(@ModelAttribute("addAdvertisement")  Tia12Advertisements advertisement,  BindingResult result, HttpServletRequest req) {
    	  HttpSession session = req.getSession(true);
    	  Tia12Users loginUserRole1=(Tia12Users)session.getAttribute("logInUserObject"); 
		  String loginUserCompanyName=(String) session.getAttribute("loggedInUserCmpName"); 
		  ModelAndView modelView = new ModelAndView("advertisement/AdvertisementAdd");
		  int companyid = loginUserRole1.getTia12Enterprises().getEnterpriseId();
		  modelView.addObject("loginUserCompanyName", loginUserCompanyName);
		  modelView.addObject("loginusercompanyid", companyid);
		  List<Tia12LookupCodes> lclist=advertisementService.listLookupCodes("BOOLEAN", "desc");
		  modelView.addObject("booleanList", lclist);
		  List<Tia12LookupCodes> atypes = advertisementService.listLookupCodes("ADVERTISEMENT_TYPES", "asc");
		  modelView.addObject("atypesList", atypes);
		  List<Tia12Enterprises> companyList = advertisementService.listCompanies();
		  modelView.addObject("companyList", companyList);
		  String logInUserDateFormat = (String) session.getAttribute("logInUserDateFormat");
	      String logInUserJqueryDateFormat = (String) session.getAttribute("logInUserJqueryDateFormat");
	      modelView.addObject("logInUserDateFormat",logInUserDateFormat);
	      modelView.addObject("logInUserJqueryDateFormat",logInUserJqueryDateFormat);
		 return modelView;
    }
    
    @RequestMapping(value = "/insertadvertisement", method = RequestMethod.POST)
    public String insertAdvertisement(HttpServletRequest req, HttpServletResponse res, @RequestParam(value ="add_advertisement_addverimage_name", required = false) MultipartFile image, @ModelAttribute("addAdvertisement") @Validated Tia12Advertisements advertisement, BindingResult result, Model model) throws HibernateException, IOException, Exception { 
        	HttpSession session = req.getSession(true);
	    	Tia12Users logInUserObject=(Tia12Users) session.getAttribute("logInUserObject");
			Date d = commonservice.getUTCDateTime();
	    	String aenterprise = req.getParameter("tia12Enterprises.enterpriseName");
            String aname = WordUtils.capitalizeFully(req.getParameter("advertisementName").trim().replaceAll(" +", " "));
            String enable = req.getParameter("tia12LookupCodesByEnabledFlag.lookupCode");
            String adescripction = req.getParameter("advertisementDesc").trim();
            String atype = req.getParameter("tia12LookupCodesByAdvertisementType.lookupCode");
            String astart = req.getParameter("effective_start_date_name");
            String aend = req.getParameter("effective_end_date_name");
            String asequence = req.getParameter("advertisementSeq").trim();
            String aurl = req.getParameter("advertisementUrl");
            String afilename = req.getParameter("advertisementFileName");
            String anotes = req.getParameter("advertisementNotes");
            /*byte[] bFile=null;
			try{
				if (!image.isEmpty()) {
					bFile = new byte[(int) image.getSize()];
		            bFile=image.getBytes();
		        }
	        }
	        catch(Exception e){
	        	e.printStackTrace();
	        }*/
			
            Tia12Advertisements advertisementodj = new Tia12Advertisements();
            advertisementodj = insertadvertisement(aenterprise,aname,enable,adescripction,atype,astart,aend,asequence,aurl,afilename,d,logInUserObject,anotes,image); 
           
            String enterpriseaudit=""; String atypetaudit="";String enableaudit="";
            
            if (aenterprise != null) { enterpriseaudit=commonservice.parseName(aenterprise); }
            if (atype != null ) { atypetaudit=commonservice.parseName(atype); }
            if (enable != null) { enableaudit=commonservice.parseName(enable); }
            String[] auditColums = {"ENTERPRISE_ID","ADVERTISEMENT_NAME","ADVERTISEMENT_DESC","ADVERTISEMENT_NOTES",
            						"ADVERTISEMENT_TYPE","ADVERTISEMENT_START_DATE", "ADVERTISEMENT_END_DATE", 
            						"ADVERTISEMENT_SEQ", "ADVERTISEMENT_FILE_NAME",
            						"ADVERTISEMENT_URL", "ENABLED", "DELETED", "ADVERTISEMENT_IMG"};
            String[] auditData = {enterpriseaudit,aname,adescripction,anotes,atypetaudit,astart,aend,asequence,
            						afilename,aurl,enableaudit,"No",aname};
            Tia12AuditTrail[] arrayAuditData = commonservice.insertIntoAuditTrail(auditColums, auditData, aenterprise, "tia12_advertisements", "Add", d,null,logInUserObject);
            advertisementService.saveadvertisement(advertisementodj, arrayAuditData); 
            return "redirect:/listAdvertisements";
    }

	private Tia12Advertisements insertadvertisement(String aenterprise, String aname, String enable, String adescripction, String atype, String astart,
													String aend, String asequence, String aurl, String afilename, Date d, Tia12Users logInUserObject, String anotes,MultipartFile image) {
		 Tia12Advertisements a = new Tia12Advertisements();
		 Tia12Enterprises enterpriseId = new Tia12Enterprises();
		 if(aenterprise != null){
			 enterpriseId.setEnterpriseId(commonservice.parseId(aenterprise));
		 }
		 a.setAdvertisementNotes(anotes);
		 a.setTia12Enterprises(enterpriseId);
		 a.setAdvertisementName(aname);
		 a.setAdvertisementDesc(adescripction);
		 a.setAdvertisementSeq(Integer.parseInt(asequence));
		 a.setAdvertisementUrl(aurl);
		 a.setAdvertisementFileName(afilename);
		 
		 byte[] bFile=null;
			try{
				if (image !=null && !image.isEmpty()) {
					bFile = new byte[(int) image.getSize()];
		            bFile=image.getBytes();
		        }
				a.setAdvertisementImg(bFile);
	        }
			
	        catch(Exception e){
	        	e.printStackTrace();
	        }
		 
		 /*byte[] b = aimage.getBytes(Charset.forName("UTF-8"));
		 a.setAdvertisementImg(b);*/
		 if (astart != null) { 
	         SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			 Date date = new Date();
				try {
					date = formatter1.parse(astart);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				a.setAdvertisementStartDate(date);
	     }
		 if (aend != null) { 
	         SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			 Date date = new Date();
				try {
					date = formatter1.parse(aend);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				a.setAdvertisementEndDate(date);
	     }
		 
		Tia12LookupCodes alc = new Tia12LookupCodes();
		if(enable != ""){
			alc.setLookupId(commonservice.parseId(enable));
		}
		a.setTia12LookupCodesByEnabledFlag(alc);
		Tia12LookupCodes delflag = commonservice.getLookupCode("BOOLEAN", "No").get(0);
		a.setTia12LookupCodesByDeletedFlag(delflag);
		Tia12LookupCodes alc1 = new Tia12LookupCodes();
		if(atype != ""){
			alc1.setLookupId(commonservice.parseId(atype));
		}
		a.setTia12LookupCodesByAdvertisementType(alc1);
		a.setCreationDate(d);
		a.setLastUpdatedDate(d);
		Tia12Users user = new Tia12Users();
		user.setUserId(logInUserObject.getUserId());
		a.setTia12UsersByCreatedBy(user);
		a.setTia12UsersByLastUpdatedBy(user);
		
		return a;
	}
	@RequestMapping(value = "/editAdvertisement", method = RequestMethod.POST)
	public ModelAndView editServiceProviders(@ModelAttribute("editAdvertisement") Tia12Advertisements advertisement, BindingResult result, HttpServletRequest request) {
		 ModelAndView modelView = new ModelAndView("advertisement/AdvertisementEdit");
		 int selectedRecordId = Integer.parseInt(request.getParameter("selectedRecordIdName"));
		 advertiselistview = advertisementService.selectedRecordDetails(selectedRecordId);
		 Tia12Advertisements editDetails = advertiselistview.get(0);
		 modelView.addObject("EditDetails", editDetails);
		 List<Tia12LookupCodes> lclist=advertisementService.listLookupCodes("BOOLEAN", "desc");
	     modelView.addObject("booleanList", lclist);
	     List<Tia12LookupCodes> atypes = advertisementService.listLookupCodes("ADVERTISEMENT_TYPES", "asc");
	     modelView.addObject("atypesList", atypes);
	     List<Tia12AuditTrail> auditTrail = commonservice.auditTrailData("tia12_advertisements",selectedRecordId); 
		 modelView.addObject("auditTrail",auditTrail);
		//Start of DateFormat Code 
		  HttpSession sessionHttp = request.getSession(true);
		  String logInUserDateFormat = (String)sessionHttp.getAttribute("logInUserDateFormat"); 
		  String logInUserTmZone = (String) sessionHttp.getAttribute("logInUserTimeZoneMeaning");
		  SimpleDateFormat formatter2 = new SimpleDateFormat(logInUserDateFormat+" HH:mm:ss"); 
		  String[] auditCrtDate = new String[auditTrail.size()]; 
		  if(auditTrail.size() > 0) { 
		  for(int i=0;i<auditTrail.size(); i++) { 
		  Date crDate = commonservice.convertToSelectedTZone(auditTrail.get(i).getCreationDate(),logInUserTmZone); 
		  auditCrtDate[i] = formatter2.format(crDate); 
		  } 
		  }
		  modelView.addObject("auditCrtDate",auditCrtDate); 
		  Date crtDate = commonservice.convertToSelectedTZone(editDetails.getCreationDate(), logInUserTmZone); 
		  String creationDate = formatter2.format(crtDate); 
		  Date lstUDate = commonservice.convertToSelectedTZone(editDetails.getLastUpdatedDate(), logInUserTmZone);
		  String lastUpdatedDate = formatter2.format(lstUDate);
		  modelView.addObject("creationDate", creationDate);
		  modelView.addObject("lastUpdatedDate", lastUpdatedDate); 
		  //End of DateFormat Code
		  SimpleDateFormat hiddenFormatter = new SimpleDateFormat(logInUserDateFormat+" HH:mm:ss");
		  String fd = hiddenFormatter.format(editDetails.getAdvertisementStartDate());
		  modelView.addObject("startdate", fd);
		  String fd1 = hiddenFormatter.format(editDetails.getAdvertisementEndDate());
		  modelView.addObject("enddate", fd1);
		  
		  SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		  String hidden1 = formatter.format(editDetails.getAdvertisementStartDate());
		  modelView.addObject("startdate1", hidden1);
		  String hidden2 = formatter.format(editDetails.getAdvertisementEndDate());
		  modelView.addObject("enddate1", hidden2);
		  //start to send session values to common template
		  String logInUserJqueryDateFormat = (String) sessionHttp.getAttribute("logInUserJqueryDateFormat");
	      modelView.addObject("logInUserDateFormat",logInUserDateFormat);
	      modelView.addObject("logInUserJqueryDateFormat",logInUserJqueryDateFormat);
	      //end to send session values to common template
	      if(editDetails.getAdvertisementImg() != null){
	    	  String hiddenbutton = "Show";
	    	  modelView.addObject("hiddenbutton",hiddenbutton);
	      }
	      
		return modelView;
	}
	@RequestMapping(value = "/updateadvertisement", method = RequestMethod.POST)
    public String updateAdvertisement(HttpServletRequest req, HttpServletResponse res, @RequestParam(value="editAdverimgName", required = false) MultipartFile image, @ModelAttribute("editAdvertisement") @Validated Tia12Advertisements advertisement, BindingResult result, Model model) throws HibernateException, IOException, Exception { 
		    //int selectedRecordId = Integer.parseInt(req.getParameter("edit_advertisementid_name"));
			List<Tia12Advertisements> list =null;
			list = advertisementService.selectedRecordDetails(advertisement.getAdvertisementId());
			Tia12Advertisements editDetails = list.get(0);
			String oldtype =  editDetails.getTia12LookupCodesByAdvertisementType().getLookupCode();
			String oldenabled =  editDetails.getTia12LookupCodesByEnabledFlag().getLookupCode();
			String oldadvername = editDetails.getAdvertisementName().trim().replaceAll(" +", " ");
			String olddescrip = editDetails.getAdvertisementDesc().trim();
			String oldnotes = editDetails.getAdvertisementNotes()!=null ? editDetails.getAdvertisementNotes().trim() : "";
			String oldseq = editDetails.getAdvertisementSeq()+"";
			String oldfile = editDetails.getAdvertisementFileName().trim();
			String oldurl = editDetails.getAdvertisementUrl().trim();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	  		String fromDate = formatter.format(editDetails.getAdvertisementStartDate());
	  		String toDate = formatter.format(editDetails.getAdvertisementEndDate());
	  		HttpSession session = req.getSession(true);
			Tia12Users logInUserObject=(Tia12Users) session.getAttribute("logInUserObject");
			Date d = commonservice.getUTCDateTime();
	    	String aenterprise = req.getParameter("tia12Enterprises.enterpriseName");
            String aname = WordUtils.capitalizeFully(req.getParameter("advertisementName").trim().replaceAll(" +", " "));  
            String enable = req.getParameter("tia12LookupCodesByEnabledFlag.lookupCode");
            String adescripction = req.getParameter("add_advertisement_addverdescripction_name").trim();
            String atype = req.getParameter("tia12LookupCodesByAdvertisementType.lookupCode");
            String astart = req.getParameter("effective_start_date_name");
            String aend = req.getParameter("effective_end_date_name");
            String asequence = req.getParameter("advertisementSeq").trim();
            String aurl = req.getParameter("advertisementUrl");
            String afilename = req.getParameter("advertisementFileName").trim();
            
            String anotes = req.getParameter("add_advertisement_addvernotes_name").trim();
           
            byte[] bFile=null;
			try{
				if (image==null || image.isEmpty()) {
					//bFile = new byte[(int) image.getSize()];
		            //bFile=image.getBytes();
		        }else{
		        	  bFile = new byte[(int) image.getSize()];
			          bFile=image.getBytes();
		        }
	        }
	        catch(Exception e){
	        	e.printStackTrace();
	        }
            
            Tia12Advertisements advertisementodj = new Tia12Advertisements();
            String atypetaudit="";String enableaudit="";
            
            if (atype != null ) { atypetaudit=commonservice.parseName(atype); }
            if (enable != null) { enableaudit=commonservice.parseName(enable); }
            String[] auditColums = {"ADVERTISEMENT_NAME","ADVERTISEMENT_DESC","ADVERTISEMENT_NOTES",
            						"ADVERTISEMENT_TYPE","ADVERTISEMENT_START_DATE", "ADVERTISEMENT_END_DATE", 
            						"ADVERTISEMENT_SEQ", "ADVERTISEMENT_FILE_NAME",
            						"ADVERTISEMENT_URL", "ENABLED","ADVERTISEMENT_IMG"};
            String[] auditData = {aname,adescripction,anotes,atypetaudit,astart,aend,asequence,afilename,aurl,enableaudit,aname}; 
            String[] oldData = {oldadvername,olddescrip,oldnotes,oldtype,fromDate,toDate,oldseq,oldfile,oldurl,oldenabled,oldadvername};
  		  	
            int i=0;
            for(i=0; i<auditData.length;i++){
    	    	if(oldData[i].equals(auditData[i])){
    	    		
    	    	}
    	    	else{
    	    		break;
    	    	}
    	    } 
    	    if(i!=auditData.length || image!=null){
    	    	advertisementodj = updateadvertisement(aenterprise,aname,enable,adescripction,atype,astart,aend,asequence,aurl,afilename,d,editDetails,logInUserObject,anotes,bFile); 
    	    	Tia12AuditTrail[] arrayAuditData = commonservice.insertIntoAuditTrail(auditColums, auditData, aname, "tia12_advertisements", "Update", d,oldData,logInUserObject);
                advertisementService.updateadvertisement(advertisementodj, arrayAuditData); 	
    	    }
            return "redirect:/listAdvertisements";
    }

	private Tia12Advertisements updateadvertisement(String aenterprise, String aname, String enable, String adescripction, String atype, String astart,
			String aend, String asequence, String aurl, String afilename, Date d, Tia12Advertisements advertisement, Tia12Users logInUserObject, String anotes, byte[] bFile) {
		Tia12Enterprises enterpriseId = new Tia12Enterprises();
		 if(aenterprise != null){
			 enterpriseId.setEnterpriseId(commonservice.parseId(aenterprise));
		 }
		 advertisement.setTia12Enterprises(enterpriseId);
		advertisement.setAdvertisementNotes(anotes);
		advertisement.setAdvertisementName(aname);
		advertisement.setAdvertisementDesc(adescripction);
		advertisement.setAdvertisementSeq(Integer.parseInt(asequence));
		advertisement.setAdvertisementUrl(aurl);
		advertisement.setAdvertisementFileName(afilename);
		/*byte[] b = aimage.getBytes(Charset.forName("UTF-8"));
		advertisement.setAdvertisementImg(b);*/
		if(bFile == null){}else{
		advertisement.setAdvertisementImg(bFile);
		}
		if (astart != null) { 
		SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		
		try {
		date = formatter1.parse(astart);
		} catch (ParseException e) {
		e.printStackTrace();
		}
		advertisement.setAdvertisementStartDate(date);
		}
		if (aend != null) { 
		SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date1 = new Date();
		try {
			date1 = formatter1.parse(aend);
		} catch (ParseException e) {
		e.printStackTrace();
		}
		advertisement.setAdvertisementEndDate(date1);
		}
		
		Tia12LookupCodes alc = new Tia12LookupCodes();
		if(enable != ""){
		alc.setLookupId(commonservice.parseId(enable));
		}
		advertisement.setTia12LookupCodesByEnabledFlag(alc);
		Tia12LookupCodes delflag = commonservice.getLookupCode("BOOLEAN", "No").get(0);
		advertisement.setTia12LookupCodesByDeletedFlag(delflag);
		Tia12LookupCodes alc1 = new Tia12LookupCodes();
		if(atype != ""){
		alc1.setLookupId(commonservice.parseId(atype));
		}
		advertisement.setTia12LookupCodesByAdvertisementType(alc1);
		advertisement.setLastUpdatedDate(d);
		Tia12Users user = new Tia12Users();
		user.setUserId(logInUserObject.getUserId());
		advertisement.setTia12UsersByLastUpdatedBy(user);

		return advertisement;
		}
	
	@RequestMapping(value = "/deleteAdvertisement", method =RequestMethod.POST) 
	   public ModelAndView deleteAdvertisement(HttpServletRequest request) { 
		      ModelAndView modelView = new ModelAndView("advertisement/AdvertisementDelete");
			  int selectedRecordId = Integer.parseInt(request.getParameter("selectedRecordIdName"));
			  advertiselistview  = advertisementService.selectedRecordDetails(selectedRecordId);
			  Tia12Advertisements deleteDetails = advertiselistview.get(0);
			  List<Tia12AuditTrail> auditTrail = commonservice.auditTrailData("tia12_advertisements",selectedRecordId); 
			  modelView.addObject("deleteDetails",deleteDetails);
			  modelView.addObject("auditTrail",auditTrail);
			  //Start of DateFormat Code 
			  HttpSession sessionHttp = request.getSession(true);
			  String logInUserDateFormat = (String)sessionHttp.getAttribute("logInUserDateFormat"); 
			  String logInUserTmZone = (String) sessionHttp.getAttribute("logInUserTimeZoneMeaning");
			  SimpleDateFormat formatter2 = new SimpleDateFormat(logInUserDateFormat+" HH:mm:ss"); 
			  String[] auditCrtDate = new String[auditTrail.size()]; 
			  if(auditTrail.size() > 0) { 
			  for(int i=0;i<auditTrail.size(); i++) { 
			  Date crDate = commonservice.convertToSelectedTZone(auditTrail.get(i).getCreationDate(),logInUserTmZone); 
			  auditCrtDate[i] = formatter2.format(crDate); 
			  } 
			  }
			  modelView.addObject("auditCrtDate",auditCrtDate); 
			  Date crtDate = commonservice.convertToSelectedTZone(deleteDetails.getCreationDate(), logInUserTmZone); 
			  String creationDate = formatter2.format(crtDate); 
			  Date lstUDate = commonservice.convertToSelectedTZone(deleteDetails.getLastUpdatedDate(), logInUserTmZone);
			  String lastUpdatedDate = formatter2.format(lstUDate);
			  modelView.addObject("creationDate", creationDate);
			  modelView.addObject("lastUpdatedDate", lastUpdatedDate); 
			  //End of DateFormat Code
			  SimpleDateFormat formatter = new SimpleDateFormat(logInUserDateFormat);
		      String fromDate = formatter.format(deleteDetails.getAdvertisementStartDate());
			  modelView.addObject("startdate", fromDate);
			  String toDate = formatter.format(deleteDetails.getAdvertisementEndDate());
			  modelView.addObject("enddate", toDate);
			  if(deleteDetails.getAdvertisementImg() != null){
		    	  String hiddenbutton = "Show";
		    	  modelView.addObject("hiddenbutton",hiddenbutton);
		      }
			  return modelView; 
	}
	@RequestMapping(value = "/deletingAdvertisement", method = RequestMethod.POST) 
	public String deletingAdvertisement(@ModelAttribute("deleteAdvertisement") Tia12Advertisements advertisement, HttpServletRequest req,BindingResult result) {
				String id = req.getParameter("edit_advertisementid_name");
				List<Tia12Advertisements> list = advertisementService.selectedRecordDetails(Integer.parseInt(id));
				advertisement = list.get(0);
				Tia12LookupCodes delflag = commonservice.getLookupCode("BOOLEAN", "Yes").get(0);
				advertisement.setTia12LookupCodesByDeletedFlag(delflag);
				advertisementService.delMaintContracts(advertisement); 
		  return "redirect:/listAdvertisements"; 
	}
	@RequestMapping(value = "/viewAdvertisement", method =RequestMethod.POST) 
	   public ModelAndView viewAdvertisement(HttpServletRequest request) { 
		      ModelAndView modelView = new ModelAndView("advertisement/AdvertisementView");
			  int selectedRecordId = Integer.parseInt(request.getParameter("selectedRecordIdName"));
			  advertiselistview = advertisementService.selectedRecordDetails(selectedRecordId);
			  Tia12Advertisements viewDetails = advertiselistview.get(0);
			  List<Tia12AuditTrail> auditTrail = commonservice.auditTrailData("tia12_advertisements",selectedRecordId); 
			  modelView.addObject("viewDetails",viewDetails);
			  modelView.addObject("auditTrail",auditTrail);
			  //Start of DateFormat Code 
			  HttpSession sessionHttp = request.getSession(true);
			  String logInUserDateFormat = (String)sessionHttp.getAttribute("logInUserDateFormat"); 
			  String logInUserTmZone = (String) sessionHttp.getAttribute("logInUserTimeZoneMeaning");
			  SimpleDateFormat formatter2 = new SimpleDateFormat(logInUserDateFormat+" HH:mm:ss"); 
			  String[] auditCrtDate = new String[auditTrail.size()]; 
			  if(auditTrail.size() > 0) { 
			  for(int i=0;i<auditTrail.size(); i++) { 
			  Date crDate = commonservice.convertToSelectedTZone(auditTrail.get(i).getCreationDate(),logInUserTmZone); 
			  auditCrtDate[i] = formatter2.format(crDate); 
			  } 
			  }
			  if(viewDetails.getAdvertisementImg() != null){
		    	  String hiddenbutton = "Show";
		    	  modelView.addObject("hiddenbutton",hiddenbutton);
		      }
			  modelView.addObject("auditCrtDate",auditCrtDate); 
			  Date crtDate = commonservice.convertToSelectedTZone(viewDetails.getCreationDate(), logInUserTmZone); 
			  String creationDate = formatter2.format(crtDate); 
			  Date lstUDate = commonservice.convertToSelectedTZone(viewDetails.getLastUpdatedDate(), logInUserTmZone);
			  String lastUpdatedDate = formatter2.format(lstUDate);
			  modelView.addObject("creationDate", creationDate);
			  modelView.addObject("lastUpdatedDate", lastUpdatedDate); 
			  //End of DateFormat Code
			  SimpleDateFormat formatter = new SimpleDateFormat(logInUserDateFormat);
		      String fromDate = formatter.format(viewDetails.getAdvertisementStartDate());
			  modelView.addObject("startdate", fromDate);
			  String toDate = formatter.format(viewDetails.getAdvertisementEndDate());
			  modelView.addObject("enddate", toDate);
			  return modelView; 
			  }

	@RequestMapping(value = "/downloadadvertisementimg", method = RequestMethod.GET)
	public void downloadnotepadfile(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		try {
			response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
			String appName = advertiselistview.get(0).getAdvertisementName();
			appName = appName.trim().replaceAll(" ", "_");
			response.setHeader("Content-Disposition", "attachment; filename="+ appName);
			response.getOutputStream().write(advertiselistview.get(0).getAdvertisementImg());
			response.getOutputStream().close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@RequestMapping(value = "/getAdvertisementUniqueness", method = RequestMethod.GET)
	public @ResponseBody String getUniqness(Locale locale, Model model, @RequestParam("postedValue1") String entepriseId, @RequestParam("postedValue2") String advertisement, @RequestParam("postedValue3") String sequence) {
			Integer enterId = commonservice.parseId(entepriseId);
			String advert = advertisement.trim().replaceAll(" +", " ");
			Integer seq = Integer.parseInt(sequence); 
			Integer uniqData = 0;
			uniqData = advertisementService.findUniq(enterId, advert,null,seq);
			String status="";
			if(uniqData == 0){
				status = "true";
			}else {
				status = "false";			
			}
			return status;
		}
	 @RequestMapping(value = "/geteditAdvertisementUniqueness", method = RequestMethod.GET)
		public @ResponseBody String getUniqness(Locale locale, Model model, @RequestParam("postedValue1") String entepriseId, @RequestParam("postedValue2") String advertisement, @RequestParam("postedValue3") String advertisementId, @RequestParam("postedValue4") String sequence) {
			Integer enterId = commonservice.parseId(entepriseId);
			String advert = advertisement.trim().replaceAll(" +", " ");
			Integer seq = Integer.parseInt(sequence); 
			Integer uniqData = advertisementService.findUniq(enterId, advert,Integer.parseInt(advertisementId),seq);
			String status="";
			if(uniqData == 0){
				status = "true";
			}else {
				status = "false";			
			}
			return status;
		}
	 @RequestMapping(value = "/TourIndia-Advertisements-{timestamp}.xls", method = RequestMethod.POST)
	    public ModelAndView exportAdvertisements(Locale locale, HttpServletRequest req) {
			HttpSession session = req.getSession(true);
			Tia12Users loginUserRole=(Tia12Users) session.getAttribute("logInUserObject");
			int loginUserEnterpriseId = loginUserRole.getTia12Enterprises().getEnterpriseId();
	   		String searchTerm = req.getParameter("searchterm");
			searchTerm = searchTerm.trim();
		    searchTerm = commonservice.escapeSearchString(searchTerm);
			List<Tia12Advertisements> list=advertisementService.exportAdvertisements(searchTerm, loginUserEnterpriseId);
			String[] s = new String[17];
			s[0] = messages.getMessage("label.headerAdvertisementsPrompt", null, "Advertisements"+locale, locale);
		    s[1] = messages.getMessage("label.enterpriseColumnEnterpriseNamePrompt", null, "Enterprise Name"+locale, locale);
		    s[2] = messages.getMessage("label.advertisementColumntAdvertisementNamePrompt", null, "Advertisement Name"+locale, locale);
		    s[3] = messages.getMessage("label.advertisementColumntAdvertisementTypePrompt", null, "Advertisement Type"+locale, locale);
		    s[4] = messages.getMessage("label.advertisementColumntAdvertisementStartDatePrompt", null, "Advertisement Start Date"+locale, locale);
		    s[5] = messages.getMessage("label.advertisementColumntAdvertisementEndDatePrompt", null, "Advertisement End Date"+locale, locale);
		    s[6] = messages.getMessage("label.advertisementColumntAdvertisementSequencePrompt", null, "Advertisement Sequence"+locale, locale);
		    s[7] = messages.getMessage("label.advertisementColumntAdvertisementFileNamePrompt", null, "Advertisement File Name"+locale, locale);
		    s[8] = messages.getMessage("label.advertisementColumntAdvertisementURLPrompt", null, "Advertisement URL"+locale, locale);
		    s[9] = messages.getMessage("label.advertisementColumntAdvertisementDescriptionPrompt", null, "Advertisement Description"+locale, locale);
		    s[10] = messages.getMessage("label.advertisementColumntAdvertisementNotesPrompt", null, "Advertisement Notes"+locale, locale);
		    s[11] = messages.getMessage("label.commonColumntEnabledPrompt", null, "Enabled"+locale, locale);
			s[12] = messages.getMessage("label.commonColumntCreatedByPrompt", null, "Created By"+locale, locale);
			s[13] = messages.getMessage("label.commonColumntCreationDatePrompt", null, "Creation Date"+locale, locale);
			s[14] = messages.getMessage("label.commonLastUpdatedByPrompt", null, "Last Updated By"+locale, locale);
			s[15] = messages.getMessage("label.commonColumntLastUpdatedDatePrompt", null, "Last Updated Date"+locale, locale);
			s[16] = messages.getMessage("label.commonApplicationNamePrompt", null, "Tour India"+locale, locale);

			ModelAndView mv = new ModelAndView("advertisementsExport", "list", list);
			mv.addObject("columnValues", s);
			
			String logInUserDateFormat = (String) session.getAttribute("logInUserDateFormat");
	          String[] fromDate = new String[list.size()];
	          String[] toDate = new String[list.size()];
	          for (int i=0; i<list.size(); i++) {
	              SimpleDateFormat formatter = new SimpleDateFormat(logInUserDateFormat+" HH:mm:ss");
	              fromDate[i] = formatter.format(list.get(i).getAdvertisementStartDate());
	              toDate[i] = formatter.format(list.get(i).getAdvertisementEndDate());
	          }
	          mv.addObject("fromDate", fromDate);
	          mv.addObject("toDate", toDate);
			
//			Start login user time format and Time zone 150529
			String []expCrLuDates=new String[list.size()];
			for (int i=0; i<list.size(); i++) {
				String crtDate = commonservice.getTimeStampByLoginUserTzDF(req,list.get(i).getCreationDate());
				String lstUDate = commonservice.getTimeStampByLoginUserTzDF(req,list.get(i).getLastUpdatedDate());
				expCrLuDates[i]=crtDate+"~"+lstUDate;
			}
			String exportDate = commonservice.getDateByLoginUserTzDF(req, commonservice.getUTCDateTime());
			mv.addObject("exportDate", exportDate);
			mv.addObject("expCrLuDates", expCrLuDates);
//			End login user time format and Time zone 150529		

			return mv;
	    }

	 @RequestMapping(value = "/getvalidatedate", method = RequestMethod.GET)
	 public @ResponseBody boolean getvalidatedate(HttpServletRequest req,Locale locale, Model model, @RequestParam("postedValue1") String dateInString) throws ParseException {
		  HttpSession sessionHttp = req.getSession(true);
		  String logInUserTmZone = (String) sessionHttp.getAttribute("logInUserTimeZoneMeaning");
		  Date date = new Date();
		  if (dateInString != null) { 
		         SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				 try {
						date = formatter1.parse(dateInString);
					} catch (ParseException e) {
						e.printStackTrace();
					}
		  }
		  boolean validate = advertisementService.isFromDateValied(date, logInUserTmZone);
		  return validate;
		}

    }
