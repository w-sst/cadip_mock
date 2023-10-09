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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.uri.UriParameter;

import de.werum.coprs.cadip.cadip_mock.data.model.File;
import de.werum.coprs.cadip.cadip_mock.data.model.QualityInfo;
import de.werum.coprs.cadip.cadip_mock.data.model.Session;
import de.werum.coprs.cadip.cadip_mock.service.edm.EdmProvider;
import de.werum.coprs.cadip.cadip_mock.util.MappingUtil;
import de.werum.coprs.cadip.cadip_mock.util.OlingoUtil;

public class Storage {

	private static final Logger LOG = LogManager.getLogger(Storage.class);
	private List<Session> sessionsList;
	private List<QualityInfo> qualityInfoList;
	private Map<String, Set<File>> filesList;

	public Storage() {
		sessionsList = new ArrayList<Session>();
		qualityInfoList = new ArrayList<QualityInfo>();
		filesList = new HashMap<>();
	}

	/* PUBLIC FACADE */

	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(sessionsList.size() + " Sessions:\n");
		sessionsList.forEach(o -> builder.append("\t" + o.toString() + "\n"));
		filesList.forEach((s, l) -> {
			builder.append(l.size() + " Files for " + s + ":\n");
			l.forEach(o -> builder.append("\t" + o.toString() + "\n"));
		});

		return builder.toString();
	}

	public EntityCollection readEntitySetData(EdmEntitySet edmEntitySet) throws ODataApplicationException {
		return readEntitySetData(edmEntitySet.getEntityType().getName());
	}

	public EntityCollection readEntitySetData(String entityName) throws ODataApplicationException {
		switch (entityName) {
		case EdmProvider.ET_SESSION_NAME:
			return getSessionCollection();
		case EdmProvider.ET_FILE_NAME:
			return getFileCollection();
		default:
			throw new ODataApplicationException("EntitySet for requested key doesn't exist",
					HttpStatusCode.NOT_FOUND.getStatusCode(),
					Locale.ENGLISH);
		}
	}

	public Entity readEntityData(EdmEntitySet edmEntitySet, List<UriParameter> keyParams)
			throws ODataApplicationException {

		EdmEntityType edmEntityType = edmEntitySet.getEntityType();

		switch (edmEntityType.getName()) {
		case EdmProvider.ET_SESSION_NAME:
		case EdmProvider.ET_FILE_NAME:
			return getProduct(edmEntityType, keyParams);
		default:
			throw new ODataApplicationException("Entity for requested key doesn't exist",
					HttpStatusCode.NOT_FOUND.getStatusCode(),
					Locale.ENGLISH);
		}

	}

	public boolean hasSession(String sessionId) {
		boolean hasSession = sessionsList.stream().filter(o -> o.getSessionId().equals(sessionId)).findAny()
				.isPresent();
		return hasSession;
	}

	public void addSessionToList(Session sesison) {
		sessionsList.add(sesison);
	}
	
	public void addQualityInfoToList(QualityInfo qI) {
		qualityInfoList.add(qI);
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
		}
		;
		return null;
	}

	public EntityCollection getEntitiesForSession(Entity sourceEntity, EdmEntityType targetEntityType)
			throws ODataApplicationException {
		EntityCollection entitySet = new EntityCollection();
		List<Entity> entityList = entitySet.getEntities();
		String sessionId = (String) sourceEntity.getProperty("SessionId").getValue();
		if (targetEntityType.getName().equals(EdmProvider.ET_FILE_NAME)) {
			Set<File> fileSet = filesList.get(sessionId);
			if (fileSet == null) {
				throw new ODataApplicationException("Found no Files for Session: " + sourceEntity,
						HttpStatusCode.NOT_FOUND.getStatusCode(),
						Locale.ENGLISH);
			}
			entityList.addAll(MappingUtil.mapFileListToEntityList(fileSet));
		} else if (targetEntityType.getName().equals(EdmProvider.ET_QUALITYINFO_NAME)) {
			for (QualityInfo qualityInfo : qualityInfoList) {
				if (qualityInfo.getSessionId().equals(sessionId)) {
					entityList.add(MappingUtil.mapQualityInfoToEntity(qualityInfo));
				}
			}
			
		}
		return entitySet;
	}

	public Entity getEntityForSession(Entity sourceEntity, EdmEntityType targetEntityType)
			throws ODataApplicationException {
		String sessionId = (String) sourceEntity.getProperty("SessionId").getValue();
		if (targetEntityType.getName().equals(EdmProvider.ET_QUALITYINFO_NAME)) {
			for (QualityInfo qualityInfo : qualityInfoList) {
				if (qualityInfo.getSessionId().equals(sessionId)) {
					return MappingUtil.mapQualityInfoToEntity(qualityInfo);
				}
			}
			throw new ODataApplicationException("Found no QualityInfo for Session: " + sourceEntity,
					HttpStatusCode.NOT_FOUND.getStatusCode(),
					Locale.ENGLISH);
		}
		return null;
	}

	private EntityCollection getSessionCollection() {
		EntityCollection entitySet = new EntityCollection();
		List<Entity> entityList = entitySet.getEntities();
		for (Session session : sessionsList) {
			Entity sessionEntity = MappingUtil.mapSessionToEntity(session);
			entityList.add(sessionEntity);
		}
		return entitySet;
	}

	private EntityCollection getFileCollection() {
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

	private Entity getProduct(EdmEntityType edmEntityType, List<UriParameter> keyParams)
			throws ODataApplicationException {

		// the list of entities at runtime
		EntityCollection entitySet = readEntitySetData(edmEntityType.getName());

		Entity requestedEntity = OlingoUtil.findEntity(edmEntityType, entitySet, keyParams);

		if (requestedEntity == null) {
			throw new ODataApplicationException("Entity for requested key doesn't exist",
					HttpStatusCode.NOT_FOUND.getStatusCode(),
					Locale.ENGLISH);
		}

		return requestedEntity;
	}

	public InputStream readMedia(String filePath) throws FileNotFoundException {
		java.io.File initialFile = new java.io.File(filePath);
		InputStream fileStream = new FileInputStream(initialFile);

		return fileStream;
	}

	public InputStream readMedia(String filePath, long offset, long length) throws FileNotFoundException, ODataApplicationException {
		RandomAccessFile randomAccessFile = new RandomAccessFile(filePath, "r");
		byte[] buffer = new byte[(int) length];
		try {
			randomAccessFile.seek(offset);
			randomAccessFile.readFully(buffer);
		} catch (IOException e) {
			LOG.error("Error reading {} and writing partially into buffer", filePath, e);
			throw new ODataApplicationException("Error reading requested File" ,
					HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(),
					Locale.ENGLISH);
		} finally {
			try {
				randomAccessFile.close();
			} catch (IOException e) {
				LOG.error(e);
			}
		}
		return new ByteArrayInputStream(buffer);
	}
}
