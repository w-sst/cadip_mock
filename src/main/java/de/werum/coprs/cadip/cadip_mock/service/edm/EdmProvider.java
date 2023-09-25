package de.werum.coprs.cadip.cadip_mock.service.edm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlAbstractEdmProvider;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainer;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainerInfo;
import org.apache.olingo.commons.api.edm.provider.CsdlEntitySet;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityType;
import org.apache.olingo.commons.api.edm.provider.CsdlProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlPropertyRef;
import org.apache.olingo.commons.api.edm.provider.CsdlSchema;
import org.apache.olingo.commons.api.ex.ODataException;

public class EdmProvider extends CsdlAbstractEdmProvider {

	// Service Namespace
	public static final String NAMESPACE = "OData.CSC";

	// EDM Container
	public static final String CONTAINER_NAME = "Container";
	public static final FullQualifiedName CONTAINER = new FullQualifiedName(NAMESPACE, CONTAINER_NAME);

	// Entity Types Names
	public static final String ET_SESSION_NAME = "Session";
	public static final FullQualifiedName ET_SESSION_FQN = new FullQualifiedName(NAMESPACE, ET_SESSION_NAME);

	public static final String ET_FILE_NAME = "File";
	public static final FullQualifiedName ET_FILE_FQN = new FullQualifiedName(NAMESPACE, ET_FILE_NAME);

	// Entity Set Names
	public static final String ES_SESSIONS_NAME = "Sessions";
	public static final String ES_FILES_NAME = "Files";

	@Override
	public CsdlEntityType getEntityType(FullQualifiedName entityTypeName) throws ODataException {

		if (entityTypeName.equals(ET_SESSION_FQN)) {
			// create EntityType properties
			CsdlProperty id = new CsdlProperty().setName("Id")
					.setType(EdmPrimitiveTypeKind.Guid.getFullQualifiedName());
			CsdlProperty sessionId = new CsdlProperty().setName("SessionId")
					.setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
			CsdlProperty numChannels = new CsdlProperty().setName("NumChannels")
					.setType(EdmPrimitiveTypeKind.Int64.getFullQualifiedName());
			CsdlProperty publicationDate = new CsdlProperty().setName("PublicationDate")
					.setType(EdmPrimitiveTypeKind.DateTimeOffset.getFullQualifiedName()).setPrecision(3);
			CsdlProperty satellite = new CsdlProperty().setName("Satellite")
					.setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
			CsdlProperty stationUnitId = new CsdlProperty().setName("StationUnitId")
					.setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
			CsdlProperty downlinkOrbit = new CsdlProperty().setName("DownlinkOrbit")
					.setType(EdmPrimitiveTypeKind.Int64.getFullQualifiedName());
			CsdlProperty acquisitionId = new CsdlProperty().setName("AcquisitionId")
					.setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
			CsdlProperty antennaId = new CsdlProperty().setName("AntennaId")
					.setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
			CsdlProperty frontEndId = new CsdlProperty().setName("FrontEndId")
					.setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
			CsdlProperty retransfer = new CsdlProperty().setName("Retransfer")
					.setType(EdmPrimitiveTypeKind.Boolean.getFullQualifiedName());
			CsdlProperty antennaStatusOK = new CsdlProperty().setName("AntennaStatusOK")
					.setType(EdmPrimitiveTypeKind.Boolean.getFullQualifiedName());
			CsdlProperty frontEndStatusOK = new CsdlProperty().setName("FrontEndStatusOK")
					.setType(EdmPrimitiveTypeKind.Boolean.getFullQualifiedName());
			CsdlProperty plannedDataStart = new CsdlProperty().setName("PlannedDataStart")
					.setType(EdmPrimitiveTypeKind.DateTimeOffset.getFullQualifiedName()).setPrecision(6);
			CsdlProperty plannedDataStop = new CsdlProperty().setName("PlannedDataStop")
					.setType(EdmPrimitiveTypeKind.DateTimeOffset.getFullQualifiedName()).setPrecision(6);
			CsdlProperty downlinkStart = new CsdlProperty().setName("DownlinkStart")
					.setType(EdmPrimitiveTypeKind.DateTimeOffset.getFullQualifiedName()).setPrecision(6);
			CsdlProperty downlinkStop = new CsdlProperty().setName("DownlinkStop")
					.setType(EdmPrimitiveTypeKind.DateTimeOffset.getFullQualifiedName()).setPrecision(6);
			CsdlProperty downlinkStatusOK = new CsdlProperty().setName("DownlinkStatusOK")
					.setType(EdmPrimitiveTypeKind.Boolean.getFullQualifiedName());
			CsdlProperty deliveryPushOK = new CsdlProperty().setName("DeliveryPushOK")
					.setType(EdmPrimitiveTypeKind.Boolean.getFullQualifiedName());

			// create CsdlPropertyRef for Key element
			CsdlPropertyRef propertyRef = new CsdlPropertyRef();
			propertyRef.setName("Id");

			// configure EntityType
			CsdlEntityType entityType = new CsdlEntityType();
			entityType.setName(ET_SESSION_NAME);
			entityType.setProperties(Arrays.asList(id, sessionId, numChannels, publicationDate, satellite,
					stationUnitId, downlinkOrbit, acquisitionId, antennaId, frontEndId, retransfer, antennaStatusOK,
					frontEndStatusOK, plannedDataStart, plannedDataStop, downlinkStart, downlinkStop, downlinkStatusOK,
					deliveryPushOK));

			entityType.setKey(Collections.singletonList(propertyRef));
			return entityType;
		} else if (entityTypeName.equals(ET_FILE_FQN)) {
			CsdlProperty id = new CsdlProperty().setName("Id")
					.setType(EdmPrimitiveTypeKind.Guid.getFullQualifiedName());
			CsdlProperty name = new CsdlProperty().setName("Name")
					.setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
			CsdlProperty sessionId = new CsdlProperty().setName("SessionId")
					.setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
			CsdlProperty channel = new CsdlProperty().setName("Channel")
					.setType(EdmPrimitiveTypeKind.Int64.getFullQualifiedName());
			CsdlProperty blockNumber = new CsdlProperty().setName("BlockNumber")
					.setType(EdmPrimitiveTypeKind.Int64.getFullQualifiedName());
			CsdlProperty finalBlock = new CsdlProperty().setName("FinalBlock")
					.setType(EdmPrimitiveTypeKind.Boolean.getFullQualifiedName());
			CsdlProperty publicationDate = new CsdlProperty().setName("PublicationDate")
					.setType(EdmPrimitiveTypeKind.DateTimeOffset.getFullQualifiedName()).setPrecision(3);
			CsdlProperty evictionDate = new CsdlProperty().setName("EvictionDate")
					.setType(EdmPrimitiveTypeKind.DateTimeOffset.getFullQualifiedName()).setPrecision(3);
			CsdlProperty size = new CsdlProperty().setName("Size")
					.setType(EdmPrimitiveTypeKind.Int64.getFullQualifiedName());
			CsdlProperty retransfer = new CsdlProperty().setName("Retransfer")
					.setType(EdmPrimitiveTypeKind.Boolean.getFullQualifiedName());

			// create CsdlPropertyRef for Key element
			CsdlPropertyRef propertyRef = new CsdlPropertyRef();
			propertyRef.setName("Id");

			// configure EntityType
			CsdlEntityType entityType = new CsdlEntityType();
			entityType.setName(ET_FILE_NAME);
			entityType.setProperties(Arrays.asList(id, name, sessionId, channel, blockNumber, finalBlock,
					publicationDate, evictionDate, size, retransfer));

			entityType.setKey(Collections.singletonList(propertyRef));
			entityType.setHasStream(true);

			return entityType;
		}

		return null;
	}

