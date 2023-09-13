package de.werum.coprs.cadip.cadip_mock.data;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.uri.UriParameter;

import de.werum.coprs.cadip.cadip_mock.data.model.File;
import de.werum.coprs.cadip.cadip_mock.data.model.Session;
import de.werum.coprs.cadip.cadip_mock.service.edm.EdmProvider;
import de.werum.coprs.cadip.cadip_mock.util.MappingUtil;
import de.werum.coprs.cadip.cadip_mock.util.OlingoUtil;

public class Storage {

    private List<Session> sessionsList;
    private Map<String, Set<File>> filesList;
    
    public Storage() {
        sessionsList = new ArrayList<Session>();
        filesList = new HashMap<>();
    }

    /* PUBLIC FACADE */

    public void printAll() {
    	
    	sessionsList.forEach(o -> System.out.println(o.toString()));
    	filesList.forEach((s, l) -> {
    		System.out.println(l.size()+ " Files for " + s);
    		l.forEach(o -> System.out.println("\t"+o.toString()));
    	});
    }
    
    public EntityCollection readEntitySetData(EdmEntitySet edmEntitySet)throws ODataApplicationException{
    	return readEntitySetData(edmEntitySet.getEntityType().getName());
    }
    
    public EntityCollection readEntitySetData(String entityName)throws ODataApplicationException{
    	switch(entityName) {
    		case EdmProvider.ET_SESSION_NAME:
    			return getSessionsSet();
    		case EdmProvider.ET_FILE_NAME:
    			return getFilesSet();
    		default:
    			return null;
    	}
    }

    public Entity readEntityData(EdmEntitySet edmEntitySet, List<UriParameter> keyParams) throws ODataApplicationException{

        EdmEntityType edmEntityType = edmEntitySet.getEntityType();

        switch(edmEntityType.getName()) {
			case EdmProvider.ET_SESSION_NAME:
			case EdmProvider.ET_FILE_NAME:
				return getProduct(edmEntityType, keyParams);
			default:
				return null;
        }
            
    }

    public boolean hasSession(String sessionId) {
    	boolean hasSession = sessionsList.stream().filter(o -> o.getSessionId().equals(sessionId)).findAny().isPresent();
    	return hasSession;
    }
    
    public void addSessionToList(Session sesison) {
    	sessionsList.add(sesison);
    }
    
    public void createFileSet(String sessionId) {
    	filesList.put(sessionId, new LinkedHashSet<File>());
    }
    
    public Set<File> getFileSet(String sessionId) {
    	return filesList.get(sessionId);
    }
    
    public File getFile(String UUID) {
    	for (Entry<String, Set<File>> t : filesList.entrySet()) {
    		Optional<File> optFile = t.getValue().stream().filter(o -> o.getId().toString().equals(UUID)).findAny();
    		if (optFile.isPresent()) {
    			return optFile.get();
    		}
    	};
    	return null;
    }
    
    /*  INTERNAL */

    private List<Entity> getEntityList(String entityName) throws ODataApplicationException {
    	switch (entityName) {
    		case EdmProvider.ET_SESSION_NAME:
    			return null; //sessionsList;
    		case EdmProvider.ET_FILE_NAME:
    			return null; // filesList;
    		default:
    			throw new ODataApplicationException("Entity for requested key doesn't exist",
                        HttpStatusCode.NOT_FOUND.getStatusCode(), Locale.ENGLISH);
    	}
    }
    
    private EntityCollection getProductsSet(String entityName) throws ODataApplicationException{
        EntityCollection retEntitySet = new EntityCollection();
        List<Entity> entityList = getEntityList(entityName);
        
        for(Entity productEntity : entityList){
            retEntitySet.getEntities().add(productEntity);
        }

        return retEntitySet;
    }
    
    private EntityCollection getSessionsSet() {
    	EntityCollection entitySet = new EntityCollection();
    	List<Entity> entityList = entitySet.getEntities();
    	for (Session session : sessionsList) {
    		Entity sessionEntity = MappingUtil.mapSessionToEntity(session);
    		entityList.add(sessionEntity);
    	}
    	return entitySet;
    }
    
    private EntityCollection getFilesSet() {
    	EntityCollection entitySet = new EntityCollection();
    	List<Entity> entityList = entitySet.getEntities();
    	filesList.forEach((k, v) -> {
    		v.forEach(o -> {
    			Entity fileEntity = MappingUtil.mapFileToEntity(o);
    			entityList.add(fileEntity);
    		});
    	});
    	return entitySet;
    }

    private Entity getProduct(EdmEntityType edmEntityType, List<UriParameter> keyParams) throws ODataApplicationException{

        // the list of entities at runtime
        EntityCollection entitySet = readEntitySetData(edmEntityType.getName());

        /*  generic approach  to find the requested entity */
        Entity requestedEntity = OlingoUtil.findEntity(edmEntityType, entitySet, keyParams);

        if(requestedEntity == null){
            // this variable is null if our data doesn't contain an entity for the requested key
            // Throw suitable exception
            throw new ODataApplicationException("Entity for requested key doesn't exist",
                                       HttpStatusCode.NOT_FOUND.getStatusCode(), Locale.ENGLISH);
        }

        return requestedEntity;
     }

	public InputStream readMedia(String filePath) throws FileNotFoundException {
		java.io.File initialFile = new java.io.File(filePath);
		InputStream fileStream = new FileInputStream(initialFile);
		
		return fileStream;
	}
	
	public InputStream readMedia(String filePath, long offset, long length) throws FileNotFoundException {
		RandomAccessFile randomAccessFile = new RandomAccessFile(filePath, "r");
		byte[] buffer = new byte[(int) length];
        try {
			randomAccessFile.seek(offset);
			randomAccessFile.readFully(buffer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				randomAccessFile.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}        
        return new ByteArrayInputStream(buffer);
	}
}
