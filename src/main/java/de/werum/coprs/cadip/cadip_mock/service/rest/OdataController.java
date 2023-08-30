package de.werum.coprs.cadip.cadip_mock.service.rest;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.olingo.commons.api.edmx.EdmxReference;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataHttpHandler;
import org.apache.olingo.server.api.ServiceMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.werum.coprs.cadip.cadip_mock.data.Storage;
import de.werum.coprs.cadip.cadip_mock.service.edm.EdmProvider;
import de.werum.coprs.cadip.cadip_mock.service.processor.ProductEntityCollectionProcessor;

@RestController
@RequestMapping(value = "/odata")
public class OdataController {

	@Autowired
	private EdmProvider edmProvider;
	@Autowired
	private Storage storage;
	
	@RequestMapping(value = "/v1/**")
	public void process(HttpServletRequest request, HttpServletResponse response) {		
		OData oData = OData.newInstance();                                       
		ServiceMetadata serviceMetadata = oData.createServiceMetadata(edmProvider, new ArrayList<EdmxReference>());
		ODataHttpHandler handler = oData.createHandler(serviceMetadata);
		handler.register(new ProductEntityCollectionProcessor(storage));
		
		handler.process(new HttpServletRequestWrapper(request) {
	         @Override
	         public String getServletPath() {
	            return "odata/v1"; // just the prefix up to /odata/v1, the rest is used as parameters by Olingo
	         }
	         
	         // /**
	         //  * @see javax.servlet.http.HttpServletRequestWrapper#getQueryString()
	         //  */
	         // @Override
	         // public String getQueryString() {
	         //    // normalise query string
	         //    return handleGeometricRequests(super.getQueryString());
	         // }
	      }, response);
	}
	
}