	@Override
	public CsdlEntitySet getEntitySet(FullQualifiedName entityContainer, String entitySetName) throws ODataException {
		if (entityContainer.equals(CONTAINER)) {
			if (entitySetName.equals(ES_SESSIONS_NAME)) {
				CsdlEntitySet entitySet = new CsdlEntitySet();
				entitySet.setName(ES_SESSIONS_NAME);
				entitySet.setType(ET_SESSION_FQN);

				return entitySet;
			} else if (entitySetName.equals(ES_FILES_NAME)) {
				CsdlEntitySet entitySet = new CsdlEntitySet();
				entitySet.setName(ES_FILES_NAME);
				entitySet.setType(ET_FILE_FQN);

				return entitySet;
			}
		}

		return null;
	}

	@Override
	public CsdlEntityContainer getEntityContainer() throws ODataException {

		// create EntitySets
		List<CsdlEntitySet> entitySets = new ArrayList<CsdlEntitySet>();
		entitySets.add(getEntitySet(CONTAINER, ES_SESSIONS_NAME));
		entitySets.add(getEntitySet(CONTAINER, ES_FILES_NAME));
		// create EntityContainer
		CsdlEntityContainer entityContainer = new CsdlEntityContainer();
		entityContainer.setName(CONTAINER_NAME);
		entityContainer.setEntitySets(entitySets);

		return entityContainer;
	}

	@Override
	public CsdlEntityContainerInfo getEntityContainerInfo(FullQualifiedName entityContainerName) throws ODataException {

		// This method is invoked when displaying the Service Document at e.g.
		// http://localhost:8080/DemoService/DemoService.svc
		if (entityContainerName == null || entityContainerName.equals(CONTAINER)) {
			CsdlEntityContainerInfo entityContainerInfo = new CsdlEntityContainerInfo();
			entityContainerInfo.setContainerName(CONTAINER);
			return entityContainerInfo;
		}

		return null;
	}

	@Override
	public List<CsdlSchema> getSchemas() throws ODataException {
		// create Schema
		CsdlSchema schema = new CsdlSchema();
		schema.setNamespace(NAMESPACE);

		// add EntityTypes
		List<CsdlEntityType> entityTypes = new ArrayList<CsdlEntityType>();
		entityTypes.add(getEntityType(ET_SESSION_FQN));
		entityTypes.add(getEntityType(ET_FILE_FQN));
		schema.setEntityTypes(entityTypes);

		// add EntityContainer
		schema.setEntityContainer(getEntityContainer());

		// finally
		List<CsdlSchema> schemas = new ArrayList<CsdlSchema>();
		schemas.add(schema);

		return schemas;
	}

}
